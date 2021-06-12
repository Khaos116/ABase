package cc.ab.base.widget.engine

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.core.graphics.drawable.toBitmap
import cc.ab.base.ext.*
import cc.ab.base.utils.MediaUtils
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.blankj.utilcode.util.Utils
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.photoview.PhotoView
import com.luck.picture.lib.widget.longimage.*
import java.lang.ref.WeakReference

/**
 * Author:Khaos
 * Date:2020/8/28
 * Time:15:32
 */
class ImageEngine : com.luck.picture.lib.engine.ImageEngine {
  //预览图片的加载图片
  override fun loadImage(context: Context, url: String, imageView: ImageView) {
    val weak = WeakReference(imageView)
    val isPhotoView = imageView is PhotoView
    val isImageFile = MediaUtils.isImageFile(url)
    val isVideoFile = MediaUtils.isVideoFile(url)
    if (imageView.width == 0 && isPhotoView && (isImageFile || isVideoFile)) {
      imageView.post { weak.get()?.let { iv -> delayLoad(url, iv) } }
    } else delayLoad(url, imageView)
  }

  private fun delayLoad(url: String, imageView: ImageView) {
    val width = imageView.width
    val height = imageView.height
    if (imageView is PhotoView) {
      if (MediaUtils.isImageFile(url)) {
        val options = Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(url.toFile()?.path, options)
        val widthImg = options.outWidth * 1f
        val heightImg = options.outHeight * 1f
        if (widthImg > 0) {
          val isHorizontalVideo = (widthImg * 1f / heightImg) > (width * 1f / height)
          val defaultRatio = if (isHorizontalVideo) height * 1f / heightImg else width * 1f / widthImg
          val ratio = if (isHorizontalVideo) (width * 1f / widthImg / defaultRatio)
          else (height * 1f / heightImg) / defaultRatio
          imageView.setScaleLevels(ratio, ratio * 1.5f, ratio * 2f)
          imageView.scale = ratio
        } else imageView.setScaleLevels(0.5f, 1f, 2f)
      } else if (MediaUtils.isVideoFile(url)) {
        //读取视频尺寸和旋转角度
        val mMetadataRetriever = MediaMetadataRetriever()
        try {
          mMetadataRetriever.setDataSource(url)
          val videoRotation = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION) ?: "0"
          val videoHeight = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "0"
          val videoWidth = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "0"
          mMetadataRetriever.release()
          var heightVideo = 0
          val widthVideo = if (Integer.parseInt(videoRotation) == 90 || Integer.parseInt(videoRotation) == 270) {
            //角度不对需要宽高调换
            heightVideo = videoWidth.toInt()
            videoHeight.toInt()
          } else {
            heightVideo = videoHeight.toInt()
            videoWidth.toInt()
          }
          if (widthVideo > 0) {
            val isHorizontalVideo = (widthVideo * 1f / heightVideo) > (width * 1f / height)
            val defaultRatio = if (isHorizontalVideo) height * 1f / heightVideo else width * 1f / widthVideo
            val ratio = if (isHorizontalVideo) (width * 1f / widthVideo / defaultRatio)
            else (height * 1f / heightVideo) / defaultRatio
            imageView.setScaleLevels(ratio, ratio * 1.5f, ratio * 2f)
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
    imageView.loadImgVertical(url, 720f / 1280)
  }

  //加载网络图片适配长图方案(此方法只有加载网络图片才会回调)
  override fun loadImage(
      context: Context,
      url: String,
      imageView: ImageView,
      longImageView: SubsamplingScaleImageView?,
      callback: OnImageCompleteCallback?
  ) {
    val weakReference = WeakReference(imageView)
    val weakReferenceLong = WeakReference(longImageView)
    Utils.getApp().imageLoader.enqueue(
        ImageRequest.Builder(Utils.getApp()).data(url).target(
            onStart = {
              weakReferenceLong.get()?.gone()
              weakReference.get()?.let { iv ->
                iv.visible()
                iv.clearLoad()
                iv.load(PlaceHolderUtils.getLoadingHolder(720f / 1280))
              }
            },
            onSuccess = { resource -> weakReference.get()?.let { iv -> loadNetImage(resource, iv, weakReferenceLong.get()) } },
            onError = {
              weakReferenceLong.get()?.gone()
              weakReference.get()?.let { iv ->
                iv.visible()
                iv.clearLoad()
                iv.load(PlaceHolderUtils.getErrorHolder(720f / 1280))
              }
            }
        ).build()
    )
  }

  //加载网络图片
  private fun loadNetImage(drawable: Drawable, imageView: ImageView, longImageView: SubsamplingScaleImageView?) {
    val bitmap = drawable.toBitmap()
    val eqLongImage: Boolean = com.luck.picture.lib.tools.MediaUtils.isLongImg(bitmap.width, bitmap.height)
    longImageView?.visibility = if (eqLongImage) View.VISIBLE else View.GONE
    imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
    if (eqLongImage) {
      // 加载长图
      longImageView?.apply {
        isQuickScaleEnabled = true
        isZoomEnabled = true
        isPanEnabled = true
        setDoubleTapZoomDuration(100)
        setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
        setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
        setImage(ImageSource.bitmap(bitmap), ImageViewState(0f, PointF(0f, 0f), 0))
      }
    } else {
      // 普通图片
      imageView.load(drawable)
    }
  }

  //已废弃
  override fun loadImage(context: Context, url: String, imageView: ImageView, longImageView: SubsamplingScaleImageView?) {
    loadImage(context, url, imageView, longImageView, null)
  }

  //加载相册目录
  override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    imageView.loadImgSquare(url)
  }

  //加载gif
  override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
    if (imageView.scaleType == ScaleType.CENTER_CROP) {
      imageView.loadImgSquare(url)
    } else {
      imageView.loadImgVertical(url, 720f / 1280)
    }
  }

  //加载图片列表图片
  override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    imageView.loadImgSquare(url)
  }
}
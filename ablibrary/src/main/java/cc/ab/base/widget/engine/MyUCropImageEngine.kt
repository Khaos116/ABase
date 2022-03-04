package cc.ab.base.widget.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import cc.ab.base.ext.PlaceHolderUtils
import cc.ab.base.ext.loadImgVertical
import cc.ab.base.ext.toFile
import cc.ab.base.utils.MediaUtils
import coil.imageLoader
import coil.request.ImageRequest
import com.luck.picture.lib.photoview.PhotoView
import com.yalantis.ucrop.UCropImageEngine
import java.lang.ref.WeakReference

/**
 * Author:Khaos116
 * Date:2022/3/2
 * Time:18:59
 */
class MyUCropImageEngine : UCropImageEngine {
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
        val options = BitmapFactory.Options()
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

  override fun loadImage(context: Context, url: Uri, maxWidth: Int, maxHeight: Int, call: UCropImageEngine.OnCallbackListener<Bitmap>?) {
    context.imageLoader.enqueue(
      ImageRequest.Builder(context)
        .size(720, 1280)
        .data(url)
        .target(
          onStart = {
            call?.onCall(PlaceHolderUtils.getLoadingHolder(720f / 1280).toBitmap())
          },
          onSuccess = {
            call?.onCall(it.toBitmap())
          },
          onError = {
            call?.onCall(PlaceHolderUtils.getErrorHolder(720f / 1280).toBitmap())
          }
        )
        .build())
  }
}
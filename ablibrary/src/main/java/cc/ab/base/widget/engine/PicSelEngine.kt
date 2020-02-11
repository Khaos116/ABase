package cc.ab.base.widget.engine

import android.content.Context
import android.graphics.PointF
import android.widget.ImageView
import cc.ab.base.R
import cc.ab.base.ext.gone
import cc.ab.base.ext.loadSquare
import cc.ab.base.ext.visible
import cc.ab.base.ext.visibleGone
import cc.ab.base.utils.MediaUtils
import cc.ab.base.widget.sketch.VideoThumbnailUriModel
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.ImageCompleteCallback
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import me.panpf.sketch.Sketch
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.request.*


/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/30 17:16
 */
class PicSelEngine : ImageEngine {
  //文件夹图片大小
  private var sizeDir = 0
  //列表中的图片大小
  private var sizeGrid = 0
  //占位图
  private var optionGrid: DisplayOptions? = null

  //文件夹对应图片
  override fun loadFolderImage(context: Context, path: String, imageView: ImageView) {
    val url =
      if (MediaUtils.instance.isVideoFile(path)) {
        VideoThumbnailUriModel.makeUri(path)
      } else path
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    if (sizeDir == 0) {
      imageView.post {
        sizeDir = imageView.width
        loadFolderImage(context, url, imageView)
      }
    } else {
      imageView.layoutParams.width = sizeDir
      imageView.layoutParams.height = sizeDir
      if (imageView is SketchImageView) {
        imageView.setOptions(optionGrid)
        imageView.displayImage(url)
      } else {
        imageView.loadSquare(url)
      }
    }
  }

  //查看大图的图片
  override fun loadImage(
    context: Context,
    url: String,
    imageView: ImageView
  ) {
    //如果是相同的图片则不再进行加载
    if (imageView.getTag(R.id.id_tag_sketch_suc) == url) return
    imageView.setTag(R.id.id_tag_sketch_suc, null)
    Sketch.with(context)
        .load(url, object : LoadListener {
          override fun onStarted() {
            imageView.setImageResource(R.drawable.place_holder_square_loading2)
          }

          override fun onCanceled(cause: CancelCause) {
            imageView.setImageResource(R.drawable.place_holder_square_fail2)
          }

          override fun onError(cause: ErrorCause) {
            imageView.setImageResource(R.drawable.place_holder_square_fail2)
          }

          override fun onCompleted(result: LoadResult) {
            imageView.setImageBitmap(result.bitmap)
            imageView.setTag(R.id.id_tag_sketch_suc, url)
          }
        })
        .commit()
  }

  override fun loadImage(
    context: Context,
    url: String,
    imageView: ImageView,
    longImageView: SubsamplingScaleImageView
  ) {
    loadImage(context, url, imageView, longImageView, null)
  }

  //加载网络长图适配
  override fun loadImage(
    context: Context,
    url: String,
    imageView: ImageView,
    longImageView: SubsamplingScaleImageView,
    callback: ImageCompleteCallback?
  ) {
    imageView.visible()
    longImageView.gone()
    //如果是相同的图片则不再进行加载
    if (imageView.getTag(R.id.id_tag_sketch_suc) == url) return
    imageView.setTag(R.id.id_tag_sketch_suc, null)
    Sketch.with(context)
      .load(url, object : LoadListener {
        override fun onStarted() {
          imageView.setImageResource(R.drawable.place_holder_square_loading2)
        }

        override fun onCanceled(cause: CancelCause) {
          imageView.setImageResource(R.drawable.place_holder_square_fail2)
        }

        override fun onError(cause: ErrorCause) {
          imageView.setImageResource(R.drawable.place_holder_square_fail2)
        }

        override fun onCompleted(result: LoadResult) {
          result.bitmap?.let { resource ->
            imageView.setTag(R.id.id_tag_sketch_suc, url)
            val eqLongImage: Boolean = com.luck.picture.lib.tools.MediaUtils.isLongImg(
              resource.width,
              resource.height
            )
            longImageView.visibleGone(eqLongImage)
            imageView.visibleGone(!eqLongImage)
            if (eqLongImage) { // 加载长图
              longImageView.isQuickScaleEnabled = true
              longImageView.isZoomEnabled = true
              longImageView.isPanEnabled = true
              longImageView.setDoubleTapZoomDuration(100)
              longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
              longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
              longImageView.setImage(
                ImageSource.bitmap(resource),
                ImageViewState(0f, PointF(0f, 0f), 0)
              )
            } else { // 普通图片
              imageView.setImageBitmap(resource)
            }
          }
        }
      })
      .commit()
  }

  override fun loadAsGifImage(
    context: Context,
    url: String,
    imageView: ImageView
  ) {
    imageView.loadSquare(url)
  }

  //列表预览图片的加载
  override fun loadGridImage(context: Context, path: String, imageView: ImageView) {
    val url =
      if (MediaUtils.instance.isVideoFile(path)) {
        VideoThumbnailUriModel.makeUri(path)
      } else path
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    if (sizeGrid == 0) {
      imageView.post {
        sizeGrid = imageView.width
        optionGrid = DisplayOptions()
        optionGrid?.setLoadingImage(R.drawable.place_holder_square_loading2)
        optionGrid?.setErrorImage(R.drawable.place_holder_square_fail2)
        imageView.setImageResource(R.drawable.place_holder_square_loading2)
        loadGridImage(context, url, imageView)
      }
    } else {
      imageView.layoutParams.width = sizeGrid
      imageView.layoutParams.height = sizeGrid
      if (imageView is SketchImageView) {
        imageView.setOptions(optionGrid)
        imageView.displayImage(url)
      } else {
        imageView.loadSquare(url)
      }
    }
  }
}
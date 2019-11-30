package cc.ab.base.widget.engine

import android.content.Context
import android.widget.ImageView
import cc.ab.base.R
import cc.ab.base.ext.loadSquare
import cc.ab.base.utils.MediaUtils
import cc.ab.base.widget.sketch.VideoThumbnailUriModel
import com.luck.picture.lib.engine.ImageEngine
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

  //文件夹对于图片
  override fun loadFolderAsBitmapImage(
    context: Context,
    path: String,
    imageView: ImageView,
    placeholderId: Int
  ) {
    val url =
      if (MediaUtils.instance.isVideoFile(path)) {
        VideoThumbnailUriModel.makeUri(path)
      } else path
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    if (sizeDir == 0) {
      imageView.post {
        sizeDir = imageView.width
        loadAsBitmapGridImage(context, url, imageView, placeholderId)
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

  override fun loadAsGifImage(
    context: Context,
    url: String,
    imageView: ImageView
  ) {
    imageView.loadSquare(url)
  }

  //列表预览图片的加载
  override fun loadAsBitmapGridImage(
    context: Context,
    path: String,
    imageView: ImageView,
    placeholderId: Int
  ) {
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
        loadAsBitmapGridImage(context, url, imageView, placeholderId)
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
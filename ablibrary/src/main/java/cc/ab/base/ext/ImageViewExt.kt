package cc.ab.base.ext

import android.widget.ImageView
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import cc.ab.base.R
import cc.ab.base.utils.RandomPlaceholder
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils
import me.panpf.sketch.Sketch
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.display.FadeInImageDisplayer
import me.panpf.sketch.process.GaussianBlurImageProcessor
import me.panpf.sketch.request.*
import me.panpf.sketch.shaper.RoundRectImageShaper
import java.io.File

//加载正方形图片
fun ImageView.loadSquare(
  url: String?,
  size: Int = ScreenUtils.getScreenWidth() / 2
) {
  if (url.isNullOrBlank()) {
    setImageResource(R.drawable.place_holder_square_fail2)
  } else {
    //如果是相同的图片则不再进行加载
    if (getTag(R.id.id_tag_sketch_suc) == url) return
    setTag(R.id.id_tag_sketch_suc, null)
    val option = LoadOptions()
    val size2 = if (width > 0) width else size
    option.resize = Resize(size2, size2)
    option.isInPreferQualityOverSpeed = true
    option.isLowQualityImage = true
    if (!url.startsWith("http")) {
      option.isThumbnailMode = true
    }
    Sketch.with(context)
      .load(url, object : LoadListener {
        override fun onStarted() {
          setImageResource(R.drawable.place_holder_square_loading2)
        }

        override fun onCanceled(cause: CancelCause) {
          setImageResource(R.drawable.place_holder_square_fail2)
        }

        override fun onError(cause: ErrorCause) {
          setImageResource(R.drawable.place_holder_square_fail2)
        }

        override fun onCompleted(result: LoadResult) {
          setImageBitmap(result.bitmap)
          setTag(R.id.id_tag_sketch_suc, url)
        }
      })
      .options(option)
      .commit()
  }
}

//获取缓存文件
fun SketchImageView.getCacheFile(url: String?): File? {
  return if (url.isNullOrBlank()) {
    null
  } else {
    val cache = Sketch.with(Utils.getApp())
        .configuration.diskCache
    return if (cache.exist(url)) cache.get(url)?.file else null
  }
}

//加载文件
fun SketchImageView.load(file: File?) {
  if (file == null) this.setImageResource(R.drawable.svg_placeholder_fail)
  file?.let {
    this.displayImage(file.path)
  }
}

//普通加载
fun SketchImageView.load(
  url: String?,
  holderRes: Int = 0,
  errorRes: Int = 0
) {
  loadCornerBlur(url, holderRes, errorRes, 0f, 0)
}

//加载圆角
fun SketchImageView.loadCorner(
  url: String?,
  holderRes: Int = 0,
  errorRes: Int = 0,
  cornerDP: Float
) {
  loadCornerBlur(url, holderRes, errorRes, cornerDP, 0)
}

//加载高斯模糊
fun SketchImageView.loadBlur(
  url: String?,
  holderRes: Int = 0,
  errorRes: Int = 0,
  @IntRange(from = 0, to = 100) blur: Int
) {
  loadCornerBlur(url, holderRes, errorRes, 0f, blur)
}

//加载圆角图片
fun SketchImageView.loadCornerBlur(
  url: String?,
  holderRes: Int = 0,
  errorRes: Int = 0,
  @FloatRange(from = 0.0) cornerDP: Float,
  @IntRange(from = 0, to = 100) blur: Int
) {
  val displayOptions = DisplayOptions()
  if (holderRes >= 0 || errorRes >= 0) {
    val holder = RandomPlaceholder.instance.getPlaceHolder(url)
    val fail = R.drawable.svg_placeholder_fail
    if (holderRes >= 0) displayOptions.setLoadingImage(if (holderRes == 0) holder else holderRes)
    if (errorRes >= 0) displayOptions.setErrorImage(if (errorRes == 0) fail else errorRes)
  }
  if (cornerDP > 0) {
    if (this.width > 0) {
      //图片尺寸如果有圆角，则需要设置图片的尺寸，防止各个图片的圆角大小不一样
      displayOptions.shapeSize = ShapeSize(this.width, this.height, ImageView.ScaleType.CENTER_CROP)
    } else {
      //保存拿到尺寸后再加载
      this.post { loadCornerBlur(url, holderRes, errorRes, cornerDP, blur) }
      return
    }
  }
  if (cornerDP > 0) displayOptions.shaper = RoundRectImageShaper(SizeUtils.dp2px(cornerDP) * 1f)
  displayOptions.displayer = FadeInImageDisplayer()
  this.setOptions(displayOptions)
  val helper = Sketch.with(context)
      .display(url, this)
  if (blur > 0) helper.processor(GaussianBlurImageProcessor.makeRadius(blur))
  helper.commit()
}
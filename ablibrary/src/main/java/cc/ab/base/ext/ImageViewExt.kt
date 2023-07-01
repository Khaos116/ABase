package cc.ab.base.ext

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import cc.ab.base.R
import cc.ab.base.config.PathConfig
import cc.ab.base.utils.MediaMetadataRetrieverUtils
import cc.ab.base.utils.PlaceHolderUtils
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.blankj.utilcode.util.EncryptUtils
import java.io.File

/**
 * 预加载图片https://coil-kt.github.io/coil/getting_started/#preloading
 * 如果需要对加载的图片进行截屏，可能需要设置非硬件加载：allowHardware(false) https://coil-kt.github.io/coil/recipes/
 * diskCachePolicy(CachePolicy.DISABLED)
 * memoryCachePolicy(CachePolicy.DISABLED)
 * networkCachePolicy(CachePolicy.DISABLED)
 * Author:Khaos
 * Date:2020/8/12
 * Time:18:28
 */
private const val duration = 300

//清除上次的加载状态，保证重新加载
fun ImageView.clearLoad() {
  CoilUtils.dispose(this)
  setTag(R.id.suc_img, null)
}

//加载网络图片或者URI图片
fun ImageView.loadCoilImg(
  url: String?,
  holderRatio: Float = 1f,
  hasHolder: Boolean = true,
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f,
  blackWhite: Boolean = false,
  @FloatRange(from = 0.0) corner: Float = 0f
) {
  this.blackWhiteMode(false)
  if (url.isNullOrBlank()) {
    this.clearLoad()
    if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(holderRatio, corner = corner))
  } else {
    if (getTag(R.id.suc_img) == url) return
    this.clearLoad()
    val iv = this
    val f = url.toFile()
    if (f != null) {
      iv.load(f)
    } else {
      val request = ImageRequest.Builder(context)
        .data(url)
        .crossfade(if (hasHolder) 0 else duration)
        .target(
          onStart = { if (hasHolder) this.load(PlaceHolderUtils.getLoadingHolder(holderRatio, corner = corner)) },
          onError = { if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(holderRatio, corner = corner)) },
          onSuccess = { result ->
            this.blackWhiteMode(blackWhite)
            this.load(result)
            iv.setTag(R.id.suc_img, url)
          },
        )
        .build()
      context.applicationContext.imageLoader.enqueue(request)
    }
  }
}

//加载图片资源
fun ImageView.loadCoilImgRes(
  @DrawableRes resId: Int,
  holderRatio: Float = 1f,
  hasHolder: Boolean = true,
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f,
  blackWhite: Boolean = false,
  @FloatRange(from = 0.0) corner: Float = 0f
) {
  this.clearLoad()
  this.blackWhiteMode(false)
  val request = ImageRequest.Builder(context)
    .data(resId)
    .target(
      onStart = { if (hasHolder) this.load(PlaceHolderUtils.getLoadingHolder(holderRatio, corner = corner)) },
      onError = { if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(holderRatio, corner = corner)) },
      onSuccess = { result ->
        this.blackWhiteMode(blackWhite)
        this.load(result)
      },
    )
    .build()
  context.applicationContext.imageLoader.enqueue(request)
}

//加载视频网络封面
fun ImageView.loadNetVideoCover(url: String?, holderRatio: Float = 16f / 9, hasHolder: Boolean = true) {
  (getTag(R.id.id_retriever) as? MediaMetadataRetriever)?.release() //防止之前的图还没完成.
  if (url.isNullOrBlank()) { //有封面复用为无封面
    if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
  } else {
    val cacheFile = File(PathConfig.VIDEO_OVER_CACHE_DIR, EncryptUtils.encryptMD5ToString(url))
    if (cacheFile.exists()) {
      this.load(cacheFile) { if (!hasHolder) crossfade(false) }
    } else {
      if (hasHolder) this.load(PlaceHolderUtils.getLoadingHolder(holderRatio))
      val retriever = MediaMetadataRetriever()
      setTag(R.id.id_retriever, retriever)
      MediaMetadataRetrieverUtils.getNetVideoCover(retriever, cacheFile, url) { bit ->
        setTag(R.id.id_retriever, null)
        if (bit != null) {
          this.load(bit) { if (!hasHolder) crossfade(false) }
        } else {
          if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
        }
      }
    }
  }
}

//设置ImageView为黑白模式
fun ImageView?.blackWhiteMode(blackWhite: Boolean) {
  if (blackWhite) {
    val cm = ColorMatrix()
    cm.setSaturation(0f) // 设置饱和度
    val grayColorFilter = ColorMatrixColorFilter(cm)
    this?.colorFilter = grayColorFilter // 如果想恢复彩色显示，设置为null即可
  } else {
    this?.colorFilter = null
  }
}
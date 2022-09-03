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
import coil.*
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import coil.transform.Transformation
import coil.util.CoilUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.Utils
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.File

/**
 * 如果需要对加载的图片进行截屏，可能需要设置非硬件加载：allowHardware(false)
 * Author:Khaos
 * Date:2020/8/12
 * Time:18:28
 */
private const val duration = 300

//清除上次的加载状态，保证重新加载
fun ImageView.clearLoad() {
  this.clear()
  setTag(R.id.suc_img, null)
}

//正方形图片加载
fun ImageView.loadImgSquare(url: String?, hasHolder: Boolean = true) {
  this.scaleType = ImageView.ScaleType.CENTER_CROP
  if (url.isNullOrBlank()) {
    this.clearLoad()
    if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder())
  } else {
    if (getTag(R.id.suc_img) == url) return
    val iv = this
    val build = fun ImageRequest.Builder.() {
      if (hasHolder) {
        crossfade(duration)
        placeholder(PlaceHolderUtils.getLoadingHolder())
        error(PlaceHolderUtils.getErrorHolder())
      } else {
        crossfade(false)
      }
      listener(onError = { r, e -> "方形图片加载失败:${r.data},e=${e.message ?: "null"}".logE() }) { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
    val f = url.toFile()
    if (f != null) iv.load(f, builder = build) else iv.load(url, builder = build)
  }
}

//横向图片加载
fun ImageView.loadImgHorizontal(url: String?, holderRatio: Float = 720f / 400, hasHolder: Boolean = true) {
  this.loadImgHorizontalBlur(url, holderRatio, hasHolder)
}

//横向高斯模糊+黑白图片加载
fun ImageView.loadImgHorizontalBlur(
  url: String?, holderRatio: Float = 720f / 400, hasHolder: Boolean = true,
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f, blackWhite: Boolean = false
) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
  } else {
    if (getTag(R.id.suc_img) == url) {
      return
    }
    val iv = this
    if (blackWhite) {//由于GrayscaleTransformation会导致加载后图片无法填充满整个ImageView，所以采用ColorMatrix实现黑白效果
      val cm = ColorMatrix()
      cm.setSaturation(0f) // 设置饱和度
      val grayColorFilter = ColorMatrixColorFilter(cm)
      iv.colorFilter = grayColorFilter // 如果想恢复彩色显示，设置为null即可
    } else {
      iv.colorFilter = null
    }
    val build = fun ImageRequest.Builder.() {
      if (hasHolder) {
        crossfade(duration)
        placeholder(PlaceHolderUtils.getLoadingHolder(holderRatio))
        error(PlaceHolderUtils.getErrorHolder(holderRatio))
      } else {
        crossfade(false)
      }
      if (blurRadius > 0) {//|| blackWhite) {
        val list = mutableListOf<Transformation>()
        if (blurRadius > 0) list.add(BlurTransformation(context, blurRadius))
        //if (blackWhite) list.add(GrayscaleTransformation())
        transformations(list)
      }
      listener(onError = { r, e -> "横向图片加载失败:${r.data},e=${e.message ?: "null"}".logE() }) { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
    val f = url.toFile()
    if (f != null) iv.load(f, builder = build) else iv.load(url, builder = build)
  }
}

//加载高斯模糊资源
fun ImageView.loadImgBlurRes(
  @DrawableRes resId: Int, holderRatio: Float = 720f / 400, hasHolder: Boolean = true,
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f, blackWhite: Boolean = false
) {
  val iv = this
  if (blackWhite) {//由于GrayscaleTransformation会导致加载后图片无法填充满整个ImageView，所以采用ColorMatrix实现黑白效果
    val cm = ColorMatrix()
    cm.setSaturation(0f) // 设置饱和度
    val grayColorFilter = ColorMatrixColorFilter(cm)
    iv.colorFilter = grayColorFilter // 如果想恢复彩色显示，设置为null即可
  } else {
    iv.colorFilter = null
  }
  val build = fun ImageRequest.Builder.() {
    if (hasHolder) {
      crossfade(duration)
      placeholder(PlaceHolderUtils.getLoadingHolder(holderRatio))
      error(PlaceHolderUtils.getErrorHolder(holderRatio))
    } else {
      crossfade(false)
    }
    if (blurRadius > 0) {// || blackWhite) {
      val list = mutableListOf<Transformation>()
      if (blurRadius > 0) list.add(BlurTransformation(context, blurRadius))
      //if (blackWhite) list.add(GrayscaleTransformation())
      transformations(list)
    }
  }
  this.load(resId, builder = build)
}

//竖向图片加载
fun ImageView.loadImgVertical(url: String?, holderRatio: Float = 720f / 1280, hasHolder: Boolean = true) {
  this.loadImgVerticalBlur(url, holderRatio, hasHolder)
}

//竖向高斯模糊+黑白图片加载
fun ImageView.loadImgVerticalBlur(
  url: String?, holderRatio: Float = 720f / 1280, hasHolder: Boolean = true,
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f, blackWhite: Boolean = false
) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
  } else {
    if (getTag(R.id.suc_img) == url) {
      return
    }
    val iv = this
    if (blackWhite) {//由于GrayscaleTransformation会导致加载后图片无法填充满整个ImageView，所以采用ColorMatrix实现黑白效果
      val cm = ColorMatrix()
      cm.setSaturation(0f) // 设置饱和度
      val grayColorFilter = ColorMatrixColorFilter(cm)
      iv.colorFilter = grayColorFilter // 如果想恢复彩色显示，设置为null即可
    } else {
      iv.colorFilter = null
    }
    val build = fun ImageRequest.Builder.() {
      if (hasHolder) {
        crossfade(duration)
        placeholder(PlaceHolderUtils.getLoadingHolder(holderRatio))
        error(PlaceHolderUtils.getErrorHolder(holderRatio))
      } else {
        crossfade(false)
      }
      if (blurRadius > 0) {//|| blackWhite) {
        val list = mutableListOf<Transformation>()
        if (blurRadius > 0) list.add(BlurTransformation(context, blurRadius))
        //if (blackWhite) list.add(GrayscaleTransformation())
        transformations(list)
      }
      listener(onError = { r, e -> "竖向图片加载失败:${r.data},e=${e.message ?: "null"}".logE() }) { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
    val f = url.toFile()
    if (f != null) iv.load(f, builder = build) else iv.load(url, builder = build)
  }
}

//获取缓存文件
fun ImageView.getCacheFile(url: String?): File? {
  url?.let { u ->
    var f = u.toFile()
    if (f != null) {
      return f
    } else {
      url.toHttpUrlOrNull()?.let { h ->
        f = CoilUtils.createDefaultCache(Utils.getApp()).directory.listFiles()?.firstOrNull { v -> v.name.contains(Cache.key(h)) }
        if (f?.exists() == true) {
          return f
        }
      }
    }
  }
  return null
}

//加载缓存文件
fun ImageView.loadCacheFileFullScreen(url: String?, holderRatio: Float = 720f / 1280) {
  if (url.isNullOrBlank()) {
    this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
  } else {
    url.toHttpUrlOrNull()?.let { u ->
      val f = CoilUtils.createDefaultCache(Utils.getApp()).directory.listFiles()?.firstOrNull { f -> f.name.contains(Cache.key(u)) }
      if (f?.exists() == true) { //文件存在直接加载
        this.load(f, context.imageLoader)
      } else { //文件不存在，进行下载
        Utils.getApp().imageLoader.enqueue(
          ImageRequest.Builder(Utils.getApp()).data(u).target(
            onStart = {
              "缓存图片开始下载".logE()
            },
            onSuccess = {
              "缓存图片下载成功".logE()
            },
            onError = {
              "缓存图片下载失败:${u}".logE()
            }
          ).build()
        )
      }
    }
  }
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
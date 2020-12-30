package cc.ab.base.ext

import android.media.MediaMetadataRetriever
import android.widget.ImageView
import cc.ab.base.R
import cc.ab.base.config.PathConfig
import cc.ab.base.utils.MediaMetadataRetrieverUtils
import coil.*
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.Utils
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.File

/**
 * Author:case
 * Date:2020/8/12
 * Time:18:28
 */
private const val duration = 300

//清除上次的加载状态，保证重新加载
fun ImageView.clearLoad() {
  this.clear()
  setTag(R.id.suc_img, null)
}

//正方形图片加载s
fun ImageView.loadImgSquare(url: String?) {
  this.scaleType = ImageView.ScaleType.CENTER_CROP
  if (url.isNullOrBlank()) {
    this.clearLoad()
    this.load(PlaceHolderUtils.getErrorHolder())
  } else {
    if (getTag(R.id.suc_img) == url) return
    val iv = this
    val build = fun ImageRequest.Builder.() {
      crossfade(duration)
      placeholder(PlaceHolderUtils.getLoadingHolder())
      error(PlaceHolderUtils.getErrorHolder())
      listener(onError = { r, e -> "方形图片加载失败:${r.data},e=${e.message ?: "null"}".logE() }) { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
    val f = url.toFile()
    if (f != null) iv.load(f, builder = build) else iv.load(url, builder = build)
  }
}

//横向图片加载
fun ImageView.loadImgHorizontal(url: String?, holderRatio: Float = 720f / 400) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
  } else {
    if (getTag(R.id.suc_img) == url) {
      return
    }
    val iv = this
    val build = fun ImageRequest.Builder.() {
      crossfade(duration)
      placeholder(PlaceHolderUtils.getLoadingHolder(holderRatio))
      error(PlaceHolderUtils.getErrorHolder(holderRatio))
      listener(onError = { r, e -> "横向图片加载失败:${r.data},e=${e.message ?: "null"}".logE() }) { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
    val f = url.toFile()
    if (f != null) iv.load(f, builder = build) else iv.load(url, builder = build)
  }
}

//竖向图片加载
fun ImageView.loadImgVertical(url: String?, holderRatio: Float = 720f / 1280) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
  } else {
    if (getTag(R.id.suc_img) == url) {
      return
    }
    val iv = this
    val build = fun ImageRequest.Builder.() {
      crossfade(duration)
      placeholder(PlaceHolderUtils.getLoadingHolder(holderRatio))
      error(PlaceHolderUtils.getErrorHolder(holderRatio))
      listener(onError = { r, e -> "竖向图片加载失败:${r.data},e=${e.message ?: "null"}".logE() }) { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
    val f = url.toFile()
    if (f != null) iv.load(f, builder = build) else iv.load(url, builder = build)
  }
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
fun ImageView.loadNetVideoCover(url: String?, holderRatio: Float = 16f / 9) {
  (getTag(R.id.id_retriever) as? MediaMetadataRetriever)?.release() //防止之前的图还没完成
  if (url.isNullOrBlank()) { //有封面复用为无封面
    this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
  } else {
    val cacheFile = File(PathConfig.VIDEO_OVER_CACHE_DIR, EncryptUtils.encryptMD5ToString(url))
    if (cacheFile.exists()) load(cacheFile) else {
      this.load(PlaceHolderUtils.getLoadingHolder(holderRatio))
      val retriever = MediaMetadataRetriever()
      setTag(R.id.id_retriever, retriever)
      MediaMetadataRetrieverUtils.getNetVideoCover(retriever, cacheFile, url) { bit ->
        setTag(R.id.id_retriever, null)
        if (bit != null) this.load(bit) else {
          this.load(PlaceHolderUtils.getErrorHolder(holderRatio))
        }
      }
    }
  }
}
package cc.ab.base.ext

import android.content.Intent
import android.net.Uri
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.blankj.utilcode.util.*
import timber.log.Timber
import java.io.File
import java.math.BigDecimal
import java.util.regex.Pattern

/**
 * Author:Khaos
 * Date:2020-9-28
 * Time:19:01
 */
inline fun String?.logE() {
  if (!this.isNullOrBlank()) {
    Timber.e("Khaos-$this")
  }
}

inline fun String?.logW() {
  if (!this.isNullOrBlank()) {
    Timber.w("Khaos-$this")
  }
}

inline fun String?.logI() {
  if (!this.isNullOrBlank()) {
    Timber.i("Khaos-$this")
  }
}

inline fun String?.logD() {
  if (!this.isNullOrBlank()) {
    Timber.d("Khaos-$this")
  }
}

inline fun CharSequence?.toast() {
  if (!this.isNullOrBlank() && AppUtils.isAppForeground() && this.toString().lowercase() != "null") {
    ToastUtils.cancel()
    ToastUtils.showShort(this)
  }
}

inline fun CharSequence?.toastLong() {
  if (!this.isNullOrBlank() && AppUtils.isAppForeground() && this.toString().lowercase() != "null") {
    ToastUtils.cancel()
    ToastUtils.showLong(this)
  }
}

fun String?.isNetImageUrl(): Boolean {
  return if (this.isNullOrBlank()) {
    false
  } else if (!this.startsWith("http", true)) {
    false
  } else {
    Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)").matcher(this.lowercase()).matches()
  }
}

fun String?.isVideoUrl(): Boolean {
  return if (this.isNullOrBlank()) {
    false
  } else if (!this.lowercase().startsWith("http", true)) {
    false
  } else {
    Pattern.compile(".*?(avi|rmvb|rm|asf|divx|mpg|mpeg|mpe|wmv|mp4|mkv|vob)")
      .matcher(this.lowercase()).matches()
  }
}

fun String?.isLiveUrl(): Boolean {
  return if (this.isNullOrBlank()) {
    false
  } else {
    this.lowercase().run {
      startsWith("rtmp") || startsWith("rtsp")
    }
  }
}

//文件目录转file
fun String?.toFile(): File? {
  if (this != null) {
    return if (this.startsWith("http", true)) null else {
      val f = File(this)
      if (f.exists()) f else UriUtils.uri2File(Uri.parse(this))
    }
  }
  return null
}

//Coil获取缓存图片文件 directory是实验方法，可能会在后续版本中移除
@ExperimentalCoilApi
fun String?.getCoilCacheFile(): File? {
  if (this.isNullOrBlank()) return null
  val request = ImageRequest.Builder(Utils.getApp())
    .data(this)
    .memoryCachePolicy(CachePolicy.DISABLED)
    .diskCachePolicy(CachePolicy.ENABLED)
    .build()
  val cacheFile = Utils.getApp().imageLoader.diskCache?.directory?.toFile()?.listFiles()?.find { file ->
    file.name.endsWith(".1") && file.name.startsWith(request.diskCacheKey ?: "")
  }
  return if (cacheFile != null && cacheFile.exists()) cacheFile else null
}

//读取Host
fun String?.getHost(): String {
  return if (this.isNullOrBlank()) "" else Uri.parse(this).host ?: this
}

//打开外部链接
fun String?.openOutLink() {
  if (!this.isNullOrBlank()) {
    try {
      val newUrl = if (this.startsWith("http", true)) this else "http://$this"
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
      ActivityUtils.getTopActivity()?.startActivity(intent)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}

//16进制的字符串转换为String
fun String?.hexToString(): String {
  return if (this.isNullOrBlank()) {
    ""
  } else {
    String(this.chunked(2).map { it.toInt(16).toByte() }.toByteArray())
  }
}

//正则获取两个符号之间的内容，特殊符号需要加双斜杠\\
fun String?.findBySymbols(start: String = "\\(", end: String = "\\)"): String {
  if (this.isNullOrBlank()) return ""
  return Regex("${start}(.*?)${end}").find(this)?.groupValues?.lastOrNull() ?: ""
}

//多空格替换 "AA   BB  CC"->"AA-BB-CC"
fun String?.replaceSpace(replacement: String = "-"): String {
  return this?.trim()?.replace("\\s+".toRegex(), replacement) ?: ""
}

fun String?.checkImgUrl(): String? {
  return when {
    this.isNullOrBlank() -> null
    this.startsWith("http", true) -> this
    this.startsWith("/storage/emulated/0") -> "file://$this"
    this.startsWith("/") -> "http:$this"
    else -> this
  }
}

//<editor-fold defaultstate="collapsed" desc="处理精度丢失问题">
fun String?.toFloatMy(): Float {
  return if (this.isNullOrBlank()) {
    0f
  } else {
    return try {
      if (this.contains(",") && this.contains(".")) {
        BigDecimal(this.replace(",", "")).toFloat()
      } else {
        BigDecimal(this).toFloat()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      return 0f
    }
  }
}

fun String?.toDoubleMy(): Double {
  return if (this.isNullOrBlank()) {
    0.0
  } else {
    return try {
      if (this.contains(",") && this.contains(".")) {
        BigDecimal(this.replace(",", "")).toDouble()
      } else {
        BigDecimal(this).toDouble()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      return 0.0
    }
  }
}
//</editor-fold>
package cc.ab.base.ext

import android.content.Intent
import android.net.Uri
import coil.util.CoilUtils
import com.blankj.utilcode.util.*
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
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

//Coil获取缓存图片文件
fun String?.getCoilCacheFile(): File? {
  return this?.toFile() ?: this?.toHttpUrlOrNull()?.let { u ->
    CoilUtils.createDefaultCache(Utils.getApp()).directory.listFiles()?.lastOrNull { it.name.endsWith(".1") && it.name.contains(Cache.key(u)) }
  }
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

//正则获取两个符号之间的内容，只查找第一个
@Suppress("ConvertToStringTemplate")
fun String?.findBySymbols(start: String = "(", end: String = ")"): String {
  if (this.isNullOrBlank()) return ""
  val matcher = Pattern.compile("(?<=\\" + start + ")(\\S+)(?=\\" + end + ")").matcher(this)
  return if (matcher.find()) matcher.group() else ""
}

//多空格替换 "AA   BB  CC"->"AA-BB-CC"
fun String?.replaceSpace(replacement: String = "-"): String {
  return this?.trim()?.replace("\\s+".toRegex(), replacement) ?: ""
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
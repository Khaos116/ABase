package cc.ab.base.ext

import android.content.Intent
import android.net.Uri
import android.view.Gravity
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.blankj.utilcode.util.*
import kotlinx.coroutines.GlobalScope
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import timber.log.Timber
import java.io.File
import java.util.Locale
import java.util.regex.Pattern

/**
 * Author:CASE
 * Date:2020-9-28
 * Time:19:01
 */
inline fun String?.logE() {
  if (!this.isNullOrBlank()) {
    Timber.e("CASE-$this")
  }
}

inline fun String?.logW() {
  if (!this.isNullOrBlank()) {
    Timber.w("CASE-$this")
  }
}

inline fun String?.logI() {
  if (!this.isNullOrBlank()) {
    Timber.i("CASE-$this")
  }
}

inline fun String?.logD() {
  if (!this.isNullOrBlank()) {
    Timber.d("CASE-$this")
  }
}

inline fun CharSequence?.toast() {
  if (!this.isNullOrBlank() && AppUtils.isAppForeground()) {
    ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).show(this)
  }
}

inline fun CharSequence?.toastLong() {
  if (!this.isNullOrBlank() && AppUtils.isAppForeground()) {
    ToastUtils.make().setDurationIsLong(true).setGravity(Gravity.CENTER, 0, 0).show(this)
  }
}

fun String?.isNetImageUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else if (!this.startsWith("http", true)) {
    false
  } else {
    Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)").matcher(this.toLowerCase(Locale.getDefault())).matches()
  }
}

fun String?.isVideoUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else if (!this.toLowerCase(Locale.getDefault()).startsWith("http", true)) {
    false
  } else {
    Pattern.compile(".*?(avi|rmvb|rm|asf|divx|mpg|mpeg|mpe|wmv|mp4|mkv|vob)")
        .matcher(this.toLowerCase(Locale.getDefault())).matches()
  }
}

fun String?.isLiveUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else {
    this.toLowerCase(Locale.getDefault()).run {
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

//将图片保存到相册
@Suppress("DEPRECATION")
fun String?.downloadImg(call: (start: Boolean, end: Boolean, sucPath: String) -> Unit) {
  val url = this
  if (url.isNullOrBlank()) {
    call.invoke(false, true, "")
    return
  }
  GlobalScope.launchError(handler = { _, e ->
    e.logE()
    call.invoke(false, true, "")
  }) {
    call.invoke(true, false, "")
    val cacheFile = url.getCacheFile()
    if (cacheFile == null && url.startsWith("http")) {
      val request = ImageRequest.Builder(Utils.getApp())
          .data(url)
          .memoryCachePolicy(CachePolicy.DISABLED)
          .listener(
              onCancel = { call.invoke(false, true, "") },
              onError = { _, t ->
                t.logE()
                call.invoke(false, true, "")
              },
              onSuccess = { _, _ ->
                url.getCacheFile()?.let { f ->
                  val dcim = PathUtils.getExternalDcimPath()
                  val picture = PathUtils.getExternalPicturesPath()
                  val dest = if (dcim.isNullOrBlank()) picture else dcim
                  if (dest.isNullOrBlank()) {
                    call.invoke(false, true, "")
                  } else {
                    val destPath = "$dest${File.separator}${EncryptUtils.encryptMD5File2String(f)}.jpg"
                    val result = if (File(destPath).exists()) true else FileUtils.copy(f.path, destPath)
                    //发送广播刷新图片
                    if (result) {
                      Utils.getApp().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, UriUtils.file2Uri(f)))
                      call.invoke(false, true, destPath)
                    } else call.invoke(false, true, "")
                  }
                } ?: call.invoke(false, true, "")
              }
          )
          .build()
      Utils.getApp().imageLoader.enqueue(request)
    } else {
      (cacheFile ?: url.toFile())?.let { f ->
        val dcim = PathUtils.getExternalDcimPath()
        val picture = PathUtils.getExternalPicturesPath()
        val dest = if (dcim.isNullOrBlank()) picture else dcim
        if (dest.isNullOrBlank()) {
          call.invoke(false, true, "")
        } else {
          val destPath = "$dest${File.separator}${EncryptUtils.encryptMD5File2String(f)}.jpg"
          val result = if (File(destPath).exists()) true else FileUtils.copy(f.path, destPath)
          //发送广播刷新图片
          if (result) {
            Utils.getApp().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, UriUtils.file2Uri(f)))
            call.invoke(false, true, destPath)
          } else call.invoke(false, true, "")
        }
      } ?: call.invoke(false, true, "")
    }
  }
}

//获取缓存文件
fun String?.getCacheFile(): File? {
  val url = this
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
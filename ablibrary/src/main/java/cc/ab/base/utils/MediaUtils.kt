package cc.ab.base.utils

import android.net.Uri
import com.blankj.utilcode.util.UriUtils
import java.io.File
import java.net.URLConnection

/**
 * Description: https://www.oschina.net/question/571282_223549
 * @author: Khaos
 * @date: 2019/11/2 18:09
 */
object MediaUtils {

  //获取文件类型
  private fun getMimeType(fileName: String?): String {
    if (fileName.isNullOrBlank()) return ""
    return URLConnection.getFileNameMap()?.getContentTypeFor(fileName) ?: ""
  }

  //判断文件是否是图片
  fun isImageFile(filePath: String?): Boolean {
    if (filePath.isNullOrBlank()) return false
    return if (File(filePath).exists()) {
      getMimeType(filePath).contains("image/")
    } else {
      val f = UriUtils.uri2File(Uri.parse(filePath))
      if (f?.exists() == true) getMimeType(f.path).contains("image/") else false
    }
  }

  //判断文件是否是视频
  fun isVideoFile(filePath: String?): Boolean {
    if (filePath.isNullOrBlank()) return false
    return if (File(filePath).exists()) {
      getMimeType(filePath).contains("video/")
    } else {
      val f = UriUtils.uri2File(Uri.parse(filePath))
      if (f?.exists() == true) getMimeType(f.path).contains("video/") else false
    }
  }

  //是视频或者图片
  fun isImgOrVideo(filePath: String?): Boolean {
    if (filePath.isNullOrBlank()) return false
    if (filePath.isNullOrBlank()) return false
    return isImageFile(filePath) || isVideoFile(filePath)
  }
}
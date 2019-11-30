package cc.ab.base.utils

import java.io.File
import java.net.FileNameMap
import java.net.URLConnection

/**
 * Description: https://www.oschina.net/question/571282_223549
 * @author: caiyoufei
 * @date: 2019/11/2 18:09
 */
class MediaUtils private constructor() {
  private object SingletonHolder {
    val holder = MediaUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //获取文件类型
  private fun getMimeType(fileName: String): String {
    val fileNameMap: FileNameMap = URLConnection.getFileNameMap()
    return fileNameMap.getContentTypeFor(fileName)
  }

  //判断文件是否是图片
  fun isImageFile(filePath: String): Boolean {
    if (!File(filePath).exists()) return false
    return getMimeType(filePath).contains("image/")
  }

  //判断文件是否是视频
  fun isVideoFile(filePath: String): Boolean {
    if (!File(filePath).exists()) return false
    return getMimeType(filePath).contains("video/")
  }
}
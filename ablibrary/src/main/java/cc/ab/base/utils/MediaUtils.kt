package cc.ab.base.utils

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.File

/**
 * Description:
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

  //判断文件是否是图片
  fun isImageFile(filePath: String): Boolean {
    if (!File(filePath).exists()) return false
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, options)
    return options.outWidth != -1
  }

  //判断文件是否是视频
  fun isVideoFile(filePath: String): Boolean {
    if (!File(filePath).exists()) return false
    return try {
      val mmr = MediaMetadataRetriever()
      mmr.setDataSource(filePath)
      val mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
      mmr.release()
      mimeType?.contains("video", true) == true
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }
}
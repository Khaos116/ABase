package cc.ab.base.utils

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Author:Khaos116
 * Date:2023/8/31
 * Time:9:56
 */
object GzipUtils {
  //GZIP加密
  fun gzip(str: String): String {
    return Base64.encodeToString(ByteArrayOutputStream().also { bos -> GZIPOutputStream(bos).use { it.write(str.toByteArray()) } }.toByteArray(), Base64.NO_WRAP)
  }

  //解密
  fun unGzip(str: String): String {
    return try {
      String(GZIPInputStream(Base64.decode(str, Base64.NO_WRAP).inputStream()).use { it.readBytes() }, Charsets.UTF_8)
    } catch (e: Exception) {
      e.printStackTrace()
      str
    }
  }
}

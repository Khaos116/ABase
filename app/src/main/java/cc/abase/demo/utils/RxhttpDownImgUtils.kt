package cc.abase.demo.utils

import android.annotation.SuppressLint
import android.content.Intent
import cc.ab.base.ext.*
import cc.abase.demo.rxhttp.config.RxHttpConfig
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.TimeUtils
import kotlinx.coroutines.GlobalScope
import okhttp3.internal.and
import rxhttp.RxHttp
import java.io.*
import java.util.Locale

/**
 * https://blog.csdn.net/weixin_43870026/article/details/87353345
 * @Description Rxhttp下载图片到DCIM/Pictures相册
 * @Author：CASE
 * @Date：2021/2/6
 * @Time：13:52
 */
object RxhttpDownImgUtils {
  //常用文件格式
  private val FILE_TYPE_MAP = mutableMapOf(
      Pair("ffd8ffe000104a464946", "jpg"),
      Pair("89504e470d0a1a0a0000", "png"),
      Pair("47494638396126026f01", "gif"),
      Pair("49492a00227105008037", "tif"),
      Pair("424d228c010000000000", "bmp"),
      Pair("424d8240090000000000", "bmp"),
      Pair("424d8e1b030000000000", "bmp"),
      Pair("41433130313500000000", "dwg"),
      Pair("3c21444f435459504520", "html"),
      Pair("3c21646f637479706520", "htm"),
      Pair("48544d4c207b0d0a0942", "css"),
      Pair("696b2e71623d696b2e71", "js"),
      Pair("7b5c727466315c616e73", "rtf"),
      Pair("38425053000100000000", "psd"),
      Pair("46726f6d3a203d3f6762", "eml"),
      Pair("d0cf11e0a1b11ae10000", "doc"),
      Pair("d0cf11e0a1b11ae10000", "vsd"),
      Pair("5374616E64617264204A", "mdb"),
      Pair("252150532D41646F6265", "ps"),
      Pair("255044462d312e350d0a", "pdf"),
      Pair("2e524d46000000120001", "rmvb"),
      Pair("464c5601050000000900", "flv"),
      Pair("00000020667479706d70", "mp4"),
      Pair("49443303000000002176", "mp3"),
      Pair("000001ba210001000180", "mpg"),
      Pair("3026b2758e66cf11a6d9", "wmv"),
      Pair("52494646e27807005741", "wav"),
      Pair("52494646d07d60074156", "avi"),
      Pair("4d546864000000060001", "mid"),
      Pair("504b0304140000000800", "zip"),
      Pair("526172211a0700cf9073", "rar"),
      Pair("235468697320636f6e66", "ini"),
      Pair("504b03040a0000000000", "jar"),
      Pair("4d5a9000030000000400", "exe"),
      Pair("3c25402070616765206c", "jsp"),
      Pair("4d616e69666573742d56", "mf"),
      Pair("3c3f786d6c2076657273", "xml"),
      Pair("494e5345525420494e54", "sql"),
      Pair("7061636b616765207765", "java"),
      Pair("406563686f206f66660d", "bat"),
      Pair("1f8b0800000000000000", "gz"),
      Pair("6c6f67346a2e726f6f74", "properties"),
      Pair("cafebabe0000002e0041", "class"),
      Pair("49545346030000006000", "chm"),
      Pair("04000000010000001300", "mxp"),
      Pair("504b0304140006000800", "docx"),
      Pair("d0cf11e0a1b11ae10000", "wps"),
      Pair("6431303a637265617465", "torrent"),

      Pair("6D6F6F76", "mov"),
      Pair("FF575043", "wpd"),
      Pair("CFAD12FEC5FD746F", "dbx"),
      Pair("2142444E", "pst"),
      Pair("AC9EBD8F", "qdf"),
      Pair("E3828596", "pwl"),
      Pair("2E7261FD", "ram"),
      Pair("null", ""),
  )

  //得到文件的文件头
  private fun bytesToHexString(src: ByteArray?): String {
    val stringBuilder = StringBuilder()
    if (src == null || src.isEmpty()) return stringBuilder.toString()
    for (b in src) {
      val v = b and 0xFF
      val hv = Integer.toHexString(v)
      if (hv.length < 2) stringBuilder.append(0)
      stringBuilder.append(hv)
    }
    return stringBuilder.toString()
  }

  //根据制定文件的文件头判断其文件类型
  private fun getFileType(filePath: String): String {
    var res: String? = null
    var input: FileInputStream? = null
    try {
      input = FileInputStream(filePath)
      val b = ByteArray(10)
      input.read(b, 0, b.size)
      val fileCode: String = bytesToHexString(b).toLowerCase(Locale.getDefault())
      if (fileCode.length > 5) for (t in FILE_TYPE_MAP.keys) {
        val keyLower = t.toLowerCase(Locale.getDefault())
        // 验证前5个字符比较
        if (keyLower.startsWith(fileCode.substring(0, 5)) || fileCode.substring(0, 5).startsWith(keyLower)) {
          res = FILE_TYPE_MAP[keyLower]
          break
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      if (null != input) {
        try {
          input.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
    }
    return res ?: ""
  }

  //将图片保存到相册
  @SuppressLint("CheckResult")
  @Suppress("DEPRECATION")
  fun downloadImgByRxhttp(url: String?, call: (start: Boolean, end: Boolean, sucPath: String) -> Unit) {
    if (url.isNullOrBlank()) { //下载地址不存在
      call.invoke(false, true, "")
      return
    }
    val dcim = PathUtils.getExternalDcimPath()
    val picture = PathUtils.getExternalPicturesPath()
    val destDir = if (dcim.isNullOrBlank()) picture else dcim
    if (destDir.isNullOrBlank()) { //没有获取到DCIM和Pictures文件夹
      call.invoke(false, true, "")
      return
    }
    GlobalScope.launchError(handler = { _, e ->
      e.logE()
      call.invoke(false, true, "")
    }) {
      call.invoke(true, false, "")
      val fileName = TimeUtils.millis2String(System.currentTimeMillis(), "yyyyMMdd_HHmmss")
      if (url.startsWith("http")) { //是http开头
        RxHttp.get(url)
            .setOkClient(RxHttpConfig.getOkHttpClient().build()) //不要加log打印，否则文件太大要OOM
            .asDownload(File(destDir, fileName).path)  //指定回调(进度/成功/失败)线程,不指定,默认在请求所在线程回调
            .subscribe({ destPath ->
              destPath.logE()
              val suffix = getFileType(destPath) //文件类型
              //下载成功，处理相关逻辑
              if (suffix.isNotBlank()) {
                val destF = File(destDir, "${fileName}.${suffix}")
                if (!destF.exists()) {
                  FileUtils.rename(File(destPath), destF.name)
                  Utils.getApp().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, UriUtils.file2Uri(destF)))
                } else {
                  FileUtils.delete(destPath)
                }
                call.invoke(false, true, destF.path)
              } else {
                val destF = File(destPath)
                Utils.getApp().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, UriUtils.file2Uri(destF)))
                call.invoke(false, true, destF.path)
              }
            }, { e ->
              e.logE()
              call.invoke(false, true, "")
            })
      } else { //本地文件
        val cacheFile = url.toFile() //获取缓存文件
        if (cacheFile == null) {
          call.invoke(false, true, "")
        } else {
          val suffix = getFileType(cacheFile.path) //文件类型
          val destF = File(destDir, if (suffix.isNotBlank()) "${fileName}.${suffix}" else fileName)
          val result = if (destF.exists()) true else FileUtils.copy(cacheFile.path, destF.path)
          //发送广播刷新图片
          if (result) {
            Utils.getApp().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, UriUtils.file2Uri(destF)))
            call.invoke(false, true, destF.path)
          } else call.invoke(false, true, "")
        }
      }
    }
  }
}
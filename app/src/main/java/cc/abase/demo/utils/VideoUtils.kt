package cc.abase.demo.utils

import cc.ab.base.config.PathConfig
import cc.ab.base.ext.launchError
import cc.ab.base.ext.logE
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.iceteck.silicompressorr.CompressCall
import com.iceteck.silicompressorr.SiliCompressor
import kotlinx.coroutines.*
import java.io.File

/**
 * Description:
 * @author: Khaos
 * @date: 2019/10/28 11:24
 */
object VideoUtils {
  //<editor-fold defaultstate="collapsed" desc="变量区">

  //输出文件目录
  private val outParentVideo = PathConfig.TEMP_VIDEO_DIR

  //产生的封面保存地址
  private val outParentImgs = PathConfig.TEMP_IMG_DIR

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  //创建文件夹
  init {
    if (XXPermissions.isGranted(Utils.getApp(), Permission.MANAGE_EXTERNAL_STORAGE)) {
      if (!File(outParentVideo).exists())
        "创建Video文件夹:${File(outParentVideo).mkdirs()}".logE()
      if (!File(outParentImgs).exists())
        "创建Temp文件夹:${File(outParentImgs).mkdirs()}".logE()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  //开始压缩
  fun startCompressed(
    originFile: File,
    result: ((suc: Boolean, info: String) -> Unit)? = null,
    pro: ((progress: Float) -> Unit)? = null
  ) {
    if (CompressCall.progressCall != null) {
      "Khaos:正在压缩中".logE()
      result?.invoke(false, "压缩失败")
      return
    }
    disposableCompress?.cancel()
    CompressCall.progressCall = { path, progress ->
      if (originFile.path == path) launchError(Dispatchers.Main) { pro?.invoke(progress) }
    }
    disposableCompress = launchError(handler = { _, _ ->
      CompressCall.release()
      result?.invoke(false, "压缩失败")
    }) {
      withContext(Dispatchers.IO) {
        SiliCompressor.with(Utils.getApp()).compressVideo(originFile.path, outParentVideo)
      }.let { resultPath ->
        if (File(resultPath).exists()) {
          CompressCall.release()
          result?.invoke(true, resultPath)
          "视频压缩前大小:${FileUtils.getSize(originFile)}".logE()
          "Khaos:视频压缩后大小:${FileUtils.getSize(resultPath)}".logE()
        } else {
          CompressCall.release()
          result?.invoke(false, "压缩失败")
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放、清理">
  private var disposableCompress: Job? = null

  //释放
  fun release() {
    disposableCompress?.cancel()
    disposableCompress = null
    CompressCall.release()
  }

  //清理封面
  fun clearFirstFrame() {
    FileUtils.deleteAllInDir(outParentImgs)
  }

  //清理压缩的视频文件(保留发布失败的视频)
  fun clearCompressVideos(keepFile: File? = null) {
    if (keepFile == null) {
      FileUtils.deleteAllInDir(outParentVideo)
    } else {
      File(outParentVideo).listFiles()
        ?.let { files ->
          for (file in files) {
            if (file == keepFile) {
              continue
            }
            FileUtils.delete(file)
          }
        }
    }
  }
  //</editor-fold>
}
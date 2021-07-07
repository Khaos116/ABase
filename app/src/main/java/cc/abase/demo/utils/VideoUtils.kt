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

  //<editor-fold defaultstate="collapsed" desc="★暂未使用★RxFFmpeg视频压缩和封面获取">

  //  //封面获取命令
  //  @Throws
  //  private fun getCommandFirstFrame(
  //    originPath: String,
  //    outPath: String
  //  ): Array<String> {
  //    //读取图片尺寸和旋转角度
  //    val mMetadataRetriever = MediaMetadataRetriever()
  //    mMetadataRetriever.setDataSource(originPath)
  //    val videoRotation =
  //      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
  //    val videoHeight =
  //      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
  //    val videoWidth =
  //      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
  //    mMetadataRetriever.release()
  //    val width: Int
  //    val height: Int
  //    if (Integer.parseInt(videoRotation) == 90 || Integer.parseInt(videoRotation) == 270) {
  //      //角度不对需要宽高调换
  //      width = videoHeight.toInt()
  //      height = videoWidth.toInt()
  //    } else {
  //      width = videoWidth.toInt()
  //      height = videoHeight.toInt()
  //    }
  //    //return String.format("ffmpeg -y -i %1\$s -y -f image2 -t 0.001 -s %2\$sx%3\$s %4\$s",originPath, width, height, outPath)
  //    val list = mutableListOf<String>()
  //    list.add("ffmpeg")
  //    list.add("-y")
  //    list.add("-i")
  //    list.add(originPath)
  //    list.add("-y")
  //    list.add("-f")
  //    list.add("image2")
  //    list.add("-t")
  //    list.add("0.001")
  //    list.add("-s")
  //    list.add("${width}x${height}")
  //    list.add(outPath)
  //    return list.toTypedArray()//采用list转换，防止文件名带空格造成分割错误
  //  }
  //
  //  //文件压缩命令
  //  private fun getCommandCompress(
  //    originPath: String,
  //    outPath: String
  //  ): Array<String>? {
  //    //https://blog.csdn.net/qq_31332467/article/details/79166945
  //    //4K视频可能会闪退，所以需要添加尺寸压缩
  //    val mMetadataRetriever = MediaMetadataRetriever()
  //    mMetadataRetriever.setDataSource(originPath)
  //    val videoRotation =
  //      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
  //    val videoHeight =
  //      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
  //    val videoWidth =
  //      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
  //    val bitrate = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
  //    mMetadataRetriever.release()
  //    //码率低于400不进行压缩
  //    if (bitrate.toInt() < 400 * 1000) return null
  //    val newBitrate = if (bitrate.toInt() > 3000 * 1000) {
  //      3000
  //    } else {
  //      (bitrate.toInt() * 0.8f / 1000f).toInt()
  //    }
  //    val width: Int
  //    val height: Int
  //    if (Integer.parseInt(videoRotation) == 90 || Integer.parseInt(videoRotation) == 270) {
  //      //角度不对需要宽高调换
  //      width = videoHeight.toInt()
  //      height = videoWidth.toInt()
  //    } else {
  //      width = videoWidth.toInt()
  //      height = videoHeight.toInt()
  //    }
  //    //需要根据视频大小和视频时长计算得到需要压缩的码率，不然会导致高清视频压缩后变模糊，非高清视频压缩后文件变大
  //    //https://blog.csdn.net/zhezhebie/article/details/79263492
  //    val list = mutableListOf<String>()
  //    list.add("ffmpeg")
  //    list.add("-y")
  //    list.add("-i")
  //    list.add(originPath)
  //    list.add("-b")
  //    list.add("${newBitrate}k")
  //    list.add("-r")
  //    list.add("30")
  //    list.add("-vcodec")
  //    list.add("libx264")
  //    if (min(width, height) > 1080) {//大于1080p
  //      list.add("-vf")
  //      list.add("scale=${if (width > height) "1080:-1" else "-1:1080"}")
  //    }
  //    list.add("-preset")
  //    list.add("superfast")
  //    list.add(outPath)
  //    return list.toTypedArray()//采用list转换，防止文件名带空格造成分割错误
  //  }
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
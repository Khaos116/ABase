package cc.abase.demo.utils

import android.Manifest
import android.media.MediaMetadataRetriever
import android.util.Log
import cc.abase.demo.R
import com.blankj.utilcode.util.*
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import java.io.File
import kotlin.math.min

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/28 11:24
 */
class VideoUtils private constructor() {
  //输出文件目录
  private val outParentVideo = PathUtils.getExternalAppDataPath() + File.separator + "video"
  //产生的封面保存地址
  private val outParentImgs = PathUtils.getExternalAppDataPath() + File.separator + "temp"

  private object SingletonHolder {
    val holder = VideoUtils()
  }

  //创建文件夹
  init {
    if (PermissionUtils.isGranted(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    ) {
      if (!File(outParentVideo).exists())
        Log.e("CASE", "创建Video文件夹:${File(outParentVideo).mkdirs()}")
      if (!File(outParentImgs).exists())
        Log.e("CASE", "创建Temp文件夹:${File(outParentImgs).mkdirs()}")
    }
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //视频压缩进度
  private var compressPro = -1

  //开始压缩
  fun startCompressed(
    originFile: File,
    result: ((suc: Boolean, info: String) -> Unit)? = null,
    pro: ((progress: Int) -> Unit)? = null
  ) {
    val outFile = File(outParentVideo, EncryptUtils.encryptMD5File2String(originFile) + ".mp4")
    if (outFile.exists() && outFile.length() > 1024 * 1024) {//大于1M
      //视频已压缩过，不再压缩，直接返回
      result?.invoke(true, outFile.path)
      return
    }
    Log.e("CASE", "视频压缩前大小:${FileUtils.getFileSize(originFile)}")
    val command = getCommandCompress(originFile.path, outFile.path)
    RxFFmpegInvoke.getInstance()
        .runCommandRxJava(command.split(" ").toTypedArray())
        .subscribe(object : RxFFmpegSubscriber() {
          override fun onFinish() {
            Log.e("CASE", "视频压缩成功后大小:${FileUtils.getFileSize(outFile)}")
            result?.invoke(outFile.length() > 1024 * 1024, outFile.path)
          }

          override fun onCancel() {
            result?.invoke(false, StringUtils.getString(R.string.compress_video_fail))
          }

          override fun onProgress(
            progress: Int,
            progressTime: Long
          ) {
            if (progress >= 0 && progress != compressPro) {
              compressPro = progress
              Log.e("CASE", "视频压缩进度:$progress")
              pro?.invoke(progress)
            }
          }

          override fun onError(message: String?) {
            Log.e("CASE", "视频压缩失败")
            result?.invoke(false, StringUtils.getString(R.string.compress_video_fail))
          }
        })
  }

  //获取视频封面第一帧
  fun getFirstFrame(
    originFile: File,
    call: ((suc: Boolean, info: String) -> Unit)?
  ) {
    val destImg = File(outParentImgs, EncryptUtils.encryptMD5File2String(originFile) + ".jpg")
    val command = getCommandFirstFrame(originFile.path, destImg.path)
    RxFFmpegInvoke.getInstance()
        .runCommandRxJava(command.split(" ").toTypedArray())
        .subscribe(object : RxFFmpegSubscriber() {
          override fun onFinish() {
            if (destImg.exists() && destImg.length() > 0) {
              call?.invoke(true, destImg.path)
            } else {
              call?.invoke(false, StringUtils.getString(R.string.pic_first_frame_fail))
            }
          }

          override fun onCancel() {
            call?.invoke(false, StringUtils.getString(R.string.pic_first_frame_fail))
          }

          override fun onProgress(
            progress: Int,
            progressTime: Long
          ) {
          }

          override fun onError(message: String?) {
            call?.invoke(false, message ?: StringUtils.getString(R.string.pic_first_frame_fail))
          }
        })
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

  //清理封面
  fun clearFirstFrame() {
    FileUtils.deleteAllInDir(outParentImgs)
  }

  //封面获取命令
  @Throws
  private fun getCommandFirstFrame(
    originPath: String,
    outPath: String
  ): String {
    //读取图片尺寸和旋转角度
    val mMetadataRetriever = MediaMetadataRetriever()
    mMetadataRetriever.setDataSource(originPath)
    val videoRotation =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
    val videoHeight =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
    val videoWidth =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
    val width: Int
    val height: Int
    if (Integer.parseInt(videoRotation) == 90 || Integer.parseInt(videoRotation) == 270) {
      //角度不对需要宽高调换
      width = videoHeight.toInt()
      height = videoWidth.toInt()
    } else {
      width = videoWidth.toInt()
      height = videoHeight.toInt()
    }
    return String.format(
        "ffmpeg -y -i %1\$s -y -f image2 -t 0.001 -s %2\$sx%3\$s %4\$s",
        originPath, width, height, outPath
    )
  }

  //文件压缩命令
  private fun getCommandCompress(
    originPath: String,
    outPath: String
  ): String {
    //4K视频可能会闪退，所以需要添加尺寸压缩
    val mMetadataRetriever = MediaMetadataRetriever()
    mMetadataRetriever.setDataSource(originPath)
    val videoRotation =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
    val videoHeight =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
    val videoWidth =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
    val width: Int
    val height: Int
    if (Integer.parseInt(videoRotation) == 90 || Integer.parseInt(videoRotation) == 270) {
      //角度不对需要宽高调换
      width = videoHeight.toInt()
      height = videoWidth.toInt()
    } else {
      width = videoWidth.toInt()
      height = videoHeight.toInt()
    }
    return if (min(width, height) > 1080) {
      //大于1080p
      String.format(
          "ffmpeg -y -i %1\$s -b 3000k -r 30 -vcodec libx264 -vf scale=%2\$s -preset superfast %3\$s",
          originPath, if (width > height) "1080:-1" else "-1:1080", outPath
      )
    } else {
      //小于1080p
      String.format(
          "ffmpeg -y -i %1\$s -b 2097k -r 30 -vcodec libx264 -preset superfast %2\$s",
          originPath, outPath
      )
    }
  }
}
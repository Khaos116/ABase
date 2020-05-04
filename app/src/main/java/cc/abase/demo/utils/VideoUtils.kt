package cc.abase.demo.utils

import android.Manifest
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import cc.ab.base.utils.RxUtils
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
    RxFFmpegInvoke.getInstance().setDebug(true)
    if (PermissionUtils.isGranted(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    ) {
      if (!File(outParentVideo).exists())
        LogUtils.e("CASE:创建Video文件夹:${File(outParentVideo).mkdirs()}")
      if (!File(outParentImgs).exists())
        LogUtils.e("CASE:创建Temp文件夹:${File(outParentImgs).mkdirs()}")
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
    LogUtils.e("CASE:视频压缩前大小:${FileUtils.getSize(originFile)}")
    val command = getCommandCompress(originFile.path, outFile.path)
    //码率太低不进行压缩，直接拷贝原文件并返回
    if (command == null) {
      FileUtils.copy(originFile, outFile,null)
      LogUtils.e("CASE:视频码率太小，不用压缩，直接拷贝上传")
      result?.invoke(true, outFile.path)
      return
    }
    LogUtils.e("CASE:执行的压缩命令:$command")
    RxFFmpegInvoke.getInstance()
        .runCommandRxJava(command)
        .subscribe(object : RxFFmpegSubscriber() {
          override fun onFinish() {
            LogUtils.e("CASE:视频压缩成功后大小:${FileUtils.getSize(outFile)}")
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
              LogUtils.e("CASE:视频压缩进度:$progress")
              pro?.invoke(progress)
            }
          }

          override fun onError(message: String?) {
            LogUtils.e("CASE:视频压缩失败")
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
        .runCommandRxJava(command)
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
  ): Array<String> {
    //读取图片尺寸和旋转角度
    val mMetadataRetriever = MediaMetadataRetriever()
    mMetadataRetriever.setDataSource(originPath)
    val videoRotation =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
    val videoHeight =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
    val videoWidth =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
    mMetadataRetriever.release()
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
    //return String.format("ffmpeg -y -i %1\$s -y -f image2 -t 0.001 -s %2\$sx%3\$s %4\$s",originPath, width, height, outPath)
    val list = mutableListOf<String>()
    list.add("ffmpeg")
    list.add("-y")
    list.add("-i")
    list.add(originPath)
    list.add("-y")
    list.add("-f")
    list.add("image2")
    list.add("-t")
    list.add("0.001")
    list.add("-s")
    list.add("${width}x${height}")
    list.add(outPath)
    return list.toTypedArray()//采用list转换，防止文件名带空格造成分割错误
  }

  //文件压缩命令
  private fun getCommandCompress(
    originPath: String,
    outPath: String
  ): Array<String>? {
    //https://blog.csdn.net/qq_31332467/article/details/79166945
    //4K视频可能会闪退，所以需要添加尺寸压缩
    val mMetadataRetriever = MediaMetadataRetriever()
    mMetadataRetriever.setDataSource(originPath)
    val videoRotation =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
    val videoHeight =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
    val videoWidth =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
    val bitrate = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
    mMetadataRetriever.release()
    //码率低于400不进行压缩
    if (bitrate.toInt() < 400 * 1000) return null
    val newBitrate = if (bitrate.toInt() > 3000 * 1000) {
      3000 * 1000
    } else {
      (bitrate.toInt() * 0.8f / 1000f).toInt()
    }
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
    //需要根据视频大小和视频时长计算得到需要压缩的码率，不然会导致高清视频压缩后变模糊，非高清视频压缩后文件变大
    //https://blog.csdn.net/zhezhebie/article/details/79263492
    val list = mutableListOf<String>()
    list.add("ffmpeg")
    list.add("-y")
    list.add("-i")
    list.add(originPath)
    list.add("-b")
    list.add("${newBitrate}k")
    list.add("-r")
    list.add("30")
    list.add("-vcodec")
    list.add("libx264")
    if (min(width, height) > 1080) {//大于1080p
      list.add("-vf")
      list.add("scale=${if (width > height) "1080:-1" else "-1:1080"}")
    }
    list.add("-preset")
    list.add("superfast")
    list.add(outPath)
    return list.toTypedArray()//采用list转换，防止文件名带空格造成分割错误
  }

  //网络视频封面获取
  fun getNetVideoFistFrame(videoUrl: String, call: (bit: Bitmap?) -> Unit) {
    val ob = io.reactivex.Observable.just(videoUrl)
      .flatMap {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(it, HashMap())
        val bitmap = retriever.frameAtTime
        retriever.release()
        io.reactivex.Observable.just(
          if (bitmap.width > ScreenUtils.getScreenWidth()) {
            val width = ScreenUtils.getScreenWidth()
            val height = ScreenUtils.getScreenWidth() * 1f / bitmap.width * bitmap.height
            ImageUtils.compressBySampleSize(bitmap, width, height.toInt())
          } else {
            bitmap
          }
        )
      }
      .compose(RxUtils.instance.rx2SchedulerHelperO())
      .subscribe({ bit ->
        call.invoke(bit)
      }, { call.invoke(null) })
  }
}
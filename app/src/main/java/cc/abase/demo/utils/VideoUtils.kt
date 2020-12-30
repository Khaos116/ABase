package cc.abase.demo.utils

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import cc.ab.base.ext.logE
import cc.ab.base.utils.PermissionUtils
import cc.ab.base.utils.RxUtils
import cc.abase.demo.R
import com.blankj.utilcode.util.*
import com.iceteck.silicompressorr.CompressCall
import com.iceteck.silicompressorr.SiliCompressor
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/28 11:24
 */
class VideoUtils private constructor() {
  //<editor-fold defaultstate="collapsed" desc="变量区">

  //输出文件目录
  private val outParentVideo = PathUtils.getExternalAppDataPath() + File.separator + "video"

  //产生的封面保存地址
  private val outParentImgs = PathUtils.getExternalAppDataPath() + File.separator + "temp"

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">

  private object SingletonHolder {
    val holder = VideoUtils()
  }

  //创建文件夹
  init {
    if (PermissionUtils.hasSDPermission()) {
      if (!File(outParentVideo).exists())
        LogUtils.e("CASE:创建Video文件夹:${File(outParentVideo).mkdirs()}")
      if (!File(outParentImgs).exists())
        LogUtils.e("CASE:创建Temp文件夹:${File(outParentImgs).mkdirs()}")
    }
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  //开始压缩
  fun startCompressed(originFile: File,
      result: ((suc: Boolean, info: String) -> Unit)? = null,
      pro: ((progress: Float) -> Unit)? = null) {
    if (CompressCall.instance.progressCall != null) {
      LogUtils.e("CASE:正在压缩中")
      return
    }
    disposableCompress?.dispose()
    CompressCall.instance.progressCall = { path, progress ->
      if (originFile.path == path) Flowable.just(progress)
          .onBackpressureLatest()
          .compose(RxUtils.instance.rx2SchedulerHelperF())
          .subscribe { pro?.invoke(progress) }
    }
    disposableCompress = Observable.just(originFile.path)
        .flatMap {
          val resultPath = SiliCompressor.with(Utils.getApp()).compressVideo(it, outParentVideo)
          if (File(resultPath).exists()) {
            Observable.just(resultPath)
          } else {
            Observable.error(Throwable("压缩失败"))
          }
        }
        .compose(RxUtils.instance.rx2SchedulerHelperO())
        .subscribe({
          CompressCall.instance.release()
          result?.invoke(true, it)
          LogUtils.e("\nCASE:视频压缩前大小:${FileUtils.getSize(originFile)}")
          LogUtils.e("\nCASE:视频压缩后大小:${FileUtils.getSize(it)}\n")
        }, {
          CompressCall.instance.release()
          result?.invoke(false, "压缩失败")
        })
  }

  //获取视频封面第一帧
  fun getFirstFrame(originFile: File, maxW: Int = 0, maxH: Int = 0, call: ((suc: Boolean, info: String) -> Unit)?) {
    val destImg = File(outParentImgs, EncryptUtils.encryptMD5File2String(originFile) + ".jpg")
    //已存在则直接返回
    if (destImg.exists()) {
      call?.invoke(true, destImg.path)
      "已存在封面，直接返回:${destImg.path}".logE()
      return
    }
    //获取封面
    val mmr = FFmpegMediaMetadataRetriever()
    mmr.setDataSource(originFile.path)
    disposableCover?.dispose()
    disposableCover = Observable.just(mmr)
        .flatMap {
          val rotation = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION).toInt()
          val originWidth = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
          val originHeight = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
          //限制图片的最大宽高
          val maxWidth = if (maxW > 0) maxW else (ScreenUtils.getScreenWidth() * 0.8f).toInt()
          val maxHeight = if (maxH > 0) maxH else (ScreenUtils.getScreenHeight() * 0.8f).toInt()
          var width = originWidth
          var height = originHeight
          if (originWidth > maxWidth) {
            width = maxWidth
            height = (maxWidth * 1f / originWidth * originHeight).toInt()
          } else if (originHeight > maxHeight) {
            width = (maxHeight * 1f / originHeight * originWidth).toInt()
            height = maxHeight
          }
          var bit = it.getScaledFrameAtTime(-1, width, height)
          //纠正旋转角度
          if (rotation != 0) bit = ImageUtils.rotate(bit, rotation, bit.width / 2f, bit.height / 2f)
          //保存到指定位置
          ImageUtils.save(bit, destImg, Bitmap.CompressFormat.JPEG)
          "\n修改前封面尺寸:width=${originWidth},height=${originHeight}".logE()
          "\n修改后封面尺寸:width=${width},height=${height}\n".logE()
          bit.recycle()
          Observable.just(destImg.path)
        }
        .compose(RxUtils.instance.rx2SchedulerHelperO())
        .subscribe({
          mmr.release()
          call?.invoke(true, it)
        }, {
          mmr.release()
          call?.invoke(true, StringUtils.getString(R.string.pic_first_frame_fail))
        })
  }

  //网络视频封面获取
  fun getNetVideoFistFrame(videoUrl: String, call: (bit: Bitmap?) -> Unit) {
    disposableNet?.dispose()
    disposableNet = Observable.just(videoUrl)
        .flatMap {
          val retriever = MediaMetadataRetriever()
          retriever.setDataSource(it, HashMap())
          val bitmap = retriever.frameAtTime
          retriever.release()
          if (bitmap == null) Observable.error(Throwable("get video cover is null"))
          else {
            //限制图片的最大宽高
            val maxWidth = (ScreenUtils.getScreenWidth() * 0.8f).toInt()
            val maxHeight = (ScreenUtils.getScreenHeight() * 0.8f).toInt()
            Observable.just(
                when {
                  bitmap.width > maxWidth -> {
                    val width = maxWidth
                    val height = maxWidth * 1f / bitmap.width * bitmap.height
                    ImageUtils.compressBySampleSize(bitmap, width, height.toInt())
                  }
                  bitmap.height > maxHeight -> {
                    val width = maxHeight * 1f / bitmap.height * bitmap.width
                    val height = maxHeight
                    ImageUtils.compressBySampleSize(bitmap, width.toInt(), height)
                  }
                  else -> {
                    bitmap
                  }
                })
          }
        }
        .compose(RxUtils.instance.rx2SchedulerHelperO())
        .subscribe({ bit ->
          call.invoke(bit)
        }, { call.invoke(null) })
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
  private var disposableCompress: Disposable? = null
  private var disposableCover: Disposable? = null
  private var disposableNet: Disposable? = null

  //释放
  fun release() {
    disposableCompress?.dispose()
    disposableCover?.dispose()
    disposableNet?.dispose()
    CompressCall.instance.release()
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
package cc.ab.base.widget.engine

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import cc.ab.base.config.PathConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CompressEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E5%8E%8B%E7%BC%A9%EF%BC%9F
 * Author:Khaos116
 * Date:2022/3/2
 * Time:19:05
 */
class MyCompressEngine : CompressEngine {
  override fun onStartCompress(context: Context, list: java.util.ArrayList<LocalMedia>?, listener: OnCallbackListener<java.util.ArrayList<LocalMedia>>?) {
    if (list.isNullOrEmpty()) {
      listener?.onCall(arrayListOf())
      return
    }
    // 1、构造可用的压缩数据源
    val compress: MutableList<Uri> = ArrayList()
    for (i in 0 until list.size) {
      val media = list[i]
      val availablePath = media.availablePath
      val uri = if (PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(availablePath)) Uri.parse(availablePath) else Uri.fromFile(File(availablePath))
      compress.add(uri)
    }
    if (compress.size == 0) {
      listener?.onCall(list)
      return
    }
    // 2、调用Luban压缩
    Luban.with(context)
      .load(compress)
      .ignoreBy(100)
      .setTargetDir(PathConfig.TEMP_IMG_DIR)
      .filter { path -> PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path) }
      .setRenameListener { filePath ->
        val indexOf = filePath.lastIndexOf(".")
        val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
        DateUtils.getCreateFileName("CMP_").toString() + postfix
      }
      .setCompressListener(object : OnCompressListener {
        override fun onStart() {}
        override fun onSuccess(index: Int, compressFile: File) {
          // 压缩完构造LocalMedia对象
          val media = list[index]
          if (compressFile.exists() && !TextUtils.isEmpty(compressFile.absolutePath)) {
            media.isCompressed = true
            media.compressPath = compressFile.absolutePath
            media.sandboxPath = if (SdkVersionUtils.isQ()) media.compressPath else null
          }
          // 因为是多图压缩，所以判断压缩到最后一张时返回结果
          if (index == list.size - 1) {
            listener?.onCall(list)
          }
        }

        override fun onError(index: Int, e: Throwable) {
          // 压缩失败
          if (index != -1) {
            val media = list[index]
            media.isCompressed = false
            media.compressPath = null
            media.sandboxPath = null
            if (index == list.size - 1) {
              listener?.onCall(list)
            }
          }
        }
      }).launch()
  }
}
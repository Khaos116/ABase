package cc.ab.base.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import cc.ab.base.widget.sketch.VideoThumbnailUriModel
import me.panpf.sketch.Configuration
import me.panpf.sketch.Sketch

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:40
 */
class SketchInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //Sketch配置视频封面加载
    val configuration: Configuration = Sketch.with(context).configuration
    configuration.uriModelManager.add(VideoThumbnailUriModel())
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(MmkvInit::class.java)
  }
}
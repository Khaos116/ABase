package cc.abase.demo.startup

import android.content.Context
import cc.ab.base.config.PathConfig
import cc.ab.base.ext.logI
import cc.abase.demo.widget.dkplayer.MyExoPlayerFactory
import com.blankj.utilcode.constant.MemoryConstants
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.rousetime.android_startup.AndroidStartup
import com.rousetime.android_startup.Startup
import xyz.doikki.videoplayer.exo.ExoMediaSourceHelper
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager
import java.io.File

/**
 * Author:Khaos
 * Date:2020/12/23
 * Time:13:04
 */
class DKPlayerInit : AndroidStartup<Int>() {
  //<editor-fold defaultstate="collapsed" desc="初始化线程问题">
  //create()方法调时所在的线程：如果callCreateOnMainThread返回true，则表示在主线程中初始化，会导致waitOnMainThread返回值失效；当返回false时，才会判断waitOnMainThread的返回值
  override fun callCreateOnMainThread(): Boolean = false

  //是否需要在主线程进行等待其完成:如果返回true，将在主线程等待，并且阻塞主线程
  override fun waitOnMainThread(): Boolean = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun create(context: Context): Int {
    //全局设置播放器内核
    VideoViewManager.setConfig(
      VideoViewConfig.newBuilder()
        //使用使用IjkPlayer解码
        //.setPlayerFactory(IjkPlayerFactory.create())
        //使用ExoPlayer解码
        //.setPlayerFactory(MyExoPlayerFactory.create())
        //使用MediaPlayer解码
        //.setPlayerFactory(AndroidMediaPlayerFactory.create())
        //自己重写，兼容m3u8缓存的播放器
        .setPlayerFactory(MyExoPlayerFactory())
        .build()
    )
    //设置缓存地址
    ExoMediaSourceHelper.getInstance(context).setCache(
      SimpleCache(
        File(PathConfig.VIDEO_CACHE_DIR), //缓存目录
        LeastRecentlyUsedCacheEvictor(1L * MemoryConstants.GB), //缓存大小，默认1GB，使用LRU算法实现
        ExoDatabaseProvider(context)
      )
    )
    "初始化完成".logI()
    return 0
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="依赖">
  override fun dependencies(): List<Class<out Startup<*>>> {
    return mutableListOf(CoilInit::class.java)
  }
  //</editor-fold>
}
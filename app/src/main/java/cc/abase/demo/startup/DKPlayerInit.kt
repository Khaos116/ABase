package cc.abase.demo.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.config.PathConfig
import cc.ab.base.ext.logI
import cc.abase.demo.widget.dkplayer.MyExoPlayerFactory
import com.blankj.utilcode.constant.MemoryConstants
import com.dueeeke.videoplayer.exo.ExoMediaSourceHelper
import com.dueeeke.videoplayer.player.VideoViewConfig
import com.dueeeke.videoplayer.player.VideoViewManager
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

/**
 * Author:CASE
 * Date:2020/12/23
 * Time:13:04
 */
class DKPlayerInit : Initializer<Int> {
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

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(CoilInit::class.java)
  }
}
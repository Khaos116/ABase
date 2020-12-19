package cc.abase.demo.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:16:36
 */
class GysPlayerInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //EXOPlayer内核，支持格式更多
    PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
    //exo缓存模式，支持m3u8，只支持exo
    CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(RxHttpInit::class.java)
  }
}
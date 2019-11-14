package cc.abase.demo.widget.video

import android.net.Uri
import cc.abase.demo.widget.video.player.ExoVideoCacheManager
import com.blankj.utilcode.util.Utils

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/14 11:20
 */
class ExoVideoCacheUtils private constructor() {
  private object SingletonHolder {
    val holder = ExoVideoCacheUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //APP视频的特有标识
  private val appVideoTag = "happyrundianbovideo"
  //APP视频的类型
  private val appVideoType = ".mp4"
  //视频总长度缓存key
  private val appVideoLenKey = "exo_len"

  //根据播放地址去获取缓存地址,如果返回没有缓存完成则返回原来的地址
  fun getCacheUrl(originUrl: String?): String? {
    //错误的播放地址
    if (originUrl.isNullOrBlank()) return originUrl
    //非网络地址
    if (!originUrl.startsWith("http", true)) {
      return Uri.parse(originUrl)
          .toString()
    }
    //当前播放地址的前半部分，用于缓存视频判断
    val videoPrefix: String
    //网络地址
    if (originUrl.contains(appVideoTag) && originUrl.contains("${appVideoType}?")) {
      //APP内部播放地址
      videoPrefix = originUrl.split("${appVideoType}?")[0] + appVideoType
    } else {
      //非当前APP
      return originUrl
    }
    //判断是否缓存完成
    val cache = ExoVideoCacheManager.getCache(Utils.getApp())
    //拿到所有缓存的key
    cache.keys?.let { keys ->
      //拿到和当前mp4匹配的key
      val result = keys.filter { key -> key.startsWith(videoPrefix) }
      if (!result.isNullOrEmpty()) {
        //遍历key
        result.forEach { key ->
          //获取视频总的需要缓存的长度
          val mMetadata = cache.getContentMetadata(key)
          val len = mMetadata.get(appVideoLenKey, 0)
          //获取到缓存完成的地址
          if (len > 0L && cache.isCached(key, 0, len)) {
            return key
          }
        }
      } else {
        return originUrl
      }
    }
    return originUrl
  }

  //打开APP的时候去清理没有缓存完成的视频信息(★★★子线程中执行★★★)
  fun openAappClearNoCacheComplete() {
    val cache = ExoVideoCacheManager.getCache(Utils.getApp())
    //只处理单独针对APP的
    val result = cache.keys.filter { it.contains(appVideoTag) && it.contains("${appVideoType}?") }
    if (!result.isNullOrEmpty()) {
      for (i in result.size - 1..0 step -1) {
        val key = result[i]
        //获取视频总的需要缓存的长度
        val mMetadata = cache.getContentMetadata(key)
        val len = mMetadata.get(appVideoLenKey, 0)
        //获取到缓存完成的地址
        if (len <= 0L || !cache.isCached(key, 0, len)) {
          val cachedSpans = cache.getCachedSpans(key)
          if (!cachedSpans.isNullOrEmpty()) {
            val list = cachedSpans.toMutableList()
            for (j in list.size - 1..0 step -1) {
              cache.removeSpan(list[j])
            }
          }
        }
      }
    }
  }
}
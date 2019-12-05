package cc.abase.demo.widget.video

import android.net.Uri
import android.util.Log
import com.blankj.utilcode.util.Utils
import com.dueeeke.videoplayer.exo.ExoMediaSourceHelper
import com.google.android.exoplayer2.upstream.cache.Cache

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

  private var mVideoCache: Cache? = null

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
      Log.e("CASE", "无需判断缓存的播放地址:$originUrl")
      return originUrl
    }
    //判断是否缓存完成
    if (mVideoCache == null) mVideoCache = initExoCache()
    mVideoCache?.let { cache ->
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
              Log.e("CASE", "播放转换前的地址:$originUrl")
              Log.e("CASE", "播放转换后的地址:$key")
              return key
            }
          }
        } else {
          Log.e("CASE", "边播边缓存的地址1:$originUrl")
          return originUrl
        }
      }
    }
    Log.e("CASE", "边播边缓存的地址2:$originUrl")
    return originUrl
  }

  //打开APP的时候去清理没有缓存完成的视频信息(★★★子线程中执行★★★)
  fun openAappClearNoCacheComplete() {
    if (mVideoCache == null) mVideoCache = initExoCache()
    mVideoCache?.let { cache ->
      //只处理单独针对APP的
      val result = cache.keys.filter { it.contains(appVideoTag) && it.contains("${appVideoType}?") }
      if (!result.isNullOrEmpty()) {
        for (i in result.size - 1 downTo 0) {
          val key = result[i]
          //获取视频总的需要缓存的长度
          val mMetadata = cache.getContentMetadata(key)
          val len = mMetadata.get(appVideoLenKey, 0)
          //获取到缓存完成的地址
          if (len <= 0L || !cache.isCached(key, 0, len)) {
            val cachedSpans = cache.getCachedSpans(key)
            if (!cachedSpans.isNullOrEmpty()) {
              val list = cachedSpans.toMutableList()
              for (j in list.size - 1 downTo 0) {
                cache.removeSpan(list[j])
              }
            }
          }
        }
      }
    }
  }

  //这里使用了反射的方式，所以ExoMediaSourceHelper不能进行混淆
  private fun initExoCache(): Cache {
    val temp = mVideoCache
    if (temp != null) return temp
    val helper = ExoMediaSourceHelper.getInstance(Utils.getApp())
    val method = helper.javaClass.getDeclaredMethod("getCacheDataSourceFactory")//反射方法
    method.isAccessible = true
    method.invoke(helper)
    val field = helper.javaClass.getDeclaredField("mCache")//反射对象
    field.isAccessible = true
    val result = field.get(helper)
    if (result is Cache) {
      return result
    } else {
      val msg = "ExoMediaSourceHelper has no \"mCache\" member"
      Log.e("CASE", msg)
      throw Throwable(msg)
    }
  }
}
package cc.abase.demo.rxhttp.repository

import androidx.annotation.IntRange
import cc.abase.demo.bean.gank.GankAndroidBean
import cc.abase.demo.constants.TimeConstants
import cc.abase.demo.constants.api.GankUrls
import rxhttp.cc.RxHttp
import rxhttp.cc.toAwaitResponseGank
import rxhttp.wrapper.cache.CacheMode
import rxhttp.wrapper.coroutines.Await

/**
 * Description:
 * @author: Khaos
 * @date: 2020/3/5 18:09
 */
object GankRepository : BaseRepository() {
  //安卓列表
  suspend fun androidList(@IntRange(from = 1) page: Int, size: Int, readCache: Boolean = true): Await<MutableList<GankAndroidBean>> {
    return RxHttp.get(String.format(GankUrls.ANDROID, page, size))
      .setCacheValidTime(TimeConstants.DYN_CACHE) //设置缓存时长
      .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //先读取缓存，失败再请求数据
      .toAwaitResponseGank()
  }
}
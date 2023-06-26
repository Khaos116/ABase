package cc.abase.demo.rxhttp.repository

import androidx.annotation.IntRange
import cc.ab.base.net.http.response.BasePageList
import cc.abase.demo.bean.wan.ArticleBean
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.constants.TimeConstants
import cc.abase.demo.constants.api.WanUrls
import rxhttp.cc.RxHttp
import rxhttp.cc.toAwaitResponseWan
import rxhttp.onErrorReturnItem
import rxhttp.wrapper.cache.CacheMode
import rxhttp.wrapper.coroutines.Await

/**
 * Description:
 * @author: Khaos
 * @date: 2020/3/5 18:10
 */
object WanRepository {

  //获取Banner
  suspend fun banner(readCache: Boolean = true): Await<MutableList<BannerBean>> {
    return RxHttp.get(WanUrls.Home.BANNER)
      .setCacheValidTime(TimeConstants.HOME_CACHE) //设置缓存时长
      .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //先读取缓存，失败再请求数据
      .toAwaitResponseWan<MutableList<BannerBean>>()
      .onErrorReturnItem(mutableListOf())
  }

  //获取文章列表
  suspend fun article(@IntRange(from = 0) page: Int, readCache: Boolean = true): Await<BasePageList<ArticleBean>> {
    return RxHttp.get(String.format(WanUrls.Home.ARTICLE, page))
      .setCacheValidTime(TimeConstants.HOME_CACHE) //设置缓存时长
      .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //先读取缓存，失败再请求数据
      .toAwaitResponseWan<BasePageList<ArticleBean>>()
      .onErrorReturnItem(BasePageList())
  }
}
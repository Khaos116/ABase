package cc.abase.demo.rxhttp.repository

import androidx.annotation.IntRange
import cc.ab.base.net.http.response.BasePageList
import cc.abase.demo.bean.wan.ArticleBean
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.constants.TimeConstants
import cc.abase.demo.constants.WanUrls
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponseWan

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/5 18:10
 */
object WanRepository {

  //获取Banner
  suspend fun banner(readCache: Boolean = true): MutableList<BannerBean> {
    return RxHttp.get(WanUrls.Home.BANNER)
        .setDomainToWanIfAbsent()
        .setCacheValidTime(TimeConstants.HOME_CACHE) //设置缓存时长
        .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //先读取缓存，失败再请求数据
        .toResponseWan<MutableList<BannerBean>>()
        .await()
  }

  //获取文章列表
  suspend fun article(@IntRange(from = 0) page: Int, readCache: Boolean = true): BasePageList<ArticleBean> {
    return RxHttp.get(String.format(WanUrls.Home.ARTICLE, page))
        .setDomainToWanIfAbsent()
        .setCacheValidTime(TimeConstants.HOME_CACHE) //设置缓存时长
        .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //先读取缓存，失败再请求数据
        .toResponseWan<BasePageList<ArticleBean>>()
        .await()
  }
}
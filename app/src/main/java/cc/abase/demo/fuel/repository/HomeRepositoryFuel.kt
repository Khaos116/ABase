package cc.abase.demo.fuel.repository

import androidx.annotation.IntRange
import cc.ab.base.utils.RxUtils
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.fuel.repository.base.BaseRepository
import cc.abase.demo.bean.wan.ArticleDataBean
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.fuel.repository.request.WanRequest
import com.github.kittinunf.fuel.httpGet
import io.reactivex.Single

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/13 17:45
 */
class HomeRepositoryFuel private constructor() : BaseRepository() {
  private object SingletonHolder {
    val holder = HomeRepositoryFuel()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //获取Banner--->Single<MutableList<...>> 需要调用startRequest...List的请求
  fun banner(readCache: Boolean = true): Single<MutableList<BannerBean>> {
    val request = WanUrls.Home.BANNER.httpGet()
    return if (readCache) {
      WanRequest.instance.startRequestWithCacheList(request)
    } else {
      WanRequest.instance.startRequestList<BannerBean>(request)
    }.flatMap { justRespons(it) }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }

  //获取文章列表--->Single<...> 需要调用startRequest...不包含List的请求
  fun article(
    @IntRange(
        from = 0
    ) page: Int, readCache: Boolean = true
  ): Single<ArticleDataBean> {
    val request = String.format(WanUrls.Home.ARTICLE, page)
        .httpGet()
    return if (readCache) {
      WanRequest.instance.startRequestWithCache(request, page = page)
    } else {
      WanRequest.instance.startRequest<ArticleDataBean>(request)
    }.flatMap { justRespons(it) }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }
}
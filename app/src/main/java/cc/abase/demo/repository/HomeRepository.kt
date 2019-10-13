package cc.abase.demo.repository

import androidx.annotation.IntRange
import cc.ab.base.net.http.response.BaseResponse
import cc.ab.base.utils.RxUtils
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.repository.base.BaseRepository
import cc.abase.demo.repository.bean.wan.*
import cc.abase.demo.repository.request.WanRequest
import com.github.kittinunf.fuel.httpGet
import com.google.gson.reflect.TypeToken
import io.reactivex.Single

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/13 17:45
 */
class HomeRepository private constructor() : BaseRepository() {
  private object SingletonHolder {
    val holder = HomeRepository()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //数据转换type
  private val bannerType = object : TypeToken<BaseResponse<MutableList<BannerBean>>>() {}
  private val articleType = object : TypeToken<BaseResponse<ArticleDataBean>>() {}

  //获取Banner
  fun banner(readCache: Boolean = true): Single<MutableList<BannerBean>> {
    val request = WanUrls.Home.BANNER.httpGet()
    return if (readCache) {
      WanRequest.instance.startRequestWithCache(request, type = bannerType)
    } else {
      WanRequest.instance.startRequest(request, bannerType)
    }.flatMap { justRespons(it) }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }

  //获取文章列表
  fun article(
    @IntRange(
        from = 0
    ) page: Int, readCache: Boolean = true
  ): Single<ArticleDataBean> {
    val request = String.format(WanUrls.Home.ARTICLE, page)
        .httpGet()
    return if (readCache) {
      WanRequest.instance.startRequestWithCache(request, page = page, type = articleType)
    } else {
      WanRequest.instance.startRequest(request, articleType)
    }.flatMap { justRespons(it) }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }
}
package cc.abase.demo.component.main.viewmodel

import cc.ab.base.mvrx.MvRxViewModel
import cc.ab.base.net.http.response.BasePageList
import cc.abase.demo.bean.wan.ArticleBean
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.rxhttp.repository.WanRepository
import com.airbnb.mvrx.*
import io.reactivex.functions.BiFunction

/**
 * Description:
 * @author: CASE
 * @date: 2019/9/30 16:36
 */
data class HomeState(
    val banners: MutableList<BannerBean>? = mutableListOf(),
    val hasMore: Boolean = false,
    val articles: MutableList<ArticleBean> = mutableListOf(),
    val request: Async<Any> = Uninitialized
) : MvRxState

class HomeViewModel(
    state: HomeState = HomeState()
) : MvRxViewModel<HomeState>(state) {
  private var page = 0

  //刷新数据
  fun refreshData() = withState { state ->
    if (state.request is Loading) return@withState
    WanRepository.instance.banner()
        .zipWith(WanRepository.instance.article(0),
            BiFunction<MutableList<BannerBean>, BasePageList<ArticleBean>,
                Pair<MutableList<BannerBean>, BasePageList<ArticleBean>>> { t1, t2 ->
              Pair(t1, t2)
            })
        .execute {
          val suc = it is Success
          if (suc) page = 0
          val pair = it.invoke()
          val bannerList = pair?.first ?: mutableListOf()
          val articleList = pair?.second?.datas?.toMutableList() ?: mutableListOf()
          val hasMore = pair?.second?.datas?.isNullOrEmpty() == false
          copy(
              banners = if (suc) bannerList else state.banners,
              hasMore = if (suc) hasMore else state.hasMore,
              articles = if (suc) articleList else state.articles,
              request = it
          )
        }
  }

  //加载更多
  fun loadMoreData(curPage: Int = page + 1) = withState { state ->
    if (state.request is Loading) return@withState
    WanRepository.instance.article(curPage)
        .execute {
          val suc = it is Success
          if (suc) page = curPage
          val articleList = it.invoke()?.datas ?: mutableListOf()
          copy(
              articles = if (suc) (state.articles + articleList).toMutableList() else state.articles,
              request = it
          )
        }
  }
}
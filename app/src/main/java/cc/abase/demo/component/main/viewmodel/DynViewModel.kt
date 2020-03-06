package cc.abase.demo.component.main.viewmodel

import cc.ab.base.mvrx.MvRxViewModel
import cc.abase.demo.bean.gank.GankAndroidBean
import cc.abase.demo.config.NetConfig
import cc.abase.demo.fuel.repository.GankRepositoryFuel
import cc.abase.demo.rxhttp.repository.GankRepository
import com.airbnb.mvrx.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 20:14
 */
data class DynState(
  val androidList: MutableList<GankAndroidBean> = mutableListOf(),
  val hasMore: Boolean = false,
  val request: Async<Any> = Uninitialized
) : MvRxState

class DynViewModel(
  state: DynState = DynState()
) : MvRxViewModel<DynState>(state) {
  private var page = 1
  private var pageSize = 20

  //刷新
  fun refreshData() {
    getAndroidList(1)
  }

  //加载更多
  fun loadMoreData() {
    getAndroidList(page + 1)
  }

  private fun getAndroidList(tempPage: Int) = withState { state ->
    if (state.request is Loading) return@withState
    if (NetConfig.USE_RXHTTP) GankRepository.instance.androidList(tempPage, pageSize)
        .execute {
          val result: MutableList<GankAndroidBean> = it.invoke() ?: mutableListOf()
          if (it is Success) page = tempPage
          copy(
              //只有刷新成功后才会清数据
              androidList = if (tempPage == 1 && it is Success) result//刷新成功
              else if (result.isNullOrEmpty()) state.androidList//请求失败
              else (state.androidList + result).toMutableList(),//加载更多
              hasMore = if (it is Success) result.size == pageSize else state.hasMore,
              request = it
          )
        }
    else GankRepositoryFuel.instance.androidList(tempPage, pageSize)
        .execute {
          val result: MutableList<GankAndroidBean> = it.invoke() ?: mutableListOf()
          if (it is Success) page = tempPage
          copy(
              //只有刷新成功后才会清数据
              androidList = if (tempPage == 1 && it is Success) result//刷新成功
              else if (result.isNullOrEmpty()) state.androidList//请求失败
              else (state.androidList + result).toMutableList(),//加载更多
              hasMore = if (it is Success) result.size == pageSize else state.hasMore,
              request = it
          )
        }
  }
}
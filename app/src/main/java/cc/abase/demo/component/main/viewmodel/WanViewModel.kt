package cc.abase.demo.component.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.ui.viewmodel.DataState.FailMore
import cc.ab.base.ui.viewmodel.DataState.FailRefresh
import cc.ab.base.ui.viewmodel.DataState.Start
import cc.ab.base.ui.viewmodel.DataState.SuccessMore
import cc.ab.base.ui.viewmodel.DataState.SuccessRefresh
import cc.abase.demo.bean.wan.ArticleBean
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.rxhttp.repository.WanRepository
import kotlinx.coroutines.launch
import rxhttp.awaitResult
import rxhttp.onStart

/**
 * Author:Khaos
 * Date:2020/8/29
 * Time:17:18
 */
class WanViewModel : CommViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部访问">
  //banner
  val bannerLiveData = MutableLiveData<DataState<MutableList<BannerBean>>?>()

  //文章列表
  val articleLiveData = MutableLiveData<DataState<MutableList<ArticleBean>>?>()

  //刷新
  fun refresh() {
    requestBanner()
    requestWanList(0)
  }

  //加载更多
  fun loadMore() = requestWanList(currentPage + 1)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部处理">
  private var currentPage = 0
  private var hasMore = false

  //请求banner(有数据就不再请求了)
  private fun requestBanner() {
    val old = bannerLiveData.value?.data
    if (bannerLiveData.value is Start || !old.isNullOrEmpty()) return
    viewModelScope.launch {
      WanRepository.banner()
        .onStart {
          bannerLiveData.value = Start(oldData = old)
        }
        .awaitResult {
          bannerLiveData.value = SuccessRefresh(newData = it)
        }
        .onFailure { e ->
          bannerLiveData.value = FailRefresh(oldData = old, exc = e)
        }
    }
  }

  //请求文章列表
  private fun requestWanList(page: Int) {
    if (articleLiveData.value is Start) return
    val old = articleLiveData.value?.data
    viewModelScope.launch {
      WanRepository.article(page)
        .onStart {
          articleLiveData.value = Start(oldData = old)
        }
        .awaitResult { response ->
          val result = response.datas?.toMutableList() ?: mutableListOf()
          hasMore = response.curPage < response.total
          currentPage = page
          //可以直接更新UI
          articleLiveData.value = if (page == 0) SuccessRefresh(newData = result)
          else SuccessMore(newData = result, totalData = if (old.isNullOrEmpty()) result else (old + result).toMutableList())
        }.onFailure { e ->
          articleLiveData.value = if (page == 0) FailRefresh(oldData = old, exc = e) else FailMore(oldData = old, exc = e)
        }
    }
  }
  //</editor-fold>
}
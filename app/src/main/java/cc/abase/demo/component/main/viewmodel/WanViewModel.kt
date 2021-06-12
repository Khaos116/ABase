package cc.abase.demo.component.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.ui.viewmodel.DataState.Complete
import cc.ab.base.ui.viewmodel.DataState.FailMore
import cc.ab.base.ui.viewmodel.DataState.FailRefresh
import cc.ab.base.ui.viewmodel.DataState.Start
import cc.ab.base.ui.viewmodel.DataState.SuccessMore
import cc.ab.base.ui.viewmodel.DataState.SuccessRefresh
import cc.abase.demo.bean.wan.ArticleBean
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.rxhttp.repository.WanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    rxLifeScope.launch({
      withContext(Dispatchers.IO) { WanRepository.banner() }.let { bannerLiveData.value = SuccessRefresh(newData = it) }
    }, { e ->
      bannerLiveData.value = FailRefresh(oldData = old, exc = e)
    }, {
      bannerLiveData.value = Start(oldData = old)
    }, {
      bannerLiveData.value = Complete(totalData = bannerLiveData.value?.data, hasMore = false)
    })
  }

  //请求文章列表
  private fun requestWanList(page: Int) {
    if (articleLiveData.value is Start) return
    val old = articleLiveData.value?.data
    rxLifeScope.launch({
      //协程代码块
      val response = WanRepository.article(page)
      val result = response.datas?.toMutableList() ?: mutableListOf()
      hasMore = response.curPage < response.total
      currentPage = page
      //可以直接更新UI
      articleLiveData.value = if (page == 0) SuccessRefresh(newData = result)
      else SuccessMore(newData = result, totalData = if (old.isNullOrEmpty()) result else (old + result).toMutableList())
    }, { e -> //异常回调，这里可以拿到Throwable对象
      articleLiveData.value = if (page == 0) FailRefresh(oldData = old, exc = e) else FailMore(oldData = old, exc = e)
    }, { //开始回调，可以开启等待弹窗
      articleLiveData.value = Start(oldData = old)
    }, { //结束回调，可以销毁等待弹窗
      articleLiveData.value = Complete(totalData = articleLiveData.value?.data, hasMore = hasMore)
    })
  }
  //</editor-fold>
}
package cc.abase.demo.component.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.ab.base.ext.logE
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.ui.viewmodel.DataState.*
import cc.abase.demo.bean.wan.ArticleBean
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.rxhttp.repository.WanRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Author:Khaos
 * Date:2020/8/29
 * Time:17:18
 */
class WanViewModel : CommViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部访问">
  //banner+文章列表
  val pairLiveData = MutableLiveData<DataState<Pair<MutableList<BannerBean>, MutableList<ArticleBean>>>>()

  //刷新
  fun refresh(readCache: Boolean) {
    requestBannerAndWanList(0, readCache)
  }

  //加载更多
  fun loadMore() = requestBannerAndWanList(currentPage + 1, false)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部处理">
  private var currentPage = 0

  //请求文章列表
  private fun requestBannerAndWanList(page: Int, readCache: Boolean) {
    if (pairLiveData.value is Start) return
    val old = pairLiveData.value?.data
    val oldBanner = old?.first ?: mutableListOf()
    val oldArticle = old?.second ?: mutableListOf()
    viewModelScope.launch {
      pairLiveData.value = Start(oldData = old)
      //并发请求
      val dBanner = async { if (page == 0 && (oldBanner.isEmpty() || !readCache)) WanRepository.banner(readCache = readCache).await() else oldBanner }//第一页+(没有数据或者不读缓存)才进行请求
      val dArticle = async { WanRepository.article(page = page, readCache = readCache).await() }
      val resultPair = Pair(dBanner.await(), dArticle.await())
      var e1: Throwable? = null
      var e2: Throwable? = null
      resultPair.first.firstOrNull()?.errorInfo?.let { error ->
        e1 = error
        "Banner获取失败:${error.message}".logE()
      }
      resultPair.second.errorInfo?.let { error ->
        e2 = error
        "文章列表获取失败:${error.message}".logE()
      }
      if (e2 == null) currentPage = page
      val firstList: MutableList<BannerBean> = mutableListOf()
      val secondList: MutableList<ArticleBean> = mutableListOf()
      val newArticle = resultPair.second.datas?.toMutableList() ?: mutableListOf()
      firstList.addAll(if (e1 == null) resultPair.first else oldBanner)
      secondList.addAll(if (e2 == null) (oldArticle + newArticle).toMutableList() else oldArticle)
      val hasMore = resultPair.second.curPage < resultPair.second.total
      if (page == 0) {//刷新
        if (e1 == null || e2 == null) {//成功
          pairLiveData.value = SuccessRefresh(newData = Pair(firstList, secondList), hasMore = hasMore)
        } else {
          pairLiveData.value = FailRefresh(oldData = old, exc = e2 ?: e1 ?: Throwable())
        }
      } else {//加载更多
        if (e2 == null) {//成功
          pairLiveData.value = SuccessMore(
            newData = Pair(firstList, newArticle),
            totalData = Pair(firstList, secondList),
            hasMore = hasMore
          )
        } else {
          pairLiveData.value = FailMore(oldData = old, exc = e2)
        }
      }
    }
  }
  //</editor-fold>
}
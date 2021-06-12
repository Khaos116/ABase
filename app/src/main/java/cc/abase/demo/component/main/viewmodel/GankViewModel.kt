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
import cc.abase.demo.bean.gank.GankAndroidBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.rxhttp.repository.GankRepository

/**
 * Author:Khaos
 * Date:2020/8/21
 * Time:9:52
 */
class GankViewModel : CommViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部访问">
  val androidLiveData = MutableLiveData<DataState<MutableList<GankAndroidBean>>?>()

  //刷新
  fun refresh() = requestAndroidList(1)

  //加载更多
  fun loadMore() = requestAndroidList(currentPage + 1)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部处理">
  private var currentPage = 1
  private var pageSize = 10
  private fun requestAndroidList(page: Int) {
    if (androidLiveData.value is Start) return
    val old = androidLiveData.value?.data //加载前的旧数据
    rxLifeScope.launch({
      //协程代码块
      val result = GankRepository.androidList(page = page, size = pageSize)
      currentPage = page
      //可以直接更新UI
      androidLiveData.value = if (page == 1) SuccessRefresh(newData = result)
      else SuccessMore(newData = result, totalData = if (old.isNullOrEmpty()) result else (old + result).toMutableList())
    }, { e -> //异常回调，这里可以拿到Throwable对象
      androidLiveData.value = if (page == 1) FailRefresh(oldData = old, exc = e) else FailMore(oldData = old, exc = e)
    }, { //开始回调，可以开启等待弹窗
      androidLiveData.value = Start(oldData = old)
    }, { //结束回调，可以销毁等待弹窗
      val data = androidLiveData.value?.data
      androidLiveData.value = Complete(totalData = data, hasMore = !data.isNullOrEmpty() && data.size % pageSize == 0)
    })
  }
  //</editor-fold>
}
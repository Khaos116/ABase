package cc.abase.demo.component.comm

import cc.ab.base.ext.*
import cc.ab.base.ui.viewmodel.BaseViewModel
import cc.ab.base.ui.viewmodel.DataState
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Author:Khaos
 * Date:2020-11-30
 * Time:16:17
 */
abstract class CommViewModel : BaseViewModel() {
  //处理刷新状态
  inline fun <reified T> handleRefresh(refreshLayout: SmartRefreshLayout?, dataState: DataState<MutableList<T>>?) {
    when (dataState) {
      is DataState.SuccessRefresh -> { //刷新成功，如果有数据则可以拉出"加载更多"或者"没有更多"
        refreshLayout?.setEnableRefresh(true) //允许下拉刷新(空数据重新刷新)
        refreshLayout?.setEnableLoadMore(!dataState.data.isNullOrEmpty()) //列表数据不为空才能上拉
        if (dataState.hasMore) refreshLayout?.hasMoreData() else refreshLayout?.noMoreData()
        refreshLayout?.finishRefresh() //结束刷新(不论成功还是失败)
      }
      is DataState.FailRefresh -> {
        dataState.exc.logE()
        refreshLayout?.finishRefresh() //结束刷新(不论成功还是失败)
      }
      is DataState.SuccessMore -> {
        refreshLayout?.finishLoadMore()
        if (dataState.hasMore) refreshLayout?.hasMoreData() else refreshLayout?.noMoreData()
      } //加载更多成功
      is DataState.FailMore -> {
        dataState.exc.logE()
        refreshLayout?.finishLoadMore(false) //加载更多失败
      }
      else -> {
      }
    }
  }

  //处理Main异常
  fun getMainContext(handler: (CoroutineContext, Throwable) -> Unit): CoroutineContext {
    return Dispatchers.Main + CoroutineExceptionHandler(handler)
  }

  //处理IO异常
  fun getIOContext(handler: (CoroutineContext, Throwable) -> Unit): CoroutineContext {
    return Dispatchers.IO + CoroutineExceptionHandler(handler)
  }
}
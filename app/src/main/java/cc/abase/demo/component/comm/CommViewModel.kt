package cc.abase.demo.component.comm

import cc.ab.base.ext.logE
import cc.ab.base.ui.viewmodel.BaseViewModel
import cc.ab.base.ui.viewmodel.DataState
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState

/**
 * Author:CASE
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
      }
      is DataState.SuccessMore -> refreshLayout?.finishLoadMore() //加载更多成功
      is DataState.FailMore -> {
        refreshLayout?.finishLoadMore(false) //加载更多失败
        dataState.exc.logE()
      }
      is DataState.Complete -> { //请求完成
        refreshLayout?.finishRefresh() //结束刷新(不论成功还是失败)
        refreshLayout?.setNoMoreData(!dataState.hasMore) //判断是否还有更多
        if (refreshLayout?.state == RefreshState.Loading) refreshLayout.finishLoadMore() //加载更多太快可能出现加载更多不消失，所以纠正
      }
      is DataState.FailRefresh -> dataState.exc.logE()
      else -> {
      }
    }
  }
}
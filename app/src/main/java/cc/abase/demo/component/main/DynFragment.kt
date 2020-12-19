package cc.abase.demo.component.main

import android.util.Log
import android.view.View
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.main.viewmodel.DynState
import cc.abase.demo.component.main.viewmodel.DynViewModel
import cc.abase.demo.component.web.WebActivity
import cc.abase.demo.epoxy.base.*
import cc.abase.demo.epoxy.item.gankAndroidItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.mvrx.*
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.billy.android.swipe.consumer.SlidingConsumer
import com.blankj.utilcode.util.*
import kotlinx.android.synthetic.main.fragment_dyn.dynEpoxyRecycler

/**
 * Description:
 * @author: CASE
 * @date: 2019/9/30 18:12
 */
class DynFragment : CommFragment() {
  private var mSmartSwipeRefresh: SmartSwipeRefresh? = null

  companion object {
    fun newInstance(): DynFragment {
      return DynFragment()
    }
  }

  //数据层
  private val viewModel: DynViewModel by lazy {
    DynViewModel()
  }
  override val contentLayout = R.layout.fragment_dyn

  override fun initView(root: View?) {
    dynEpoxyRecycler.setController(epoxyController)
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(dynEpoxyRecycler, false)
    mSmartSwipeRefresh?.swipeConsumer?.let {
      if (it is SlidingConsumer) {
        it.setOverSwipeFactor(2f)
        it.relativeMoveFactor = 0.5f
      }
    }
    mSmartSwipeRefresh?.disableLoadMore()
        ?.disableRefresh()
    //把加载更多全部显示出来开始回调加载更多
    EpoxyVisibilityTracker().attach(dynEpoxyRecycler)
  }

  override fun initData() {
    //下拉刷新
    mSmartSwipeRefresh?.dataLoader = object : SmartSwipeRefreshDataLoader {
      override fun onLoadMore(ssr: SmartSwipeRefresh?) {
      }

      override fun onRefresh(ssr: SmartSwipeRefresh?) {
        viewModel.refreshData()
      }
    }
    //请求状态和结果监听
    viewModel.subscribe { state ->
      if (state.request is Loading) {//请求开始
        //如果没有显示下拉刷新则显示loading
        if (mSmartSwipeRefresh?.swipeConsumer?.isAllDirectionsLocked == false && state.androidList.isNullOrEmpty()) {
          //显示loading
          showLoadingView()
          //为了防止loading结束后还存在失败的view所以需刷新一下
          if (state.androidList.isNullOrEmpty()) epoxyController.data = state
        }
      } else if (state.request.complete) {//请求结束
        mSmartSwipeRefresh?.finished(state.request is Success)
        dismissLoadingView()
        epoxyController.data = state
        if (state.request is Fail) {//请求失败
          LogUtils.e("CASE:失败原因:${state.request.error.message ?: ""}")
        }
      }
    }
    //请求数据
    viewModel.refreshData()
  }

  //epoxy
  private val epoxyController = MvRxEpoxyController<DynState> { state ->
    //有数据
    if (!state.androidList.isNullOrEmpty()) {
      state.androidList.forEachIndexed { index, bean ->
        //数据
        gankAndroidItem {
          id("dyn_${bean._id}")
          dataBean(bean)
          onItemClick { data -> WebActivity.startActivity(mContext, data.url ?: "") }
        }
        //分割线
        dividerItem {
          id("dyn_line_${bean._id}")
        }
      }
      //有数据支持下拉刷新
      mSmartSwipeRefresh?.swipeConsumer?.enableTop()
      //根据返回信息判断是否可以加载更多
      if (state.hasMore) {
        loadMoreItem {
          id("dyn_line_more")
          fail(state.request is Fail)
          onLoadMore {
            viewModel.loadMoreData()
          }
        }
      }
    } else {
      //没有数据，不能下拉刷新也不能加载更多
      mSmartSwipeRefresh?.disableRefresh()
      //无数据
      when {
        state.request is Success -> errorEmptyItem {
          id("dyn_suc_no_data")
          imageResource(R.drawable.svg_no_data)
          tipsText(StringUtils.getString(R.string.no_data))
        }
        //无网络或者请求失败
        state.request is Fail -> errorEmptyItem {
          id("dyn_fail_no_data")
          if (NetworkUtils.isConnected()) {
            imageResource(R.drawable.svg_fail)
            tipsText(mContext.getString(R.string.net_fail_retry))
          } else {
            imageResource(R.drawable.svg_no_network)
            tipsText(mContext.getString(R.string.net_error_retry))
          }
          onRetryClick { viewModel.refreshData() }
        }
        else -> LogUtils.i("初始化无数据空白")
      }
    }
  }
}
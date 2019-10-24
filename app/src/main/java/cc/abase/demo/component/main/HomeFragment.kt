package cc.abase.demo.component.main

import android.util.Log
import android.view.View
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.main.viewmodel.HomeState
import cc.abase.demo.component.main.viewmodel.HomeViewModel
import cc.abase.demo.component.web.WebActivity
import cc.abase.demo.epoxy.base.*
import cc.abase.demo.epoxy.item.bannerItem
import cc.abase.demo.epoxy.item.wanArticleItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.mvrx.*
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.billy.android.swipe.consumer.SlidingConsumer
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.android.synthetic.main.fragment_home.homeEpoxyRecycler

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 18:02
 */
class HomeFragment : CommFragment() {
  private var mSmartSwipeRefresh: SmartSwipeRefresh? = null

  companion object {
    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }

  //数据层
  private val viewModel: HomeViewModel by lazy {
    HomeViewModel()
  }

  override val contentLayout = R.layout.fragment_home

  override fun initView(root: View?) {
    homeEpoxyRecycler.setController(epoxyController)
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(homeEpoxyRecycler, false)
    mSmartSwipeRefresh?.swipeConsumer?.let {
      if (it is SlidingConsumer) {
        it.setOverSwipeFactor(2f)
        it.relativeMoveFactor = 0.5f
      }
    }
    mSmartSwipeRefresh?.disableLoadMore()
        ?.disableRefresh()
    //把加载更多全部显示出来开始回调加载更多
    EpoxyVisibilityTracker().attach(homeEpoxyRecycler)
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
        if (mSmartSwipeRefresh?.swipeConsumer?.isAllDirectionsLocked == false &&
            state.banners.isNullOrEmpty() && state.articles.isNullOrEmpty()
        ) {
          //显示loading
          showLoadingView()
          //为了防止loading结束后还存在失败的view所以需刷新一下
          epoxyController.data = state//透明背景的loading需要这样设置
          //epoxyController.requestModelBuild()非透明背景的loading还可以这样设置
        }
      } else if (state.request.complete) {//请求结束
        mSmartSwipeRefresh?.finished(state.request is Success)
        dismissLoadingView()
        epoxyController.data = state
        if (state.request is Fail) {//请求失败
          Log.e("CASE", "失败原因:${(state.request as Fail<Any>).error.message ?: ""}")
        }
      }
    }
    //请求数据
    viewModel.refreshData()
  }

  //epoxy
  private val epoxyController = MvRxEpoxyController<HomeState> { state ->
    //有数据
    if (!state.banners.isNullOrEmpty() || !state.articles.isNullOrEmpty()) {
      //处理Banner'
      if (!state.banners.isNullOrEmpty()) {
        bannerItem {
          id("home_banner_${state.banners.hashCode() + state.banners.size}")
          dataList(state.banners)
        }
      }
      //处理文章列表
      if (!state.articles.isNullOrEmpty()) {
        state.articles.forEachIndexed { index, articleBean ->
          //文章
          wanArticleItem {
            id(articleBean.id)
            dataBean(articleBean)
            onItemClick { bean ->
              bean?.link?.let { url -> WebActivity.startActivity(mContext, url) }
            }
          }
          //分割线
          dividerItem {
            id("home_line_banner")
          }
        }
      }
      //有数据支持下拉刷新
      mSmartSwipeRefresh?.swipeConsumer?.enableTop()
      //根据返回信息判断是否可以加载更多
      if (state.hasMore) {
        loadMoreItem {
          id("home_line_more")
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
          id("home_suc_no_data")
          imageResource(R.drawable.svg_no_data)
          tipsText(mContext.getString(R.string.no_data))
        }
        //无网络或者请求失败
        state.request is Fail -> errorEmptyItem {
          id("home_fail_no_data")
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
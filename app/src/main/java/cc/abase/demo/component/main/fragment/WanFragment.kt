package cc.abase.demo.component.main.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.widget.livedata.MyObserver
import cc.abase.demo.bean.local.EmptyErrorBean
import cc.abase.demo.bean.local.LoadingBean
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.component.main.viewmodel.WanViewModel
import cc.abase.demo.component.web.WebActivity
import cc.abase.demo.databinding.FragmentWanBinding
import cc.abase.demo.item.*
import cc.abase.demo.sticky.StickyAnyAdapter
import cc.abase.demo.sticky.StickyHeaderLinearLayoutManager

/**
 * Author:Khaos
 * Date:2020/8/12
 * Time:20:48
 */
class WanFragment : CommBindFragment<FragmentWanBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): WanFragment = WanFragment()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求和监听
  private val mViewModel: WanViewModel by lazy { WanViewModel() }

  //多类型适配器
  private val stickyAdapter = object : StickyAnyAdapter(stickyBgColor = Color.CYAN) {
    override fun isStickyHeader(position: Int): Boolean {
      return position == 1
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  @SuppressLint("NotifyDataSetChanged")
  override
  fun lazyInit() {
    mActivity.window?.setBackgroundDrawable(null)
    mRootLayout?.setBackgroundColor(Color.WHITE)
    viewBinding.wanRefreshLayout.setEnableScrollContentWhenLoaded(false) //加载更多完成整体下移，手动上滑显示更多内容
    viewBinding.wanRefreshLayout.setOnRefreshListener { mViewModel.refresh(false) }
    viewBinding.wanRefreshLayout.setOnLoadMoreListener { mViewModel.loadMore() }
    //设置适配器
    viewBinding.wanRecycler.layoutManager = StickyHeaderLinearLayoutManager<StickyAnyAdapter>(mContext, LinearLayoutManager.VERTICAL, false)
    viewBinding.wanRecycler.adapter = stickyAdapter
    //注册多类型
    stickyAdapter.register(LoadingItem())
    stickyAdapter.register(EmptyErrorItem() { mViewModel.refresh(false) })
    stickyAdapter.register(BannerItem() { bean, _ ->
      bean.url?.let { u -> WebActivity.startActivity(mActivity, u) }
    })
    stickyAdapter.register(ArticleItem().also {
      it.onItemClick = { bean ->
        bean.link?.let { u -> WebActivity.startActivity(mActivity, u) }
      }
    })
    //数据监听
    mViewModel.pairLiveData.observe(this, MyObserver { dataState ->
      //刷新空间状态处理
      when (dataState) {
        is DataState.SuccessRefresh -> { //刷新成功，如果有数据则可以拉出"加载更多"或者"没有更多"
          viewBinding.wanRefreshLayout.setEnableRefresh(true) //允许下拉刷新(空数据重新刷新)
          viewBinding.wanRefreshLayout.setEnableLoadMore(!dataState.data?.second.isNullOrEmpty()) //列表数据不为空才能上拉
          if (dataState.hasMore) viewBinding.wanRefreshLayout.hasMoreData() else viewBinding.wanRefreshLayout.noMoreData()
          viewBinding.wanRefreshLayout.finishRefresh() //结束刷新(不论成功还是失败)
        }
        is DataState.FailRefresh -> {
          dataState.exc.logE()
          viewBinding.wanRefreshLayout.finishRefresh() //结束刷新(不论成功还是失败)
        }
        is DataState.SuccessMore -> {
          viewBinding.wanRefreshLayout.finishLoadMore()
          if (dataState.hasMore) viewBinding.wanRefreshLayout.hasMoreData() else viewBinding.wanRefreshLayout.noMoreData()
        } //加载更多成功
        is DataState.FailMore -> {
          dataState.exc.logE()
          viewBinding.wanRefreshLayout.finishLoadMore(false) //加载更多失败
        }
        else -> {
        }
      }
      //正常数据处理
      var items = mutableListOf<Any>()
      when (dataState) {
        //开始请求
        is DataState.Start -> {
          if (dataState.data?.first.isNullOrEmpty() && dataState.data?.second.isNullOrEmpty()) items.add(LoadingBean()) //加载中
          else items = stickyAdapter.items.toMutableList()
        }
        //刷新成功
        is DataState.SuccessRefresh -> {
          if (dataState.data?.first.isNullOrEmpty() && dataState.data?.second.isNullOrEmpty()) items.add(EmptyErrorBean(isEmpty = true, isError = false)) //如果请求成功没有数据
          else {
            dataState.data?.first?.let { l1 -> items.add(l1) }
            dataState.data?.second?.let { l2 -> items.addAll(l2) }
          }
        }
        //加载更多成功
        is DataState.SuccessMore -> {
          items = stickyAdapter.items.toMutableList()
          dataState.newData?.second?.let { l3 -> items.addAll(l3) }
        }
        //刷新失败
        is DataState.FailRefresh -> {
          if (dataState.data?.first.isNullOrEmpty() && dataState.data?.second.isNullOrEmpty()) items.add(EmptyErrorBean()) //如果是请求异常没有数据
          else items = stickyAdapter.items.toMutableList()
        }
        //加载更多失败
        is DataState.FailMore -> {
          items = stickyAdapter.items.toMutableList()
          dataState.exc.toast()
        }
        else -> {
        }
      }
      stickyAdapter.items = items
      stickyAdapter.notifyDataSetChanged()
    })
    //请求数据
    mViewModel.refresh(true)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="公共方法调用">
  override fun scroll2Top() {
    viewBinding.wanRecycler.layoutManager?.let { manager ->
      val firstPosition = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
      if (firstPosition > 5) viewBinding.wanRecycler.scrollToPosition(5)
      viewBinding.wanRecycler.smoothScrollToPosition(0)
    }
  }
  //</editor-fold>
}
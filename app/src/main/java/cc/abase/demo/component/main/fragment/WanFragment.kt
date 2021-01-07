package cc.abase.demo.component.main.fragment

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.R
import cc.abase.demo.bean.local.EmptyErrorBean
import cc.abase.demo.bean.local.LoadingBean
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.main.viewmodel.WanViewModel
import cc.abase.demo.component.web.WebActivity
import cc.abase.demo.item.*
import cc.abase.demo.sticky.StickyAnyAdapter
import cc.abase.demo.sticky.StickyHeaderLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_wan.wanRecycler
import kotlinx.android.synthetic.main.fragment_wan.wanRefreshLayout

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:48
 */
class WanFragment private constructor() : CommFragment() {

  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): WanFragment {
      val fragment = WanFragment()
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_wan
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求和监听
  private val mViewModel: WanViewModel by lazy { WanViewModel() }

  //多类型适配器
  private val stickyAdapter = object : StickyAnyAdapter(bgColor = Color.CYAN) {
    override fun isStickyHeader(position: Int): Boolean {
      return position == 1
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  override
  fun lazyInit() {
    wanRefreshLayout.setOnRefreshListener { mViewModel.refresh() }
    wanRefreshLayout.setOnLoadMoreListener { mViewModel.loadMore() }
    //设置适配器
    wanRecycler.layoutManager = StickyHeaderLinearLayoutManager<StickyAnyAdapter>(mContext, LinearLayoutManager.VERTICAL, false)
    wanRecycler.adapter = stickyAdapter
    //注册多类型
    stickyAdapter.register(LoadingItem())
    stickyAdapter.register(EmptyErrorItem() { mViewModel.refresh() })
    stickyAdapter.register(BannerItem() { bean, _ ->
      bean.url?.let { u -> WebActivity.startActivity(mActivity, u) }
    })
    stickyAdapter.register(ArticleItem { bean ->
      bean.link?.let { u -> WebActivity.startActivity(mActivity, u) }
    })
    //文章列表监听
    mViewModel.articleLiveData.observe(this) {
      mViewModel.handleRefresh(wanRefreshLayout, it)
      //正常数据处理
      var items = mutableListOf<Any>()
      when (it) {
        //开始请求
        is DataState.Start -> {
          if (it.data.isNullOrEmpty()) items.add(LoadingBean()) //加载中
          else items = stickyAdapter.items.toMutableList()
        }
        //刷新成功
        is DataState.SuccessRefresh -> {
          if (it.data.isNullOrEmpty()) items.add(EmptyErrorBean(isEmpty = true, isError = false)) //如果请求成功没有数据
          else it.data?.forEach { articleBean -> items.add(articleBean) }
        }
        //加载更多成功
        is DataState.SuccessMore -> {
          items = stickyAdapter.items.toMutableList()
          it.newData?.forEach { articleBean -> items.add(articleBean) }
        }
        //刷新失败
        is DataState.FailRefresh -> {
          if (it.data.isNullOrEmpty()) items.add(EmptyErrorBean()) //如果是请求异常没有数据
          else items = stickyAdapter.items.toMutableList()
        }
        else -> {
        }
      }
      if (it?.dataMaybeChange() == true) {
        //Banner
        if (!items.any { d -> d is MutableList<*> }) {
          mViewModel.bannerLiveData.value?.data?.let { banner ->
            if (banner.isNotEmpty()) {
              items.add(0, banner)
              items = items.filterNot { d -> d is LoadingBean || d is EmptyErrorBean }.toMutableList()
            }
          }
        }
        stickyAdapter.items = items
        stickyAdapter.notifyDataSetChanged()
      }
    }
    //banner监听
    mViewModel.bannerLiveData.observe(this) {
      if (it is DataState.Complete) { //如果banner刷新靠后，则请求完成后重新刷一下文章列表
        val articleData = mViewModel.articleLiveData.value
        if (articleData is DataState.Complete) {
          mViewModel.articleLiveData.value = DataState.Complete(totalData = articleData.data, hasMore = articleData.hasMore)
        }
      }
    }
    //请求数据
    mViewModel.refresh()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="公共方法调用">
  override fun scroll2Top() {
    wanRecycler.layoutManager?.let { manager ->
      val firstPosition = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
      if (firstPosition > 5) wanRecycler.scrollToPosition(5)
      wanRecycler.smoothScrollToPosition(0)
    }
  }
  //</editor-fold>
}
package cc.abase.demo.component.main.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.widget.livedata.MyObserver
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.EmptyErrorBean
import cc.abase.demo.bean.local.LoadingBean
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.component.main.viewmodel.ReadhubViewModel
import cc.abase.demo.component.web.WebActivity
import cc.abase.demo.databinding.FragmentReadhubBinding
import cc.abase.demo.item.DividerItem
import cc.abase.demo.item.EmptyErrorItem
import cc.abase.demo.item.LoadingItem
import cc.abase.demo.item.ReadhubItem
import com.drakeet.multitype.MultiTypeAdapter

/**
 * Author:Khaos116
 * Date:2022/3/8
 * Time:16:30
 */
class ReadhubFragment : CommBindFragment<FragmentReadhubBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): ReadhubFragment {
      val fragment = ReadhubFragment()
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求
  private val mViewModel by lazy { ReadhubViewModel() }

  //多类型适配器
  private val multiTypeAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  @SuppressLint("NotifyDataSetChanged")
  override fun lazyInit() {
    mRootLayout?.setBackgroundColor(Color.WHITE)
    viewBinding.readhubRefreshLayout.setOnRefreshListener { mViewModel.refresh() }
    viewBinding.readhubRefreshLayout.setOnLoadMoreListener { mViewModel.loadMore() }
    //设置适配器
    viewBinding.readhubRecycler.adapter = multiTypeAdapter
    //注册多类型
    multiTypeAdapter.register(LoadingItem())
    multiTypeAdapter.register(DividerItem())
    multiTypeAdapter.register(EmptyErrorItem { mViewModel.refresh() })
    multiTypeAdapter.register(ReadhubItem().also { it.onItemClick = { bean -> bean.newsArray.lastOrNull()?.mobileUrl?.let { u -> WebActivity.startActivity(mActivity, u) } } })
    //监听加载结果
    mViewModel.topicLiveData.observe(this, MyObserver {
      mViewModel.handleRefresh(viewBinding.readhubRefreshLayout, it)
      //正常数据处理
      var items = mutableListOf<Any>()
      when (it) {
        //开始请求
        is DataState.Start -> {
          if (it.data.isNullOrEmpty()) items.add(LoadingBean()) //加载中
          else items = multiTypeAdapter.items.toMutableList()
        }
        //刷新成功
        is DataState.SuccessRefresh -> {
          if (it.data.isNullOrEmpty()) items.add(EmptyErrorBean(isEmpty = true, isError = false)) //如果请求成功没有数据
          else it.data?.forEachIndexed { index, androidBean ->
            items.add(androidBean) //话题
            if (index < (it.data?.size ?: 0) - 1) items.add(DividerBean(heightPx = 1, bgColor = Color.GREEN)) //分割线
          }
        }
        //加载更多成功
        is DataState.SuccessMore -> {
          items = multiTypeAdapter.items.toMutableList()
          it.newData?.forEach { androidBean ->
            items.add(DividerBean(heightPx = 1, bgColor = Color.GREEN)) //分割线
            items.add(androidBean) //文章
          }
        }
        //刷新失败
        is DataState.FailRefresh -> {
          if (it.data.isNullOrEmpty()) items.add(EmptyErrorBean()) //如果是请求异常没有数据
          else items = multiTypeAdapter.items.toMutableList()
        }
        else -> {
        }
      }
      if (it?.dataMaybeChange() == true) {
        multiTypeAdapter.items = items
        multiTypeAdapter.notifyDataSetChanged()
      }
    })
    //请求数据
    mViewModel.refresh()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="公共方法调用">
  override fun scroll2Top() {
    viewBinding.readhubRecycler.layoutManager?.let { manager ->
      val firstPosition = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
      if (firstPosition > 5) viewBinding.readhubRecycler.scrollToPosition(5)
      viewBinding.readhubRecycler.smoothScrollToPosition(0)
    }
  }
  //</editor-fold>
}
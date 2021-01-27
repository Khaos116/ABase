package cc.abase.demo.component.main.fragment

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.widget.engine.ImageEngine
import cc.abase.demo.R
import cc.abase.demo.bean.local.*
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.main.viewmodel.GankViewModel
import cc.abase.demo.component.web.WebActivity
import cc.abase.demo.item.*
import com.drakeet.multitype.MultiTypeAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.fragment_gank.gankRecycler
import kotlinx.android.synthetic.main.fragment_gank.gankRefreshLayout

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:48
 */
class GankFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): GankFragment {
      val fragment = GankFragment()
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_gank
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求
  private val mViewModel by lazy { GankViewModel() }

  //多类型适配器
  private val multiTypeAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  override fun lazyInit() {
    mRootView?.setBackgroundColor(Color.WHITE)
    gankRefreshLayout.setOnRefreshListener { mViewModel.refresh() }
    gankRefreshLayout.setOnLoadMoreListener { mViewModel.loadMore() }
    //设置适配器
    gankRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    gankRecycler.adapter = multiTypeAdapter
    //注册多类型
    multiTypeAdapter.register(LoadingItem())
    multiTypeAdapter.register(DividerItem())
    multiTypeAdapter.register(EmptyErrorItem() { mViewModel.refresh() })
    multiTypeAdapter.register(GankParentItem(
        onItemClick = { bean ->
          bean.url?.let { u -> WebActivity.startActivity(mActivity, u) }
        },
        onImgClick = { _, p, _, list ->
          val tempList = mutableListOf<LocalMedia>()
          list.forEach { s -> tempList.add(LocalMedia().also { it.path = s }) }
          //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-Api%E8%AF%B4%E6%98%8E
          //开始预览
          PictureSelector.create(this)
              .themeStyle(R.style.picture_default_style)
              .isNotPreviewDownload(true)
              .imageEngine(ImageEngine())
              .openExternalPreview(p, tempList)
        }
    ))
    //监听加载结果
    mViewModel.androidLiveData.observe(this) {
      mViewModel.handleRefresh(gankRefreshLayout, it)
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
            items.add(androidBean) //文章
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
    }
    //请求数据
    mViewModel.refresh()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="公共方法调用">
  override fun scroll2Top() {
    gankRecycler.layoutManager?.let { manager ->
      val firstPosition = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
      if (firstPosition > 5) gankRecycler.scrollToPosition(5)
      gankRecycler.smoothScrollToPosition(0)
    }
  }
  //</editor-fold>
}
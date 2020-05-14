package cc.abase.demo.component.sticky

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.mContext
import cc.ab.base.ext.visible
import cc.abase.demo.R
import cc.abase.demo.bean.local.UserScoreBean
import cc.abase.demo.bean.local.UserStickyBean
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.sticky.adapter.StickyHeaderAdapter2
import cc.abase.demo.component.sticky.widget.StickyHeaderLinearLayoutManager
import cc.abase.demo.epoxy.base.dividerItem
import cc.abase.demo.epoxy.item.sticky2LeftItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_sticky2.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/4/21 14:34
 */
class StickyActivity2 : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, StickyActivity2::class.java)
      context.startActivity(intent)
    }
  }

  //下拉刷新
  private var mSmartSwipeRefresh: SmartSwipeRefresh? = null

  override fun layoutResContentId() = R.layout.activity_sticky2

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_sticky2))
    val manager = StickyHeaderLinearLayoutManager<StickyHeaderAdapter2>(this)
    sticky2Recycler2.layoutManager = manager
    sticky2Recycler1.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    sticky2Recycler1.setController(leftController)
    //两个竖向滑动列表位置同步
    sticky2Recycler1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (sticky2Recycler1.scrollState != 0) sticky2Recycler2.scrollBy(dx, dy) //使右边recyclerView进行联动
      }
    })
    sticky2Recycler2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (sticky2Recycler2.scrollState != 0) sticky2Recycler1.scrollBy(dx, dy) //使左边边recyclerView进行联动
      }
    })
    //两个横向列表位置同步
    sticky2TopHSV.setScrollViewListener { _, x, y, _, _ -> sticky2BottomHSV.scrollTo(x, y) }
    sticky2BottomHSV.setScrollViewListener { _, x, y, _, _ -> sticky2TopHSV.scrollTo(x, y) }
    leftController.addModelBuildListener {
      sticky2Recycler2?.postDelayed({
        //如果不满一页，则不能加载更多了
        if (!sticky2Recycler1.canScrollVertically(1)) mSmartSwipeRefresh?.disableLoadMore()
        dismissLoadingView()
        sticky2Recycler1Parent?.visible()
        sticky2Recycler2?.visible()
      }, 200)
    }
    //加载更多
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(sticky2RootView, false)
    mSmartSwipeRefresh?.disableRefresh()
    mSmartSwipeRefresh?.dataLoader = object : SmartSwipeRefreshDataLoader {
      override fun onLoadMore(ssr: SmartSwipeRefresh?) {
        //停止惯性滚动
        sticky2Recycler1.stopScroll()
        sticky2Recycler2.stopScroll()
        sticky2Recycler2.postDelayed({
          (sticky2Recycler2?.adapter as StickyHeaderAdapter2).addMoreData(originDatas.takeLast(40))
          leftController.data?.addAll(originDatas.takeLast(40))
          leftController.requestModelBuild()
          mSmartSwipeRefresh?.finished(true)
          mSmartSwipeRefresh?.isNoMoreData = true
        }, 1000)
      }

      override fun onRefresh(ssr: SmartSwipeRefresh?) {}
    }
  }

  //模拟数据
  private var originDatas = mutableListOf<UserStickyBean>()

  //标题数据
  private var titleBean = UserStickyBean(name = "", title = true)

  override fun initData() {
    showLoadingView()
    sticky2Recycler2.postDelayed({
      //随机增加个学生成绩
      for (i in 0..79) originDatas.add(UserStickyBean(score = UserScoreBean()))
      //按总成绩排序
      originDatas = originDatas.sortedByDescending { it.score?.scores?.sum() }.toMutableList()
      //添加标题
      // originDatas.add(0, titleBean)//旧版本的滑动效果2
      //第一页随机数量
      val size = (Math.random() * 40).toInt() + 1
      sticky2Recycler2?.adapter = StickyHeaderAdapter2(originDatas.take(size).toMutableList())
      leftController.data = originDatas.take(size).toMutableList()
    }, 100)
  }

  private val leftController = MvRxEpoxyController<MutableList<UserStickyBean>> { list ->
    list.forEachIndexed { index, bean ->
      sticky2LeftItem {
        id(bean.name + index.toString())
        name(bean.name)
      }
      dividerItem {
        id(if (bean.title) "title_line" else "${bean.name + index.toString()}_line")
        bgColorRes(R.color.gray)
        heightPx(1)
      }
    }
  }
}
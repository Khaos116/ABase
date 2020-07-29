package cc.abase.demo.component.simple

import android.graphics.Color
import android.view.Gravity
import android.view.View
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.epoxy.item.simpleTextItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.widget.decoration.SpacesItemDecoration
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.billy.android.swipe.consumer.SlidingConsumer
import kotlinx.android.synthetic.main.fragment_simple.simpleRecycler

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/19 14:44
 */
class SimpleFragment : CommFragment() {

  companion object {
    //0-无刷新 1-Swipe
    fun newInstance(type: Int = 0): SimpleFragment {
      val fragment = SimpleFragment()
      fragment.mType = type
      return fragment
    }
  }

  //下拉刷新
  private var mSmartSwipeRefresh: SmartSwipeRefresh? = null
  private var mType = 0

  override val contentLayout: Int = R.layout.fragment_simple

  override fun initView(root: View?) {
    simpleRecycler.addItemDecoration(SpacesItemDecoration())
    simpleRecycler.setController(epoxyController)
    val datas = mutableListOf<String>()
    for (i in 0 until 20) datas.add(
      "这是${
      when (mType) {
        1 -> "Swipe"
        else -> "Nomal"
      }
      }测试数据${i + 1}"
    )
    epoxyController.data = datas
    if (mType == 1) mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(simpleRecycler, false)
    mSmartSwipeRefresh?.swipeConsumer?.let {
      if (it is SlidingConsumer) {
        it.setOverSwipeFactor(2f)
        it.relativeMoveFactor = 0.5f
      }
    }
    mSmartSwipeRefresh?.disableLoadMore()
    //下拉刷新
    mSmartSwipeRefresh?.dataLoader = object : SmartSwipeRefreshDataLoader {
      override fun onLoadMore(ssr: SmartSwipeRefresh?) {
      }

      override fun onRefresh(ssr: SmartSwipeRefresh?) {
        val list = mutableListOf<String>()
        val size = epoxyController.data?.size ?: 0
        for (i in size until size + 10) list.add(
          "这是${
          when (mType) {
            1 -> "Swipe"
            else -> "Nomal"
          }
          }测试数据${i + 1}"
        )
        list.addAll(epoxyController.data ?: mutableListOf())
        epoxyController.data = list
        mSmartSwipeRefresh?.finished(true)
      }
    }
  }

  override fun initData() {
  }

  //epoxy
  private val epoxyController =
    MvRxEpoxyController<List<String>> { list ->
      list.forEachIndexed { index, str ->
        //内容
        simpleTextItem {
          id("str_$index")
          msg(str)
          textColor(Color.BLACK)
          gravity(Gravity.CENTER_VERTICAL)
          onItemClick { }
        }
      }
    }
}
package cc.abase.demo.component.simple

import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.lifecycle.rxLifeScope
import androidx.recyclerview.widget.LinearLayoutManager
import cc.abase.demo.R
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.item.DividerItem
import cc.abase.demo.item.SimpleTxtItem
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.fragment_simple.simpleRecycler
import kotlinx.android.synthetic.main.fragment_simple.simpleRefresh
import kotlinx.coroutines.*

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/19 14:44
 */
class SimpleFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    //0-无刷新 1-Smart
    fun newInstance(type: Int = 0): SimpleFragment {
      val fragment = SimpleFragment()
      fragment.mType = type
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">

  //类型
  private var mType = 0

  //适配器
  private val multiTypeAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentLayout: Int = R.layout.fragment_simple
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView(root: View?) {
    simpleRecycler.layoutManager = LinearLayoutManager(mContext)
    multiTypeAdapter.register(SimpleTxtItem())
    multiTypeAdapter.register(DividerItem())
    simpleRecycler.adapter = multiTypeAdapter
    simpleRefresh.setEnableLoadMore(false)
    simpleRefresh.setEnableRefresh(mType != 0)
    //simpleRefresh.setRefreshHeader(if (System.currentTimeMillis() % 2 == 0L) BlackLoadingHeader(mContext) else MidaMusicHeader(mContext))
    simpleRefresh.setOnRefreshListener {
      rxLifeScope.launch {
        withContext(Dispatchers.IO) { delay(2000) }.let {
          val list = mutableListOf<String>()
          val size = multiTypeAdapter.items.size / 2
          for (i in size + 10 downTo size + 1) list.add(
              "这是${
                when (mType) {
                  1 -> "Smart"
                  else -> "Normal"
                }
              }测试数据${i}"
          )
          val items = mutableListOf<Any>()
          list.forEach {
            items.add(SimpleTxtBean(txt = it).also { stb -> stb.gravity = Gravity.CENTER_VERTICAL })
            items.add(DividerBean(bgColor = Color.parseColor("#f3f3f3")))
          }
          items.addAll(multiTypeAdapter.items)
          multiTypeAdapter.items = items
          multiTypeAdapter.notifyItemRangeInserted(0, 10)
          simpleRecycler.smoothScrollToPosition(0)
          simpleRefresh.finishRefresh()
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
    val datas = mutableListOf<String>()
    for (i in 20 downTo 1) datas.add(
        "这是${
          when (mType) {
            1 -> "Smart"
            else -> "Normal"
          }
        }测试数据${i}"
    )
    val items = mutableListOf<Any>()
    datas.forEach {
      items.add(SimpleTxtBean(txt = it).also { stb -> stb.gravity = Gravity.CENTER_VERTICAL })
      items.add(DividerBean(bgColor = Color.parseColor("#f3f3f3")))
    }
    multiTypeAdapter.items = items
    multiTypeAdapter.notifyDataSetChanged()
  }
  //</editor-fold>
}
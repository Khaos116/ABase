package cc.abase.demo.component.simple

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.rxLifeScope
import cc.ab.base.ext.noMoreData
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.databinding.FragmentSimpleBinding
import cc.abase.demo.item.DividerItem
import cc.abase.demo.item.SimpleTxtItem
import cc.abase.demo.widget.SpeedLinearLayoutManager
import cc.abase.demo.widget.smart.MidaMusicHeader
import com.drakeet.multitype.MultiTypeAdapter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/19 14:44
 */
class SimpleFragment private constructor() : CommBindFragment<FragmentSimpleBinding>() {
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

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  override fun lazyInit() {
    viewBinding.simpleRecycler.layoutManager = SpeedLinearLayoutManager(mContext).also { it.MILLISECONDS_PER_INCH = 100f }
    multiTypeAdapter.register(SimpleTxtItem())
    multiTypeAdapter.register(DividerItem())
    viewBinding.simpleRecycler.adapter = multiTypeAdapter
    viewBinding.simpleRefresh.setEnableRefresh(mType != 0)
    viewBinding.simpleRefresh.setRefreshHeader(object : MidaMusicHeader(mContext) {
      override fun onFinish(refreshLayout: RefreshLayout, success: Boolean) = 0
    })
    viewBinding.simpleRefresh.setOnRefreshListener {
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
          multiTypeAdapter.notifyItemRangeInserted(0, 20)
          viewBinding.simpleRefresh.finishRefresh(0)
          viewBinding.simpleRecycler.smoothScrollToPosition(0)
          viewBinding.simpleRefresh.noMoreData()
        }
      }
    }
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
    viewBinding.simpleRefresh.noMoreData()
  }
  //</editor-fold>
}
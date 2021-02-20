package cc.abase.demo.component.decoration

import android.graphics.Color
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import cc.ab.base.ext.dp2Px
import cc.ab.base.ext.toast
import cc.abase.demo.R
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.item.SimpleTxtItem
import cc.abase.demo.widget.decoration.GridSpaceItemDecoration
import com.blankj.utilcode.util.KeyboardUtils
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.fragment_decoration.decorRecycler

/**
 * Description:
 * @author: CASE
 * @date: 2020/4/16 9:54
 */
class DecorationFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(type: Int): DecorationFragment {
      val fragment = DecorationFragment()
      fragment.mType = type
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  private var mType: Int = 0

  //因为奇数个数的Grid容易计算出错，所以我们采用奇数个数演示
  private var mSpanCount = 3

  //适配器
  private var multiTypeAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_decoration
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  override fun lazyInit() {
    multiTypeAdapter.register(SimpleTxtItem(height = 70.dp2Px(), bgColor = Color.CYAN).also { it.onItemClick = { bean -> bean.txt.toast() } })
    //页面重建View不再重新设置
    if (decorRecycler.itemDecorationCount == 0) {
      val decorator = if (mType != 8) {
        GridSpaceItemDecoration(20).setNoDragGridEdge(
            //前4个没有，后4个有
            mType >= 4,
            //没有-没有-有-有；没有-没有-有-有；
            mType == 2 || mType == 3 || mType == 6 || mType == 7,
            //没有-有-没有-有;没有-有-没有-有;
            mType == 1 || mType == 3 || mType == 5 || mType == 7)
      } else {
        mSpanCount = 6
        GridSpaceItemDecoration(20)
      }
      val layoutManager = GridLayoutManager(mContext, mSpanCount)
      val spanSizeLookup = object : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
          return if (mType != 8) 1 else when (position % 6) {
            0 -> 6
            1 -> 3
            2 -> 3
            3 -> 2
            4 -> 2
            else -> 2
          }
        }
      }
      layoutManager.spanSizeLookup = spanSizeLookup
      decorRecycler.layoutManager = layoutManager
      decorRecycler.adapter = multiTypeAdapter
      decorRecycler.addItemDecoration(decorator)
    }
    val items = mutableListOf<Any>()
    for (i in 1..30) {
      items.add(SimpleTxtBean(txt = "我是第${i}个元素").also { stb ->
        stb.textSizePx = 12.dp2Px() * 1f
        stb.gravity = Gravity.CENTER
      })
    }
    multiTypeAdapter.items = items
    multiTypeAdapter.notifyDataSetChanged()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="键盘内存释放">
  override fun onDestroyView() {
    super.onDestroyView()
    KeyboardUtils.fixSoftInputLeaks(mActivity)
  }
  //</editor-fold>
}
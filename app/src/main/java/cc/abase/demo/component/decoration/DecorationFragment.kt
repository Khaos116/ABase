package cc.abase.demo.component.decoration

import android.graphics.Color
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import cc.ab.base.ext.dp2px
import cc.ab.base.ext.toast
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.databinding.FragmentDecorationBinding
import cc.abase.demo.item.SimpleTxtItem
import cc.abase.demo.widget.decoration.GridItemDecoration
import com.blankj.utilcode.util.KeyboardUtils
import com.drakeet.multitype.MultiTypeAdapter

/**
 * Description:
 * @author: Khaos
 * @date: 2020/4/16 9:54
 */
class DecorationFragment private constructor() : CommBindFragment<FragmentDecorationBinding>() {
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

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  override fun lazyInit() {
    multiTypeAdapter.register(SimpleTxtItem(height = 70.dp2px(), bgColor = Color.CYAN).also { it.onItemClick = { bean -> bean.txt.toast() } })
    //页面重建View不再重新设置
    if (viewBinding.decorRecycler.itemDecorationCount == 0) {
      val decorator = if (mType != 8) {
        GridItemDecoration(
          24,
          //前4个没有，后4个有
          mType >= 4,
          //没有-没有-有-有；没有-没有-有-有；
          mType == 2 || mType == 3 || mType == 6 || mType == 7,
          //没有-有-没有-有;没有-有-没有-有;
          mType == 1 || mType == 3 || mType == 5 || mType == 7
        )
      } else {
        mSpanCount = 6
        GridItemDecoration(24)
      }
      val layoutManager = GridLayoutManager(mContext, mSpanCount)
      val list = mutableListOf(
        intArrayOf(1, 5),
        intArrayOf(2, 4),
        intArrayOf(3, 3)
      )
      val arr3 = list[((Math.random() * 100).toInt()) % list.size]
      list.remove(arr3)
      val arr2 = list[((Math.random() * 100).toInt()) % list.size]
      list.remove(arr2)
      val arr1 = list[0]
      val spanSizeLookup = object : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
          return if (mType != 8) 1 else when (position % 16) {
            0 -> 6
            1 -> arr1[0]
            2 -> arr1[1]
            3 -> arr2[0]
            4 -> arr2[1]
            5 -> arr3[0]
            6 -> arr3[1]
            13, 14, 15 -> 2
            else -> 1
          }
        }
      }
      layoutManager.spanSizeLookup = spanSizeLookup
      viewBinding.decorRecycler.layoutManager = layoutManager
      viewBinding.decorRecycler.adapter = multiTypeAdapter
      viewBinding.decorRecycler.addItemDecoration(decorator)
    }
    val items = mutableListOf<Any>()
    val rom = (Math.random() * 50).toInt()
    for (i in 0 until 30 + rom) {
      items.add(SimpleTxtBean(txt = "index=${i}").also { stb ->
        stb.textSizePx = 12.dp2px() * 1f
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
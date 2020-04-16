package cc.abase.demo.component.decoration

import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cc.ab.base.ext.toast
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.epoxy.item.simpleTextItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.widget.decoration.GridItemDecoration
import com.blankj.utilcode.util.SizeUtils
import kotlinx.android.synthetic.main.fragment_decoration.decorRecycler

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/4/16 9:54
 */
class DecorationFragment : CommFragment() {

  companion object {
    fun newInstance(type: Int): DecorationFragment {
      val fragment = DecorationFragment()
      fragment.mType = type
      return fragment
    }
  }

  private var mType: Int = 0

  //因为奇数个数的Grid容易计算出错，所以我们采用奇数个数演示
  private var mSpanCount = 3

  override val contentLayout = R.layout.fragment_decoration

  override fun initView(root: View?) {
    //页面重建View不再重新设置
    if (decorRecycler.itemDecorationCount == 0) {
      val layoutManager = GridLayoutManager(mContext, mSpanCount)
      epoxyController.spanCount = mSpanCount
      layoutManager.spanSizeLookup = epoxyController.spanSizeLookup
      decorRecycler.layoutManager = layoutManager
      decorRecycler.adapter = epoxyController.adapter
      val decorator = GridItemDecoration(mSpanCount, 20,
          //前4个没有，后4个有
          includeStartEnd = mType >= 4,
          //没有-没有-有-有；没有-没有-有-有；
          includeTop = mType == 2 || mType == 3 || mType == 6 || mType == 7,
          //没有-有-没有-有;没有-有-没有-有;
          includeBottom = mType == 1 || mType == 3 || mType == 5 || mType == 7)
      decorRecycler.addItemDecoration(decorator)
    }
  }

  override fun initData() {
    val datas = mutableListOf<String>()
    for (i in 1..30) datas.add("我是第${i}个元素")
    epoxyController.data = datas
  }

  private val epoxyController = MvRxEpoxyController<MutableList<String>> { list ->
    list.forEachIndexed { index, s ->
      simpleTextItem {
        id(index)
        gravity(Gravity.CENTER)
        bgColor(Color.parseColor("#550fff00"))
        heightDp(70f)
        textSizePx(SizeUtils.dp2px(12f) * 1f)
        bgColor(Color.CYAN)
        bgColorRes(null)
        msg(s)
        spanCount = mSpanCount
        onItemClick { mActivity.toast(s) }
        spanSizeOverride { _, _, _ -> 1 }
      }
    }
  }
}
package cc.abase.demo.item

import android.graphics.Typeface
import android.util.TypedValue
import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.SimpleTxtBean
import kotlinx.android.synthetic.main.item_simple_text.itemSimpleTv
import kotlinx.android.synthetic.main.item_simple_text.view.itemSimpleTv

/**
 * Author:CASE
 * Date:2021/1/2
 * Time:15:22
 */
class SimpleTxtItem(
    private val height: Int = -2,
    private val bgColor: Int = 0,
) : BaseItemView<SimpleTxtBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_simple_text
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: SimpleTxtBean): BaseViewHolder.() -> Unit = {
    itemSimpleTv.layoutParams.height = height
    if (bgColor != 0) itemView.itemSimpleTv.setBackgroundColor(bgColor)
    itemSimpleTv.setTextColor(item.textColor)
    itemSimpleTv.gravity = item.gravity
    itemSimpleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSizePx)
    itemSimpleTv.typeface = Typeface.defaultFromStyle(if (item.needBold) Typeface.BOLD else Typeface.NORMAL)
    itemSimpleTv.setPadding(item.paddingStartPx, item.paddingTopPx, item.paddingEndPx, item.paddingBottomPx)
    itemSimpleTv.text = item.txt
  }
  //</editor-fold>
}
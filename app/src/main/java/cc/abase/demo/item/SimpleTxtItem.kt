package cc.abase.demo.item

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.SimpleTxtBean
import kotlinx.android.synthetic.main.item_simple_text.view.itemSimpleTv

/**
 * Author:CASE
 * Date:2021/1/2
 * Time:15:22
 */
class SimpleTxtItem(
    private val onItemClick: ((item: SimpleTxtBean) -> Unit)? = null
) : BaseItemView<SimpleTxtBean>() {
  override fun layoutResId() = R.layout.item_simple_text

  override fun fillData(holder: ViewHolder, itemView: View, item: SimpleTxtBean) {
    itemView.itemSimpleTv.setTextColor(item.textColor)
    itemView.itemSimpleTv.gravity = item.gravity
    itemView.itemSimpleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSizePx)
    itemView.itemSimpleTv.typeface = Typeface.defaultFromStyle(if (item.needBold) Typeface.BOLD else Typeface.NORMAL)
    itemView.itemSimpleTv.setPadding(item.paddingStartPx, item.paddingTopPx, item.paddingEndPx, item.paddingBottomPx)
    itemView.itemSimpleTv.text = item.txt
    if (onItemClick == null) {
      itemView.pressEffectDisable()
    } else {
      itemView.pressEffectBgColor()
    }
    itemView.click { onItemClick?.invoke(item) }
  }
}
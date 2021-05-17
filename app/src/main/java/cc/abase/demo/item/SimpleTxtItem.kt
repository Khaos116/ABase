package cc.abase.demo.item

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.databinding.ItemSimpleTextBinding

/**
 * Author:CASE
 * Date:2021/1/2
 * Time:15:22
 */
class SimpleTxtItem(
    private val height: Int = -2,
    private val bgColor: Int = 0,
) : BaseBindItemView<SimpleTxtBean, ItemSimpleTextBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemSimpleTextBinding>, item: SimpleTxtBean) {
    val viewBinding = holder.viewBinding
    viewBinding.itemSimpleTv.layoutParams.height = height
    if (bgColor != 0) viewBinding.itemSimpleTv.setBackgroundColor(bgColor)
    viewBinding.itemSimpleTv.setTextColor(item.textColor)
    viewBinding.itemSimpleTv.gravity = item.gravity
    viewBinding.itemSimpleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSizePx)
    viewBinding.itemSimpleTv.typeface = Typeface.defaultFromStyle(if (item.needBold) Typeface.BOLD else Typeface.NORMAL)
    viewBinding.itemSimpleTv.setPadding(item.paddingStartPx, item.paddingTopPx, item.paddingEndPx, item.paddingBottomPx)
    viewBinding.itemSimpleTv.text = item.txt
  }
  //</editor-fold>
}
package cc.abase.demo.item

import android.graphics.Typeface
import android.util.TypedValue
import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.NoMoreBean
import kotlinx.android.synthetic.main.item_no_more.noMoreTv

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/6
 * @Time：21:27
 */
class NoMoreItem : BaseItemView<NoMoreBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_no_more
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: NoMoreBean): BaseViewHolder.() -> Unit = {
    noMoreTv.layoutParams.height = item.heightPx
    noMoreTv.setTextColor(item.textColor)
    noMoreTv.setBackgroundColor(item.bgColor)
    noMoreTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize)
    noMoreTv.setTypeface(Typeface.DEFAULT, if (item.bold) Typeface.BOLD else Typeface.NORMAL)
  }
  //</editor-fold>
}
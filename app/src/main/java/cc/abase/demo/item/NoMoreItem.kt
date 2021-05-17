package cc.abase.demo.item

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.NoMoreBean
import cc.abase.demo.databinding.ItemNoMoreBinding

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/6
 * @Time：21:27
 */
class NoMoreItem : BaseBindItemView<NoMoreBean, ItemNoMoreBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemNoMoreBinding>, item: NoMoreBean) {
    val viewBinding = holder.viewBinding
    viewBinding.noMoreTv.layoutParams.height = item.heightPx
    viewBinding.noMoreTv.setTextColor(item.textColor)
    viewBinding.noMoreTv.setBackgroundColor(item.bgColor)
    viewBinding.noMoreTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize)
    viewBinding.noMoreTv.setTypeface(Typeface.DEFAULT, if (item.bold) Typeface.BOLD else Typeface.NORMAL)
  }
  //</editor-fold>
}
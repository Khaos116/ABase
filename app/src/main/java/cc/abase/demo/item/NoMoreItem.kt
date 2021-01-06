package cc.abase.demo.item

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.NoMoreBean
import kotlinx.android.synthetic.main.item_no_more.view.noMoreTv

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/6
 * @Time：21:27
 */
class NoMoreItem : BaseItemView<NoMoreBean>() {
  override fun layoutResId() = R.layout.item_no_more

  override fun fillData(holder: ViewHolder, itemView: View, item: NoMoreBean) {
    itemView.noMoreTv.layoutParams.height = item.heightPx
    itemView.noMoreTv.setTextColor(item.textColor)
    itemView.noMoreTv.setBackgroundColor(item.bgColor)
    itemView.noMoreTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize)
    itemView.noMoreTv.setTypeface(Typeface.DEFAULT, if (item.bold) Typeface.BOLD else Typeface.NORMAL)
  }
}
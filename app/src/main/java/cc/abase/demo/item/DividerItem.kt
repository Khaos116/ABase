package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.DividerBean

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:35
 */
class DividerItem : BaseItemView<DividerBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_divider
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: RecyclerView.ViewHolder, itemView: View, item: DividerBean) {
    itemView.layoutParams.height = item.heightPx
    itemView.setBackgroundColor(item.bgColor)
  }
  //</editor-fold>
}
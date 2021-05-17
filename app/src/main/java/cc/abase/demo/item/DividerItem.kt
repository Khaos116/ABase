package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.databinding.ItemDividerBinding

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:35
 */
class DividerItem : BaseBindItemView<DividerBean, ItemDividerBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemDividerBinding>, item: DividerBean) {
    holder.itemView.layoutParams?.height = item.heightPx
    holder.itemView.setBackgroundColor(item.bgColor)
  }
  //</editor-fold>
}
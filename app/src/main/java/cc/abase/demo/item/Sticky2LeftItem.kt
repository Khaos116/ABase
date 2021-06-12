package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.databinding.ItemSticky2LeftBinding

/**
 * Author:Khaos
 * Date:2021/1/4
 * Time:14:42
 */
class Sticky2LeftItem : BaseBindItemView<String, ItemSticky2LeftBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemSticky2LeftBinding>, item: String) {
    val viewBinding = holder.viewBinding
    viewBinding.itemStickyUserName.text = item
  }
  //</editor-fold>
}
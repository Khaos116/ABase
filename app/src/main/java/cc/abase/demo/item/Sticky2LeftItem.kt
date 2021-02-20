package cc.abase.demo.item

import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import kotlinx.android.synthetic.main.item_sticky2_left.itemStickyUserName

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:14:42
 */
class Sticky2LeftItem : BaseItemView<String>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_sticky2_left
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: String): BaseViewHolder.() -> Unit = {
    itemStickyUserName.text = item
  }
  //</editor-fold>
}
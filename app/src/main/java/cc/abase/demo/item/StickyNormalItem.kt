package cc.abase.demo.item

import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.CityBean
import kotlinx.android.synthetic.main.item_sticky_normal.itemStickyNormalText

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:10:30
 */
class StickyNormalItem : BaseItemView<CityBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_sticky_normal
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: CityBean): BaseViewHolder.() -> Unit = {
    itemStickyNormalText.text = if (item.regionCode?.startsWith("+") == true) {
      String.format("%s  (%s)", item.regionFullName, item.regionCode)
    } else {
      item.regionFullName
    }
  }
  //</editor-fold>
}
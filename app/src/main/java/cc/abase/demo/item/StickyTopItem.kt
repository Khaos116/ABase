package cc.abase.demo.item

import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.ProvinceBean
import kotlinx.android.synthetic.main.item_sticky_top.itemStickyTopText

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:10:29
 */
class StickyTopItem : BaseItemView<ProvinceBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_sticky_top
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="填充数据">
  override fun fillData(item: ProvinceBean): BaseViewHolder.() -> Unit = {
    itemStickyTopText.text = item.regionName
  }
  //</editor-fold>
}
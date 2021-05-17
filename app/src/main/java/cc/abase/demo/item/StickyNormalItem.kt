package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.CityBean
import cc.abase.demo.databinding.ItemStickyNormalBinding

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:10:30
 */
class StickyNormalItem : BaseBindItemView<CityBean, ItemStickyNormalBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemStickyNormalBinding>, item: CityBean) {
    val viewBinding = holder.viewBinding
    viewBinding.itemStickyNormalText.text = if (item.regionCode?.startsWith("+") == true) {
      String.format("%s  (%s)", item.regionFullName, item.regionCode)
    } else {
      item.regionFullName
    }
  }
  //</editor-fold>
}
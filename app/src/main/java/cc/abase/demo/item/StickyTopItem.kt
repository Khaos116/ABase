package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.ProvinceBean
import cc.abase.demo.databinding.ItemStickyTopBinding

/**
 * Author:Khaos
 * Date:2021/1/4
 * Time:10:29
 */
class StickyTopItem : BaseBindItemView<ProvinceBean, ItemStickyTopBinding>() {
  //<editor-fold defaultstate="collapsed" desc="填充数据">
  override fun fillData(holder: BaseViewHolder<ItemStickyTopBinding>, item: ProvinceBean) {
    val viewBinding = holder.viewBinding
    viewBinding.itemStickyTopText.text = item.regionName
  }
  //</editor-fold>
}
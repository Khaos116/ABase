package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ext.setNumberNo00
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.gank.GankAndroidBean
import cc.abase.demo.databinding.ItemGankBinding

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:39
 */
class GankItem : BaseBindItemView<GankAndroidBean, ItemGankBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemGankBinding>, item: GankAndroidBean) {
    val viewBinding = holder.viewBinding
    viewBinding.itemGankTime.text = item.publishedAt
    viewBinding.itemGankTitle.text = item.title
    viewBinding.itemGankDes.text = item.desc
    viewBinding.itemGankSeeCounts.setNumberNo00(item.views.toDouble())
    viewBinding.itemGankStoreCounts.setNumberNo00(item.stars.toDouble())
    viewBinding.itemGankPraiseCounts.setNumberNo00(item.likeCounts.toDouble())
  }
  //</editor-fold>
}
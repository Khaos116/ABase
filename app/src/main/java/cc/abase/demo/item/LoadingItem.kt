package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.LoadingBean
import cc.abase.demo.databinding.ItemLoadingBinding

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:43
 */
class LoadingItem : BaseBindItemView<LoadingBean, ItemLoadingBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemLoadingBinding>, item: LoadingBean) {
    val viewBinding = holder.viewBinding
    if (!item.msg.isNullOrBlank()) viewBinding.itemLoadingTv.text = item.msg
  }
  //</editor-fold>
}
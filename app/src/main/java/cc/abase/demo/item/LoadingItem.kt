package cc.abase.demo.item

import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.LoadingBean
import cc.abase.demo.databinding.ItemLoadingBinding

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:43
 */
class LoadingItem(
  private val width: Int = 0,
  private val height: Int = 0,
) : BaseBindItemView<LoadingBean, ItemLoadingBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemLoadingBinding>, item: LoadingBean) {
    val viewBinding = holder.viewBinding
    viewBinding.root.layoutParams.let { lp ->
      if (item.width > 0) lp.width = item.width else if (width > 0) lp.width = width
      if (item.height > 0) lp.height = item.height else if (height > 0) lp.height = height
    }
    if (!item.msg.isNullOrBlank()) viewBinding.itemLoadingTv.text = item.msg
  }
  //</editor-fold>
}
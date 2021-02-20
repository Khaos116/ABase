package cc.abase.demo.item

import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.LoadingBean
import kotlinx.android.synthetic.main.item_loading.itemLoadingTv

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:43
 */
class LoadingItem : BaseItemView<LoadingBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_loading
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: LoadingBean): BaseViewHolder.() -> Unit = {
    if (!item.msg.isNullOrBlank()) itemLoadingTv.text = item.msg
  }
  //</editor-fold>
}
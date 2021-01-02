package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.LoadingBean
import kotlinx.android.synthetic.main.item_loading.view.itemLoadingTv

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
  override fun fillData(holder: ViewHolder, itemView: View, item: LoadingBean) {
    if (!item.msg.isNullOrBlank()) itemView.itemLoadingTv.text = item.msg
  }
  //</editor-fold>
}
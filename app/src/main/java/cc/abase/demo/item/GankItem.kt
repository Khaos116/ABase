package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.gank.GankAndroidBean
import kotlinx.android.synthetic.main.item_gank.view.*

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:39
 */
class GankItem(
    private val onItemClick: ((item: GankAndroidBean) -> Unit)? = null
) : BaseItemView<GankAndroidBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_gank
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: GankAndroidBean) {
    itemView.itemGankTime.text = item.publishedAt
    itemView.itemGankTitle.text = item.title
    itemView.itemGankDes.text = item.desc
    itemView.itemGankSeeCounts.setNumberNo00(item.views.toDouble())
    itemView.itemGankStoreCounts.setNumberNo00(item.stars.toDouble())
    itemView.itemGankPraiseCounts.setNumberNo00(item.likeCounts.toDouble())
    if (onItemClick != null) {
      itemView.pressEffectBgColor()
      itemView.click { onItemClick.invoke(item) }
    } else {
      itemView.setOnClickListener(null)
      itemView.pressEffectDisable()
    }
  }
  //</editor-fold>
}
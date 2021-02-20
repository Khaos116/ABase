package cc.abase.demo.item

import cc.ab.base.ext.setNumberNo00
import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.gank.GankAndroidBean
import kotlinx.android.synthetic.main.item_gank.*

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:39
 */
class GankItem : BaseItemView<GankAndroidBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_gank
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: GankAndroidBean): BaseViewHolder.() -> Unit = {
    itemGankTime.text = item.publishedAt
    itemGankTitle.text = item.title
    itemGankDes.text = item.desc
    itemGankSeeCounts.setNumberNo00(item.views.toDouble())
    itemGankStoreCounts.setNumberNo00(item.stars.toDouble())
    itemGankPraiseCounts.setNumberNo00(item.likeCounts.toDouble())
  }
  //</editor-fold>
}
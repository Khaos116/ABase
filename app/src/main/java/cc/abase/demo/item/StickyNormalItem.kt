package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ext.click
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.CityBean
import kotlinx.android.synthetic.main.item_sticky_normal.view.itemStickyNormalText

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:10:30
 */
class StickyNormalItem(
    var onItemClick: ((city: CityBean) -> Unit)? = null
) : BaseItemView<CityBean>() {

  override fun layoutResId() = R.layout.item_sticky_normal

  override fun fillData(holder: ViewHolder, itemView: View, item: CityBean) {
    itemView.itemStickyNormalText.text = if (item.regionCode?.startsWith("+") == true) {
      String.format("%s  (%s)", item.regionFullName, item.regionCode)
    } else {
      item.regionFullName
    }
    itemView.itemStickyNormalText.click { onItemClick?.invoke(item) }
  }
}
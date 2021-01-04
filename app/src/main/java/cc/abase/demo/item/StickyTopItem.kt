package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ext.click
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.ProvinceBean
import kotlinx.android.synthetic.main.item_sticky_top.view.itemStickyTopText

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:10:29
 */
class StickyTopItem(
    var onItemClick: ((province: ProvinceBean) -> Unit)? = null
) : BaseItemView<ProvinceBean>() {

  override fun layoutResId() = R.layout.item_sticky_top

  override fun fillData(holder: ViewHolder, itemView: View, item: ProvinceBean) {
    itemView.itemStickyTopText.text = item.regionName
    itemView.itemStickyTopText.click { onItemClick?.invoke(item) }
  }
}
package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import kotlinx.android.synthetic.main.item_sticky2_left.view.itemStickyUserName

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:14:42
 */
class Sticky2LeftItem : BaseItemView<String>() {
  override fun layoutResId() = R.layout.item_sticky2_left

  override fun fillData(holder: ViewHolder, itemView: View, item: String) {
    itemView.itemStickyUserName.text = item
  }
}
package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.UserScoreBean
import kotlinx.android.synthetic.main.item_sticky2_bottom.view.*

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:14:44
 */
class Sticky2RightItem : BaseItemView<UserScoreBean>() {
  override fun layoutResId() = R.layout.item_sticky2_bottom

  override fun fillData(holder: ViewHolder, itemView: View, item: UserScoreBean) {
    item.scores.forEachIndexed { index, i ->
      when (index) {
        0 -> itemView.itemStickyUserScore1
        1 -> itemView.itemStickyUserScore2
        2 -> itemView.itemStickyUserScore3
        3 -> itemView.itemStickyUserScore4
        4 -> itemView.itemStickyUserScore5
        5 -> itemView.itemStickyUserScore6
        6 -> itemView.itemStickyUserScore7
        7 -> itemView.itemStickyUserScore8
        8 -> itemView.itemStickyUserScore9
        else -> null
      }?.text = i.toString()
      itemView.itemStickyUserScoreTotal.text = (item.scores.sum()).toString()
    }
  }
}
package cc.abase.demo.item

import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.UserScoreBean
import kotlinx.android.synthetic.main.item_sticky2_bottom.*

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:14:44
 */
class Sticky2RightItem : BaseItemView<UserScoreBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_sticky2_bottom
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: UserScoreBean): BaseViewHolder.() -> Unit = {
    item.scores.forEachIndexed { index, i ->
      when (index) {
        0 -> itemStickyUserScore1
        1 -> itemStickyUserScore2
        2 -> itemStickyUserScore3
        3 -> itemStickyUserScore4
        4 -> itemStickyUserScore5
        5 -> itemStickyUserScore6
        6 -> itemStickyUserScore7
        7 -> itemStickyUserScore8
        8 -> itemStickyUserScore9
        else -> null
      }?.text = i.toString()
      itemStickyUserScoreTotal.text = (item.scores.sum()).toString()
    }
  }
  //</editor-fold>
}
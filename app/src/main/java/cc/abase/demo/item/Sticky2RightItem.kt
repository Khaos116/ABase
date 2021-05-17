package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.UserScoreBean
import cc.abase.demo.databinding.ItemSticky2BottomBinding

/**
 * Author:CASE
 * Date:2021/1/4
 * Time:14:44
 */
class Sticky2RightItem : BaseBindItemView<UserScoreBean, ItemSticky2BottomBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemSticky2BottomBinding>, item: UserScoreBean) {
    val viewBinding = holder.viewBinding
    item.scores.forEachIndexed { index, i ->
      when (index) {
        0 -> viewBinding.itemStickyUserScore1
        1 -> viewBinding.itemStickyUserScore2
        2 -> viewBinding.itemStickyUserScore3
        3 -> viewBinding.itemStickyUserScore4
        4 -> viewBinding.itemStickyUserScore5
        5 -> viewBinding.itemStickyUserScore6
        6 -> viewBinding.itemStickyUserScore7
        7 -> viewBinding.itemStickyUserScore8
        8 -> viewBinding.itemStickyUserScore9
        else -> null
      }?.text = i.toString()
      viewBinding.itemStickyUserScoreTotal.text = (item.scores.sum()).toString()
    }
  }
  //</editor-fold>
}
package cc.abase.demo.epoxy.item

import android.view.View
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.UserScoreBean
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_sticky2_bottom.view.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/4/21 14:51
 */
@EpoxyModelClass(layout = R.layout.item_sticky2_bottom)
abstract class Sticky2BottomItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  @EpoxyAttribute
  var bean: UserScoreBean? = null

  override fun onBind(itemView: View) {
    bean?.scores?.forEachIndexed { index, i ->
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
      itemView.itemStickyUserScoreTotal.text = (bean?.scores?.sum() ?: 0).toInt().toString()
    }
  }
}
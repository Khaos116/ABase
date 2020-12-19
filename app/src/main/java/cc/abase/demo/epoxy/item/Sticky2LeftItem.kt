package cc.abase.demo.epoxy.item

import android.view.View
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_sticky2_left.view.itemStickyUserName

/**
 * Description:
 * @author: CASE
 * @date: 2020/4/21 14:47
 */
@EpoxyModelClass(layout = R.layout.item_sticky2_left)
abstract class Sticky2LeftItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  @EpoxyAttribute
  var name: String? = null

  override fun onBind(itemView: View) {
    itemView.itemStickyUserName.text = name
  }
}
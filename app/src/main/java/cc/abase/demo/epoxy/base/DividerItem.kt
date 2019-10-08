package cc.abase.demo.epoxy.base

import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/1 16:58
 */
@EpoxyModelClass(layout = R.layout.item_comm_divider)
abstract class DividerItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  @EpoxyAttribute
  var heightPx: Int? = null

  init {
    heightPx = 1
  }

  override fun bind(holder: BaseEpoxyHolder) {
    super.bind(holder)
    if (heightDp == null) {
      heightPx?.let {
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = it
      }
    }
  }
}
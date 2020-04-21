package cc.abase.demo.epoxy.item

import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import com.airbnb.epoxy.EpoxyModelClass

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/4/21 14:46
 */
@EpoxyModelClass(layout = R.layout.item_sticky2_top)
abstract class Sticky2TopItem : BaseEpoxyModel<BaseEpoxyHolder>()
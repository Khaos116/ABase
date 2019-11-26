package cc.abase.demo.epoxy.item

import android.view.View
import cc.ab.base.ext.click
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import cc.abase.demo.repository.bean.local.CityBean
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_sticky_normal.view.itemStickyNormalText

// This more traditional style uses an Epoxy view holder pattern.
// The KotlinHolder is used to cache the view look ups, but uses property delegates to simplify it.
// The annotations allow for code generation of a subclass, which has equals/hashcode, and some other
// helpers. An extension function is also generated to make it easier to use this in an EpoxyController.
@EpoxyModelClass(layout = R.layout.item_sticky_normal)
abstract class StickyNormalItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  @EpoxyAttribute
  var city: CityBean? = null
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onItemClick: ((city: CityBean) -> Unit)? = null

  override fun onBind(itemView: View) {
    city?.let { data ->
      itemView.itemStickyNormalText.text = data.regionFullName
      itemView.itemStickyNormalText.click { onItemClick?.invoke(data) }
    }
  }
}
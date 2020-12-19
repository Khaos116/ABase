package cc.abase.demo.epoxy.item

import android.view.View
import android.widget.ImageView
import cc.ab.base.ext.*
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.blankj.utilcode.util.SizeUtils
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.item_drag.view.*

/**
 * Description:
 * @author: CASE
 * @date: 19-5-11 下午6:01
 */
@EpoxyModelClass(layout = R.layout.item_drag)
abstract class DragImgItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onItemClick: ((bean: LocalMedia?, iv: ImageView?) -> Unit)? = null
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onClickDel: ((bean: LocalMedia?) -> Unit)? = null
  @EpoxyAttribute
  var localMedia: LocalMedia? = null
  @EpoxyAttribute
  var roundRadiusDp: Float = 8f
  var sizeInt: Int = SizeUtils.dp2px(96f)

  override fun onBind(itemView: View) {
    itemView.layoutParams.width = sizeInt
    itemView.layoutParams.height = sizeInt
    //解决sketch加载闪动的问题
    itemView.itemDragImgCover.layoutParams?.width = sizeInt
    itemView.itemDragImgCover.layoutParams?.height = sizeInt
    setListener(itemView)
    val media = localMedia
    if (media != null) {
      itemView.itemDragImgCover.loadCorner(
          media.path ?: "",
          holderRes = R.drawable.place_holder_square_loading,
          errorRes = R.drawable.place_holder_square_fail,
          cornerDP = roundRadiusDp
      )
      itemView.itemDragImgDel.visibility = View.VISIBLE
      itemView.itemDragImgAdd.visibility = View.GONE
      itemView.itemDragImgCover.background = null
      itemView.pressEffectDisable()
    } else {
      itemView.itemDragImgCover.setImageResource(0)
      itemView.itemDragImgDel.visibility = View.GONE
      itemView.itemDragImgAdd.visibility = View.VISIBLE
      itemView.pressEffectAlpha()
    }
  }

  private fun setListener(view: View) {
    if (onItemClick == null) {
      view.setOnClickListener(null)
    } else {
      view.click { onItemClick?.invoke(localMedia, view.itemDragImgCover) }
    }
    view.itemDragImgDel.click { onClickDel?.invoke(localMedia) }
    if (onClickDel != null) {
      view.itemDragImgDel.pressEffectAlpha()
    } else {
      view.itemDragImgDel.pressEffectDisable()
    }
  }
}
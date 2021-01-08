package cc.abase.demo.item

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import kotlinx.android.synthetic.main.item_nine_img.view.*

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:44
 */
class NineImgItem(
    private val onItemClick: ((item: String, position: Int, iv: ImageView) -> Unit)? = null,
    private val onDelClick: ((item: String, position: Int, iv: ImageView) -> Unit)? = null,
) : BaseItemView<String>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_nine_img
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: String) {
    if (item.isBlank()) {
      holder.itemView.itemNineImgIv.invisible()
      holder.itemView.itemNineImgAdd.visible()
      holder.itemView.itemNineImgDel.gone()
    } else {
      holder.itemView.itemNineImgIv.visible()
      holder.itemView.itemNineImgAdd.gone()
      holder.itemView.itemNineImgDel.visibleGone(onDelClick != null)
      holder.itemView.itemNineImgIv.loadImgSquare(item)
    }
    if (onDelClick != null) {
      itemView.itemNineImgDel.pressEffectAlpha(0.9f)
      itemView.itemNineImgDel.click { onDelClick.invoke(item, holder.layoutPosition, holder.itemView.itemNineImgIv) }
    } else {
      itemView.itemNineImgDel.setOnClickListener(null)
      itemView.itemNineImgDel.pressEffectDisable()
    }
    if (onItemClick != null) {
      itemView.pressEffectAlpha(0.9f)
      itemView.click { onItemClick.invoke(item, holder.layoutPosition, holder.itemView.itemNineImgIv) }
    } else {
      itemView.setOnClickListener(null)
      itemView.pressEffectDisable()
    }
  }
  //</editor-fold>
}
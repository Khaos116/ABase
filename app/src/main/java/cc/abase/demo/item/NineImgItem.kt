package cc.abase.demo.item

import android.widget.ImageView
import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import kotlinx.android.synthetic.main.item_nine_img.*

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:44
 */
class NineImgItem(
    private val onDelClick: ((item: String, position: Int, iv: ImageView) -> Unit)? = null,
    private val onItemChildClick: ((item: String, position: Int, iv: ImageView) -> Unit)? = null,
) : BaseItemView<String>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_nine_img
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: String): BaseViewHolder.() -> Unit = {
    if (item.isBlank() && onDelClick != null) {
      itemNineImgIv.invisible()
      itemNineImgAdd.visible()
      itemNineImgDel.gone()
    } else {
      itemNineImgIv.visible()
      itemNineImgAdd.gone()
      itemNineImgDel.visibleGone(onDelClick != null)
      itemNineImgIv.loadImgSquare(item)
    }
    if (onDelClick != null) {
      itemNineImgDel.pressEffectAlpha(0.9f)
      itemNineImgDel.click { onDelClick.invoke(item, layoutPosition, itemNineImgIv) }
    } else {
      itemNineImgDel.setOnClickListener(null)
      itemNineImgDel.pressEffectDisable()
    }
    if (onItemChildClick != null) {
      itemView.pressEffectAlpha(0.9f)
      itemView.click { onItemChildClick.invoke(item, layoutPosition, itemNineImgIv) }
    } else {
      itemView.setOnClickListener(null)
      itemView.pressEffectDisable()
    }
  }
  //</editor-fold>
}
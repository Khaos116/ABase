package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.databinding.ItemNineImgBinding

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:44
 */
class NineImgItem(
    private val onDelClick: ((item: String, position: Int, iv: ImageView) -> Unit)? = null,
    private val onItemChildClick: ((item: String, position: Int, iv: ImageView) -> Unit)? = null,
) : BaseBindItemView<String, ItemNineImgBinding>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun loadViewBinding(inflater: LayoutInflater, parent: ViewGroup) = ItemNineImgBinding.inflate(inflater, parent, false)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemNineImgBinding>, item: String) {
    val viewBinding = holder.viewBinding
    if (item.isBlank() && onDelClick != null) {
      viewBinding.itemNineImgIv.invisible()
      viewBinding.itemNineImgAdd.visible()
      viewBinding.itemNineImgDel.gone()
    } else {
      viewBinding.itemNineImgIv.visible()
      viewBinding.itemNineImgAdd.gone()
      viewBinding.itemNineImgDel.visibleGone(onDelClick != null)
      viewBinding.itemNineImgIv.loadImgSquare(item)
    }
    if (onDelClick != null) {
      viewBinding.itemNineImgDel.pressEffectAlpha(0.9f)
      viewBinding.itemNineImgDel.click { onDelClick.invoke(item, mLayoutPosition, viewBinding.itemNineImgIv) }
    } else {
      viewBinding.itemNineImgDel.setOnClickListener(null)
      viewBinding.itemNineImgDel.pressEffectDisable()
    }
    if (onItemChildClick != null) {
      holder.itemView.pressEffectAlpha(0.9f)
      holder.itemView.click { onItemChildClick.invoke(item, mLayoutPosition, viewBinding.itemNineImgIv) }
    } else {
      holder.itemView.setOnClickListener(null)
      holder.itemView.pressEffectDisable()
    }
  }
  //</editor-fold>
}
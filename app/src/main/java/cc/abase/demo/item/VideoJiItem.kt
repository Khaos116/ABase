package cc.abase.demo.item

import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.databinding.ItemVideoJiBinding

/**
 * @Description
 * @Author：Khaos
 * @Date：2021-06-15
 * @Time：12:27
 */
class VideoJiItem(
  private val onItemPlayClick: ((pair: Pair<String, String>) -> Unit)? = null
) : BaseBindItemView<Pair<String, String>, ItemVideoJiBinding>() {

  override fun fillData(holder: BaseViewHolder<ItemVideoJiBinding>, item: Pair<String, String>) {
    val ji = item.second.findBySymbols()
    holder.viewBinding.root.pressEffectAlpha()
    holder.viewBinding.root.text = if (ji.trim().isBlank()) item.second else ji
    holder.viewBinding.root.click { onItemPlayClick?.invoke(item) }
  }
}
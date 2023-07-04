package cc.abase.demo.item

import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.databinding.ItemListVideoBinding

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/6
 * @Time：21:12
 */
class VideoListItem(
  private val onItemPlayClick: ((videoBean: VideoBean) -> Unit)? = null
) : BaseBindItemView<VideoBean, ItemListVideoBinding>() {
  //<editor-fold defaultstate="collapsed" desc="注释">
  override fun fillData(holder: BaseViewHolder<ItemListVideoBinding>, item: VideoBean) {
    val viewBinding = holder.viewBinding
    viewBinding.itemPlayPagerThumb.loadCoilSimpleUrl(url = item.url ?: "", holderRatio = 16f / 9)
    viewBinding.itemVideoListTitle.text = item.title
    viewBinding.itemPlayPagerBtn.pressEffectAlpha(0.9f)
    viewBinding.itemPlayPagerBtn.click { onItemPlayClick?.invoke(item) }
  }
  //</editor-fold>
}
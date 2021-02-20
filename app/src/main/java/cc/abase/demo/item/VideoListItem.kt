package cc.abase.demo.item

import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.VideoBean
import kotlinx.android.synthetic.main.item_list_video.*

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/6
 * @Time：21:12
 */
class VideoListItem(
    private val onItemPlayClick: ((videoBean: VideoBean) -> Unit)? = null
) : BaseItemView<VideoBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_list_video
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="注释">
  override fun fillData(item: VideoBean): BaseViewHolder.() -> Unit = {
    itemPlayPagerThumb.loadNetVideoCover(item.url ?: "")
    itemVideoListTitle.text = item.title
    itemPlayPagerBtn.pressEffectAlpha(0.9f)
    itemPlayPagerBtn.click { onItemPlayClick?.invoke(item) }
  }
  //</editor-fold>
}
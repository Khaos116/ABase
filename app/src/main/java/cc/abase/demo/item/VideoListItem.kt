package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.VideoBean
import kotlinx.android.synthetic.main.item_list_video.view.*

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/6
 * @Time：21:12
 */
class VideoListItem(
    private val onItemPlayClick: ((videoBean: VideoBean) -> Unit)? = null
) : BaseItemView<VideoBean>() {
  override fun layoutResId() = R.layout.item_list_video

  override fun fillData(holder: ViewHolder, itemView: View, item: VideoBean) {
    itemView.itemPlayPagerThumb.loadNetVideoCover(item.url ?: "")
    itemView.itemVideoListTitle.text = item.title
    itemView.itemPlayPagerBtn.pressEffectAlpha(0.9f)
    itemView.itemPlayPagerBtn.click { onItemPlayClick?.invoke(item) }
  }
}
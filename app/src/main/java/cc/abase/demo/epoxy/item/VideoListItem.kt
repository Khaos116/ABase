package cc.abase.demo.epoxy.item

import android.annotation.SuppressLint
import android.view.View
import cc.ab.base.ext.*
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_list_video.view.*

/**
 * Description:
 * @author: CASE
 * @date: 2019/12/12 12:12
 */
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.item_list_video)
abstract class VideoListItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  @EpoxyAttribute
  var videoBean: VideoBean? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onItemPlayClick: ((videoBean: VideoBean) -> Unit)? = null

  override fun onBind(itemView: View) {
    videoBean?.let { data ->
      itemView.itemPlayPagerThumb.loadNetVideoCover(data.url ?: "")
      itemView.itemVideoListTitle.text = data.title
      itemView.itemPlayPagerBtn.pressEffectAlpha(0.9f)
      itemView.itemPlayPagerBtn.click { onItemPlayClick?.invoke(data) }
    }
  }
}
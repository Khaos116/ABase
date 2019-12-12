package cc.abase.demo.epoxy.item

import android.view.View
import cc.ab.base.ext.click
import cc.ab.base.ext.load
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import cc.abase.demo.repository.bean.local.VideoBean
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.dkplayer_layout_prepare_view.view.thumb
import kotlinx.android.synthetic.main.item_list_video.view.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/12/12 12:12
 */
@EpoxyModelClass(layout = R.layout.item_list_video)
abstract class VideoListItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  @EpoxyAttribute
  var videoBean: VideoBean? = null
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onItemClick: ((videoBean: VideoBean) -> Unit)? = null
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onContainerClick: ((videoBean: VideoBean) -> Unit)? = null

  override fun onBind(itemView: View) {
    videoBean?.let { data ->
      itemView.itemVideoListTitle.text = data.title
      itemView.itemVideoPrepareView.thumb.load(
        url = data.thumb,
        holderRes = R.drawable.place_holder_video_16_9,
        errorRes = R.drawable.error_holder_video_16_9
      )
      itemView.click { onItemClick?.invoke(data) }
      itemView.itemVideoContainer.click { onContainerClick?.invoke(data) }
    }
  }
}
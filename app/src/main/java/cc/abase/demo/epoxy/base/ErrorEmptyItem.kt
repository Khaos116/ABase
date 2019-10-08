package cc.abase.demo.epoxy.base

import android.view.View
import androidx.annotation.DrawableRes
import cc.ab.base.ext.click
import cc.ab.base.ext.visibleGone
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.utils.NetUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_comm_error_empty.view.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/1 16:34
 */
@EpoxyModelClass(layout = R.layout.item_comm_error_empty)
abstract class ErrorEmptyItem : BaseEpoxyModel<BaseEpoxyHolder>() {

  //图片的资源ID
  @EpoxyAttribute
  @DrawableRes
  var imageResource: Int? = null

  //提示的文字，不设置就没有文字
  @EpoxyAttribute
  var tipsText: CharSequence? = null
  @EpoxyAttribute
  var tipsColor: Int? = null

  //重试点击事件
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onRetryClick: (() -> Unit)? = null

  override fun onBind(itemView: View) {
    super.onBind(itemView)
    imageResource?.let { itemView.errorEmptyIv.setImageResource(it) }
    tipsColor?.let { itemView.errorEmptyTips.setTextColor(it) }
    tipsText?.let { itemView.errorEmptyTips.text = it }
    itemView.errorEmptyIv.visibleGone(imageResource != null)
    itemView.errorEmptyTips.visibleGone(!tipsText.isNullOrBlank())
    itemView.errorEmptyRoot.click {
      if (NetUtils.instance.checkToast()) {
        onRetryClick?.invoke()
      }
    }
  }
}
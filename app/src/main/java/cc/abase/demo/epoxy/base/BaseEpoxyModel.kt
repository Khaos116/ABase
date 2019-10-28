package cc.abase.demo.epoxy.base

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import cc.ab.base.ext.getColorRes
import cc.ab.base.ui.holder.BaseEpoxyHolder
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.blankj.utilcode.util.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/1 16:36
 */
abstract class BaseEpoxyModel<T : BaseEpoxyHolder> :
    EpoxyModelWithHolder<T>() {

  @EpoxyAttribute
  var topMarginDp: Float? = null
  @EpoxyAttribute
  var leftMarginDp: Float? = null
  @EpoxyAttribute
  var rightMarginDp: Float? = null
  @EpoxyAttribute
  var bottomMarginDp: Float? = null

  @EpoxyAttribute
  var heightDp: Float? = null
  @EpoxyAttribute
  var widthDp: Float? = null

  @EpoxyAttribute
  @ColorInt
  var bgColor: Int? = null

  @EpoxyAttribute
  @ColorRes
  var bgColorRes: Int? = null

  @EpoxyAttribute
  var bgDrawable: Drawable? = null

  @EpoxyAttribute
  @DrawableRes
  var bgDrawableRes: Int? = null

  override fun bind(holder: T) {
    super.bind(holder)
    setSize(holder.itemView)
    setBgColor(holder.itemView)
    setBgDrawable(holder.itemView)
    setMargin(holder.itemView)
    onBind(holder)
    onBind(holder.itemView)
  }

  private fun setSize(view: View) {
    if (heightDp == null && widthDp == null) {
      return
    }
    val layoutParams = view.layoutParams
    heightDp?.let {
      layoutParams.height = SizeUtils.dp2px(it)
    }
    widthDp?.let {
      layoutParams.width = SizeUtils.dp2px(it)
    }
    view.layoutParams = layoutParams
  }

  private fun setBgColor(view: View) {
    bgColor?.let {
      view.setBackgroundColor(it)
    }
    bgColorRes?.let {
      view.setBackgroundColor(ColorUtils.getColor(it))
    }
  }

  private fun setBgDrawable(view: View) {
    bgDrawable?.let {
      view.background = it
    }
    bgDrawableRes?.let {
      view.setBackgroundResource(it)
    }
  }

  private fun setMargin(view: View) {
    if (leftMarginDp == null
        && topMarginDp == null
        && rightMarginDp == null
        && bottomMarginDp == null
    ) {
      return
    }
    if (view.layoutParams is ViewGroup.MarginLayoutParams) {
      val marginLayoutParams = (view.layoutParams as ViewGroup.MarginLayoutParams)
      val left = if (leftMarginDp == null) {
        marginLayoutParams.leftMargin
      } else {
        SizeUtils.dp2px(leftMarginDp!!)
      }
      val top = if (topMarginDp == null) {
        marginLayoutParams.topMargin
      } else {
        SizeUtils.dp2px(topMarginDp!!)
      }
      val right = if (rightMarginDp == null) {
        marginLayoutParams.rightMargin
      } else {
        SizeUtils.dp2px(rightMarginDp!!)
      }
      val bottom = if (bottomMarginDp == null) {
        marginLayoutParams.bottomMargin
      } else {
        SizeUtils.dp2px(bottomMarginDp!!)
      }
      marginLayoutParams.setMargins(
          left,
          top,
          right,
          bottom
      )
      view.layoutParams = marginLayoutParams
    }
  }

  /**
   * 需要使用自定义Holder时重写该方法
   */
  open fun onBind(holder: T) {}

  /**
   * 不需要使用自定义Holder是只是使用ItemView重写该方法
   * 使用anko的话简化步骤，参数view就是holder的ItemView
   */
  open fun onBind(itemView: View) {}
}
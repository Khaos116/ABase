package cc.ab.base.widget.roundlayout.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import cc.ab.base.R
import cc.ab.base.widget.roundlayout.abs.GeneralRoundViewImpl
import cc.ab.base.widget.roundlayout.abs.IRoundView
import com.blankj.utilcode.util.SizeUtils

/**
 * 默认5dp圆角
 * Author:CASE
 * Date:2020-10-12
 * Time:17:48
 */
class GeneralRoundImageView(
    context: Context,
    attrs: AttributeSet?
) : AppCompatImageView(context, attrs), IRoundView {
  private var generalRoundViewImpl: GeneralRoundViewImpl = GeneralRoundViewImpl(
      this, context, attrs,
      R.styleable.GeneralRoundImageView,
      R.styleable.GeneralRoundImageView_corner_radius
  ).apply { setCornerRadius(SizeUtils.dp2px(5f) * 1f) }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    generalRoundViewImpl.onLayout(changed, left, top, right, bottom)
  }

  override fun dispatchDraw(canvas: Canvas?) {
    generalRoundViewImpl.beforeDispatchDraw(canvas)
    super.dispatchDraw(canvas)
    generalRoundViewImpl.afterDispatchDraw(canvas)
  }

  override fun setCornerRadius(cornerRadius: Float) {
    generalRoundViewImpl.setCornerRadius(cornerRadius)
  }
}
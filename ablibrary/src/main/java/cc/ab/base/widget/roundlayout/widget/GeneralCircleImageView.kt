package cc.ab.base.widget.roundlayout.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import cc.ab.base.R
import cc.ab.base.widget.roundlayout.abs.GeneralRoundViewImpl
import cc.ab.base.widget.roundlayout.abs.IRoundView

/**
 * Author:CASE
 * Date:2020-10-12
 * Time:17:48
 */
class GeneralCircleImageView(
    context: Context,
    attrs: AttributeSet?
) : AppCompatImageView(context, attrs), IRoundView {
  private var generalRoundViewImpl: GeneralRoundViewImpl = GeneralRoundViewImpl(
      this, context, attrs,
      R.styleable.GeneralCircleImageView,
      R.styleable.GeneralCircleImageView_corner_radius
  )

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    generalRoundViewImpl.onLayout(changed, left, top, right, bottom)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    generalRoundViewImpl.setCornerRadius(w / 2f)
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
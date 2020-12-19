package cc.abase.demo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import cc.abase.demo.R

/**
 * Description:
 * @author: CASE
 * @date: 2020/4/21 18:42
 */
class LineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
  private val mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)

  init {
    mLinePaint.color = ContextCompat.getColor(context, R.color.gray)
    mLinePaint.strokeWidth = 1f
    mLinePaint.style = Paint.Style.FILL
    mLinePaint.isAntiAlias = true
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    canvas?.let {
      canvas.drawLine(0f, 0f, width * 1f, height * 1f, mLinePaint);
    }
  }
}
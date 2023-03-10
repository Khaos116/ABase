package cc.abase.demo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R

/**
 * https://blog.csdn.net/weixin_42279592/article/details/117277853
 */
class MyFlashTextView @kotlin.jvm.JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private val mMatrix = Matrix()
  private var mTransX = 0f
  private var mFlashColor1 = Color.parseColor("#b29873")
  private var mFlashColor2 = Color.parseColor("#f8b923")
  private var mFlashColor3 = Color.parseColor("#f36f23")
  private var mFlashColor4 = Color.parseColor("#C62430")
  private var mViewWidth = 0
  private var mRunnable: Runnable = Runnable { postInvalidate() }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绘制">
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    if (width > 0 && text.toString().contains(R.string.加载中.xmlToString(), true)) {
      if (mViewWidth != width) {
        mViewWidth = width
        paint.shader = LinearGradient(
          0f, 0f, mViewWidth * 1f, 0f,
          intArrayOf(currentTextColor, mFlashColor1, mFlashColor2, mFlashColor3, mFlashColor4, currentTextColor),
          floatArrayOf(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f),
          Shader.TileMode.CLAMP
        )
      }
      mTransX += mViewWidth / 5f
      if (mTransX > 2 * mViewWidth) {
        mTransX = -1f * mViewWidth
      }
      //关键代码通过矩阵的平移实现
      mMatrix.setTranslate(mTransX, 0f)
      paint.shader?.setLocalMatrix(mMatrix)
      removeCallbacks(mRunnable)
      postDelayed(mRunnable, 120)
    }
  }
  //</editor-fold>
}
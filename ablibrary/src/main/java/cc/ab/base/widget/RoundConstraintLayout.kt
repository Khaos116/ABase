package cc.ab.base.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import cc.ab.base.R

/**
 * 优势：四个角可以单独配置也可以统一配置
 * 劣势：不抗锯齿
 * 其他：如果四个角一致，使用GeneralRoundConstraintLayout即可
 * 原文说明：https://blog.csdn.net/ldld1717/article/details/106652831
 * author: lllddd
 * created on: 2020/6/9 10:55
 * description: https://github.com/leidongld/RoundCornerDemmo
 */
class RoundConstraintLayout @JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0) : ConstraintLayout(c, a, d) {
  private val mCorners: Float
  private val mLeftTopCorner: Float
  private val mRightTopCorner: Float
  private val mLeftBottomCorner: Float
  private val mRightBottomCorner: Float
  private var mWidth = 0
  private var mHeight = 0

  init {
    val typedArray = context.obtainStyledAttributes(a, R.styleable.RoundConstraintLayout)
    mCorners = typedArray.getDimension(R.styleable.RoundConstraintLayout_corner, 0f)
    mLeftTopCorner = typedArray.getDimension(R.styleable.RoundConstraintLayout_leftTopCorner, 0f)
    mRightTopCorner = typedArray.getDimension(R.styleable.RoundConstraintLayout_rightTopCorner, 0f)
    mRightBottomCorner = typedArray.getDimension(R.styleable.RoundConstraintLayout_rightBottomCorner, 0f)
    mLeftBottomCorner = typedArray.getDimension(R.styleable.RoundConstraintLayout_leftBottomCorner, 0f)
    typedArray.recycle()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    mWidth = measuredWidth
    mHeight = measuredHeight
    setMeasuredDimension(mWidth, mHeight)
  }

  override fun draw(canvas: Canvas) {
    canvas.save()
    val path = Path()
    val rectF = RectF(0f, 0f, mWidth.toFloat(), mHeight.toFloat())
    if (mCorners > 0f) {
      path.addRoundRect(rectF, mCorners, mCorners, Path.Direction.CCW)
    } else {
      val radii = floatArrayOf(
          mLeftTopCorner, mLeftTopCorner,
          mRightTopCorner, mRightTopCorner,
          mRightBottomCorner, mRightBottomCorner,
          mLeftBottomCorner, mLeftBottomCorner
      )
      path.addRoundRect(rectF, radii, Path.Direction.CCW)
    }
    canvas.clipPath(path)
    super.draw(canvas)
  }
}
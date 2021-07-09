package cc.ab.base.widget.roundlayout

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import cc.ab.base.R

/**
 *适用性：
 * 1.需要不同圆角，又想解决抗锯齿，选择RoundRectView【不支持设置背景】
 * 2.需要不同圆角，抗锯齿无所谓，选择RoundConstraintLayout【抗锯齿性能差】
 * 3.四个角圆角一致，选择GeneralRoundConstraintLayout【不支持单圆角设置】
 *
 * 原文说明：https://blog.csdn.net/ldld1717/article/details/106652831
 * 原文: https://github.com/leidongld/RoundCornerDemmo
 */
class RoundConstraintLayout @kotlin.jvm.JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0) : ConstraintLayout(c, a, d) {
  private var mCorners: Float = 0f
  private var mLeftTopCorner: Float = 0f
  private var mRightTopCorner: Float = 0f
  private var mLeftBottomCorner: Float = 0f
  private var mRightBottomCorner: Float = 0f
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

  private var mPath = Path()
  private var mRectF = RectF()
  override fun draw(canvas: Canvas) {
    canvas.save()
    mPath = Path()
    mRectF = RectF(0f, 0f, mWidth.toFloat(), mHeight.toFloat())
    if (mCorners > 0f) {
      mPath.addRoundRect(mRectF, mCorners, mCorners, Path.Direction.CCW)
    } else {
      val radii = floatArrayOf(
          mLeftTopCorner, mLeftTopCorner,
          mRightTopCorner, mRightTopCorner,
          mRightBottomCorner, mRightBottomCorner,
          mLeftBottomCorner, mLeftBottomCorner
      )
      mPath.addRoundRect(mRectF, radii, Path.Direction.CCW)
    }
    canvas.clipPath(mPath)
    super.draw(canvas)
    canvas.restore()
  }
}
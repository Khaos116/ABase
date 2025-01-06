package cc.ab.base.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cc.ab.base.R

/**
 * 只保留四个角，中间透明，方便实现图片四个角遮挡
 */
class OnlyCornerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private var mCornerRadius = 20f // 设置为 20dp
  private var mBgColor = Color.parseColor("#dddddd")
  private var mCornerType = CornerType.ALL
  private var mPath = Path()
  private val mPaint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    mCornerRadius = dp2px(mCornerRadius)
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.OnlyCornerView)
    mCornerRadius = typedArray.getDimension(R.styleable.OnlyCornerView_cornerRadius, mCornerRadius)
    mBgColor = typedArray.getColor(R.styleable.OnlyCornerView_bgColor, mBgColor)
    mCornerType = CornerType.values()[typedArray.getInt(R.styleable.OnlyCornerView_cornerType, 0)]
    typedArray.recycle()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绘制镂空">
  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    // 创建路径
    mPath.reset()
    mPath.addRoundRect(
      RectF(0f, 0f, width.toFloat(), height.toFloat()),
      when (mCornerType) {
        CornerType.TOP -> floatArrayOf(mCornerRadius, mCornerRadius, mCornerRadius, 0f, 0f, 0f, 0f)
        CornerType.BOTTOM -> floatArrayOf(0f, 0f, 0f, 0f, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius)
        else -> floatArrayOf(mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius)
      },
      Path.Direction.CW
    )
    // 保存当前画布状态
    val saveCount = canvas.saveLayer(null, null)
    // 绘制背景
    canvas.drawColor(mBgColor)
    // 设置画笔颜色和模式
    mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
    // 绘制蓝色部分
    canvas.drawPath(mPath, mPaint)
    // 恢复画布状态
    canvas.restoreToCount(saveCount)
    // 还原画笔模式
    mPaint.xfermode = null
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="dp2px">
  private fun dp2px(dpValue: Float): Float {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return (dpValue * scale + 0.5f).toInt().toFloat()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="圆角位置">
  enum class CornerType {
    //如果不满足，再添加即可
    ALL, TOP, BOTTOM
  }
  //</editor-fold>
}



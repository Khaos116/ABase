package cc.abase.demo.widget.marquee

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.Html
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import cc.ab.base.ext.dp2px
import cc.abase.demo.R
import cc.abase.demo.widget.MarqueeTextView

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/13
 * @Time：13:08
 */
class VerticalMarqueeTextView @kotlin.jvm.JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0) : FrameLayout(c, a, d) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private val mTv1 = MarqueeTextView(context)
  private val mTv2 = MarqueeTextView(context)
  private var isFirstSize = true
  private var mHeight: Float = 22.dp2px() * 1f
  private var isAnim = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    this.setBackgroundResource(R.drawable.shape_half_orange)
    //抗锯齿
    mTv1.paintFlags = Paint.ANTI_ALIAS_FLAG
    mTv2.paintFlags = Paint.ANTI_ALIAS_FLAG
    mTv1.gravity = Gravity.CENTER_VERTICAL
    mTv2.gravity = Gravity.CENTER_VERTICAL
    //文字大小
    val textSize = 13f
    mTv1.textSize = textSize
    mTv2.textSize = textSize
    //文字颜色
    val textColor = Color.parseColor("#87827D")
    mTv1.setTextColor(textColor)
    mTv2.setTextColor(textColor)
    //内部间距
    val paddingStartEnd = 11.dp2px()
    val paddingTopTxt = 1.dp2px()
    mTv1.setPadding(paddingStartEnd, paddingTopTxt, paddingStartEnd, 0)
    mTv2.setPadding(paddingStartEnd, paddingTopTxt, paddingStartEnd, 0)
    addView(mTv1, FrameLayout.LayoutParams(-1, -1))
    addView(mTv2, FrameLayout.LayoutParams(-1, -1))
    //第二个先看不见
    mTv2.translationY = 1000f
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取到高度后将第二个控件向下移">
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    if (isFirstSize) {
      isFirstSize = false
      mHeight = h * 1f
      mTv2.translationY = mHeight
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  //更新公告
  fun updateNotice(notice: CharSequence) {
    //第一次设置
    if (mTv1.text.toString().isBlank() && mTv2.text.toString().isBlank()) {
      mTv1.text = if (notice.contains("<font")) Html.fromHtml(notice.toString()) else notice
      return
    }
    if (isAnim) return
    isAnim = true
    val inTv = if (mTv1.translationY == 0f) mTv1 else mTv2
    val outTv = if (inTv == mTv1) mTv2 else mTv1
    outTv.marqueeRepeatLimit = 0
    outTv.text = if (notice.contains("<font")) Html.fromHtml(notice.toString()) else notice
    inTv.animate().translationY(-mHeight).setDuration(500).start()
    outTv.animate().translationY(0f).setDuration(500)
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            isAnim = false
            inTv.text = ""
            inTv.translationY = mHeight
            outTv.animate().setListener(null)
            outTv.postInvalidate()
            outTv.marqueeRepeatLimit = -1
            outTv.onWindowFocusChanged(true)
          }
        })
        .start()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="移除后删除动画">
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    mTv2.animate().setListener(null)
    mTv1.clearAnimation()
    mTv2.clearAnimation()
  }
  //</editor-fold>
}
package cc.abase.demo.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import cc.abase.demo.config.AppLiveData
import kotlin.math.abs

/**
 * FrameLayout包含2个子控件，可以实现左右滑动显示功能
 * Author:Khaos116
 * Date:2022/12/23
 * Time:14:30
 */
class TransFrameLayout @kotlin.jvm.JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private var isShowRight: Boolean = false
  var mType: Int = 0//不一样的类型，不需要同时滑动
  var needTouchScroll: Boolean = true//默认是开启手指滑动的
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    isClickable = true
    AppLiveData.frameLayoutScrollLiveData.value?.let { p ->
      if (p.first == mType) isShowRight = p.second
    }
    AppLiveData.frameLayoutScrollLiveData.observe(context as AppCompatActivity) { p ->
      if (p.first == mType) {
        if (childCount == 2) {
          startAnim(p.second)
        } else {
          isShowRight = p.second
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="尺寸变化时修改位置">
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    if (childCount == 2 && w > 0) {
      val child1 = getChildAt(0)
      val child2 = getChildAt(1)
      if (isShowRight) {
        child1.translationX = -1f * w
        child2.translationX = 0f
      } else {
        child1.translationX = 0f
        child2.translationX = 1f * w
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="滑动拖拽操作">
  //记录触摸位置
  private var mDownX = 0f
  private var mDownY = 0f

  //记录当前位置
  private var mCurrentX = 0f
  private var mCurrentY = 0f

  //需要正常滑动判断的最小距离
  private var minMoveDistance = ViewConfiguration.get(context).scaledTouchSlop

  //是否触发滑动
  private var hasStartTrans = false
  override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
    if (childCount == 2 && event != null && needTouchScroll) {
      when (event.action and MotionEvent.ACTION_MASK) {
        MotionEvent.ACTION_DOWN -> {
          mDownX = event.rawX //记录获取按压位置
          mDownY = event.rawY
          mCurrentX = mDownX //记录当前位置
          mCurrentY = mDownY
          hasStartTrans = false //按下默认为非移动状态
        }
        MotionEvent.ACTION_MOVE -> {
          mCurrentX = event.rawX //更新当前位置
          mCurrentY = event.rawY
          //判断触发滑动
          if (!hasStartTrans) {
            hasStartTrans = abs(mCurrentX - mDownX) >= minMoveDistance
            if (hasStartTrans) { //触发滑动后执行移动
              AppLiveData.frameLayoutScrollLiveData.value = Pair(mType, mCurrentX <= mDownX)
            }
          }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        }
      }
    }
    return if (hasStartTrans) false else super.dispatchTouchEvent(event)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="执行动画">
  private fun startAnim(seeRight: Boolean) {
    if (childCount == 2 && width > 0) {
      val child1 = getChildAt(0)
      val child2 = getChildAt(1)
      if (seeRight) {
        if (child2.translationX != 0f) {
          ObjectAnimator.ofFloat(child1, "translationX", -1f * width)
            .also { anim -> anim.interpolator = LinearInterpolator() }
            .setDuration(300)
            .start()
          ObjectAnimator.ofFloat(child2, "translationX", 0f)
            .also { anim -> anim.interpolator = LinearInterpolator() }
            .setDuration(300)
            .start()
        }
      } else {
        if (child1.translationX != 0f) {
          ObjectAnimator.ofFloat(child1, "translationX", 0f)
            .also { anim -> anim.interpolator = LinearInterpolator() }
            .setDuration(300)
            .start()
          ObjectAnimator.ofFloat(child2, "translationX", 1f * width)
            .also { anim -> anim.interpolator = LinearInterpolator() }
            .setDuration(300)
            .start()
        }
      }
    }
  }
  //</editor-fold>
}
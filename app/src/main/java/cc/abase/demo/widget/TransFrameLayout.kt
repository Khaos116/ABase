package cc.abase.demo.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.view.isVisible
import cc.ab.base.widget.livedata.MyObserver
import cc.abase.demo.config.AppLiveData
import com.blankj.utilcode.util.ScreenUtils
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
  private var isCanMove = true
  var mTransType: Int = -1
    //可能存在多组滑动，每组设置不同类别
    set(value) {
      field = value
      updateTranStatus()
    }

  //滑动监听
  private var myObserver = MyObserver { p: Pair<Int, Boolean> ->
    if (p.first == mTransType && isCanMove) {
      if (childCount == 2) {
        startAnim(p.second)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    isClickable = true
    updateTranStatus()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="尺寸变化时修改位置">
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    updateTranStatus()
    AppLiveData.frameLayoutScrollLiveData.removeObserver(myObserver)
    if (h > 0 && this.isVisible) AppLiveData.frameLayoutScrollLiveData.observeForever(myObserver)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="隐藏/显示状态处理">
  override fun setVisibility(visibility: Int) {
    super.setVisibility(visibility)
    AppLiveData.frameLayoutScrollLiveData.removeObserver(myObserver)
    if (visibility == View.VISIBLE && height > 0) AppLiveData.frameLayoutScrollLiveData.observeForever(myObserver)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="更新位移状态">
  private fun updateTranStatus() {
    var isShowRight = false
    AppLiveData.transStateMaps[mTransType]?.let { b -> if (isCanMove) isShowRight = b }
    if (childCount == 2 && width > 0) {
      val child1 = getChildAt(0)
      val child2 = getChildAt(1)
      if (isShowRight) {
        child1.translationX = -1f * width
        child2.translationX = 0f
      } else {
        child1.translationX = 0f
        child2.translationX = 1f * width
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
    if (childCount == 2 && event != null && isCanMove) {
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
              val p = Pair(mTransType, mCurrentX <= mDownX)
              AppLiveData.frameLayoutScrollLiveData.value = p
              AppLiveData.transStateMaps[p.first] = p.second
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

  //<editor-fold defaultstate="collapsed" desc="是否禁止滚动">
  fun setCanTans(tans: Boolean) {
    isCanMove = tans
    if (!tans) {
      mTransType = -1
      val count = childCount
      for (i in 0 until count) {
        getChildAt(i).translationX = if (i == 0) 0f else ScreenUtils.getScreenWidth() * 1f
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="添加/移除时监听">
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    AppLiveData.frameLayoutScrollLiveData.removeObserver(myObserver)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    AppLiveData.frameLayoutScrollLiveData.removeObserver(myObserver)
    if (this.isVisible && height > 0) AppLiveData.frameLayoutScrollLiveData.observeForever(myObserver)
  }
  //</editor-fold>
}
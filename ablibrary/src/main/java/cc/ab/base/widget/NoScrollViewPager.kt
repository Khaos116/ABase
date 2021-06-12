package cc.ab.base.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field

/**
 * Author:Khaos
 * Date:2020-10-10
 * Time:10:10
 */
class NoScrollViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    initSpeedViewPager()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="禁止ViewPager手动滚动">
  //是否可以手动滑动,false禁止
  var canScroll = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(ev: MotionEvent?): Boolean = if (!canScroll) false else super.onTouchEvent(ev)

  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = if (!canScroll) false else super.onInterceptTouchEvent(ev)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="反射修改滚动速度">
  private var scroller: SpeedScroller? = null
  private fun initSpeedViewPager() {
    try {
      val scrollerField: Field = ViewPager::class.java.getDeclaredField("mScroller")
      scrollerField.isAccessible = true
      val interpolator: Field = ViewPager::class.java.getDeclaredField("sInterpolator")
      interpolator.isAccessible = true
      scroller = SpeedScroller(context, interpolator.get(null) as Interpolator)
      scrollerField.set(this, scroller)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部设置滚动速度">
  fun setScrollSpeed(scrollSpeed: Float) {
    scroller?.setScrollSpeed(scrollSpeed)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="速度控制">
  class SpeedScroller(context: Context, interpolator: Interpolator) : Scroller(context, interpolator) {
    private var scrollSpeed = 1f//数值越大滚动越慢
    fun setScrollSpeed(speed: Float) {
      scrollSpeed = speed
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
      super.startScroll(startX, startY, dx, dy, (duration * scrollSpeed).toInt())
    }
  }
  //</editor-fold>
}
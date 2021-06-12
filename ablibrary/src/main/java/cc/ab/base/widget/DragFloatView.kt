package cc.ab.base.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import cc.ab.base.R
import cc.ab.base.ext.removeParent
import com.blankj.utilcode.util.BarUtils
import kotlin.math.abs

/**
 * @Description 1.尽量添加到"android.R.id.content"这个父控件 2.页面关闭时,需要把本控件从父控件移除,防止父控件监听没有移除
 * @Author：Khaos
 * @Date：2021/1/14
 * @Time：9:55
 */
class DragFloatView @kotlin.jvm.JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0) : FrameLayout(c, a, d) {
  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    setBackgroundResource(R.drawable.shape_circle_primary)
    setOnLongClickListener { true }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="监听父控件大小改变">
  //记录父控件，1是为了添加高度监听；2是为了获取子控件相对父控件的位置
  private var mParentView: View? = null
  private var mParentHeight = 0

  //相对于父控件的位置，根据位置+偏移量来修改控件位置
  private var mLocationX: Int = -1
  private var mLocationY: Int = -1

  //为了不超出边界，需要记录最大和最小的边界值
  private var minTransX: Float = 0f
  private var maxTransX: Float = 0f
  private var minTransY: Float = 0f
  private var maxTransY: Float = 0f

  //监听父控件高度改变
  private val globalLayoutListener = OnGlobalLayoutListener {
    mParentView?.let { parentView ->
      if (mParentHeight != parentView.height) { //只监听父控件高度变化，所以只做了父控件高度改变的适配(宽度改变不适配,主要兼容虚拟导航键的隐藏和显示)
        if (mLocationX < 0 && mLocationY < 0) { //第一次获取位置
          val array1 = intArrayOf(0, 0) //当前控件位置
          this.getLocationOnScreen(array1)
          val array2 = intArrayOf(0, 0) //父控件位置
          parentView.getLocationOnScreen(array2)
          mLocationX = array1[0] - array2[0] //相对父控件的X间距
          mLocationY = array1[1] - array2[1] //相对父控件的Y间距
          minTransX = -mLocationX * 1f //最小可移动X距离
          val fitSystem = array2[1] < BarUtils.getStatusBarHeight() //父控件是否填充到了状态栏
          minTransY = -mLocationY * 1f + (if (fitSystem) BarUtils.getStatusBarHeight() else 0) //最小可移动Y距离
          maxTransX = parentView.width * 1f - (mLocationX + this.width) //最大可移动X距离
          maxTransY = parentView.height * 1f - (mLocationY + this.height) //最大可移动Y距离
        } else (this.layoutParams as? FrameLayout.LayoutParams)?.gravity?.let { g -> //非第一次，则需要计算高度改变了多少
          when (g and Gravity.VERTICAL_GRAVITY_MASK) { //计算上中下比重
            Gravity.CENTER_VERTICAL -> { //中间->上下增加和减小都需要减半
              minTransY -= (parentView.height - mParentHeight) / 2f //最小高度改变
              maxTransY += (parentView.height - mParentHeight) / 2f //最大高度改变
            }
            Gravity.BOTTOM -> minTransY -= (parentView.height - mParentHeight) //下面->只需要修改顶部最小距离
            else -> maxTransY += (parentView.height - mParentHeight) //上面->只需要修改底部最大距离
          }
        }
        checkEdge() //检查是否超出父控件
        checkAnim() //检查是否贴边
        mParentHeight = parentView.height //记录高度，只有高度改变才修改
      }
    }
  }

  //添加到父控件后监听父控件高度改变
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    mParentView = this.parent as? View
    mParentView?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
  }

  //移除本控件后取消监听
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    mParentView?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
    mParentView = null
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="检测边缘+帖边动画">
  private var isAniming = false

  //不允许超出边界
  private fun checkEdge() {
    this.translationX = when {
      this.translationX < minTransX -> minTransX
      this.translationX > maxTransX -> maxTransX
      else -> this.translationX
    }
    this.translationY = when {
      this.translationY < minTransY -> minTransY
      this.translationY > maxTransY -> maxTransY
      else -> this.translationY
    }
  }

  //拖拽结束后自动贴边
  private fun checkAnim() {
    mParentView?.let { parentView ->
      val array1 = intArrayOf(0, 0) //当前控件位置
      this.getLocationOnScreen(array1)
      val array2 = intArrayOf(0, 0) //父控件位置
      parentView.getLocationOnScreen(array2)
      val transX = if (array1[0] - array2[0] + width / 2f <= parentView.width / 2f) minTransX else maxTransX
      isAniming = true
      this.animate().translationX(transX).setDuration(200)
          .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
              super.onAnimationEnd(animation)
              isAniming = false
            }
          })
          .start()
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
  private var minMoveDistance = ViewConfiguration.get(c).scaledTouchSlop

  //是否触发滑动
  private var hasStartTrans = false

  //上次移动的距离
  private var mDownTransX = 0f
  private var mDownTransY = 0f

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (isAniming) return super.onTouchEvent(event)
    val parentView = mParentView
    if (parentView != null && event != null) {
      when (event.action and MotionEvent.ACTION_MASK) {
        MotionEvent.ACTION_DOWN -> {
          this.setBackgroundResource(R.drawable.shape_circle_accent)
          mDownX = event.rawX //记录获取按压位置
          mDownY = event.rawY
          mCurrentX = mDownX //记录当前位置
          mCurrentY = mDownY
          mDownTransX = this.translationX //记录按压时的移动量
          mDownTransY = this.translationY
          hasStartTrans = false //按下默认为非移动状态
        }
        MotionEvent.ACTION_MOVE -> {
          mCurrentX = event.rawX //更新当前位置
          mCurrentY = event.rawY
          //判断触发滑动
          if (!hasStartTrans) hasStartTrans = (abs(mCurrentX - mDownX) >= minMoveDistance || abs(mCurrentY - mDownY) >= minMoveDistance)
          if (hasStartTrans) { //触发滑动后执行移动
            this.translationX = mDownTransX + (mCurrentX - mDownX)
            this.translationY = mDownTransY + (mCurrentY - mDownY)
            checkEdge()
          }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          checkAnim()
          this.setBackgroundResource(R.drawable.shape_circle_primary)
        }
      }
    }
    return if (hasStartTrans) true //再次判断是否执行到了本地的滑动，如果产生了滑动，则劫持掉其他控件的触摸事件，直接返回true
    else if (abs(mCurrentX - mDownX) < minMoveDistance && abs(mCurrentY - mDownY) < minMoveDistance) { //要保证触发滑动，必须先返回true防止其他控件劫持滑动事件
      //内部控件需要点击事件,所以添加内部响应
      super.onTouchEvent(event)
      true //返回true不允许外部劫持控件自己的内部事件
    } else super.onTouchEvent(event) //其他情况直接交给内部控件处理
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放内存">
  fun release() {
    setBackgroundResource(R.drawable.shape_circle_primary)
    this.isAniming = false
    this.clearAnimation()
    this.removeParent()
  }
  //</editor-fold>
}
package cc.ab.base.widget.discretescrollview

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import cc.ab.base.R
import cc.ab.base.ext.getMyLifecycleOwner
import cc.ab.base.widget.discretescrollview.adapter.DiscretePageAdapter
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import com.blankj.utilcode.util.SizeUtils
import kotlin.math.abs

/**
 * Description:参考 https://github.com/saiwu-bigkoo/Android-ConvenientBanner/blob/master/convenientbanner/src/main/java/com/bigkoo/convenientbanner/ConvenientBanner.java
 * @author: CASE
 * @date: 2019/10/14 11:44
 */
class DiscreteBanner<T, V : ViewBinding> @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), LifecycleObserver {
  //横向还是竖向
  private var orientation = DSVOrientation.HORIZONTAL.ordinal

  //数据源
  private var mData: MutableList<T> = mutableListOf()

  //列表
  private lateinit var mPager: DiscreteScrollView

  //真正使用的adapter
  private var mPagerAdapter: DiscretePageAdapter<T,V>? = null

  //圆点
  private lateinit var mIndicator: DotsIndicator

  //是否无限循环
  private var looper: Boolean = false

  //无限循环adapter
  private var mLooperAdapter: InfiniteScrollAdapter<DiscreteHolder<T,V>>? = null

  //是否需要自动轮播
  private var needAutoPlay = false

  //是否在ViewPager中
  private var inPager = false

  //默认间距
  val defaultOffset: Float = SizeUtils.dp2px(8f) * 1f

  //默认滚动时间
  val defaultScrollTime = 200

  //自动轮播时间间隔
  val defaultAutoPlayDuration = 5000L

  //初始化
  init {
    if (attrs != null) {
      val ta = getContext().obtainStyledAttributes(attrs, R.styleable.DiscreteBanner)
      this.orientation = ta.getInt(R.styleable.DiscreteBanner_dsv_orientation, DSVOrientation.HORIZONTAL.ordinal)
      ta.recycle()
    }
    initPager()
    initIndicator()
  }

  //初始化banner
  private fun initPager() {
    mPager = DiscreteScrollView(context)
    mPager.setItemTransitionTimeMillis(defaultScrollTime)
    mPager.addOnItemChangedListener { viewHolder, adapterPostion, end ->
      if (end) return@addOnItemChangedListener
      val position = if (looper && mLooperAdapter != null) {
        mLooperAdapter?.getRealPosition(adapterPostion) ?: 0
      } else {
        adapterPostion
      }
      mIndicator.setDotSelection(position)
    }
    if (orientation == DSVOrientation.HORIZONTAL.ordinal) { //横向
      mPager.setOrientation(DSVOrientation.HORIZONTAL)
    } else { //竖向
      mPager.setOrientation(DSVOrientation.VERTICAL)
    }
    //添加View
    addView(mPager)
  }

  //初始化indicator
  private fun initIndicator() {
    mIndicator = DotsIndicator(context)
    mIndicator.onSelectListener = {
      val temp = mLooperAdapter
      if (looper && temp != null) {
        //拿到当前无限循环的位置
        val currentP = mPager.currentItem
        //拿到当前真实位置
        val realP = temp.getRealPosition(currentP)
        //计算需要移动的数量
        val offsetP = it - realP
        //如果需要移动，则进行移动
        if (offsetP != 0) mPager.smoothScrollToPosition(currentP + offsetP)
      } else {
        mPager.smoothScrollToPosition(it)
      }
    }
    val indicatorParam = LayoutParams(-2, -2)
    if (orientation == DSVOrientation.HORIZONTAL.ordinal) { //横向
      mIndicator.orientation = LinearLayout.HORIZONTAL
      indicatorParam.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
      mIndicator.translationY = -defaultOffset
    } else { //竖向
      mIndicator.orientation = LinearLayout.VERTICAL
      indicatorParam.gravity = Gravity.END or Gravity.CENTER_VERTICAL
      mIndicator.translationX = -defaultOffset
    }
    addView(mIndicator, indicatorParam)
  }

  //设置横竖切换
  fun setOrientation(orientation: DSVOrientation): DiscreteBanner<T, V> {
    this.orientation = orientation.ordinal
    mPager.setOrientation(orientation)
    if (orientation == DSVOrientation.HORIZONTAL) { //横向
      mIndicator.orientation = LinearLayout.HORIZONTAL
      (mIndicator.layoutParams as LayoutParams).gravity =
          Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
      mIndicator.translationX = 0f
      mIndicator.translationY = -defaultOffset
    } else { //竖向
      mIndicator.orientation = LinearLayout.VERTICAL
      (mIndicator.layoutParams as LayoutParams).gravity = Gravity.END or Gravity.CENTER_VERTICAL
      mIndicator.translationX = -defaultOffset
      mIndicator.translationY = 0f
    }
    return this
  }

  //设置显示位置,请在setOrientation后设置
  fun setIndicatorGravity(gravity: Int): DiscreteBanner<T, V> {
    (mIndicator.layoutParams as LayoutParams).gravity = gravity
    return this
  }

  //设置偏移量X,请在setOrientation后设置
  fun setIndicatorOffsetX(offsetX: Float): DiscreteBanner<T, V> {
    mIndicator.translationX = offsetX
    return this
  }

  //设置偏移量Y,请在setOrientation后设置
  fun setIndicatorOffsetY(offsetY: Float): DiscreteBanner<T, V> {
    mIndicator.translationY = offsetY
    return this
  }

  //是否需要无限循环
  fun setLooper(loop: Boolean): DiscreteBanner<T, V> {
    this.looper = loop
    return this
  }

  //设置是否自动轮播
  fun setAutoPlay(auto: Boolean): DiscreteBanner<T, V> {
    this.needAutoPlay = auto
    return this
  }

  //设置点击事件
  fun setOnItemClick(click: (position: Int, t: T) -> Unit): DiscreteBanner<T, V> {
    itemClick = click
    return this
  }

  //获取pager对象，外面设置更多属性
  fun getPager(): DiscreteScrollView? {
    return mPager
  }

  //获取Indicator对象，外面设置更多属性
  fun getIndicator(): DotsIndicator? {
    return mIndicator
  }

  //点击banner
  private var itemClick: ((position: Int, t: T) -> Unit)? = null

  //设置数据
  @Suppress("UNCHECKED_CAST")
  fun setPages(holderCreator: DiscreteHolderCreator<T,V>, datas: MutableList<T>): DiscreteBanner<T, V> {
    stopPlay()
    this.mData = datas
    this.mPagerAdapter = DiscretePageAdapter(holderCreator, mData) { position, t ->
      itemClick?.invoke(position, t as T)
    }
    if (looper) {
      mPagerAdapter?.let {
        this.mLooperAdapter = InfiniteScrollAdapter.wrap(it)
        this.mPager.adapter = mLooperAdapter
      }
    } else {
      this.mPager.adapter = mPagerAdapter
    }
    mIndicator.initDots(datas.size)
    mIndicator.setDotSelection(0)
    startPlay()
    return this
  }

  //开始轮播
  fun startPlay() {
    stopPlay()
    if (needAutoPlay) {
      playHandler.postDelayed(playRunnable, defaultAutoPlayDuration)
    }
  }

  //关闭轮播
  fun stopPlay() {
    playHandler.removeCallbacks(playRunnable)
  }

  //自动轮播的Handler
  private val playHandler = Handler()

  //自动轮播的Runnable
  private val playRunnable = Runnable {
    kotlin.run {
      if ((mPagerAdapter?.itemCount ?: 0) > 1 && (mPager.adapter?.itemCount ?: 0) > 1) mPager.smoothScrollToPosition(mPager.currentItem + 1)
      next()
    }
  }

  //执行下一页
  private fun next() {
    playHandler.postDelayed(playRunnable, defaultAutoPlayDuration)
  }

  //添加-播放
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    onAttachedToWindowLifecycle()
    startPlay()
    checkInPager(parent)
  }

  //判断是否处于ViewPager中
  private fun checkInPager(parent: ViewParent?): ViewParent? {
    if (parent == null || parent.parent == null) return null
    return if (parent.parent is ViewPager || parent.parent is ViewPager2) {
      inPager = true
      null
    } else {
      checkInPager(parent.parent)
    }
  }

  //移除-停止
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    onDetachedFromWindowLifecycle()
    stopPlay()
    inPager = false
  }

  //显示播放，隐藏停止
  override fun onWindowVisibilityChanged(visibility: Int) {
    super.onWindowVisibilityChanged(visibility)
    if (visibility == View.VISIBLE) {
      startPlay()
    } else if (visibility == View.INVISIBLE || visibility == View.GONE) {
      stopPlay()
    }
  }

  //是否确定滑动方向，只有在ViewPager中才需要
  private var hasConfirmDirection = false

  //需要正常滑动判断的最小距离
  private var minMoveDistance = 25

  //记录触摸位置
  private var mPosX = 0f
  private var mPosY = 0f
  private var mCurPosX = 0f
  private var mCurPosY = 0f

  //手指触摸打断自动轮播
  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    if (mPagerAdapter?.itemCount ?: 0 > 1) {
      ev?.let { e ->
        val action = e.action and MotionEvent.ACTION_MASK
        if (action == MotionEvent.ACTION_DOWN) {
          hasConfirmDirection = false
          stopPlay()
          if (!inPager && this.orientation == DSVOrientation.VERTICAL.ordinal) {
            this.requestDisallowInterceptTouchEvent(true) //没在ViewPager中，自己处理竖向滑动
          } else if (inPager) {
            this.requestDisallowInterceptTouchEvent(true) //在ViewPager中，自己判断是横向还是纵向
            mPosX = e.rawX //记录地址，方便判断方向
            mPosY = e.rawY
          }
        } else if (action == MotionEvent.ACTION_MOVE && inPager && !hasConfirmDirection) {
          mCurPosX = e.rawX
          mCurPosY = e.rawY
          //滑动的距离
          val distanceX = abs(mCurPosX - mPosX)
          val distanceY = abs(mCurPosY - mPosY)
          if (distanceX >= minMoveDistance || distanceY >= minMoveDistance) { //确定方向
            hasConfirmDirection = true
            if (distanceX < distanceY && this.orientation == DSVOrientation.HORIZONTAL.ordinal) { //竖向滑动，横向ViewPager
              parent.requestDisallowInterceptTouchEvent(false) //让父控件执行
            } else if (distanceX > distanceY && this.orientation == DSVOrientation.VERTICAL.ordinal) { //横向滑动，竖向ViewPager
              parent.requestDisallowInterceptTouchEvent(false) //让父控件执行
            } //其余情况自己处理即可(已在MotionEvent.ACTION_DOWN中自行处理)
          }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
          startPlay()
        } else return super.dispatchTouchEvent(ev)
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  fun getOrientation() = orientation

  //<editor-fold defaultstate="collapsed" desc="自感应生命周期">
  private fun onAttachedToWindowLifecycle() {
    setLifecycleOwner(getMyLifecycleOwner())
  }

  private fun onDetachedFromWindowLifecycle() {
    setLifecycleOwner(null)
    onPauseBanner()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Lifecycle生命周期">
  private var mLifecycle: Lifecycle? = null

  //通过Lifecycle内部自动管理暂停和播放(如果不需要后台播放)
  private fun setLifecycleOwner(owner: LifecycleOwner?) {
    if (owner == null) {
      mLifecycle?.removeObserver(this)
      mLifecycle = null
    } else {
      mLifecycle?.removeObserver(this)
      mLifecycle = owner.lifecycle
      mLifecycle?.addObserver(this)
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  private fun onPauseBanner() {
    stopPlay()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  private fun onResumeBanner() {
    if (visibility == View.VISIBLE) startPlay()
  }
  //</editor-fold>
}
package cc.ab.base.widget.discretescrollview

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import cc.ab.base.R
import cc.ab.base.widget.discretescrollview.adapter.DiscretePageAdapter
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import com.blankj.utilcode.util.SizeUtils

/**
 * Description:参考https://github.com/saiwu-bigkoo/Android-ConvenientBanner/blob/master/convenientbanner/src/main/java/com/bigkoo/convenientbanner/ConvenientBanner.java
 * @author: caiyoufei
 * @date: 2019/10/14 11:44
 */
class DiscreteBanner<T> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
                                                 ) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
  //横向还是竖向
  private var orientation = DSVOrientation.HORIZONTAL.ordinal

  //数据源
  private var mData: List<T> = emptyList()

  //列表
  private lateinit var mPager: DiscreteScrollView

  //真正使用的adapter
  private var mPagerAdapter: DiscretePageAdapter<T>? = null

  //圆点
  private lateinit var mIndicator: DotsIndicator

  //是否无限循环
  private var looper: Boolean = false

  //无限循环adapter
  private var mLooperAdapter: InfiniteScrollAdapter<DiscreteHolder<T>>? = null

  //是否需要自动轮播
  private var needAutoPlay = false

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
      this.orientation =
          ta.getInt(R.styleable.DiscreteBanner_dsv_orientation, DSVOrientation.HORIZONTAL.ordinal)
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
    if (orientation == DSVOrientation.HORIZONTAL.ordinal) {//横向
      mPager.setOrientation(DSVOrientation.HORIZONTAL)
    } else {//竖向
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
    if (orientation == DSVOrientation.HORIZONTAL.ordinal) {//横向
      mIndicator.orientation = LinearLayout.HORIZONTAL
      indicatorParam.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
      mIndicator.translationY = -defaultOffset
    } else {//竖向
      mIndicator.orientation = LinearLayout.VERTICAL
      indicatorParam.gravity = Gravity.END or Gravity.CENTER_VERTICAL
      mIndicator.translationX = -defaultOffset
    }
    addView(mIndicator, indicatorParam)
  }

  //设置横竖切换
  fun setOrientation(orientation: DSVOrientation): DiscreteBanner<T> {
    this.orientation = orientation.ordinal
    mPager.setOrientation(orientation)
    if (orientation == DSVOrientation.HORIZONTAL) {//横向
      mIndicator.orientation = LinearLayout.HORIZONTAL
      (mIndicator.layoutParams as LayoutParams).gravity =
          Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
      mIndicator.translationX = 0f
      mIndicator.translationY = -defaultOffset
    } else {//竖向
      mIndicator.orientation = LinearLayout.VERTICAL
      (mIndicator.layoutParams as LayoutParams).gravity = Gravity.END or Gravity.CENTER_VERTICAL
      mIndicator.translationX = -defaultOffset
      mIndicator.translationY = 0f
    }
    return this
  }

  //设置显示位置,请在setOrientation后设置
  fun setIndicatorGravity(gravity: Int): DiscreteBanner<T> {
    (mIndicator.layoutParams as LayoutParams).gravity = gravity
    return this
  }

  //设置偏移量X,请在setOrientation后设置
  fun setIndicatorOffsetX(offsetX: Float): DiscreteBanner<T> {
    mIndicator.translationX = offsetX
    return this
  }

  //设置偏移量Y,请在setOrientation后设置
  fun setIndicatorOffsetY(offsetY: Float): DiscreteBanner<T> {
    mIndicator.translationY = offsetY
    return this
  }

  //是否需要无限循环
  fun setLooper(loop: Boolean): DiscreteBanner<T> {
    this.looper = loop
    return this
  }

  //设置是否自动轮播
  fun setAutoPlay(auto: Boolean): DiscreteBanner<T> {
    this.needAutoPlay = auto
    return this
  }

  //设置点击事件
  fun setOnItemClick(click: (position: Int, t: T) -> Unit): DiscreteBanner<T> {
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
  fun setPages(
      holderCreator: DiscreteHolderCreator,
      datas: List<T>
              ): DiscreteBanner<T> {
    stopPlay()
    this.mData = datas
    this.mPagerAdapter = DiscretePageAdapter(holderCreator, mData)
    this.mPagerAdapter?.setOnItemClickListener { position, t ->
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
    if (needAutoPlay && mPagerAdapter?.itemCount ?: 0 > 1) {
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
      mPager.smoothScrollToPosition(mPager.currentItem + 1)
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
    startPlay()
  }

  //移除-停止
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    stopPlay()
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

  //手指触摸打断自动轮播
  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    if (needAutoPlay && mPagerAdapter?.itemCount ?: 0 > 1) {
      ev?.let { e ->
        val action = e.action
        if (action == MotionEvent.ACTION_DOWN) {
          //如果需要竖向滑动，则请求父控件不拦截
          if (this.orientation == DSVOrientation.VERTICAL.ordinal && mPagerAdapter?.itemCount ?: 0 > 1) {
            mPager.parent.requestDisallowInterceptTouchEvent(true)
          }
          stopPlay()
        } else if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_CANCEL ||
            action == MotionEvent.ACTION_OUTSIDE
        ) {
          startPlay()
        } else {
          return super.dispatchTouchEvent(ev)
        }
      }
    }
    return super.dispatchTouchEvent(ev)
  }
}
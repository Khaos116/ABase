package cc.ab.base.widget.discretescrollview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.R
import cc.ab.base.widget.discretescrollview.DiscreteScrollView.OnItemChangedListener
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
  //默认间距
  var defaultOffset: Float = SizeUtils.dp2px(8f) * 1f
  //是否无限循环
  private var looper: Boolean = false
  //无限循环adapter
  private var mLooperAdapter: InfiniteScrollAdapter<DiscreteHolder<T>>? = null
  //默认滚动时间
  private var defaultScrollTime = 200

  //初始化
  init {
    if (attrs != null) {
      val ta = getContext().obtainStyledAttributes(attrs, R.styleable.DiscreteBanner)
      orientation =
        ta.getInt(R.styleable.DiscreteBanner_dsv_orientation, DSVOrientation.HORIZONTAL.ordinal)
      ta.recycle()
    }
    initPager()
    initIndicator()
  }

  //初始化banner
  private fun initPager() {
    mPager = DiscreteScrollView(context)
    mPager.setBanner(true)
    mPager.setItemTransitionTimeMillis(defaultScrollTime)
    mPager.addOnItemChangedListener { viewHolder, adapterPostion ->
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
    mIndicator.onSelectListener = { mPager.smoothScrollToPosition(it) }
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
    mPager.setOrientation(orientation)
    if (orientation == DSVOrientation.HORIZONTAL) {//横向
      mIndicator.orientation = LinearLayout.HORIZONTAL
      (mIndicator.layoutParams as LayoutParams).gravity =
        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
      mIndicator.translationY = -defaultOffset
    } else {//竖向
      mIndicator.orientation = LinearLayout.VERTICAL
      (mIndicator.layoutParams as LayoutParams).gravity = Gravity.END or Gravity.CENTER_VERTICAL
      mIndicator.translationX = -defaultOffset
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
    this.mData = datas
    this.mPagerAdapter = DiscretePageAdapter(holderCreator, mData)
    this.mPagerAdapter?.setOnItemClickListener { position, t ->
      itemClick?.invoke(position, t as T)
    }
    if (looper) {
      mPagerAdapter?.let {
        //        this.mLooperAdapter = InfiniteScrollAdapter.wrap(it)
        this.mPager.adapter = mPagerAdapter
      }
    } else {
      this.mPager.adapter = mPagerAdapter
    }
    mIndicator.initDots(datas.size)
    mIndicator.setDotSelection(0)
    return this
  }

  //防止存在多个监听
  private var changelistener: OnItemChangedListener<ViewHolder>? = null

  //监听item位置改变
  public fun setOnItemChangedListener(listener: OnItemChangedListener<ViewHolder>) {
    val temp = changelistener
    if (temp == null) {
      changelistener = listener
    } else {
      mPager.removeItemChangedListener(temp)
    }
    mPager.addOnItemChangedListener(listener)
  }
}
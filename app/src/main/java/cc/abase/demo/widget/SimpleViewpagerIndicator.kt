package cc.abase.demo.widget

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.Style.FILL
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

/**
 * description: 自定义ViewPager指示器.
 * Author:Khaos116
 * Date:2020/8/12
 * Time:20:41
 */
class SimpleViewpagerIndicator @JvmOverloads constructor(
    con: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(con, attrs, defStyle) {
  init {
    isFillViewport = true
    setWillNotDraw(false)
  }

  //配置属性 START-------------------------------------------------------------------------------------
  private val pageListener: PageListener = PageListener()

  /*
   * true:每个tab宽度为平分父控件剩余空间
   * false:每个tab宽度为包裹内容
   */
  private var expand = false

  /*
   * 指示器（被选中的tab下的短横线）
   */
  private var indicatorWrapText = true //true：indicator与文字等长；false：indicator与整个tab等长
  private var indicatorColor = Color.parseColor("#ff666666")
  private var indicatorHeight = 2 //dp

  /*
   * 底线（指示器的背景滑轨）
   */
  private var showUnderline = false //是否展示底线
  private var underlineColor = 0
  private var underlineHeight = 0f //dp

  /*
   * tab之间的分割线
   */
  private var showDivider = false //是否展示分隔线
  private var dividerColor = 0
  private var dividerPadding = 0 //分隔线上下的padding,dp
  private var dividerWidth = 0 //分隔线宽度,dp

  /*
   * tab
   */
  private var tabTextSize = 16 //tab字号,dp
  private var tabTextColor = Color.parseColor("#ff999999") //tab字色
  private var tabTypeface: Typeface? = null //tab字体
  private var tabTypefaceStyle = Typeface.NORMAL //tab字体样式
  private var tabBackgroundResId = 0 //每个tab的背景资源id
  private var tabPadding = 24 //每个tab的左右内边距,dp
  private var tabWidth = 0 //tab指定宽度
  private var tabRound = 0 //tab的圆角

  /*
   * 被选中的tab
   */
  private var selectedTabTextSize = 16 //dp
  private var selectedTabTextColor = Color.parseColor("#ff666666")
  private var selectedTabTypeface: Typeface? = null
  private var selectedTabTypefaceStyle = Typeface.BOLD

  //配置属性 End---------------------------------------------------------------------------------------
  /*
   * scrollView整体滚动的偏移量,dp
   */
  private var scrollOffset = 100
  private var wrapTabLayoutParams: LinearLayout.LayoutParams? = null
  private var expandTabLayoutParams: LinearLayout.LayoutParams? = null
  private lateinit var rectPaint: Paint
  private lateinit var dividerPaint: Paint
  private lateinit var measureTextPaint: Paint //测量文字宽度用的画笔
  private var userPageListener: OnPageChangeListener? = null
  private lateinit var tabsContainer: LinearLayout //tab的容器
  private lateinit var viewPager: ViewPager
  private var currentPosition = 0 //viewPager当前页面
  private var currentPositionOffset = 0f //viewPager当前页面的偏移百分比（取值：0~1）
  private var selectedPosition = 0 //viewPager当前被选中的页面
  private var tabCount = 0
  private var lastScrollX = 0
  private val textLocation = LeftRight()
  private var tabTransY = 0f //dp 默认在最底部(负数往上)
  private var textTransY = 0f //dp 默认在中间(负数往上)

  //不允许选中的位置
  private var mNoClickPosition = -1

  //点击不允许的回调
  private var mNoClickCall: (() -> Unit)? = null
  private val mEvaluator = ArgbEvaluator()
  fun setViewPager(viewPager: ViewPager): SimpleViewpagerIndicator {
    this.viewPager = viewPager
    checkNotNull(viewPager.adapter) { "ViewPager does not have adapter instance." }
    viewPager.addOnPageChangeListener(pageListener)
    init()
    initView()
    return this
  }

  fun setOnPageChangeListener(listener: OnPageChangeListener?): SimpleViewpagerIndicator {
    userPageListener = listener
    return this
  }

  private fun init() {
    /*
     * 将dp换算为px
     */
    val density = context.resources.displayMetrics.density
    indicatorHeight = dp2px(indicatorHeight.toFloat())
    underlineHeight = dp2px(underlineHeight).toFloat()
    dividerPadding = dp2px(dividerPadding.toFloat())
    dividerWidth = dp2px(dividerWidth.toFloat())
    tabTextSize = dp2px(tabTextSize.toFloat())
    tabPadding = dp2px(tabPadding.toFloat())
    selectedTabTextSize = dp2px(selectedTabTextSize.toFloat())
    scrollOffset = dp2px(scrollOffset.toFloat())
    tabWidth = dp2px(tabWidth.toFloat())
    tabRound = dp2px(tabRound.toFloat())
    tabTransY = dp2px(tabTransY).toFloat()
    textTransY = dp2px(textTransY).toFloat()
    /*
     * 创建tab的容器（LinearLayout）
     */
    tabsContainer = LinearLayout(context)
    tabsContainer.orientation = LinearLayout.HORIZONTAL
    tabsContainer.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    addView(tabsContainer)

    /*
     * 创建画笔
     */
    rectPaint = Paint()
    rectPaint.isAntiAlias = true
    rectPaint.style = FILL
    dividerPaint = Paint()
    dividerPaint.isAntiAlias = true
    dividerPaint.strokeWidth = dividerWidth.toFloat()
    measureTextPaint = Paint()
    measureTextPaint.textSize = selectedTabTextSize.toFloat()

    /*
     * 创建两个Tab的LayoutParams，一个为宽度包裹内容，一个为宽度等分父控件剩余空间
     */
    wrapTabLayoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT) //宽度包裹内容
    expandTabLayoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) //宽度等分
  }

  private fun initView() {
    //注意：currentPosition和selectedPosition的含义并不相同，它们分别在onPageScroll和onPageSelected中被赋值
    //在从tab1往tab2滑动的过程中,selectedPosition会比currentPosition先由1变成2
    currentPosition = viewPager.currentItem
    selectedPosition = viewPager.currentItem
    tabsContainer.removeAllViews()
    tabCount = viewPager.adapter?.count ?: 0
    //创建tab并添加到tabsContainer中
    for (i in 0 until tabCount) addTab(i, viewPager.adapter?.getPageTitle(i).toString() ?: "")
    //遍历tab，设置tab文字大小和样式
    updateTextStyle()
    //滚动scrollView
    viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        scrollToChild(currentPosition, 0) //滚动scrollView
      }
    })
  }

  /**
   * 添加tab
   */
  private fun addTab(position: Int, title: String) {
    val tab = TextView(context)
    tab.gravity = Gravity.CENTER
    tab.setSingleLine()
    tab.text = title
    if (tabBackgroundResId != 0) tab.setBackgroundResource(tabBackgroundResId)
    tab.setPadding(tabPadding, 0, tabPadding, 0)
    tab.setOnClickListener {
      if (position == mNoClickPosition) {
        mNoClickCall?.invoke()
      } else {
        viewPager.currentItem = position
      }
    }
    tab.translationY = textTransY
    tabsContainer.addView(tab, position, if (expand) expandTabLayoutParams else wrapTabLayoutParams)
  }

  /**
   * 遍历tab，设置tab文字大小和样式
   */
  private fun updateTextStyle() {
    for (i in 0 until tabCount) {
      val tvTab = tabsContainer.getChildAt(i) as TextView
      if (i == selectedPosition) { //被选中的tab
        tvTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedTabTextSize.toFloat())
        tvTab.setTypeface(selectedTabTypeface, selectedTabTypefaceStyle)
        tvTab.setTextColor(selectedTabTextColor)
      } else { //未被选中的tab
        tvTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize.toFloat())
        tvTab.setTypeface(tabTypeface, tabTypefaceStyle)
        tvTab.setTextColor(tabTextColor)
      }
    }
  }

  /**
   * 滚动scrollView
   *
   *
   * 注意：当普通文字字号（tabTextSize）与被选中的文字字号（selectedTabTextSize）相差过大，且tab的宽度模式为包裹内容（expand = false）时，
   * 由于文字选中状态切换时文字宽度突变，造成tab宽度突变，可能导致scrollView在滚动时出现轻微抖动。
   * 因此，当普通文字字号（tabTextSize）与被选中的文字字号（selectedTabTextSize）相差过大时，应避免使tab宽度包裹内容（expand = false）。
   */
  private fun scrollToChild(position: Int, offset: Int) {
    if (tabCount == 0) return
    //getLeft():tab相对于父控件，即tabsContainer的left
    var newScrollX = tabsContainer.getChildAt(position).left + offset
    //附加一个偏移量，防止当前选中的tab太偏左
    //可以去掉看看是什么效果
    if (position > 0 || offset > 0) {
      newScrollX -= scrollOffset
    }
    if (newScrollX != lastScrollX) {
      lastScrollX = newScrollX
      scrollTo(newScrollX, 0)
    }
  }

  /**
   * 绘制indicator、underline和divider
   */
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (isInEditMode || tabCount == 0) return
    val height = height
    /*
     * 绘制underline(indicator的背景线)
     */
    if (showUnderline) {
      rectPaint.color = underlineColor
      canvas.drawRect(0f, height - underlineHeight, tabsContainer.width.toFloat(), height.toFloat(), rectPaint)
    }
    /*
     * 绘制indicator
     */
    if (tabWidth != 0) {
      //指定宽度
      rectPaint.color = indicatorColor
      val currentTab = tabsContainer.getChildAt(currentPosition)
      var tabCenter = currentTab.left + (currentTab.right - currentTab.left) / 2.0f
      if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
        val nextTab = tabsContainer.getChildAt(currentPosition + 1)
        val nexCenter = nextTab.left + (nextTab.right - nextTab.left) / 2.0f
        tabCenter += (nexCenter - tabCenter) * currentPositionOffset
      }
      canvas.drawRoundRect(
          tabCenter - tabWidth / 2.0f, height - indicatorHeight + tabTransY,
          tabCenter + tabWidth / 2.0f,
          height + tabTransY,
          tabRound.toFloat(), tabRound.toFloat(), rectPaint
      )
    } else {
      if (indicatorWrapText) { //indicator与文字等长
        rectPaint.color = indicatorColor
        getTextLocation(currentPosition)
        var lineLeft = textLocation.left.toFloat()
        var lineRight = textLocation.right.toFloat()
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
          getTextLocation(currentPosition + 1)
          val nextLeft = textLocation.left.toFloat()
          val nextRight = textLocation.right.toFloat()
          lineLeft += (nextLeft - lineLeft) * currentPositionOffset
          lineRight += (nextRight - lineRight) * currentPositionOffset
        }
        canvas.drawRoundRect(
            lineLeft, height - indicatorHeight + tabTransY, lineRight,
            height + tabTransY, tabRound.toFloat(), tabRound.toFloat(), rectPaint
        )
      } else { //indicator与tab等长
        rectPaint.color = indicatorColor
        val currentTab = tabsContainer.getChildAt(currentPosition)
        var lineLeft = currentTab.left.toFloat()
        var lineRight = currentTab.right.toFloat()
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
          val nextTab = tabsContainer.getChildAt(currentPosition + 1)
          val nextLeft = nextTab.left.toFloat()
          val nextRight = nextTab.right.toFloat()
          lineLeft += (nextLeft - lineLeft) * currentPositionOffset
          lineRight += (nextRight - lineRight) * currentPositionOffset
        }
        canvas.drawRoundRect(
            lineLeft, height - indicatorHeight + tabTransY, lineRight,
            height + tabTransY, tabRound.toFloat(), tabRound.toFloat(), rectPaint
        )
      }
    }

    /*
     * 绘制divider
     */
    if (showDivider) {
      dividerPaint.color = dividerColor
      for (i in 0 until tabCount - 1) {
        val tab = tabsContainer.getChildAt(i)
        canvas.drawLine(
            tab.right.toFloat(), dividerPadding.toFloat(), tab.right.toFloat(), height - dividerPadding.toFloat(), dividerPaint
        )
      }
    }
  }

  /**
   * 获得指定tab中，文字的left和right
   */
  private fun getTextLocation(position: Int) {
    val tab = tabsContainer.getChildAt(position)
    val tabText = viewPager.adapter?.getPageTitle(position)?.toString() ?: ""
    val textWidth = measureTextPaint.measureText(tabText)
    val tabWidth = tab.width
    textLocation.left = tab.left + ((tabWidth - textWidth) / 2).toInt()
    textLocation.right = tab.right - ((tabWidth - textWidth) / 2).toInt()
  }

  fun setExpand(expand: Boolean): SimpleViewpagerIndicator {
    this.expand = expand
    return this
  }

  fun setNoClickPosition(position: Int, call: (() -> Unit)? = null): SimpleViewpagerIndicator {
    mNoClickPosition = position
    mNoClickCall = call
    return this
  }

  fun setIndicatorWrapText(indicatorWrapText: Boolean): SimpleViewpagerIndicator {
    this.indicatorWrapText = indicatorWrapText
    return this
  }

  //setter--------------------------------------------------------------------------------------------
  fun setTabWidth(tabWidth: Int, tabRound: Int): SimpleViewpagerIndicator {
    this.tabWidth = tabWidth
    this.tabRound = tabRound
    return this
  }

  fun setIndicatorColor(indicatorColor: Int): SimpleViewpagerIndicator {
    this.indicatorColor = indicatorColor
    return this
  }

  fun setIndicatorHeight(indicatorHeight: Int): SimpleViewpagerIndicator {
    this.indicatorHeight = indicatorHeight
    return this
  }

  fun setShowUnderline(
      showUnderline: Boolean, underlineColor: Int,
      underlineHeight: Float
  ): SimpleViewpagerIndicator {
    this.showUnderline = showUnderline
    this.underlineColor = underlineColor
    this.underlineHeight = underlineHeight
    return this
  }

  fun setShowDivider(
      showDivider: Boolean, dividerColor: Int,
      dividerPadding: Int, dividerWidth: Int
  ): SimpleViewpagerIndicator {
    this.showDivider = showDivider
    this.dividerColor = dividerColor
    this.dividerPadding = dividerPadding
    this.dividerWidth = dividerWidth
    return this
  }

  fun setTabTextSize(tabTextSize: Int): SimpleViewpagerIndicator {
    this.tabTextSize = tabTextSize
    return this
  }

  fun setTabTextColor(tabTextColor: Int): SimpleViewpagerIndicator {
    this.tabTextColor = tabTextColor
    return this
  }

  fun setTabTypeface(tabTypeface: Typeface?): SimpleViewpagerIndicator {
    this.tabTypeface = tabTypeface
    return this
  }

  fun setTabTypefaceStyle(tabTypefaceStyle: Int): SimpleViewpagerIndicator {
    this.tabTypefaceStyle = tabTypefaceStyle
    return this
  }

  fun setTabBackgroundResId(tabBackgroundResId: Int): SimpleViewpagerIndicator {
    this.tabBackgroundResId = tabBackgroundResId
    return this
  }

  fun setTabPadding(tabPadding: Int): SimpleViewpagerIndicator {
    this.tabPadding = tabPadding
    return this
  }

  fun setSelectedTabTextSize(selectedTabTextSize: Int): SimpleViewpagerIndicator {
    this.selectedTabTextSize = selectedTabTextSize
    return this
  }

  fun setSelectedTabTextColor(selectedTabTextColor: Int): SimpleViewpagerIndicator {
    this.selectedTabTextColor = selectedTabTextColor
    return this
  }

  fun setSelectedTabTypeface(selectedTabTypeface: Typeface?): SimpleViewpagerIndicator {
    this.selectedTabTypeface = selectedTabTypeface
    return this
  }

  fun setSelectedTabTypefaceStyle(selectedTabTypefaceStyle: Int): SimpleViewpagerIndicator {
    this.selectedTabTypefaceStyle = selectedTabTypefaceStyle
    return this
  }

  fun setScrollOffset(scrollOffset: Int): SimpleViewpagerIndicator {
    this.scrollOffset = scrollOffset
    return this
  }

  fun setTabTransY(tansYdp: Float): SimpleViewpagerIndicator {
    tabTransY = tansYdp
    return this
  }

  fun setTextTransY(tansYdp: Float): SimpleViewpagerIndicator {
    textTransY = tansYdp
    return this
  }

  private fun dp2px(dpValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
  }

  internal inner class LeftRight {
    var left = 0
    var right = 0
  }

  private inner class PageListener : OnPageChangeListener {
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
      currentPosition = position
      currentPositionOffset = positionOffset
      val count = tabsContainer.childCount
      //需要选中的位置
      val sel = position + if (positionOffset > 0.5) 1 else 0
      for (i in 0 until count) {
        val tv = tabsContainer.getChildAt(i) as TextView
        if (i != sel) {
          tv.setTextColor(tabTextColor)
        } else {
          val offset = if (positionOffset > 0.5) 2 - 2 * positionOffset else 2 * positionOffset
          val color = mEvaluator.evaluate(offset, selectedTabTextColor, tabTextColor) as Int
          tv.setTextColor(color)
        }
      }
      //scrollView滚动
      scrollToChild(
          position,
          (positionOffset * tabsContainer.getChildAt(position).width).toInt()
      )
      invalidate() //invalidate后onDraw会被调用,绘制indicator、divider等
      if (userPageListener != null) {
        userPageListener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
      }
    }

    override fun onPageScrollStateChanged(state: Int) {
      if (state == ViewPager.SCROLL_STATE_IDLE) {
        scrollToChild(viewPager.currentItem, 0) //scrollView滚动
        updateTextStyle() //更新tab文字大小和样式
      }
      if (userPageListener != null) {
        userPageListener?.onPageScrollStateChanged(state)
      }
    }

    override fun onPageSelected(position: Int) {
      if (position == mNoClickPosition) {
        if (position > selectedPosition) { //往左滑,向右选中
          val mAdapter = viewPager.adapter
          var count = 0
          if (mAdapter != null) count = mAdapter.count
          if (position + 1 < count) viewPager.currentItem = position + 1
        } else if (position < selectedPosition) { //往右滑，向左选中
          if (position > 0) viewPager.currentItem = position - 1
        }
        return
      }
      selectedPosition = position
      //updateTextStyle();//更新tab文字大小和样式
      if (userPageListener != null) {
        userPageListener?.onPageSelected(position)
      }
    }
  }
}
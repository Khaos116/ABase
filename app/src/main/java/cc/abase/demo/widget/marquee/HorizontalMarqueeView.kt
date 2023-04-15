package cc.abase.demo.widget.marquee

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.*
import cc.abase.demo.ext.toHtmlSingle
import kotlinx.android.extensions.LayoutContainer

/**
 * 参考：https://github.com/leiyun1993/AutoScrollLayout
 * @Description 横向滚动跑马灯
 * @Author：Khaos
 * @Date：2021/3/2
 * @Time：9:56
 */
class HorizontalMarqueeView : FrameLayout, LifecycleObserver {
  //<editor-fold defaultstate="collapsed" desc="多构造">
  constructor(c: Context) : super(c, null, 0)
  constructor(c: Context, a: AttributeSet) : super(c, a, 0)
  constructor(c: Context, a: AttributeSet?, d: Int) : super(c, a, d)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //适配器
  private var mAdapter: MarqueeAdapter = MarqueeAdapter()

  //列表
  private var mRecyclerView: MarqueeRecyclerView = MarqueeRecyclerView(context)

  //点击事件
  var mCall: ((data: String) -> Unit)? = null
    set(value) {
      field = value
      mAdapter.onItemClick = value
    }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    mRecyclerView.overScrollMode = OVER_SCROLL_NEVER
    mRecyclerView.isHorizontalScrollBarEnabled = false
    mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    mRecyclerView.adapter = mAdapter
    addView(mRecyclerView, ViewGroup.LayoutParams(-1, -1))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  //设置新数据
  fun setNewDatas(list: MutableList<String>) {
    mAdapter.setNewDatas(list)
    postDelayed({ startScroll() }, 100)
    if (mLifecycle == null) setLifecycleOwner(getMyLifecycleOwner())
  }

  //添加新数据
  fun addData(data: String) {
    mAdapter.addData(data)
  }

  fun setTextSize(sp: Float) {
    mAdapter.mTextSizeSp = sp
  }

  fun setTextColor(@ColorInt color: Int) {
    mAdapter.mTextColor = color
  }

  fun setTextColorBg(@ColorInt color: Int) {
    mAdapter.mTextColorBg = color
  }

  fun setTextBold(bold: Boolean) {
    mAdapter.mTextBold = bold
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部类">
  //公告适配器
  inner class MarqueeAdapter(var onItemClick: ((item: String) -> Unit)? = null) : RecyclerView.Adapter<MarqueeViewHolder>() {
    //公告数据
    private var mData = mutableListOf<String>()
    var mTextColor: Int = Color.BLUE
    var mTextSizeSp: Float = 14f
    var mTextColorBg: Int = Color.LTGRAY
    var mTextBold: Boolean = false

    //设置新数据
    @SuppressLint("NotifyDataSetChanged")
    fun setNewDatas(list: MutableList<String>) {
      mData.clear()
      mData.addAll(list)
      notifyDataSetChanged()
    }

    //添加新数据
    @SuppressLint("NotifyDataSetChanged")
    fun addData(data: String) {
      mData.add(data)
      notifyDataSetChanged()
    }

    //适配器数量
    override fun getItemCount() = if (mData.size > 0) Int.MAX_VALUE else 0

    //获取真实数据大小
    fun getItemRealCount() = mData.size

    //创建ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MarqueeViewHolder(MarqueeItemLayout(parent.context).also {
      it.layoutParams = ViewGroup.LayoutParams(-2, -1)
      it.addView(TextView(it.context).also { tv ->
        tv.setTextColor(mTextColor)
        tv.maxLines = 1
        tv.ellipsize = TextUtils.TruncateAt.MARQUEE
        tv.marqueeRepeatLimit = -1
        tv.isSelected = true
        //tv.setSingleLine()//SingleLine解决富文本换行后的内容不显示问题，但是部分格式会造成闪退[PARAGRAPH span must end at paragraph boundary]
        tv.gravity = Gravity.CENTER_VERTICAL
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeSp)
        tv.setBackgroundColor(mTextColorBg)
        if (mTextBold) tv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        tv.setPadding(5.dp2px(), 0, 5.dp2px(), 0)
      }, FrameLayout.LayoutParams(-2, -1).also { p ->
        p.gravity = Gravity.CENTER_VERTICAL
        p.marginEnd = 20.dp2px()
      })
    })

    //绑定数据
    override fun onBindViewHolder(holder: MarqueeViewHolder, position: Int) {
      val newPosition = position % mData.size
      val itemView = holder.itemView
      if (itemView is MarqueeItemLayout && itemView.childCount > 0) {
        (itemView.getChildAt(0) as? TextView)?.let { tv ->
          //写在fillData前，方便fillData修改点击事件
          if (onItemClick == null) {
            tv.pressEffectDisable()
            tv.setOnClickListener(null)
          } else {
            tv.pressEffectAlpha()
            tv.click { onItemClick?.invoke(mData[newPosition]) }
          }
        }
      }
      //SDL方式填充数据，解决findViewById的麻烦
      holder.apply(fillData(mData[newPosition]))
    }

    //填充数据
    fun fillData(item: String): MarqueeViewHolder.() -> Unit = {
      if (containerView is MarqueeItemLayout && containerView.childCount > 0) {
        (containerView.getChildAt(0) as? TextView)?.let { tv ->
          tv.text = item.toHtmlSingle()
        }
      }
    }
  }

  //实现ViewHolder
  inner class MarqueeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

  //禁止手动滑动列表
  inner class MarqueeRecyclerView : RecyclerView {
    //<editor-fold defaultstate="collapsed" desc="多构造">
    constructor(c: Context) : super(c, null, 0)
    constructor(c: Context, a: AttributeSet) : super(c, a, 0)
    constructor(c: Context, a: AttributeSet?, d: Int) : super(c, a, d)
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="禁止手动滑动">
    override fun onInterceptTouchEvent(e: MotionEvent?) = false
    //</editor-fold>
  }

  //要求Item消费掉按压，避免传到RecyclerView引起滑动
  inner class MarqueeItemLayout : FrameLayout {
    //<editor-fold defaultstate="collapsed" desc="多构造">
    constructor(c: Context) : super(c, null, 0)
    constructor(c: Context, a: AttributeSet) : super(c, a, 0)
    constructor(c: Context, a: AttributeSet?, d: Int) : super(c, a, d)
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="强制处理按压事件，避免传到父控件引起滑动">
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
      super.onTouchEvent(event)
      return true
    }
    //</editor-fold>
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="自感应生命周期">
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    onAttachedToWindowLifecycle()
    startScroll()
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    onDetachedFromWindowLifecycle()
    stopScroll()
  }

  private fun onAttachedToWindowLifecycle() {
    setLifecycleOwner(getMyLifecycleOwner())
  }

  private fun onDetachedFromWindowLifecycle() {
    setLifecycleOwner(null)
    onPauseScroll()
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
  private fun onPauseScroll() {
    "ON_PAUSE暂停跑马灯".logI()
    stopScroll()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  private fun onResumeScroll() {
    if (visibility == View.VISIBLE) {
      "ON_RESUME恢复跑马灯".logI()
      startScroll()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="滚动开始和关闭">
  //开始滚动
  fun startScroll() {
    val realSize = mAdapter.getItemRealCount()
    if (mRecyclerView.childCount == 0 || realSize == 0) {
      "没有数据，跑马灯跑不起来".logI()
      return
    }
    //时长用最大值
    val duration = Int.MAX_VALUE
    //通过最大移动距离来控制跑马灯的速度
    val max: Int = Int.MAX_VALUE / 20
    mRecyclerView.smoothScrollBy(max, 0, LinearInterpolator(), duration)
  }

  /**
   * 关闭滚动
   * @return 返回停止滚动的位置和偏移量
   */
  fun stopScroll() {
    val realSize = mAdapter.getItemRealCount()
    if (mRecyclerView.childCount == 0 || realSize == 0) return
    mRecyclerView.stopInertiaRolling()
    (mRecyclerView.layoutManager as LinearLayoutManager).let { m ->
      //找到位置
      val firstVisiblePosition = m.findFirstVisibleItemPosition()
      //偏移量
      var offset = 0
      //找到第一个View计算偏移量
      mRecyclerView.findViewHolderForAdapterPosition(firstVisiblePosition)?.let { offset = it.itemView.left }
      if (firstVisiblePosition >= realSize) {
        val newPosition = firstVisiblePosition % realSize
        m.scrollToPositionWithOffset(newPosition, offset)
        "重置跑马灯位置:newPosition=$newPosition,offset=$offset".logI()
      } else {
        "直接暂停跑马灯，不用重置位置".logI()
      }
    }
  }
  //</editor-fold>
}

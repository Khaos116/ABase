package cc.abase.demo.widget.marquee

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.*
import kotlinx.android.extensions.LayoutContainer

/**
 * 参考：https://github.com/leiyun1993/AutoScrollLayout
 * @Description 横向滚动跑马灯
 * @Author：Khaos
 * @Date：2021/3/2
 * @Time：9:56
 */
class HorizontalMarqueeView : FrameLayout {
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
    smoothScroll()
  }

  //添加新数据
  fun addData(data: String) {
    mAdapter.addData(data)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="滚动">
  private fun smoothScroll() {
    val max = Int.MAX_VALUE / 20
    val duration = max * 20
    mRecyclerView.smoothScrollBy(max, 0, LinearInterpolator(), duration)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部类">
  //公告适配器
  inner class MarqueeAdapter(var onItemClick: ((item: String) -> Unit)? = null) : RecyclerView.Adapter<MarqueeViewHolder>() {
    //公告数据
    private var mData = mutableListOf<String>()

    //设置新数据
    fun setNewDatas(list: MutableList<String>) {
      mData.clear()
      mData.addAll(list)
      notifyDataSetChanged()
    }

    //添加新数据
    fun addData(data: String) {
      mData.add(data)
      notifyDataSetChanged()
    }

    //适配器数量
    override fun getItemCount() = if (mData.size > 0) Int.MAX_VALUE else 0

    //创建ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MarqueeViewHolder(MarqueeItemLayout(parent.context).also {
      it.layoutParams = ViewGroup.LayoutParams(-2, -1)
      it.addView(TextView(it.context).also { tv ->
        tv.setTextColor(Color.BLUE)
        tv.gravity = Gravity.CENTER_VERTICAL
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tv.setBackgroundColor(Color.LTGRAY)
        tv.setPadding(5.dp2px(), 0, 5.dp2px(), 0)
      }, MarginLayoutParams(-2, -1).also { p -> p.marginEnd = 20.dp2px() })
    })

    //绑定数据
    override fun onBindViewHolder(holder: MarqueeViewHolder, position: Int) {
      val newPosition = position % mData.size
      if (holder.itemView is MarqueeItemLayout && holder.itemView.childCount > 0) {
        (holder.itemView.getChildAt(0) as? TextView)?.let { tv ->
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
          tv.text = item
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
}
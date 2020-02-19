package cc.abase.demo.widget

import android.graphics.*
import android.graphics.Paint.Style.FILL
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.State

/**
 * https://blog.csdn.net/Common_it/article/details/89374546
 * 万能分割线
 *
 *
 * 横向列表分割线 [.HORIZONTAL_DIV]
 * 纵向列表分割线 [.VERTICAL_DIV]
 * 表格列表分割线 [.GRID_DIV]
 *
 */
class ItemDecorationPowerful @JvmOverloads constructor(
  orientation: Int = VERTICAL_DIV,
  color: Int = Color.TRANSPARENT,
  divWidth: Int = 2
) : ItemDecoration() {
  private var mOrientation = 0
  private var mDividerWidth = 0
  private val mPaint: Paint
  /**
   * 初始化分割线类型
   *
   * @param orientation 分割线类型
   */
  fun setOrientation(orientation: Int) {
    require(
        !(mOrientation != HORIZONTAL_DIV && mOrientation != VERTICAL_DIV && mOrientation != GRID_DIV)
    ) { "ItemDecorationPowerful：分割线类型设置异常" }
    mOrientation = orientation
  }

  override fun onDraw(
    c: Canvas,
    parent: RecyclerView,
    state: State
  ) {
    when (mOrientation) {
      HORIZONTAL_DIV ->  //横向布局分割线
        drawHorizontal(c, parent)
      VERTICAL_DIV ->  //纵向布局分割线
        drawVertical(c, parent)
      GRID_DIV ->  //表格格局分割线
        drawGrid(c, parent)
      else ->  //纵向布局分割线
        drawVertical(c, parent)
    }
  }

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: State
  ) {
    val itemPosition = parent.getChildAdapterPosition(view)
    val mAdapter = parent.adapter
    if (mAdapter != null) {
      val mChildCount = mAdapter.itemCount
      when (mOrientation) {
        HORIZONTAL_DIV ->
          /**
           * 横向布局分割线
           *
           *
           * 如果是第一个Item，则不需要分割线
           *
           */
          if (itemPosition != 0) {
            outRect[mDividerWidth, 0, 0] = 0
          }
        VERTICAL_DIV ->
          /**
           * 纵向布局分割线
           *
           *
           * 如果是第一个Item，则不需要分割线
           *
           */
          if (itemPosition != 0) {
            outRect[0, mDividerWidth, 0] = 0
          }
        GRID_DIV -> {
          /**
           * 表格格局分割线
           *
           *
           * 1：当是第一个Item的时候，四周全部需要分割线
           * 2：当是第一行Item的时候，需要额外添加顶部的分割线
           * 3：当是第一列Item的时候，需要额外添加左侧的分割线
           * 4：默认情况全部添加底部和右侧的分割线
           *
           */
          val mLayoutManager = parent.layoutManager
          if (mLayoutManager is GridLayoutManager) {
            val mSpanCount = mLayoutManager.spanCount
            if (itemPosition == 0) { //1
              outRect[mDividerWidth, mDividerWidth, mDividerWidth] = mDividerWidth
            } else if (itemPosition + 1 <= mSpanCount) { //2
              outRect[0, mDividerWidth, mDividerWidth] = mDividerWidth
            } else if ((itemPosition + mSpanCount) % mSpanCount == 0) { //3
              outRect[mDividerWidth, 0, mDividerWidth] = mDividerWidth
            } else { //4
              outRect[0, 0, mDividerWidth] = mDividerWidth
            }
          }
        }
        else ->  //纵向布局分割线
          if (itemPosition != mChildCount - 1) {
            outRect[0, 0, 0] = mDividerWidth
          }
      }
    }
  }

  /**
   * 绘制横向列表分割线
   *
   * @param c 绘制容器
   * @param parent RecyclerView
   */
  private fun drawHorizontal(
    c: Canvas,
    parent: RecyclerView
  ) {
    val mChildCount = parent.childCount
    for (i in 0 until mChildCount) {
      val mChild = parent.getChildAt(i)
      drawLeft(c, mChild, parent)
    }
  }

  /**
   * 绘制纵向列表分割线
   *
   * @param c 绘制容器
   * @param parent RecyclerView
   */
  private fun drawVertical(
    c: Canvas,
    parent: RecyclerView
  ) {
    val mChildCount = parent.childCount
    for (i in 0 until mChildCount) {
      val mChild = parent.getChildAt(i)
      drawTop(c, mChild, parent)
    }
  }

  /**
   * 绘制表格类型分割线
   *
   * @param c 绘制容器
   * @param parent RecyclerView
   */
  private fun drawGrid(
    c: Canvas,
    parent: RecyclerView
  ) {
    val mChildCount = parent.childCount
    for (i in 0 until mChildCount) {
      val mChild = parent.getChildAt(i)
      val mLayoutManager = parent.layoutManager
      if (mLayoutManager is GridLayoutManager) {
        val mSpanCount = mLayoutManager.spanCount
        if (i == 0) {
          drawTop(c, mChild, parent)
          drawLeft(c, mChild, parent)
        }
        if (i + 1 <= mSpanCount) {
          drawTop(c, mChild, parent)
        }
        if ((i + mSpanCount) % mSpanCount == 0) {
          drawLeft(c, mChild, parent)
        }
        drawRight(c, mChild, parent)
        drawBottom(c, mChild, parent)
      }
    }
  }

  /**
   * 绘制右边分割线
   *
   * @param c 绘制容器
   * @param mChild 对应ItemView
   * @param recyclerView RecyclerView
   */
  private fun drawLeft(
    c: Canvas,
    mChild: View,
    recyclerView: RecyclerView
  ) {
    val mChildLayoutParams =
      mChild.layoutParams as LayoutParams
    val left = mChild.left - mDividerWidth - mChildLayoutParams.leftMargin
    val top = mChild.top - mChildLayoutParams.topMargin
    val right = mChild.left - mChildLayoutParams.leftMargin
    val bottom: Int
    bottom = if (isGridLayoutManager(recyclerView)) {
      mChild.bottom + mChildLayoutParams.bottomMargin + mDividerWidth
    } else {
      mChild.bottom + mChildLayoutParams.bottomMargin
    }
    c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
  }

  /**
   * 绘制顶部分割线
   *
   * @param c 绘制容器
   * @param mChild 对应ItemView
   * @param recyclerView RecyclerView
   */
  private fun drawTop(
    c: Canvas,
    mChild: View,
    recyclerView: RecyclerView
  ) {
    val mChildLayoutParams =
      mChild.layoutParams as LayoutParams
    val left: Int
    val top = mChild.top - mChildLayoutParams.topMargin - mDividerWidth
    val right = mChild.right + mChildLayoutParams.rightMargin
    val bottom = mChild.top - mChildLayoutParams.topMargin
    left = if (isGridLayoutManager(recyclerView)) {
      mChild.left - mChildLayoutParams.leftMargin - mDividerWidth
    } else {
      mChild.left - mChildLayoutParams.leftMargin
    }
    c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
  }

  /**
   * 绘制右边分割线
   *
   * @param c 绘制容器
   * @param mChild 对应ItemView
   * @param recyclerView RecyclerView
   */
  private fun drawRight(
    c: Canvas,
    mChild: View,
    recyclerView: RecyclerView
  ) {
    val mChildLayoutParams =
      mChild.layoutParams as LayoutParams
    val left = mChild.right + mChildLayoutParams.rightMargin
    val top: Int
    val right = left + mDividerWidth
    val bottom = mChild.bottom + mChildLayoutParams.bottomMargin
    top = if (isGridLayoutManager(recyclerView)) {
      mChild.top - mChildLayoutParams.topMargin - mDividerWidth
    } else {
      mChild.top - mChildLayoutParams.topMargin
    }
    c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
  }

  /**
   * 绘制底部分割线
   *
   * @param c 绘制容器
   * @param mChild 对应ItemView
   * @param recyclerView RecyclerView
   */
  private fun drawBottom(
    c: Canvas,
    mChild: View,
    recyclerView: RecyclerView
  ) {
    val mChildLayoutParams =
      mChild.layoutParams as LayoutParams
    val left = mChild.left - mChildLayoutParams.leftMargin
    val top = mChild.bottom + mChildLayoutParams.bottomMargin
    val bottom = top + mDividerWidth
    val right: Int
    right = if (isGridLayoutManager(recyclerView)) {
      mChild.right + mChildLayoutParams.rightMargin + mDividerWidth
    } else {
      mChild.right + mChildLayoutParams.rightMargin
    }
    c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
  }

  /**
   * 判断RecyclerView所加载LayoutManager是否为GridLayoutManager
   *
   * @param recyclerView RecyclerView
   * @return 是GridLayoutManager返回true，否则返回false
   */
  private fun isGridLayoutManager(recyclerView: RecyclerView): Boolean {
    val mLayoutManager = recyclerView.layoutManager
    return mLayoutManager is GridLayoutManager
  }

  companion object {
    //横向布局分割线
    const val HORIZONTAL_DIV = 0
    //纵向布局分割线
    const val VERTICAL_DIV = 1
    //表格布局分割线
    const val GRID_DIV = 2
    private const val TAG = "ItemDecorationPowerful"
  }
  /**
   * @param orientation 方向类型
   * @param color 分割线颜色
   * @param divWidth 分割线宽度
   */
  /**
   * @param orientation 方向类型
   */
  /**
   * 默认纵向布分割线
   */
  init {
    setOrientation(orientation)
    mDividerWidth = divWidth
    mPaint = Paint()
    mPaint.isAntiAlias = true
    mPaint.color = color
    mPaint.style = FILL
  }
}
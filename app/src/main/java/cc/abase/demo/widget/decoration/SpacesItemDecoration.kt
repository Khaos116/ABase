package cc.abase.demo.widget.decoration

import android.content.res.Resources
import android.graphics.*
import android.graphics.Paint.Style.FILL
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.State
import cc.abase.demo.R
import com.blankj.utilcode.util.*

/*
 * Copyright 2019. Bin Jing (https://github.com/youlookwhat)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * 给 LinearLayoutManager 增加分割线，可设置去除首尾分割线个数
 *
 * @author jingbin
 * https://github.com/youlookwhat/ByRecyclerView
 */
class SpacesItemDecoration @JvmOverloads constructor(private val mOrientation: Int = LinearLayoutManager.VERTICAL,
    private val mHeaderNoShowSize: Int = 0,/*头部 不显示分割线的item个数 headerViewSize + RefreshViewSize*/
    private val mFooterNoShowSize: Int = 0/*尾部 不显示分割线的item个数 footerViewSize*/) : ItemDecoration() {
  //<editor-fold defaultstate="collapsed" desc="变量区">
  private var mDivider: Drawable?
  private val mBounds = Rect()
  private var mPaint: Paint? = null
  private val attrs = intArrayOf(android.R.attr.listDivider)

  /**
   * 如果是横向 - 宽度
   * 如果是纵向 - 高度
   */
  private var mDividerSpacing = 0

  /**
   * 如果是横向 - 左边距
   * 如果是纵向 - 上边距
   */
  private var mLeftTopPadding = 0

  /**
   * 如果是横向 - 右边距
   * 如果是纵向 - 下边距
   */
  private var mRightBottomPadding = 0
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    val a = Utils.getApp().obtainStyledAttributes(attrs)
    mDivider = a.getDrawable(0)
    a.recycle()
    if (mPaint == null) setParam(dividerColor = ColorUtils.getColor(R.color.dividerColor), dividerSpacing = SizeUtils.dp2px(0.5f))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="设置Drawable分割线">
  fun setDrawable(@DrawableRes id: Int): SpacesItemDecoration {
    ContextCompat.getDrawable(Utils.getApp(), id)?.let { setDrawable(it) }
    return this
  }

  /**
   * Sets the [Drawable] for this divider.
   *
   * @param drawable Drawable that should be used as a divider.
   */
  fun setDrawable(drawable: Drawable): SpacesItemDecoration {
    mDivider = drawable
    return this
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="设置Color分割线">
  /**
   * 直接设置分割线颜色等，不设置drawable
   *
   * @param dividerColor 分割线颜色
   * @param dividerSpacing 分割线间距
   * @param leftTopPaddingDp 如果是横向 - 左边距
   * 如果是纵向 - 上边距
   * @param rightBottomPaddingDp 如果是横向 - 右边距
   * 如果是纵向 - 下边距
   */
  fun setParam(@ColorInt dividerColor: Int,
      dividerSpacing: Int,
      leftTopPaddingDp: Float = 0f,
      rightBottomPaddingDp: Float = 0f): SpacesItemDecoration {
    mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    mPaint?.style = FILL
    mPaint?.color = dividerColor
    mDividerSpacing = dividerSpacing
    mLeftTopPadding = dip2px(leftTopPaddingDp)
    mRightBottomPadding = dip2px(rightBottomPaddingDp)
    mDivider = null
    return this
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绘制分割线">
  override fun onDraw(canvas: Canvas, parent: RecyclerView, state: State) {
    if (parent.layoutManager == null || mDivider == null && mPaint == null) {
      return
    }
    if (mOrientation == LinearLayoutManager.VERTICAL) {
      drawVertical(canvas, parent, state)
    } else {
      drawHorizontal(canvas, parent, state)
    }
  }

  private fun drawVertical(canvas: Canvas, parent: RecyclerView, state: State) {
    canvas.save()
    val left: Int
    val right: Int
    if (parent.clipToPadding) {
      left = parent.paddingLeft
      right = parent.width - parent.paddingRight
      canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
    } else {
      left = 0
      right = parent.width
    }
    val childCount = parent.childCount
    val lastPosition = state.itemCount - 1
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val childRealPosition = parent.getChildAdapterPosition(child)

      // 过滤到头部不显示的分割线
      if (childRealPosition < mHeaderNoShowSize) {
        continue
      }
      // 过滤到尾部不显示的分割线
      if (childRealPosition <= lastPosition - mFooterNoShowSize) {
        if (mDivider != null) {
          parent.getDecoratedBoundsWithMargins(child, mBounds)
          val bottom = mBounds.bottom + Math.round(child.translationY)
          val top = bottom - (mDivider?.intrinsicHeight ?: 0)
          mDivider?.setBounds(left, top, right, bottom)
          mDivider?.draw(canvas)
        }
        mPaint?.let { p ->
          val params = child.layoutParams as LayoutParams
          val left1 = left + mLeftTopPadding
          val right1 = right - mRightBottomPadding
          val top1 = child.bottom + params.bottomMargin
          val bottom1 = top1 + mDividerSpacing
          canvas.drawRect(left1.toFloat(), top1.toFloat(), right1.toFloat(), bottom1.toFloat(), p)
        }
      }
    }
    canvas.restore()
  }

  private fun drawHorizontal(canvas: Canvas, parent: RecyclerView, state: State) {
    canvas.save()
    val top: Int
    val bottom: Int
    if (parent.clipToPadding) {
      top = parent.paddingTop
      bottom = parent.height - parent.paddingBottom
      canvas.clipRect(parent.paddingLeft, top,
          parent.width - parent.paddingRight, bottom)
    } else {
      top = 0
      bottom = parent.height
    }
    val childCount = parent.childCount
    val lastPosition = state.itemCount - 1
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val childRealPosition = parent.getChildAdapterPosition(child)

      // 过滤到头部不显示的分割线
      if (childRealPosition < mHeaderNoShowSize) {
        continue
      }
      // 过滤到尾部不显示的分割线
      if (childRealPosition <= lastPosition - mFooterNoShowSize) {
        if (mDivider != null) {
          parent.getDecoratedBoundsWithMargins(child, mBounds)
          val right = mBounds.right + Math.round(child.translationX)
          val left = right - (mDivider?.intrinsicWidth ?: 0)
          mDivider?.setBounds(left, top, right, bottom)
          mDivider?.draw(canvas)
        }
        mPaint?.let { p ->
          val params = child.layoutParams as LayoutParams
          val left1 = child.right + params.rightMargin
          val right1 = left1 + mDividerSpacing
          val top1 = top + mLeftTopPadding
          val bottom1 = bottom - mRightBottomPadding
          canvas.drawRect(left1.toFloat(), top1.toFloat(), right1.toFloat(), bottom1.toFloat(), p)
        }
      }
    }
    canvas.restore()
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
      state: State) {
    if (mDivider == null && mPaint == null) {
      outRect[0, 0, 0] = 0
      return
    }
    //parent.getChildCount() 不能拿到item的总数
    val lastPosition = state.itemCount - 1
    val position = parent.getChildAdapterPosition(view)

    //=============修改代码Start=============//
    val mScrollTopFix = System.currentTimeMillis() < 0
    //boolean mScrollTopFix = false;
    //if (byRecyclerView == null && parent instanceof ByRecyclerView) {
    //  byRecyclerView = (ByRecyclerView) parent;
    //}
    //if (byRecyclerView != null && byRecyclerView.isRefreshEnabled()) {
    //  mScrollTopFix = true;
    //}
    //=============修改代码End=============//

    // 滚动条置顶
    val isFixScrollTop = mScrollTopFix && position == 0
    val isShowDivider = mHeaderNoShowSize <= position && position <= lastPosition - mFooterNoShowSize
    if (mOrientation == LinearLayoutManager.VERTICAL) {
      when {
        isFixScrollTop -> outRect[0, 0, 0] = 1
        isShowDivider -> outRect[0, 0, 0] = if (mDivider != null) mDivider?.intrinsicHeight ?: 0 else mDividerSpacing
        else -> outRect[0, 0, 0] = 0
      }
    } else {
      when {
        isFixScrollTop -> outRect[0, 0, 1] = 0
        isShowDivider -> outRect[0, 0, if (mDivider != null) mDivider?.intrinsicWidth ?: 0 else mDividerSpacing] = 0
        else -> outRect[0, 0, 0] = 0
      }
    }
  }

  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   */
  private fun dip2px(dpValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
  }
  //</editor-fold>
}
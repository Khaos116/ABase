package cc.abase.demo.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

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
 * 给 GridLayoutManager or StaggeredGridLayoutManager 设置间距，可设置去除首尾间距个数
 *
 * @author jingbin
 * https://github.com/youlookwhat/ByRecyclerView
 */
class GridSpaceItemDecoration @JvmOverloads constructor(
    private val mSpacing: Int,//item 间距
    private val mIncludeStartEnd: Boolean = false,/*距屏幕左右是否有间距*/
    private val mIncludeTop: Boolean = false,/*开始的第一排顶部是否有间距*/
    private val mIncludeBottom: Boolean = false/*结束的最后一排底部是否也有间距*/) : ItemDecoration() {
  //每行个数
  private var mSpanCount = 0

  //头部 不显示间距的item个数
  private var mStartFromSize = 0

  //尾部 不显示间距的item个数
  private var mEndFromSize = 0

  //瀑布流 头部第一个整行的position
  private var fullPosition = -1

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val lastPosition = state.itemCount - 1
    var position = parent.getChildAdapterPosition(view)
    if (mStartFromSize <= position && position <= lastPosition - mEndFromSize) {
      var isFirstfRow = false//是否是第一行(新增的修改代码)
      // 行
      var spanGroupIndex = -1
      // 列
      var column = 0
      // 瀑布流是否占满一行
      var fullSpan = false
      val layoutManager = parent.layoutManager
      if (layoutManager is GridLayoutManager) {
        val spanSizeLookup = layoutManager.spanSizeLookup
        val spanCount = layoutManager.spanCount
        // 当前position的spanSize
        val spanSize = spanSizeLookup.getSpanSize(position)
        // 一行几个
        mSpanCount = spanCount / spanSize
        // =0 表示是最左边 0 2 4
        val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount)
        // 列
        column = spanIndex / spanSize
        // 行 减去mStartFromSize,得到从0开始的行
        spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount) - mStartFromSize
      } else if (layoutManager is StaggeredGridLayoutManager) {
        // 瀑布流获取列方式不一样
        val params =
            view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        // 列
        column = params.spanIndex
        // 是否是全一行
        fullSpan = params.isFullSpan
        mSpanCount = layoutManager.spanCount
      }
      // 减掉不设置间距的position,得到从0开始的position
      position -= mStartFromSize
      if (mIncludeStartEnd) {
        /*
         *示例：
         * spacing = 10 ；spanCount = 3
         * ---------10--------
         * 10   3+7   6+4    10
         * ---------10--------
         * 10   3+7   6+4    10
         * ---------10--------
         */
        if (fullSpan) {
          outRect.left = 0
          outRect.right = 0
        } else {
          outRect.left = mSpacing - column * mSpacing / mSpanCount
          outRect.right = (column + 1) * mSpacing / mSpanCount
        }
        if (spanGroupIndex > -1) {
          // grid 显示规则
          if (spanGroupIndex < 1 && position < mSpanCount) {
            // 第一行才有上间距
            outRect.top = mSpacing
            isFirstfRow = true
          }
        } else {
          if (fullPosition == -1 && position < mSpanCount && fullSpan) {
            // 找到头部第一个整行的position，后面的上间距都不显示
            fullPosition = position
          }
          // Stagger显示规则 头部没有整行或者头部体验整行但是在之前的position显示上间距
          val isFirstLineStagger = (fullPosition == -1 || position < fullPosition) && position < mSpanCount
          if (isFirstLineStagger) {
            // 第一行才有上间距
            outRect.top = mSpacing
            isFirstfRow = true
          }
        }
        outRect.bottom = mSpacing
      } else {//屏幕左右无间距
        isFirstfRow = true
        /*
         *示例：
         * spacing = 10 ；spanCount = 3
         * --------0--------
         * 0   3+7   6+4    0
         * -------10--------
         * 0   3+7   6+4    0
         * --------0--------
         */
        if (fullSpan) {
          outRect.left = 0
          outRect.right = 0
        } else {
          outRect.left = column * mSpacing / mSpanCount
          outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount
        }
        if (spanGroupIndex > -1) {
          if (spanGroupIndex >= 1) {
            // 超过第0行都显示上间距
            outRect.top = mSpacing
            isFirstfRow = false
          }
        } else {
          if (fullPosition == -1 && position < mSpanCount && fullSpan) {
            // 找到头部第一个整行的position
            fullPosition = position
          }
          // Stagger上间距显示规则
          val isStaggerShowTop = position >= mSpanCount || fullSpan && position != 0 || fullPosition != -1 && position != 0
          if (isStaggerShowTop) {
            // 超过第0行都显示上间距
            outRect.top = mSpacing
            isFirstfRow = false
          }
        }
      }
      //=============修改代码Start=============//
      if (mIncludeTop) {//有顶部间距
        if (mIncludeBottom) {//有底部间距
          //有顶+有底->处理第一行顶部
          outRect.top = if (isFirstfRow) mSpacing else 0
          outRect.bottom = mSpacing
        } else {//无底部间距
          //有顶+无底->只需要顶部
          outRect.top = mSpacing
          outRect.bottom = 0
        }
      } else {//无顶部间距
        if (mIncludeBottom) {//有底部间距
          //无顶+有底->只需要底部
          outRect.top = 0
          outRect.bottom = mSpacing
        } else {//无底部间距
          //无顶+无底->只需要处理第一排的顶部
          outRect.top = if (isFirstfRow) 0 else mSpacing
          outRect.bottom = 0
        }
      }
      //=============修改代码End=============//
    }
  }

  /**
   * 设置从哪个位置 开始设置间距
   *
   * @param startFromSize 一般为HeaderView的个数 + 刷新布局(不一定设置)
   */
  fun setStartFrom(startFromSize: Int): GridSpaceItemDecoration {
    mStartFromSize = startFromSize
    return this
  }

  /**
   * 设置从哪个位置 结束设置间距。默认为1，默认用户设置了上拉加载
   *
   * @param endFromSize 一般为FooterView的个数 + 加载更多布局(不一定设置)
   */
  fun setEndFromSize(endFromSize: Int): GridSpaceItemDecoration {
    mEndFromSize = endFromSize
    return this
  }

  /**
   * 设置从哪个位置 结束设置间距
   *
   * @param startFromSize 一般为HeaderView的个数 + 刷新布局(不一定设置)
   * @param endFromSize   默认为1，一般为FooterView的个数 + 加载更多布局(不一定设置)
   */
  fun setNoShowSpace(startFromSize: Int, endFromSize: Int): GridSpaceItemDecoration {
    mStartFromSize = startFromSize
    mEndFromSize = endFromSize
    return this
  }
}
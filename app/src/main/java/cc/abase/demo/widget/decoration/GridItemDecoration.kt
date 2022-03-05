package cc.abase.demo.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.logE
import com.blankj.utilcode.util.GsonUtils

/**
 * Author:Khaos116
 * Date:2022/3/4
 * Time:10:44
 */
class GridItemDecoration(
  private val spacing: Int,
  private val hasStartEnd: Boolean = true,
  private val hasTop: Boolean = true,
  private val hasBottom: Boolean = true,
  private val canDrag: Boolean = false
) : RecyclerView.ItemDecoration() {

  //<editor-fold defaultstate="collapsed" desc="如果分割线不能均分，会适当调节">
  private var mSpacing = spacing
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算并修改分割线间距">
  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val layoutManager = parent.layoutManager
    if (layoutManager is GridLayoutManager) {
      outRect.setEmpty()
      val position = parent.getChildAdapterPosition(view)
      if (position == RecyclerView.NO_POSITION) return
      calculatePositionDetails(parent, position, layoutManager)
      if (currentSpanSize == mSpanCount) {//占满整行
        outRect.left = if (hasStartEnd) spacing else 0
        outRect.right = if (hasStartEnd) spacing else 0
      } else {
        //左右计算参考 https://github.com/youlookwhat/ByRecyclerView/blob/master/ByRecyclerview/src/main/java/me/jingbin/library/decoration/GridSpaceItemDecoration.java
        //当前行是否均分
        var isAverage = true
        val itemCount = parent.adapter?.itemCount ?: 0
        val endPosition = (currentRowStartPosition + mSpanCount).coerceAtMost(itemCount)
        var tempSize = 0
        for (i in currentRowStartPosition until endPosition) {
          val size = layoutManager.spanSizeLookup.getSpanSize(i)
          if (tempSize == 0) {
            tempSize = size
          } else if (size != tempSize) {
            isAverage = false
            break
          }
        }
        val count = if (isAverage) mSpanCount / tempSize else currentRowColumnSize//实际列数
        val column = currentColumn//当前列数
        if (isAverage) {
          if (hasStartEnd) {//有左右间距
            //总间距：mSpacing * (count + 1) = (outRect.left + outRect.right) * count
            outRect.left = mSpacing - column * mSpacing / count
            outRect.right = (column + 1) * mSpacing / count
          } else {
            //总间距：mSpacing * (count - 1) = (outRect.left + outRect.right) * count
            outRect.left = column * mSpacing / count
            outRect.right = mSpacing - (column + 1) * mSpacing / count
          }
        } else {
          if (hasStartEnd) {//有左右间距
            outRect.left = mSpacing - column * mSpacing / count
            outRect.right = (column + 1) * mSpacing / count
          } else {
            outRect.left = column * mSpacing / count
            outRect.right = mSpacing - (column + 1) * mSpacing / count
          }
        }
        "count=$count,column=$column".logE()
      }
      outRect.top = if (isInFirstRow && !canDrag) if (hasTop) spacing else 0 else spacing / 2
      outRect.bottom = if (isInLastRow && !canDrag) if (hasBottom) spacing else 0 else spacing / 2
      "position=$position,outRect=${GsonUtils.toJson(outRect)}".logE()
      "================================================================".logE()
    } else {
      super.getItemOffsets(outRect, view, parent, state)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="根据当前位置计算边缘情况">
  private var isInFirstRow = false//是否在第一行
  private var isInLastRow = false//是否在最后一行，如果只有一行isInFirstRow和isInLastRow都为true
  private var currentRow = 0//当前行数
  private var currentColumn = 0//当前列数
  private var currentSpanSize = 0//当前位置所占列数
  private var currentRowColumnSize = 0//当前所在行的总列数
  private var lastRowStartPosition = 0//最后一排的开始位置
  private var currentRowStartPosition = 0//当前行数的开始位置
  private var mSpanCount = 0//每行列数

  /**
   * 计算位置
   */
  private fun calculatePositionDetails(recyclerView: RecyclerView, position: Int, layoutManager: GridLayoutManager) {
    val itemCount = recyclerView.adapter?.itemCount ?: 0
    val spanSizeLookup: SpanSizeLookup = layoutManager.spanSizeLookup
    val spanCount: Int = layoutManager.spanCount
    mSpanCount = spanCount
    isInFirstRow = isInFirstRow(position, spanSizeLookup, spanCount)
    isInLastRow = isInLastRow(position, itemCount, spanSizeLookup, spanCount)
    currentRow = calculateRow(position, spanSizeLookup, spanCount)
    currentColumn = calculateColumn(position, spanSizeLookup, spanCount)
    currentRowColumnSize = calculateRowColumnSize(position, itemCount, spanSizeLookup, spanCount)
    currentSpanSize = spanSizeLookup.getSpanSize(position)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="是否是第一行">
  /**
   * @param position 当前位置
   * @param spanSizeLookup 每行分配
   * @param spanCount 每行数量
   */
  private fun isInFirstRow(position: Int, spanSizeLookup: SpanSizeLookup, spanCount: Int): Boolean {
    //第一个肯定在第0行
    if (position == 0) {
      "第一行的position=0".logE()
      return true
    }
    var totalSpan = 0
    //如果当前位置前面的数量都大于等于了每行的数量，则当前肯定不是第一行
    for (i in 0 until position) {
      totalSpan += spanSizeLookup.getSpanSize(i)
      if (totalSpan >= spanCount) {
        return false
      }
    }
    "第一行的position=$position".logE()
    return true
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="是否是最后一行">
  /**
   * @param position 当前位置
   * @param itemCount 总数量
   * @param spanSizeLookup 每行分配
   * @param spanCount 每行数量
   */
  private fun isInLastRow(position: Int, itemCount: Int, spanSizeLookup: SpanSizeLookup, spanCount: Int): Boolean {
    //最后一个肯定在最后一行
    if (position >= itemCount - 1) {
      "最后一行的position=$position".logE()
      return true
    }
    var totalSpan = 0
    for (i in 0 until itemCount) {
      totalSpan += spanSizeLookup.getSpanSize(i)
    }
    //总数都没有超过每行数量，则肯定在最后一行
    if (totalSpan <= spanCount) {
      "最后一行的position=$position".logE()
      return true
    }
    //最后一行的数量
    val lastRowCount = if (totalSpan % spanCount == 0) spanCount else totalSpan % spanCount
    totalSpan = 0
    //从最后一个位置到当前position，判断有多少个，如果大于最后一行的数量，则返回非最后一行
    for (i in itemCount - 1 downTo position + 1) {
      totalSpan += spanSizeLookup.getSpanSize(i)
      if (totalSpan >= lastRowCount) {
        if (totalSpan == lastRowCount && position == i) {
          lastRowStartPosition = i
          "最后一行的开始位置position=$lastRowStartPosition".logE()
        }
        return false
      }
    }
    "最后一行的position=$position".logE()
    return true
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算当前行数">
  /**
   * @param position 当前位置
   * @param spanSizeLookup 每行分配
   * @param spanCount 每行数量
   */
  private fun calculateRow(position: Int, spanSizeLookup: SpanSizeLookup, spanCount: Int): Int {
    //第一个肯定在第0行
    if (position == 0) {
      "position=${position}所在行数=0,开始位置=0".logE()
      return 0
    }
    var totalSpan = 0
    currentRowStartPosition = 0
    for (i in 0 until position) {
      totalSpan += spanSizeLookup.getSpanSize(i)
      if (totalSpan % spanCount == 0) {
        currentRowStartPosition = i + 1
      }
    }
    "position=${position}所在行数=${totalSpan / spanCount},开始位置=${currentRowStartPosition}".logE()
    return totalSpan / spanCount
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算当前列数">
  /**
   * @param position 当前位置
   * @param spanSizeLookup 每行分配
   * @param spanCount 每行数量
   */
  private fun calculateColumn(position: Int, spanSizeLookup: SpanSizeLookup, spanCount: Int): Int {
    //第一个肯定在第0列
    if (position == 0) {
      "position=${position}所在列数=0".logE()
      return 0
    }
    //如果只有一列，那肯定在第0列
    if (spanCount == 1) {
      "position=${position}所在列数=0".logE()
      return 0
    }
    var totalSpan = 0
    var currentColumn = 0
    for (i in 0 until position) {
      totalSpan += spanSizeLookup.getSpanSize(i)
      if (totalSpan % spanCount == 0) {
        currentColumn = 0
      } else {
        currentColumn++
      }
    }
    "position=${position}所在列数=$currentColumn".logE()
    return currentColumn
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算当前行的实际列数">
  /**
   * @param position 当前位置
   * @param spanSizeLookup 每行分配
   * @param spanCount 每行数量
   */
  private fun calculateRowColumnSize(position: Int, itemCount: Int, spanSizeLookup: SpanSizeLookup, spanCount: Int): Int {
    //如果只有一列，那实际列数肯定为1
    if (spanCount == 1) {
      "position=${position}所在行的实际列数=1".logE()
      return 1
    }
    var totalSpan = 0
    var totalRowColumn = 0
    for (i in 0 until itemCount) {
      totalSpan += spanSizeLookup.getSpanSize(i)
      if (totalSpan % spanCount == 0) {
        if (i < position) {
          totalRowColumn = 0
        } else {
          totalRowColumn++
          break
        }
      } else {
        totalRowColumn++
      }
    }
    "position=${position}所在行的实际列数=$totalRowColumn ".logE()
    return totalRowColumn
  }
  //</editor-fold>
}
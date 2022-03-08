package cc.abase.demo.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.logE
import com.blankj.utilcode.util.GsonUtils

/**
 * 初步测试，适用于非均分的情况(由于为了代码看起来逻辑清晰，每次计算都采用了for循环，如果要优化，可以把计算放到一起，以便减少循环次数)
 * Author:Khaos116
 * Date:2022/3/4
 * Time:10:44
 */
class GridItemDecoration(
  private val mSpacing: Int,
  private val hasStartEnd: Boolean = true,
  private val hasTop: Boolean = true,
  private val hasBottom: Boolean = true,
  private val canDrag: Boolean = false
) : RecyclerView.ItemDecoration() {
  //<editor-fold defaultstate="collapsed" desc="Log打印开关">
  var mPrintLog = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算并修改分割线间距">
  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val layoutManager = parent.layoutManager
    //只对网格布局处理，不支持瀑布流
    if (layoutManager is GridLayoutManager) {
      //重新置空
      outRect.setEmpty()
      //找到当前位置
      val position = parent.getChildAdapterPosition(view)
      if (position == RecyclerView.NO_POSITION) return
      //计算当前位置相关参数
      calculatePositionDetails(parent, position, layoutManager)
      if (currentSpanSize == mSpanCount) {//该行只有1个，直接设置左右间距即可
        outRect.left = if (hasStartEnd) mSpacing else 0
        outRect.right = if (hasStartEnd) mSpacing else 0
        if (mPrintLog) "position=${position}占据整行左右间距=${outRect.left},${outRect.right}".logE()
      } else {//该行数量大于1或者1个但是又没有填充满的情况，需要根据实际情况进行设置
        //左右计算可以参考 https://github.com/youlookwhat/ByRecyclerView/blob/master/ByRecyclerview/src/main/java/me/jingbin/library/decoration/GridSpaceItemDecoration.java
        //当前行是否均分(均分和非均分的第一个右间距会不一样)
        var isAverage = true
        //总的item数量(用于循环遍历每行的起始位置)
        val itemCount = parent.adapter?.itemCount ?: 0
        //当前行的结束位置(用于计算左右间距)
        var endPosition = itemCount - 1//默认位置为最大值，防止最后一行不满，没有找到结束位置
        //当前行的第一个占用位置数量(用于判断是否均分以及非均分时第一个右边间距)
        val tempSize = layoutManager.spanSizeLookup.getSpanSize(currentRowStartPosition)
        //临时变量，用于查找当前结束位置
        var totalSpan = 0
        //遍历找到当前行的结束位置以及判断是否是均分(即每个Item占用最低格数一样)
        for (i in currentRowStartPosition until itemCount) {
          totalSpan += layoutManager.spanSizeLookup.getSpanSize(i)
          if (tempSize != layoutManager.spanSizeLookup.getSpanSize(i)) {
            isAverage = false//如果有和第一个占用格数不一样，则该行不均分
          }
          if (totalSpan >= mSpanCount) {
            endPosition = i//加起来等于该行的最大格数，则已经找到最后一个位置了
            break
          }
        }
        //防止最后一行不满的情况判断不准
        if (isInLastRow && !lastRowFull && isAverage) isAverage = mSpanCount % tempSize == 0
        if (mPrintLog) "position=$position,currentRowStartPosition=$currentRowStartPosition,endPosition=$endPosition，是否均分=$isAverage".logE()
        //当前行的实际列数(如果能均分，则为填满时的列数;如果最后一行不满，则默认最后空白为一个列表)
        val count = if (isAverage) mSpanCount / tempSize else (currentRowColumnSize + if (isInLastRow && !lastRowFull) 1 else 0)
        //记录位置和左右间距
        val positionRect = mutableMapOf<Int, IntArray>()
        //每行的总空余位置
        val totalSpace = if (hasStartEnd) mSpacing * (count + 1) else mSpacing * (count - 1)
        //计算每个item左右加右边应该有多少空间距
        val averageSpace = (totalSpace * 1f / count).toInt()
        if (mPrintLog) "--------------FOR-START--------------".logE()
        for (i in currentRowStartPosition..endPosition) {
          if (hasStartEnd) {//左右有边距
            if (i == currentRowStartPosition) {//左边第一个
              //均分时，直接用平均间距减去设置的间距即可(因为左边固定是设置的间距)，非均分则根据所占最小格数进行比例所发设置的间距
              val firstOffset = if (isAverage) averageSpace - mSpacing else (mSpacing * 1f * tempSize / mSpanCount).toInt()
              positionRect[i] = intArrayOf(mSpacing, firstOffset)//有左右间距，最左边肯定是设置的间距，右边根据是否均分,值不同
            } else {//非左边第一个
              val lastRight: Int = positionRect[i - 1]!![1]//左边那个的右边间距(用于计算右边那个的左边间距)
              //左边间距等于设置的间距减去左边那个右间距
              if (i == endPosition) {//如果是满行最后一个右边间距肯定是设置的间距
                positionRect[i] = intArrayOf(mSpacing - lastRight, if (isInLastRow && !lastRowFull) averageSpace - (mSpacing - lastRight) else mSpacing)
              } else {//非最后一个的右间距等于均值减去左边的间距
                positionRect[i] = intArrayOf(mSpacing - lastRight, averageSpace - (mSpacing - lastRight))
              }
            }
          } else {//左右无边距
            if (i == currentRowStartPosition) {//左边第一个
              //均分时，直接用平均间距即可(因为左边是固定值0)，非均分则根据所占最小格数进行比例所发设置的间距
              val firstOffset = if (isAverage) averageSpace else (mSpacing * 1f * (mSpanCount - tempSize) / mSpanCount).toInt()
              positionRect[i] = intArrayOf(0, firstOffset)//无左右间距，最左边肯定是0，右边根据是否均分,值不同
            } else {//非左边第一个
              val lastRight: Int = positionRect[i - 1]!![1]//左边那个的右边间距(用于计算右边那个的左边间距)
              if (i == endPosition) {//如果是满行最后一个右边间距肯定是0
                positionRect[i] = intArrayOf(mSpacing - lastRight, if (isInLastRow && !lastRowFull) averageSpace - (mSpacing - lastRight) else 0)
              } else {//非最后一个的右间距等于均值减去左边的间距
                positionRect[i] = intArrayOf(mSpacing - lastRight, averageSpace - (mSpacing - lastRight))
              }
            }
          }
          if (mPrintLog) "i=$i,left=${positionRect[i]!![0]},right=${positionRect[i]!![1]}".logE()
        }
        if (mPrintLog) "--------------FOR-ENDED--------------".logE()
        if (mPrintLog) positionRect[position]?.let { "position=${position}计算的左右间距=${it[0]},${it[1]}".logE() }
        outRect.left = positionRect[position]!![0]
        outRect.right = positionRect[position]!![1]
        if (mPrintLog) "该行列数=$count".logE()
      }
      outRect.top = if (isInFirstRow && !canDrag) if (hasTop) mSpacing else 0 else mSpacing / 2
      outRect.bottom = if (isInLastRow && !canDrag) if (hasBottom) mSpacing else 0 else mSpacing / 2
      if (mPrintLog) "position=$position,outRect=${GsonUtils.toJson(outRect)}".logE()
      if (mPrintLog) "================================================================".logE()
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
  private var lastRowFull = false//最后一排是否满了
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
      if (mPrintLog) "第一行的position=0".logE()
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
    if (mPrintLog) "第一行的position=$position".logE()
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
      if (mPrintLog) "最后一行的position=$position".logE()
      return true
    }
    var totalSpan = 0
    for (i in 0 until itemCount) {
      totalSpan += spanSizeLookup.getSpanSize(i)
    }
    //总数都没有超过每行数量，则肯定在最后一行
    if (totalSpan <= spanCount) {
      if (mPrintLog) "最后一行的position=$position".logE()
      return true
    }
    //最后一行的数量
    val lastRowCount = if (totalSpan % spanCount == 0) spanCount else totalSpan % spanCount
    lastRowFull = lastRowCount == spanCount
    totalSpan = 0
    //从最后一个位置到当前position，判断有多少个，如果大于最后一行的数量，则返回非最后一行
    for (i in itemCount - 1 downTo position + 1) {
      totalSpan += spanSizeLookup.getSpanSize(i)
      if (totalSpan >= lastRowCount) {
        if (totalSpan == lastRowCount && position == i) {
          lastRowStartPosition = i
          if (mPrintLog) "最后一行的开始位置position=$lastRowStartPosition".logE()
        }
        return false
      }
    }
    if (mPrintLog) "最后一行的position=$position".logE()
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
      if (mPrintLog) "position=${position}所在行数=0,开始位置=0".logE()
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
    if (mPrintLog) "position=${position}所在行数=${totalSpan / spanCount},开始位置=${currentRowStartPosition}".logE()
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
      if (mPrintLog) "position=${position}所在列数=0".logE()
      return 0
    }
    //如果只有一列，那肯定在第0列
    if (spanCount == 1) {
      if (mPrintLog) "position=${position}所在列数=0".logE()
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
    if (mPrintLog) "position=${position}所在列数=$currentColumn".logE()
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
      if (mPrintLog) "position=${position}所在行的实际列数=1".logE()
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
    if (mPrintLog) "position=${position}所在行的实际列数=$totalRowColumn ".logE()
    return totalRowColumn
  }
  //</editor-fold>
}
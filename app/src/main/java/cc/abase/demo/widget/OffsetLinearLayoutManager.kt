package cc.abase.demo.widget

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Description:监听列表滑动高度
 * @author: Khaos
 * @date: 2019/12/26 12:28
 */
class OffsetLinearLayoutManager(
  context: Context,
  call: ((offset: Int) -> Unit)? = null
) :
    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
  private var callBack: ((offset: Int) -> Unit)? = call
  private val heightMap: MutableMap<Int, Int> = HashMap()
  override fun onLayoutCompleted(state: RecyclerView.State) {
    super.onLayoutCompleted(state)
    val count: Int = childCount
    for (i in 0 until count) {
      val view: View? = getChildAt(i)
      heightMap[i] = view?.height ?: 0
    }
  }

  override fun computeVerticalScrollOffset(state: RecyclerView.State): Int {
    return if (childCount == 0) {
      0
    } else try {
      val firstVisiablePosition: Int = findFirstVisibleItemPosition()
      val firstVisiableView: View? = findViewByPosition(firstVisiablePosition)
      var offsetY = -(firstVisiableView?.y ?: 0f).toInt()
      for (i in 0 until firstVisiablePosition) {
        offsetY += heightMap[i] ?: 0
      }
      callBack?.invoke(offsetY)
      offsetY
    } catch (e: Exception) {
      0
    }
  }
}
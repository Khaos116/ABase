package cc.abase.demo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.State

/**
 * 修改滑动速度
 * Author:CASE
 * Date:2021/1/5
 * Time:19:26
 */
@SuppressLint("WrongConstant")
class SpeedLinearLayoutManager(c: Context, o: Int = VERTICAL, r: Boolean = false) : LinearLayoutManager(c, o, r) {
  //滑动速度比,越大速度越慢
  var speedRatio: Float = 1f

  //修改滑动速度
  private val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(c) {
    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
      return this@SpeedLinearLayoutManager.computeScrollVectorForPosition(targetPosition)
    }

    //This returns the milliseconds it takes to scroll one pixel.
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
      return speedRatio / displayMetrics.density //返回滑动一个pixel需要多少毫秒
    }
  }

  override fun smoothScrollToPosition(recyclerView: RecyclerView, state: State?, position: Int) {
    linearSmoothScroller.targetPosition = position
    startSmoothScroll(linearSmoothScroller)
  }
}
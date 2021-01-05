package cc.abase.demo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.State

/**
 * 修改滑动速度
 * @see com.google.android.material.datepicker.SmoothCalendarLayoutManager
 * @Author:CASE
 * @Date:2021/1/5
 * @Time:19:26
 */
@SuppressLint("WrongConstant")
class SpeedLinearLayoutManager(c: Context, o: Int = VERTICAL, r: Boolean = false) : LinearLayoutManager(c, o, r) {
  /** Default value in [LinearSmoothScroller] is 25f  */
  var MILLISECONDS_PER_INCH = 100f

  private val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(c) {
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
      return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
    }
  }

  override fun smoothScrollToPosition(recyclerView: RecyclerView, state: State?, position: Int) {
    linearSmoothScroller.targetPosition = position
    startSmoothScroll(linearSmoothScroller)
  }
}
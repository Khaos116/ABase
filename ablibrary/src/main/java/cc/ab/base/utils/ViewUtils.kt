package cc.ab.base.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup

/**
 * Description:
 * @author: Khaos
 * @date: 2019/9/24 11:06
 */
object ViewUtils {
  //测量这个view，最后通过getMeasuredWidth()、getMeasuredHeight()获取宽度和高度
  fun measureView(v: View) {
    var params = v.layoutParams
    if (params == null) {
      params = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
      )
    }
    val childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width)
    val lpHeight = params.height
    val childHeightSpec: Int
    childHeightSpec = if (lpHeight > 0) {
      View.MeasureSpec.makeMeasureSpec(
          lpHeight,
          View.MeasureSpec.EXACTLY
      )
    } else {
      View.MeasureSpec.makeMeasureSpec(
          0,
          View.MeasureSpec.UNSPECIFIED
      )
    }
    v.measure(childWidthSpec, childHeightSpec)
  }

  //测量view宽度
  fun measureVieWidth(view: View): Int {
    val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    view.measure(width, height)
    return view.measuredWidth
  }

  //测量view高度
  fun measureViewHeight(view: View): Int {
    val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    view.measure(width, height)
    return view.measuredHeight
  }

  //通过drawingCache获取bitmap(效果同copyByCanvas2方法)
  fun convertViewToBitmap2(view: View): Bitmap {
    view.isDrawingCacheEnabled = true
    val bitmap = Bitmap.createBitmap(view.drawingCache)
    //如果不调用这个方法，每次生成的bitmap相同
    view.isDrawingCacheEnabled = false
    return bitmap
  }

  //通过canvas复制view的bitmap(效果同convertViewToBitmap2方法)
  fun copyByCanvas2(view: View): Bitmap {
    val width = view.measuredWidth
    val height = view.measuredHeight
    val bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bp)
    view.draw(canvas)
    canvas.save()
    return bp
  }
}
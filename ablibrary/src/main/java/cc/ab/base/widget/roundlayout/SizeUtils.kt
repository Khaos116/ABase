package cc.ab.base.widget.roundlayout

import android.content.Context
import cc.ab.base.ext.toFloatMy

class SizeUtils {
  companion object {
      /**
       * 密度转换像素
       * @param dipValue dp值
       * @return 像素
       */
    fun dip2px(context: Context, dipValue: Float): Float {
      val displayMetrics = context.applicationContext.resources.displayMetrics
      return (dipValue * displayMetrics.density + 0.5f).toInt().toFloatMy()
    }
  }
}

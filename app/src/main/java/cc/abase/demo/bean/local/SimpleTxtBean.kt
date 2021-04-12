package cc.abase.demo.bean.local

import android.graphics.Color
import android.view.Gravity
import cc.ab.base.ext.dp2px

/**
 * Author:CASE
 * Date:2021/1/2
 * Time:15:28
 */
data class SimpleTxtBean(val txt: String) {
  var cls: Class<*>? = null
  var textSizePx: Float = 14.dp2px() * 1f
  var paddingStartPx: Int = 12.dp2px()
  var paddingEndPx: Int = 12.dp2px()
  var paddingTopPx: Int = 10.dp2px()
  var paddingBottomPx: Int = 10.dp2px()
  var textColor: Int = Color.parseColor("#333333")
  var gravity: Int = Gravity.CENTER
  var needBold: Boolean = false
}
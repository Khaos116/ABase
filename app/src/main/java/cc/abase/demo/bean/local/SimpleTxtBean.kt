package cc.abase.demo.bean.local

import android.graphics.Color
import android.view.Gravity
import cc.ab.base.ext.dp2Px

/**
 * Author:CASE
 * Date:2021/1/2
 * Time:15:28
 */
data class SimpleTxtBean(val txt: String) {
  var cls: Class<*>? = null
  var textSizePx: Float = 14.dp2Px() * 1f
  var paddingStartPx: Int = 12.dp2Px()
  var paddingEndPx: Int = 12.dp2Px()
  var paddingTopPx: Int = 10.dp2Px()
  var paddingBottomPx: Int = 10.dp2Px()
  var textColor: Int = Color.parseColor("#333333")
  var gravity: Int = Gravity.CENTER
  var needBold: Boolean = false
}
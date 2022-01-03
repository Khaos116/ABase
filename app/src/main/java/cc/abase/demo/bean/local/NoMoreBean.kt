package cc.abase.demo.bean.local

import cc.ab.base.ext.*
import cc.abase.demo.R

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/6
 * @Time：21:27
 */
data class NoMoreBean(val text: String = R.string.没有更多数据了.xmlToString()) {
  var textColor: Int = R.color.gray_444444.xmlToColor()
  var textSize: Float = 14.dp2px() * 1f
  var bgColor: Int = R.color.gray_CCCCCC.xmlToColor()
  var bold: Boolean = false
  val heightPx: Int = 40.dp2px()
}
package cc.ab.base.widget.fittext.emoji.core

/**
 * @Description
 * @Author：Khaos
 * @Date：2021-07-13
 * @Time：22:35
 */
data class RangeBean(
  var sb: CharSequence = "", //文字信息
  val ranges: MutableList<Range> = mutableListOf() //范围信息
)

data class Range(
  var start: Int = 0, //整行文字的开始位置
  var end: Int = 0, //整行文字的结束位置
  val type: Int = 0, //0普通;1背景色;2前景色;3背景和前景色
  val bgColor: Int = 0, //背景色
  val foreColor: Int = 0, //前景色
)
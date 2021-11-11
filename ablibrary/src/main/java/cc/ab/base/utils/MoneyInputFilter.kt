package cc.ab.base.utils

import android.text.InputFilter
import android.text.Spanned

/**
 * 金额输入限制
 * https://blog.csdn.net/guangdeshishe/article/details/93888388
 * 使用方法：editText.filters = arrayOf(MoneyInputFilter())
 * Author:CASE
 * Date:2021/11/11
 * Time:19:32
 */
class MoneyInputFilter(private val pointLen: Int = 2) : InputFilter {
  override fun filter(
    source: CharSequence, //将要输入的字符串,如果是删除操作则为空
    start: Int, //将要输入的字符串起始下标，一般为0
    end: Int, //start + source字符的长度
    dest: Spanned, //输入之前文本框中的内容
    dstart: Int, //将会被替换的起始位置
    dend: Int //dstart+将会被替换的字符串长度
  ): CharSequence {
    val newStart = dest.subSequence(0, dstart)
    val newEnd = dest.subSequence(dend, dest.length)
    val target = newStart.toString() + source + newEnd //字符串变化后的结果
    val backup = dest.subSequence(dstart, dend) //将要被替换的字符串
    if (target.indexOf(".") == 0) { //不允许第一个字符为.
      return backup
    }
    if (target.startsWith("0") && !target.startsWith("0.") && "0" != target) { //不允许出现0123、0456这类字符串
      return backup
    }
    //限制小数点后面只能有两位小数
    val index = target.indexOf(".")
    if (index >= 0 && index + pointLen + 2 <= target.length) {
      return backup
    }
    return source
  }
}
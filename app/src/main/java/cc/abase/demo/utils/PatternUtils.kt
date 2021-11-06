package cc.abase.demo.utils

import android.text.InputFilter
import java.util.regex.Pattern

/**
 * @Description editText.filters = arrayOf(PatternUtils.禁止换行, InputFilter.LengthFilter(30))
 * @Author：Khaos
 * @Date：2021-07-27
 * @Time：18:20
 */
object PatternUtils {
  //4-9个字符，前2位必须为字母，数字可选，不支持符号
  fun isAccountMatcher(str: String): Boolean {
    return Pattern.matches("^[A-Za-z]{2}[0-9A-Za-z]{2,7}$", str)
  }

  //8-15位，仅支持字母和数字组合，不支持符号
  fun isPwdMatcher(str: String): Boolean {
    return Pattern.compile("[a-z]+").matcher(str.lowercase()).find() &&
        Pattern.compile("[0-9]+").matcher(str).find() &&
        Pattern.matches("^[0-9A-Za-z]{8,15}$", str)
  }

  //3-15位，支持任意中英文或数字，不支持符号
  fun isNameMatcher(str: String): Boolean {
    return Pattern.matches("^[\\u4E00-\\u9FA5A-Za-z0-9]{3,15}$", str)
  }

  //是否是英文字符
  fun isEnglish(str: String): Boolean {
    return Pattern.matches("^[A-Za-z]+$", str)
  }

  val 数字格式 = InputFilter { source, _, _, _, _, _ ->
    val p = Pattern.compile("^[0-9]+$")
    val m = p.matcher(source.toString())
    if (!m.matches()) "" else null
  }

  val 英文数字格式 = InputFilter { source, _, _, _, _, _ ->
    val p = Pattern.compile("^[A-Za-z0-9]+$")
    val m = p.matcher(source.toString())
    if (!m.matches()) "" else null
  }

  val 英文数字下划线格式 = InputFilter { source, _, _, _, _, _ ->
    val p = Pattern.compile("^[A-Za-z0-9_]+$")
    val m = p.matcher(source.toString())
    if (!m.matches()) "" else null
  }

  val 中文英文数字格式 = InputFilter { source, _, _, _, _, _ ->
    val p = Pattern.compile("^[\\u4E00-\\u9FA5A-Za-z0-9]+$")
    val m = p.matcher(source.toString())
    if (!m.matches()) "" else null
  }

  val 禁止换行 = InputFilter { source, _, _, _, _, _ ->
    source.toString().replace("\n", "")
  }
}
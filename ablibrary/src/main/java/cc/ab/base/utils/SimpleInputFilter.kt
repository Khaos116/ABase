package cc.ab.base.utils

import android.text.*
import android.text.style.UnderlineSpan
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import java.util.regex.Pattern

/**
 * @Description 解决联想预输入的裁切问题
 * https://jmeow.org/android/jie-jue-edittext-zhong-inputfilter-yu-lian-xiang-ci-shu-ru-fa-chong-tu/
 * 只允许输入数字汉字或英文字母
 * 输入限制为最大16字符
 * 汉字计为2字符，数字和英文计为1字符
 * 当输入超过16字符时(如拼音输入法一次性输入多个文字)，输入内容截取到最大部分
 * @Author：Khaos
 * @Date：2021-06-25
 * @Time：18:25
 */
class SimpleInputFilter(private val maxLength: Int = 16) : InputFilter {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //正则判断
  private val regCN = "[\u4E00-\u9FA5]"
  private val regEnNum = "[a-zA-Z0-9]"
  private val regAll = "^[\u4E00-\u9FA5a-zA-Z0-9]+$"
  private val regExceptText = "((?![\u4E00-\u9FA5aa-zA-Z0-9]).)"

  //中文判断
  private var mCNPattern = Pattern.compile(regCN)

  //数字+字母判断
  private var mEnNumPattern = Pattern.compile(regEnNum)

  //中文+英文+数字
  private var mPattern = Pattern.compile(regAll)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="输入限制处理">
  /**
   * @param source 新输入的字符串
   * @param start  新输入的字符串起始下标
   * @param end    新输入的字符串结尾下标
   * @param dest   之前文本框内容
   * @param dstart 原内容起始下标
   * @param dend   原内容结尾下标
   * @return 输入内容
   * 如果返回值为 null，则表示接受全部的输入内容，不作任何处理
   * 如果返回值为一个空字符串 ""，则表示不接受任何输入，即丢弃用户输入的字符
   * 如果返回值为一个 CharSequence 类型的字符串，则表示替换用户输入的当前文本段，具体将替换从 start 到 end 的源字符中间的一段，返回的字符串就是要替换的内容
   */
  override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
    val sourceText = source.toString()
    //当用户处于输入法联想模式下(MIUI的中文输入法，Gboard输入法英文输入联想模式等)
    //由于输入法会进行文本预设，所以此时不对输入内容进行校验，但是需要对输入字符的情况进行剔除
    //因为仍然不允许输入字符。联想词模式下，输入位数的限制由TextChangedListener控制
    val ss = SpannableString(source)
    val spans = ss.getSpans(0, ss.length, Any::class.java)
    if (spans != null) {
      for (span in spans) {
        if (span is UnderlineSpan) {
          //检查输入的是否为中文，英文，数字
          val s: String = sourceText.replace(regExceptText.toRegex(), "") //不符合的替换为空
          //华为输入法会判断返回CharSequence是否有下划线，决定是否替换
          return if (s == sourceText) source else {
            SpannableString(s).also { str -> str.setSpan(UnderlineSpan(), 0, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
          }
        }
      }
    }
    //检查输入的是否为中文，英文，数字
    val matcher = mPattern.matcher(source)
    return if (!matcher.matches() && !TextUtils.isEmpty(sourceText)) {
      sourceText.replace(regExceptText.toRegex(), "") //不符合的替换为空
    } else source
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算文本长度(中文单个为2,其他为1)">
  /**
   * 当中文为2字符，英文数字为1字符情况下，文本的长度计算
   *
   * @param str 文本
   * @return 文本长度
   */
  private fun computeByteLength(str: String): Int {
    var length = 0
    val chars = str.toCharArray()
    for (aChar in chars) {
      //检查输入的是否为中文
      val cnMatcher = mCNPattern.matcher(aChar.toString())
      if (cnMatcher.matches()) {
        length += 2
        continue
      }
      //除了中文之外暂只判断为英文或1字节
      val matcher = mEnNumPattern.matcher(aChar.toString())
      if (matcher.matches()) {
        length += 1
      }
    }
    return length
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算截取的末尾位置">
  /**
   * 获取到需要截取的位置
   *
   * @param str       原str
   * @param maxLength 最大长度
   * @return 从0开始截取的末尾位置 index
   */
  private fun computeEndPosition(str: String, maxLength: Int): Int {
    var length = 0
    val chars = str.toCharArray()
    for ((splitIndex, aChar) in chars.withIndex()) {
      //检查输入的数量是否超规格
      if (length >= maxLength) {
        return splitIndex
      }
      //检查输入的是否为中文
      val cnMatcher = mCNPattern.matcher(aChar.toString())
      length = if (cnMatcher.matches()) {
        length + 2
      } else {
        //除了中文之外暂只判断为英文或1字节
        length + 1
      }
    }
    //不需要截取，没有超过最大值
    return str.length
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="截取文本到指定位置">
  /**
   * 截取至符合要求的位置字符串
   *
   * @param str  文本
   * @param size 要求长度
   * @return
   */
  private fun subStringTo(str: String, size: Int): String {
    var length = 0
    val chars = str.toCharArray()
    val result = StringBuilder()
    for (aChar in chars) {
      //检查输入的是否为中文
      val cnMatcher = mCNPattern.matcher(aChar.toString())
      if (cnMatcher.matches()) {
        length += 2

        //如果添加这个字符不会超过size限额，那么这个字符是可以添加的
        if (length <= size) {
          result.append(aChar)
        } else {
          break
        }
        continue
      }
      val matcher = mEnNumPattern.matcher(aChar.toString())
      if (matcher.matches()) {
        length += 1
        if (length <= size) {
          result.append(aChar)
        } else {
          break
        }
      }
    }
    return result.toString()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="真正使用方法(绑定到输入框)">
  /**
   * 绑定目标EditText
   *
   * @param editText 输入框
   * @param mCallTxtChanged 输入回调，防止冗余监听
   */
  fun bindEditText(editText: EditText, mCallTxtChanged: ((text: String) -> Unit)? = null) {
    editText.filters = arrayOf<InputFilter>(this)
    editText.addTextChangedListener(onTextChanged = { text, _, _, _ ->
      val str = text?.toString() ?: ""
      //当处于输入法预输入模式下，对输入长度进行校验
      if (computeByteLength(str) > maxLength) {
        val splitIndex = computeEndPosition(str, maxLength)
        editText.setText(str.substring(0, splitIndex))
        editText.setSelection(editText.length())
      }
      mCallTxtChanged?.invoke(str)
    })
  }
  //</editor-fold>
}
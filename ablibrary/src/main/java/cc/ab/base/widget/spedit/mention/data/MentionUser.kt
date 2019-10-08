package cc.ab.base.widget.spedit.mention.data
import android.graphics.Color
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import cc.ab.base.widget.spedit.mention.span.BreakableSpan
import cc.ab.base.widget.spedit.mention.span.IntegratedSpan

/**
 * Created by sunhapper on 2019/1/30 .
 * 使用三星输入法IntegratedSpan完整性不能保证，所以加上BreakableSpan使得@mention完整性被破坏时删除对应span
 */
data class MentionUser(
  var name: String,
  var id: Long = 0
) : IntegratedSpan, BreakableSpan {
  private val colorAt = Color.parseColor("#FF30D18B")
  private val colorBg = Color.parseColor("#3372D2FF")
  private var styleSpanBg: Any = BackgroundColorSpan(colorBg)
  private var styleSpanFore: Any? = null
  var isSpanBg: Boolean = false
  val spanStringFore: Spannable
    get() {
      styleSpanFore = ForegroundColorSpan(colorAt)
      val spannableString = SpannableString(displayText)
      spannableString.setSpan(
          styleSpanFore, 0, spannableString.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      spannableString.setSpan(
          this, 0, spannableString.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      val stringBuilder = SpannableStringBuilder()
      isSpanBg = false
      return stringBuilder.append(spannableString)
    }
  val spanStringBg: Spannable
    get() {
      styleSpanFore = ForegroundColorSpan(colorAt)
      val spannableString = SpannableString(displayText)
      spannableString.setSpan(
          styleSpanFore, 0, spannableString.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      spannableString.setSpan(
          styleSpanBg, 0, spannableString.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      spannableString.setSpan(
          this, 0, spannableString.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      val stringBuilder = SpannableStringBuilder()
      isSpanBg = true
      return stringBuilder.append(spannableString)
    }
  val displayText: CharSequence
    get() = "@$name "

  override fun isBreak(text: Spannable): Boolean {
    val spanStart = text.getSpanStart(this)
    val spanEnd = text.getSpanEnd(this)
    val isBreak = spanStart >= 0 && spanEnd >= 0 && !text.subSequence(
        spanStart,
        spanEnd
    ).toString().contentEquals(
        displayText
    )
    if (isBreak && styleSpanFore != null) {
      text.removeSpan(styleSpanFore)
      text.removeSpan(styleSpanBg)
      styleSpanFore = null
    }
    return isBreak
  }

  fun removeBgSpan(text: Spannable): Boolean {
    if (isSpanBg) {
      text.removeSpan(styleSpanBg)
      isSpanBg = false
      return true
    }
    return false
  }
}
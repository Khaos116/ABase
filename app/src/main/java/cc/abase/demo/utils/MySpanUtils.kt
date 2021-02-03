package cc.abase.demo.utils

import android.graphics.Typeface
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import cc.ab.base.widget.span.RadiusBackgroundSpan
import cc.abase.demo.R
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SizeUtils

/**
 * Author:CASE
 * Date:2020-10-20
 * Time:18:31
 */
object MySpanUtils {
  //获取简单带颜色的span
  fun getSpanSimple(text: String, colorId: Int = ColorUtils.getColor(R.color.colorAccent), bold: Boolean = false): SpannableString {
    val color = ForegroundColorSpan(colorId)
    val style = StyleSpan(Typeface.BOLD)
    val title = SpannableString(text)
    title.setSpan(color, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (bold) title.setSpan(style, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return title
  }

  //带圆角背景的span
  fun getRadiusBgSpan(text: String, textSizePx: Int, bgRadiusPx: Int, foreColor: Int, bgColor: Int, bold: Boolean = false)
      : SpannableString {
    val ss = SpannableString(text)
    val style = StyleSpan(Typeface.BOLD)
    ss.setSpan(RadiusBackgroundSpan(bgColor, foreColor, SizeUtils.dp2px(4f), SizeUtils.dp2px(1f),
        SizeUtils.dp2px(1f), bgRadiusPx, textSizePx), 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (bold) ss.setSpan(style, 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return ss
  }
}
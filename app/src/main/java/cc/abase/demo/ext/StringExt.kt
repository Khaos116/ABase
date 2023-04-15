package cc.abase.demo.ext

import android.text.SpannableStringBuilder
import androidx.core.text.HtmlCompat

/**
 * Author:Khaos
 * Date:2022/11/9
 * Time:19:38
 */
//HTML字符串处理
fun String?.toHtml(): CharSequence {
  return if (this.isNullOrBlank()) {
    ""
  } else if (this.startsWith("<") && this.endsWith(">")) {
    try {
      HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
    } catch (e: Exception) {
      e.printStackTrace()
      this
    }
  } else {
    this
  }
}

//HTML字符串处理，单行显示
fun String?.toHtmlSingle(): CharSequence {
  return if (this.isNullOrBlank()) {
    ""
  } else if (this.startsWith("<") && this.endsWith(">")) {
    try {
      val htmlString = this// "<div><p>这是第一段文本。</p></div><div><p>这是第二段文本。<br>换行的富文本应该显示出来。</p></div>"
      val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_LEGACY) // 将 HTML 字符串转换为 Spanned 对象
      val singleLineText = spanned.toString() // 将 Spanned 对象转换为字符串
      //使用 SpannableStringBuilder 类来处理文本内容，并将自定义的换行符替换为真正的空格
      val builder = SpannableStringBuilder(singleLineText.replace("(\r\n|\n)".toRegex(), "    "))
      //获取所有 <br> 标签的位置
      var index = singleLineText.indexOf("<br>")
      while (index != -1) {
        // 将 <br> 标签替换为空格
        builder.replace(index, index + 4, "  ")
        index = singleLineText.indexOf("<br>", index + 1)
      }
      builder
    } catch (e: Exception) {
      e.printStackTrace()
      this
    }
  } else {
    this
  }
}

//HTML字符串处理
fun CharSequence?.toHtml(): CharSequence {
  return if (this.isNullOrBlank()) {
    ""
  } else if (this.startsWith("<") && this.endsWith(">")) {
    try {
      HtmlCompat.fromHtml(this.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    } catch (e: Exception) {
      e.printStackTrace()
      this
    }
  } else {
    this
  }
}
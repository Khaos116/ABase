package cc.abase.demo.ext

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
package cc.abase.demo.widget.spedit

import android.text.*
import android.view.KeyEvent
import com.sunhapper.x.spedit.mention.span.IntegratedSpan
import com.sunhapper.x.spedit.view.KeyEventProxy

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/12/5 19:37
 */
class MySelectDeleteKeyEventProxy : KeyEventProxy {
  override fun onKeyEvent(
    keyEvent: KeyEvent,
    text: Editable
  ): Boolean {
    if (keyEvent.keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {
      val selectionStart = Selection.getSelectionStart(text)
      val selectionEnd = Selection.getSelectionEnd(text)
      if (selectionEnd != selectionStart) {
        return false
      }
      val integratedSpans =
        text.getSpans(selectionStart, selectionEnd, IntegratedSpan::class.java)
      integratedSpans?.firstOrNull { text.getSpanEnd(it) == selectionStart }
          ?.let { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)
            if (span is IntegratedBgSpan) {
              if (span.isShow) {
                text.replace(spanStart, spanEnd, "")
              } else {
                span.isShow = true
                text.setSpan(
                    span.createStoredBgSpan(), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
              }
            } else {
              text.replace(spanStart, spanEnd, "")
            }
            return true
          }
    }
    return false
  }
}
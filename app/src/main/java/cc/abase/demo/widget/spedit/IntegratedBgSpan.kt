package cc.abase.demo.widget.spedit

import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import com.sunhapper.x.spedit.mention.span.IntegratedSpan

/**
 * Created by sunhapper on 2019-07-19 .
 */
interface IntegratedBgSpan : IntegratedSpan {
  var isShow: Boolean
  var bgSpan: BackgroundColorSpan?

  fun removeBg(text: Spannable) {
    isShow = false
    bgSpan?.run {
      text.removeSpan(this)
    }
  }

  fun createStoredBgSpan(): BackgroundColorSpan {
    val span = generateBgSpan()
    bgSpan = span
    return span
  }

  fun generateBgSpan(): BackgroundColorSpan = BackgroundColorSpan(Color.YELLOW)
}
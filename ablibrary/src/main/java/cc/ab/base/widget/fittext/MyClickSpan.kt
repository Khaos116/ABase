package cc.ab.base.widget.fittext

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * @Description
 * @Author：Khaos
 * @Date：2021-08-09
 * @Time：18:17
 */
class MyClickSpan(
  //正常颜色Span颜色
  val mNormalSpanColor: Int = Color.BLACK,
  //按压Span颜色
  val mPressSpanColor: Int = Color.BLACK,
  //是否显示下划线
  val showUnderLine: Boolean = false,
  //点击事件回调
  private val mCallClick: ((view: View) -> Unit)? = null
) : ClickableSpan() {

  override fun onClick(widget: View) {
    mCallClick?.invoke(widget)
  }

  override fun updateDrawState(ds: TextPaint) {
    super.updateDrawState(ds)
    ds.isUnderlineText = showUnderLine
  }
}
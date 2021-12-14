package cc.ab.base.widget.span2

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.view.ViewCompat
import cc.ab.base.widget.span2.IPressedSpan

/**
 * https://github.com/still-soul/MagicTextView
 * 2021年12月14日18:46:45
 */
abstract class MyTouchLinkSpan(
    private val colorNormal: Int = Color.BLACK,
    private val colorPress: Int = Color.BLACK,
    private val colorBgNormal: Int = Color.TRANSPARENT,
    private val colorBgPress: Int = Color.TRANSPARENT,
    private val showUnderline: Boolean = false,
) : ClickableSpan(), IPressedSpan {
    //是否处于按压状态
    private var isPressed = false

    //传递点击事件
    override fun onClick(widget: View) {
        if (ViewCompat.isAttachedToWindow(widget)) {
            onSpanClick(widget)
        }
    }

    //设置按压颜色
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        if (isPressed) {
            ds.color = colorPress
            ds.bgColor = colorBgPress
        } else {
            ds.color = colorNormal
            ds.bgColor = colorBgNormal
        }
        ds.isUnderlineText = showUnderline
    }

    //更新按压状态
    override fun setPressed(pressed: Boolean) {
        isPressed = pressed
    }

    //子类接收点击事件
    abstract fun onSpanClick(widget: View)
}
package cc.ab.base.widget.span2

import android.view.View

/**
 * https://github.com/still-soul/MagicTextView
 */
interface IPressedSpan {
    //按下
    fun setPressed(pressed : Boolean)

    //点击
    fun onClick(widget : View)
}
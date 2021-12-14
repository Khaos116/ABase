package cc.ab.base.widget.span2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

/**
 * https://github.com/still-soul/MagicTextView
 * 2021年12月14日18:46:45
 */
class MyClickSpanTextView(context: Context, attrs: AttributeSet?) :
    AppCompatTextView(context, attrs) {
    private var mPressedSpan: IPressedSpan? = null

    init {
        isFocusable = false
        isLongClickable = false
        //有链接点击需求不设置则点击无效
        movementMethod = MyLinkMovementMethod
        highlightColor = Color.TRANSPARENT
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val text = text
        val spannable = Spannable.Factory.getInstance().newSpannable(text)
        if (event.action == MotionEvent.ACTION_DOWN) {
            //按下时记下clickSpan
            mPressedSpan = MyLinkMovementMethod.getPressedSpan(this, spannable, event)
        }
        return if (mPressedSpan != null) {
            //如果有clickSpan就走MyLinkMovementMethod的onTouchEvent
            MyLinkMovementMethod.onTouchEvent(this, getText() as Spannable, event)
        } else {
            super.onTouchEvent(event)
        }
    }
}
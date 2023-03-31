package cc.ab.base.widget.span2

import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView
import cc.ab.base.ext.toFloatMy

/**
 * https://github.com/still-soul/MagicTextView
 * 2021年12月14日18:46:45
 */
object MyLinkMovementMethod : LinkMovementMethod() {
    //按压的Span
    private var mPressedSpan: IPressedSpan? = null

    //处理选中
    override fun onTouchEvent(textView: TextView, spannable: Spannable, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPressedSpan = getPressedSpan(textView, spannable, event)
                if (mPressedSpan != null) {
                    //手指按下 设置按下为true，修改对应的链接文字背景颜色
                    mPressedSpan?.setPressed(true)
                    //设置选中区域
                    Selection.setSelection(
                        spannable, spannable.getSpanStart(mPressedSpan),
                        spannable.getSpanEnd(mPressedSpan)
                    )
                }
                mPressedSpan != null
            }
            MotionEvent.ACTION_MOVE -> {
                val touchedSpan = getPressedSpan(textView, spannable, event)
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    //手指移动时 设置按下为false，对应的链接文字背景颜色置回透明
                    mPressedSpan?.setPressed(false)
                    mPressedSpan = null
                    //移除选中区域
                    Selection.removeSelection(spannable)
                }
                mPressedSpan != null
            }
            MotionEvent.ACTION_UP -> {
                var touchSpanHint = false
                if (mPressedSpan != null) {
                    touchSpanHint = true
                    //手指抬起时 设置按下为false，对应的链接文字背景颜色置回透明
                    mPressedSpan?.setPressed(false)
                    //传递点击事件回调
                    mPressedSpan?.onClick(textView)
                }
                mPressedSpan = null
                Selection.removeSelection(spannable)
                touchSpanHint
            }
            else -> {
                if (mPressedSpan != null) {
                    //其它收拾 都设置按下为false,对应的链接文字背景颜色置回透明
                    mPressedSpan?.setPressed(false)
                }
                //移除选中区域
                Selection.removeSelection(spannable)
                false
            }
        }
    }

    // 判断手指是否点击在链接上
    fun getPressedSpan(textView: TextView, spannable: Spannable, event: MotionEvent): IPressedSpan? {
        var x = event.x.toInt()
        var y = event.y.toInt()
        x -= textView.totalPaddingLeft
        x += textView.scrollX
        y -= textView.totalPaddingTop
        y += textView.scrollY
        val layout = textView.layout
        val line = layout.getLineForVertical(y)
        try {
            var off = layout.getOffsetForHorizontal(line, x.toFloatMy())
            if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) {
                off = -1 // 实际上没点到任何内容
            } else if (y < layout.getLineTop(line) || y > layout.getLineBottom(line)) {
                off = -1// 实际上没点到任何内容
            }
            val linkSpans = spannable.getSpans(off, off, IPressedSpan::class.java)
            var mTouchSpan: IPressedSpan? = null
            if (!linkSpans.isNullOrEmpty()) {
                mTouchSpan = linkSpans[0]
            }
            return mTouchSpan
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
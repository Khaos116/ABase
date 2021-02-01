package cc.ab.base.widget

/**
 * Author:CASE
 * Date:2020-10-10
 * Time:14:15
 */
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import cc.ab.base.R
import com.blankj.utilcode.util.SizeUtils

@SuppressLint("PrivateResource", "CustomViewStyleable")
class DrawableEditText(context: Context, attributeSet: AttributeSet?) :
    AppCompatEditText(context, attributeSet) {
  private var leftWidth = -1
  private var leftHeight = -1
  private var topWidth = -1
  private var topHeight = -1
  private var rightWidth = -1
  private var rightHeight = -1
  private var bottomWidth = -1
  private var bottomHeight = -1

  private var leftD: Drawable? = null
  private var rightD: Drawable? = null
  private var topD: Drawable? = null
  private var bottomD: Drawable? = null

  init {
    val array = context.obtainStyledAttributes(attributeSet, R.styleable.DrawableEditText)

    leftWidth = array.getDimensionPixelSize(R.styleable.DrawableEditText_drawableELeftWidth, -1)
    leftHeight = array.getDimensionPixelSize(R.styleable.DrawableEditText_drawableELeftHeight, -1)

    topWidth = array.getDimensionPixelSize(R.styleable.DrawableEditText_drawableETopWidth, -1)
    topHeight = array.getDimensionPixelSize(R.styleable.DrawableEditText_drawableETopHeight, -1)

    rightWidth = array.getDimensionPixelSize(R.styleable.DrawableEditText_drawableERightWidth, -1)
    rightHeight = array.getDimensionPixelSize(R.styleable.DrawableEditText_drawableERightHeight, -1)

    bottomWidth = array.getDimensionPixelSize(R.styleable.DrawableEditText_drawableEBottomWidth, -1)
    bottomHeight = array.getDimensionPixelSize(R.styleable.DrawableEditText_drawableEBottomHeight, -1)

    array.recycle()

    val arrayHelper = context.obtainStyledAttributes(attributeSet, R.styleable.AppCompatTextHelper)

    var drawableLeft: Drawable? = null
    var drawableTop: Drawable? = null
    var drawableRight: Drawable? = null
    var drawableBottom: Drawable? = null
    var drawableStart: Drawable? = null
    var drawableEnd: Drawable? = null
    arrayHelper.getResourceId(R.styleable.AppCompatTextHelper_android_drawableLeft, -1)
        .takeIf { it != -1 }?.apply { drawableLeft = ContextCompat.getDrawable(context, this) }
    arrayHelper.getResourceId(R.styleable.AppCompatTextHelper_android_drawableStart, -1)
        .takeIf { it != -1 }?.apply { drawableStart = ContextCompat.getDrawable(context, this) }
    arrayHelper.getResourceId(R.styleable.AppCompatTextHelper_android_drawableTop, -1)
        .takeIf { it != -1 }?.apply { drawableTop = ContextCompat.getDrawable(context, this) }
    arrayHelper.getResourceId(R.styleable.AppCompatTextHelper_android_drawableBottom, -1)
        .takeIf { it != -1 }?.apply { drawableBottom = ContextCompat.getDrawable(context, this) }
    arrayHelper.getResourceId(R.styleable.AppCompatTextHelper_android_drawableEnd, -1)
        .takeIf { it != -1 }?.apply { drawableEnd = ContextCompat.getDrawable(context, this) }
    arrayHelper.getResourceId(R.styleable.AppCompatTextHelper_android_drawableRight, -1)
        .takeIf { it != -1 }?.apply { drawableRight = ContextCompat.getDrawable(context, this) }

    arrayHelper.recycle()

    if (drawableEnd == null && drawableRight == null) {
      rightD = ContextCompat.getDrawable(context, R.drawable.close_edit)
      rightWidth = SizeUtils.dp2px(18f)
      rightHeight = SizeUtils.dp2px(18f)
    }

    minHeight = rightHeight

    setCompoundDrawablesWithIntrinsicBounds(drawableStart ?: drawableLeft, drawableTop, null, drawableBottom)

    //监听输入
    addTextChangedListener {
      setCompoundDrawablesWithIntrinsicBounds(leftD, topD, if (it.isNullOrBlank()) null else rightD, bottomD)
    }
  }

  override fun setCompoundDrawablesWithIntrinsicBounds(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
    left?.setBounds(0, 0,
        if (leftWidth != -1) leftWidth else left.intrinsicWidth,
        if (leftHeight != -1) leftHeight else left.intrinsicHeight
    )
    top?.setBounds(0, 0,
        if (topWidth != -1) topWidth else top.intrinsicWidth,
        if (topHeight != -1) topHeight else top.intrinsicHeight
    )
    right?.setBounds(0, 0,
        if (rightWidth != -1) rightWidth else right.intrinsicWidth,
        if (rightHeight != -1) rightHeight else right.intrinsicHeight
    )
    bottom?.setBounds(0, 0,
        if (bottomWidth != -1) bottomWidth else bottom.intrinsicWidth,
        if (bottomHeight != -1) bottomHeight else bottom.intrinsicHeight
    )
    leftD = left
    topD = top
    bottomD = bottom
    setCompoundDrawables(left, top, right, bottom)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent?): Boolean {
    event?.let { e ->
      if (compoundDrawables[2] != null && (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP)) {
        val eventX = event.rawX.toInt()
        val eventY = event.rawY.toInt()
        val rect = Rect()
        getGlobalVisibleRect(rect)
        rect.left = rect.right - rightWidth - paddingEnd
        if (rect.contains(eventX, eventY)) {
          if (e.action == MotionEvent.ACTION_UP) setText("")
          return true
        }
      }
    }
    return super.onTouchEvent(event)
  }
}
package cc.ab.base.widget.discretescrollview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import cc.ab.base.R
import com.blankj.utilcode.util.SizeUtils

class DotsIndicator : LinearLayout {

  private var selection: Int = 0
  private var dotsCount: Int = 0
  var dotSize: Int = SizeUtils.dp2px(7f)
  var lastDotSize: Int = SizeUtils.dp2px(14f)
  var marginsBetweenDots: Int = SizeUtils.dp2px(8f)
  var selectedDotScaleFactor: Float = 1.4f
  var selectedDotResource: Int = R.drawable.circle_accent
  var unselectedDotResource: Int = R.drawable.circle_primary
  var firstSelectedDotResource: Int = R.drawable.ic_home_white_24dp
  var firstUnselectedDotResource: Int = R.drawable.ic_home_gray_24dp
  var needSpecial: Boolean = false//是否需要特殊处理最后一个

  var onSelectListener: ((position: Int) -> Unit)? = null

  constructor(context: Context?) : super(context) {
    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    gravity = Gravity.CENTER
  }

  constructor(
    context: Context?,
    attrs: AttributeSet
  ) : super(context, attrs) {
    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    gravity = Gravity.CENTER

    val ta = getContext().obtainStyledAttributes(
        attrs,
        R.styleable.DotsIndicator, 0, 0
    )
    dotsCount = ta.getInt(R.styleable.DotsIndicator_dots_count, 3)

    selectedDotScaleFactor =
      ta.getFloat(R.styleable.DotsIndicator_selected_dot_scale_factor, 1.4f)

    selectedDotResource =
      ta.getResourceId(R.styleable.DotsIndicator_selected_dot_resource, selectedDotResource)

    unselectedDotResource = ta.getResourceId(
        R.styleable.DotsIndicator_unselected_dot_resource,
        unselectedDotResource
    )

    firstSelectedDotResource = ta.getResourceId(
        R.styleable.DotsIndicator_first_selected_dot_resource,
        firstSelectedDotResource
    )

    firstUnselectedDotResource = ta.getResourceId(
        R.styleable.DotsIndicator_first_unselected_dot_resource,
        firstUnselectedDotResource
    )

    dotSize =
      ta.getDimensionPixelSize(R.styleable.DotsIndicator_dot_size, dotSize)

    lastDotSize =
      ta.getDimensionPixelSize(R.styleable.DotsIndicator_last_dot_size, lastDotSize)

    marginsBetweenDots =
      ta.getDimensionPixelSize(R.styleable.DotsIndicator_margins_between_dots, marginsBetweenDots)

    initDots(dotsCount)
    ta.recycle()
  }

  //当不需要特殊的时候需要设置间距(+1是为了防止放大显示不全，不能去除小数，只能往上加)
  private val marginNoSpecial = (dotSize * (selectedDotScaleFactor - 1) / 2f).toInt() + 1
  //需要特殊的时候需要设置间距(+1是为了防止放大显示不全，不能去除小数，只能往上加)
  private val marginSpecial = (lastDotSize * (selectedDotScaleFactor - 1) / 2f).toInt() + 1
  fun initDots(dotsCount: Int) {
    this.dotsCount = dotsCount
    removeAllViews()
    for (i: Int in 0 until dotsCount) {
      val dot = ImageView(context)
      dot.id = i
      dot.tag = i
      val margin = if (needSpecial) marginSpecial else marginNoSpecial
      val param =
        if (i == dotsCount - 1 && needSpecial) {
          LayoutParams(lastDotSize, lastDotSize)
        } else {
          LayoutParams(dotSize, dotSize)
        }
      if (orientation == HORIZONTAL) {
        param.marginEnd = marginsBetweenDots / 2
        param.marginStart = marginsBetweenDots / 2
        param.topMargin = margin
        param.bottomMargin = margin
        param.gravity = Gravity.CENTER_VERTICAL
      } else {
        param.marginEnd = margin
        param.marginStart = margin
        param.topMargin = marginsBetweenDots / 2
        param.bottomMargin = marginsBetweenDots / 2
        param.gravity = Gravity.CENTER_HORIZONTAL
      }
      dot.layoutParams = param
      dot.scaleType = ImageView.ScaleType.FIT_XY

      if (i == dotsCount - 1 && needSpecial) {
        if (selection == dotsCount - 1) {
          dot.setImageResource(firstSelectedDotResource)
        } else {
          dot.setImageResource(firstUnselectedDotResource)
        }
      } else {
        if (selection == i) {
          dot.setImageResource(selectedDotResource)
        } else {
          dot.setImageResource(unselectedDotResource)
        }
      }

      if (selection == i) {
        dot.scaleX = selectedDotScaleFactor
        dot.scaleY = selectedDotScaleFactor
      }

      dot.setOnClickListener {
        onSelectListener?.invoke(it.tag as Int)
        setDotSelection(it.tag as Int)
      }
      addView(dot)
    }
    setDotSelection(selection)
  }

  fun setDotSelection(position: Int) {
    if (position == selection)
      return
    val newSelection: ImageView = findViewById(position)
    val selectedDot: ImageView = findViewWithTag(selection)

    val increaseAnimator = ValueAnimator.ofFloat(1f, selectedDotScaleFactor)
    val decreaseAnimator = ValueAnimator.ofFloat(selectedDotScaleFactor, 1f)

    increaseAnimator.addUpdateListener { animator ->
      val value: Float = animator.animatedValue as Float
      newSelection.scaleX = value
      newSelection.scaleY = value
    }

    decreaseAnimator.addUpdateListener {
      val value: Float = it.animatedValue as Float
      selectedDot.scaleX = value
      selectedDot.scaleY = value
    }

    increaseAnimator.start()
    decreaseAnimator.start()

    val animationListener = object : Animator.AnimatorListener {
      override fun onAnimationRepeat(animation: Animator) {}

      override fun onAnimationEnd(animation: Animator) {
        newSelection.scaleX = selectedDotScaleFactor
        newSelection.scaleY = selectedDotScaleFactor

        selectedDot.scaleX = 1f
        selectedDot.scaleY = 1f
      }

      override fun onAnimationCancel(animation: Animator) {}

      override fun onAnimationStart(animation: Animator) {}

    }

    increaseAnimator.addListener(animationListener)
    decreaseAnimator.addListener(animationListener)

    newSelection.setImageResource(
        if (newSelection.tag == dotsCount - 1 && needSpecial) firstSelectedDotResource else selectedDotResource
    )
    selectedDot.setImageResource(
        if (selection == dotsCount - 1 && needSpecial) firstUnselectedDotResource else unselectedDotResource
    )
    selection = newSelection.tag as Int
  }
}
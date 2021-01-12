package cc.ab.base.utils

import android.graphics.Color
import android.graphics.drawable.*
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.SizeUtils
import kotlinx.coroutines.*

/**
 *description: 按下效果工具类.
 *@date 2019/7/2 14:18.
 *@author: YangYang.
 */
object PressEffectHelper {
  private val location = IntArray(2)
  private var isDown: Boolean = false
  private var time: Long = 0

  private const val LONGPRESS_TIME = 600L
  private var longPressDisposable: Job? = null
  private var isLongClick = false

  /**
   * 按下该表透明度的效果
   */
  fun alphaEffect(view: View, pressAlpha: Float = 0.8f) {
    view.setOnTouchListener { v, event ->
      if (System.currentTimeMillis() - time < 200) {
        return@setOnTouchListener true
      }
      //判断手指是否在控件范围内
      view.getLocationOnScreen(location)
      val rawX = event.rawX
      val rawY = event.rawY
      val width = view.width
      val height = view.height
      val contains =
          rawX > location[0] && rawX < location[0] + width && rawY > location[1] && rawY < location[1] + height
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          isDown = true
          v.alpha = 1f
          //为了解决列表按下滑动不执行按压效果，所以增加一点延时执行按压效果
          v.postDelayed({ v.alpha = if (isDown) pressAlpha else 1.0f }, 150)
          longClick(view)
          return@setOnTouchListener true
        }
        MotionEvent.ACTION_MOVE ->
          //如果手指移出控件，则不执行按压效果和点击效果
          if (!contains) {
            isDown = false
            v.alpha = 1f
            return@setOnTouchListener true
          }
        //-----------如果有需要执行longClick事件的需要单独处理-----------//
        MotionEvent.ACTION_UP -> {
          isDown = false
          //如果手指移出控件在放开，则不执行点击效果
          if (contains) {
            //为了解决延时执行按压效果导致点击没有按压效果的问题，所以延迟执行点击效果
            if (v.alpha == 1f) {
              v.alpha = pressAlpha
              v.postDelayed({
                v.alpha = 1f
                v.performClick()
              }, 50)
            } else {
              v.alpha = 1f
              //已经执行按压效果，则直接直接点击效果
              if (!isLongClick) {
                v.performClick()
              }
            }
            time = System.currentTimeMillis()
            cancelLongClick()
            return@setOnTouchListener true
          }
          //取消点击效果，恢复透明度
          isDown = false
          v.alpha = 1f
          cancelLongClick()
          return@setOnTouchListener true
        }
        MotionEvent.ACTION_CANCEL -> {
          isDown = false
          v.alpha = 1f
          cancelLongClick()
          return@setOnTouchListener true
        }
        else -> {
        }
      }
      false
    }
  }

  /**
   * 按下改变背景色的效果
   */
  fun bgColorEffect(
      view: View,
      bgColor: Int = Color.parseColor("#f7f7f7"),
      topLeftRadiusDp: Float = 0f,
      topRightRadiusDp: Float = 0f,
      bottomRightRadiusDp: Float = 0f,
      bottomLeftRadiusDp: Float = 0f
  ) {
    val originalBgColor = view.background
    val pressDrawable: Drawable =
        if (topLeftRadiusDp == 0f && topRightRadiusDp == 0f && bottomLeftRadiusDp == 0f && bottomRightRadiusDp == 0f) {
          ColorDrawable(bgColor)
        } else {
          val gradientDrawable = GradientDrawable()
          gradientDrawable.setColor(bgColor)
          gradientDrawable.cornerRadii =
              floatArrayOf(
                  if (topLeftRadiusDp != 0.0f) {
                    SizeUtils.dp2px(8.0f).toFloat()
                  } else {
                    0.0f
                  }, //top-left-x
                  if (topLeftRadiusDp != 0.0f) {
                    SizeUtils.dp2px(8.0f).toFloat()
                  } else {
                    0.0f
                  }, //top-left-y
                  if (topRightRadiusDp != 0.0f) {
                    SizeUtils.dp2px(8.0f).toFloat()
                  } else {
                    0.0f
                  }, //top-right-x
                  if (topRightRadiusDp != 0.0f) {
                    SizeUtils.dp2px(8.0f).toFloat()
                  } else {
                    0.0f
                  }, //top-right-y
                  if (bottomRightRadiusDp != 0.0f) {
                    SizeUtils.dp2px(8.0f).toFloat()
                  } else {
                    0.0f
                  }, //bottom-right-y
                  if (bottomRightRadiusDp != 0.0f) {
                    SizeUtils.dp2px(8.0f).toFloat()
                  } else {
                    0.0f
                  }, //bottom-right-y
                  if (bottomLeftRadiusDp != 0.0f) {
                    SizeUtils.dp2px(8.0f).toFloat()
                  } else {
                    0.0f
                  }, //bottom-left-x
                  if (bottomLeftRadiusDp != 0.0f) {
                    SizeUtils.dp2px(8.0f).toFloat()
                  } else {
                    0.0f
                  } //bottom-left-y
              )
          gradientDrawable
        }
    view.setOnTouchListener { v, event ->
      if (System.currentTimeMillis() - time < 200) {
        return@setOnTouchListener true
      }
      //判断手指是否在控件范围内
      view.getLocationOnScreen(location)
      val rawX = event.rawX
      val rawY = event.rawY
      val width = view.width
      val height = view.height
      val contains =
          rawX > location[0] && rawX < location[0] + width && rawY > location[1] && rawY < location[1] + height
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          isDown = true
          v.background = originalBgColor
          //为了解决列表按下滑动不执行按压效果，所以增加一点延时执行按压效果
          v.postDelayed({
            v.background = if (isDown) pressDrawable else originalBgColor
          }, 150)
          longClick(view)
          return@setOnTouchListener true
        }
        MotionEvent.ACTION_MOVE ->
          //如果手指移出控件，则不执行按压效果和点击效果
          if (!contains) {
            isDown = false
            v.background = originalBgColor
            return@setOnTouchListener true
          }
        //-----------如果有需要执行longClick事件的需要单独处理-----------//
        MotionEvent.ACTION_UP -> {
          isDown = false
          //如果手指移出控件在放开，则不执行点击效果
          if (contains) {
            //为了解决延时执行按压效果导致点击没有按压效果的问题，所以延迟执行点击效果
            if (v.background == originalBgColor) {
              v.background = pressDrawable
              v.postDelayed({
                v.background = originalBgColor
                v.performClick()
              }, 50)
            } else {
              v.background = originalBgColor
              //已经执行按压效果，则直接直接点击效果
              if (!isLongClick) {
                v.performClick()
              }
            }
            time = System.currentTimeMillis()
            cancelLongClick()
            return@setOnTouchListener true
          }
          //取消点击效果，恢复透明度
          isDown = false
          v.background = originalBgColor
          cancelLongClick()
          return@setOnTouchListener true
        }
        MotionEvent.ACTION_CANCEL -> {
          isDown = false
          v.background = originalBgColor
          cancelLongClick()
          return@setOnTouchListener true
        }
        else -> {
        }
      }
      false
    }
  }

  private fun longClick(view: View) {
    cancelLongClick()
    longPressDisposable = GlobalScope.launch(Dispatchers.Main) {
      delay(LONGPRESS_TIME)
      if (isActive) {
        isLongClick = true
        view.performLongClick()
      }
    }
  }

  private fun cancelLongClick() {
    longPressDisposable?.cancel()
    longPressDisposable = null
    isLongClick = false
  }
}
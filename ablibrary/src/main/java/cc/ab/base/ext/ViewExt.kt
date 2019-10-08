package cc.ab.base.ext

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.view.ViewManager
import cc.ab.base.R
import cc.ab.base.utils.PressEffectHelper

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/24 10:39
 */
//点击事件
@SuppressLint("CheckResult")
inline fun View.click(crossinline function: (view: View) -> Unit) {
  this.setOnClickListener {
    val tag = this.getTag(R.id.id_tag_click)
    if (tag == null || System.currentTimeMillis() - tag.toString().toLong() > 600) {
      this.setTag(R.id.id_tag_click, System.currentTimeMillis())
      function.invoke(it)
    }
  }
}

//显示
fun View.visible() {
  this.visibility = View.VISIBLE
}

//不显示，但占位
fun View.invisible() {
  this.visibility = View.INVISIBLE
}

//不显示，不占位
fun View.gone() {
  this.visibility = View.GONE
}

//显示或者不显示且不占位
fun View.visibleGone(visible: Boolean) = if (visible) visible() else gone()

//显示或者不显示但占位
fun View.visibleInvisible(visible: Boolean) = if (visible) visible() else invisible()

//设置按下效果为改变透明度
fun View.pressEffectAlpha(pressAlpha: Float = 0.7f) {
  PressEffectHelper.alphaEffect(this, pressAlpha)
}

//设置按下效果为改变背景色
fun View.pressEffectBgColor(
  bgColor: Int = Color.parseColor("#f7f7f7"),
  topLeftRadiusDp: Float = 0f,
  topRightRadiusDp: Float = 0f,
  bottomRightRadiusDp: Float = 0f,
  bottomLeftRadiusDp: Float = 0f
) {
  PressEffectHelper.bgColorEffect(
      this,
      bgColor,
      topLeftRadiusDp,
      topRightRadiusDp,
      bottomRightRadiusDp,
      bottomLeftRadiusDp
  )
}

//关闭按下效果
fun View.pressEffectDisable() {
  this.setOnTouchListener(null)
}

//从父控件移除
fun View.removeParent() {
  val parentTemp = parent
  if (parentTemp is ViewManager) parentTemp.removeView(this)
}

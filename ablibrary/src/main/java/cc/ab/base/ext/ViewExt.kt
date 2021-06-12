package cc.ab.base.ext

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.view.ViewManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import cc.ab.base.R
import cc.ab.base.utils.PressEffectHelper

/**
 * Description:
 * @author: Khaos
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

//获取所有父类
fun View?.getMyParents(): MutableList<View> {
  val parents = mutableListOf<View>()
  var myParent: View? = this?.parent as? View //找父控件
  for (i in 0..Int.MAX_VALUE) {
    if (myParent != null) {
      parents.add(myParent) //添加到父控件列表
      myParent = myParent.parent as? View //继续向上查找父控件
    } else break //找不到View的父控件即结束
  }
  return parents
}

//找到View所在的fragment
fun View?.getMyFragment(): Fragment? {
  (this?.context as? FragmentActivity)?.let { ac ->
    //找到所有上级View
    val parents = getMyParents()
    //找到一级(activity嵌套的fragment)fragment
    val fragments = ac.supportFragmentManager.fragments
    //再找二级(fragment嵌套的fragment)fragment
    val list = mutableListOf<Fragment>()
    list.addAll(fragments)
    fragments.forEach { c -> list.addAll(c.getAllChildFragments()) }
    if (list.isNotEmpty()) for (i in list.size - 1 downTo 0) {
      list[i].view?.let { v -> if (parents.contains(v)) return list[i] }
    }
  }
  return null //如果都找不到，则应该不是放在fragment中，可能直接放在activity中了
}

//获取生命周期管理
fun View?.getMyLifecycleOwner(): LifecycleOwner? {
  return (this?.getMyFragment()) ?: (this?.context as? LifecycleOwner) ?: ((this?.parent as? View)?.context as? LifecycleOwner)
}
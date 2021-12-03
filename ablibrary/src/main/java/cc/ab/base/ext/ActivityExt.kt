package cc.ab.base.ext

import android.app.Activity
import android.view.*
import android.widget.FrameLayout
import cc.ab.base.R

/**
 * Description:
 * @author: Khaos
 * @date: 2019/9/20 16:36
 */
//显示状态栏和虚拟导航栏(不再填充内容)
@Suppress("DEPRECATION")
fun Activity.showStatusAndNaviBar(window: Window) {
  var uiOptions = window.decorView.systemUiVisibility
  uiOptions = uiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN.inv() //显示状态栏
  uiOptions = uiOptions and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.inv() //不再填充状态栏
  uiOptions = uiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv() //显示虚拟导航键
  uiOptions = uiOptions and View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION.inv() //不再填充导航栏
  uiOptions = uiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv() //取消不会通过触摸屏幕调出导航栏
  window.decorView.systemUiVisibility = uiOptions
}

//隐藏状态栏和虚拟导航键(内容填充到虚拟导航键) https://blog.csdn.net/QQxiaoqiang1573/article/details/79867127
@Suppress("DEPRECATION")
fun Activity.hideStatusAndNaviBar(window: Window) {
  var uiOptions = window.decorView.systemUiVisibility
  uiOptions = uiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN //隐藏状态栏
  uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //拓展布局到状态栏后面
  uiOptions = uiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏导航栏，用户点击屏幕会显示导航栏
  uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //拓展布局到导航栏后面
  uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY //沉浸模式，用户可以交互的界面。同时，用户上下拉系统栏时，会自动隐藏系统栏
  window.decorView.systemUiVisibility = uiOptions
}

//常亮
fun Activity.extKeepScreenOn() {
  window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

//ContentView
val Activity.mContentView: FrameLayout
  get() {
    return this.findViewById(android.R.id.content)
  }

//Context
val Activity.mContext: Activity
  get() {
    return this
  }

//Activity
val Activity.mActivity: Activity
  get() {
    return this
  }

//监听键盘高度
fun Activity.extKeyBoard(keyCall: (statusHeight: Int, navigationHeight: Int, keyBoardHeight: Int) -> Unit) {
  mContentView.post { mContentView.layoutParams.height = mContentView.height } //防止键盘弹出导致整个布局高度变小
  this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  this.window.decorView.setOnApplyWindowInsetsListener(object : View.OnApplyWindowInsetsListener {
    var preKeyOffset: Int = 0 //键盘高度改变才回调
    override fun onApplyWindowInsets(v: View?, insets: WindowInsets?): WindowInsets {
      insets?.let { ins ->
        val navHeight = ins.systemWindowInsetBottom //下面弹窗到屏幕底部的高度，比如键盘弹出后的键盘+虚拟导航键高度
        val offset = if (navHeight < ins.stableInsetBottom) navHeight
        else navHeight - ins.stableInsetBottom
        if (offset != preKeyOffset || offset == 0) { //高度变化
          val decorHeight = mActivity.window.decorView.height //整个布局高度，包含虚拟导航键
          if (decorHeight > 0) { //为了防止手机去设置页修改虚拟导航键高度，导致整个内容显示有问题，所以需要重新设置高度(与上面设置固定高度对应)
            mContentView.layoutParams.height =
              decorHeight - navHeight.coerceAtMost(ins.stableInsetBottom) //取小值
          }
          preKeyOffset = offset
          keyCall.invoke(ins.stableInsetTop, ins.stableInsetBottom, offset)
        }
      }
      return mActivity.window.decorView.onApplyWindowInsets(insets)
    }
  })
}

//防止弹窗太快，记录弹窗时间
@Suppress("UNCHECKED_CAST")
val Activity.mDialogTimes: MutableList<Pair<String, Long>>
  get() {
    val result = mContentView.getTag(R.id.dialog_times)
    return if (result is MutableList<*>) {
      result as MutableList<Pair<String, Long>>
    } else {
      val temp = mutableListOf<Pair<String, Long>>()
      mContentView.setTag(R.id.dialog_times, temp)
      temp
    }
  }
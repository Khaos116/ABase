package cc.ab.base.ext

import android.app.Activity
import android.graphics.Color
import android.view.*
import android.widget.FrameLayout

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/20 16:36
 */

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
  mContentView.post { mContentView.layoutParams.height = mContentView.height }//防止键盘弹出导致整个布局高度变小
  this.window.decorView.setOnApplyWindowInsetsListener(object : View.OnApplyWindowInsetsListener {
    var preKeyOffset: Int = 0//键盘高度改变才回调
    override fun onApplyWindowInsets(
      v: View?,
      insets: WindowInsets?
    ): WindowInsets {
      insets?.let { ins ->
        val navHeight = ins.systemWindowInsetBottom//下面弹窗到屏幕底部的高度，比如键盘弹出后的键盘+虚拟导航键高度
        val offset = if (navHeight < ins.stableInsetBottom) navHeight
        else navHeight - ins.stableInsetBottom
        if (offset != preKeyOffset || offset == 0) {//高度变化
          val decorHeight = mActivity.window.decorView.height//整个布局高度，包含虚拟导航键
          if (decorHeight > 0) {//为了防止手机去设置页修改虚拟导航键高度，导致整个内容显示有问题，所以需要重新设置高度(与上面设置固定高度对应)
            mContentView.layoutParams.height =
              decorHeight - navHeight.coerceAtMost(ins.stableInsetBottom)//取小值
          }
          preKeyOffset = offset
          keyCall.invoke(ins.stableInsetTop, ins.stableInsetBottom, offset)
        }
      }
      return mActivity.window.decorView.onApplyWindowInsets(insets)
    }
  })
}
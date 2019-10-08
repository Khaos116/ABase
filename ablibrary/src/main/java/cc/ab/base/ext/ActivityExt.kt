package cc.ab.base.ext

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/20 16:36
 */
//全屏
fun Activity.extFullScreen() {
  window?.let { win ->
    win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    win.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
  }
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

//ContentView
val Activity.mContext: Activity
  get() {
    return this
  }


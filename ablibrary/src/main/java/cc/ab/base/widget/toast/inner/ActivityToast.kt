package cc.ab.base.widget.toast.inner

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.view.WindowManager


/**
 *description: Activity中使用的toast解决Unable to add window -- token null is not valid.
 *@date 2018/12/21 17:08.
 *@author: YangYang.
 */
class ActivityToast(context: Context) : CcToast(context) {

  override fun getWMParams(): WindowManager.LayoutParams {
    val lp = WindowManager.LayoutParams()
    lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    lp.format = PixelFormat.TRANSLUCENT
    //lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
    lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG//此方案是否更优？
    //mParams.y = mToast.getYOffset() + getNavBarHeight();
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.windowAnimations = android.R.style.Animation_Toast
    // TODO: 2018/11/20 考虑是否需要引入windowToken
    //lp.token=((Activity)getContext()).getWindow().getDecorView().getWindowToken();
    lp.gravity = getGravity()
    lp.x = getXOffset()
    lp.y = getYOffset()
    return lp
  }

  override fun getWMManager(): WindowManager? {
    //context非Activity时会抛出异常:Unable to add window -- token null is not valid; is your activity running?
    return if (mContext is Activity) {
      (mContext as Activity).getWindowManager()
    } else {
      null
    }
  }
}
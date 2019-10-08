package cc.ab.base.widget.toast.inner

import android.view.View
import cc.ab.base.widget.toast.ToastDuration

/**
 *description: 自定义toast的接口.
 *@date 2018/12/18 10:32.
 *@author: YangYang.
 */
interface IToast {

  fun show()

  fun cancel()

  fun setView(mView: View): IToast

  fun getView(): View

  fun setDuration(@ToastDuration duration: Int): IToast

  fun setGravity(gravity: Int): IToast

  fun setGravity(gravity: Int, xOffset: Int, yOffset: Int): IToast

  fun setAnimation(animation: Int): IToast

  fun setPriority(mPriority: Int): IToast
}
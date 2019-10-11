package cc.ab.base.widget.toast

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import cc.ab.base.R
import cc.ab.base.widget.toast.inner.ActivityToast
import cc.ab.base.widget.toast.inner.CcToast
import cc.ab.base.widget.toast.inner.IToast
import cc.ab.base.widget.toast.inner.SystemToast
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.RomUtils
import com.blankj.utilcode.util.SizeUtils

/**
 *description: Toast工具类防止各种异常情况,及机型适配.
 *@date 2018/12/21 17:50.
 *@author: YangYang.
 */
object AmToast {
  //上一条toast信息
  private var lastMsg: String? = null
  //上一条toast show的时间戳
  private var lastTime: Long = 0L
  //上一条toast需要展示的时长
  private var lastDuration: Int = 0

  fun make(mContext: Context?): IToast? {
    mContext?.let {
      if (NotificationManagerCompat.from(it).areNotificationsEnabled()) {
        //有通知权限直接用系统的
        return SystemToast(it)
      } else {
        //没有通知权限
        when {
          RomUtils.isHuawei() // 华为用自定义的
          -> {
            return if (ActivityUtils.getTopActivity() != null) {
              //部分机型taost需要悬浮窗权限先用activity的
              ActivityToast(ActivityUtils.getTopActivity())
            } else {
              CcToast(it)
            }
          }
          RomUtils.isXiaomi() // 小米还是用系统的没问题
          -> {
            return SystemToast(it)
          }
          RomUtils.isMeizu() // 魅族是个坑
          -> {
            return if (ActivityUtils.getTopActivity() != null) {
              //部分机型taost需要悬浮窗权限先用activity的
              ActivityToast(ActivityUtils.getTopActivity())
            } else {
              CcToast(it)
            }
          }
          else
          -> {
            //其他机型用系统的
            return SystemToast(it)
          }
        }
      }
    }
    return null
  }

  /**
   * 底部显示一个Toast
   */
  fun showBottomToast(mContext: Context?, msg: String?, duration: Int = DURATION_SHORT) {
    if (mContext == null) {
      return
    }
    if (msg.isNullOrBlank()) {
      //提示的内容为空
      return
    }
    val showDuration = if (duration == Toast.LENGTH_SHORT) {
      DURATION_SHORT
    } else {
      DURATION_LONG
    }
    if (lastMsg == msg && lastDuration != 0 && lastTime != 0L && lastDuration > (System.currentTimeMillis() - lastTime)) {
      //同样的提示且正在显示中
      return
    }
    val toast = make(mContext) ?: return
    val textView = toast.getView().findViewById(R.id.tv_content) as TextView
    textView.text = msg
    lastMsg = msg
    lastTime = System.currentTimeMillis()
    lastDuration = showDuration
    toast.setDuration(showDuration)
    toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, SizeUtils.dp2px(25F)).show()
  }

  /**
   * 中间显示一个Toast
   */
  fun showCenterToast(mContext: Context?, msg: String?, duration: Int = DURATION_SHORT) {
    if (mContext == null) {
      return
    }
    if (!AppUtils.isAppForeground()) {//非前台不提示
      return
    }
    if (msg.isNullOrBlank()) {
      //提示的内容为空
      return
    }
    val showDuration = if (duration == Toast.LENGTH_SHORT) {
      DURATION_SHORT
    } else {
      DURATION_LONG
    }
    if (lastMsg == msg && lastDuration != 0 && lastTime != 0L && lastDuration > (System.currentTimeMillis() - lastTime)) {
      //同样的提示且正在显示中
      return
    }
    val toast = make(mContext) ?: return
    val textView = toast.getView().findViewById(R.id.tv_content) as TextView
    textView.text = msg
    lastMsg = msg
    lastTime = System.currentTimeMillis()
    lastDuration = showDuration
    toast.setDuration(showDuration)
    toast.setGravity(Gravity.CENTER, 0, 0).show()
  }

  /**
   * 终止并清除所有弹窗
   */
  fun cancel() {
    CcToast.cancelAll()
    SystemToast.cancelAll()
  }

  /**
   * 清除与{@param mActivity}关联的ActivityToast，避免窗口泄漏
   */
  fun cancelActivityToast(mActivity: Activity) {
    CcToast.cancelActivityToast(mActivity)
  }
}
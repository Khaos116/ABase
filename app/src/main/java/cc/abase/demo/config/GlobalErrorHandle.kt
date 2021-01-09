package cc.abase.demo.config

import cc.ab.base.ext.toast
import cc.abase.demo.R
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.constants.ErrorCode
import com.blankj.utilcode.util.ActivityUtils

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/19 19:33
 */
object GlobalErrorHandle {
  var globalErrorCodes = mutableListOf(
      ErrorCode.NO_LOGIN //未登录
  )

  fun dealGlobalErrorCode(errorCode: Int) {
    val activity = ActivityUtils.getTopActivity()
    activity?.let { ac ->
      when (errorCode) {
        //未登录
        ErrorCode.NO_LOGIN -> {
          ac.runOnUiThread {
            ac.toast(R.string.need_login)
            LoginActivity.startActivity(ac)
          }
        }
        else -> {
        }
      }
    }
  }
}
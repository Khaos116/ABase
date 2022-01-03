package cc.abase.demo.config

import cc.ab.base.ext.toast
import cc.abase.demo.R
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.constants.MyErrorCode
import com.blankj.utilcode.util.ActivityUtils

/**
 * Description:
 * @author: Khaos
 * @date: 2019/11/19 19:33
 */
object GlobalErrorHandle {
  //需要全局的处理
  var globalErrorCodes = mutableListOf(
      MyErrorCode.NO_LOGIN //未登录
  )

  //全局处理判断，如果处理后，则将code改为已全局处理，否则使用原来的code
  fun dealGlobalErrorCode(errorCode: Int): Int  {
    val activity = ActivityUtils.getTopActivity()
    activity?.let { ac ->
      when (errorCode) {
        //未登录
        MyErrorCode.NO_LOGIN -> {
          ac.runOnUiThread {
            ac.toast(R.string.登录过期请重新登录)
            LoginActivity.startActivity(ac)
          }
        }
        else -> {
        }
      }
    }
    return if (globalErrorCodes.any { a -> a == errorCode }) MyErrorCode.ALREADY_DEAL else errorCode
  }
}
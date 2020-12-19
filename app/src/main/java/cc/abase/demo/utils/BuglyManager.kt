package cc.abase.demo.utils

import android.app.Application
import android.text.TextUtils
import cc.abase.demo.BuildConfig
import cc.abase.demo.R
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.Utils
import com.tencent.bugly.crashreport.CrashReport

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/6 17:41
 */
class BuglyManager private constructor() {
  private object SingletonHolder {
    val holder = BuglyManager()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //初始化bugly
  fun initBugly(application: Application) { //bugly异常检测
    //初始化key
    CrashReport.initCrashReport(
        application, StringUtils.getString(R.string.bugly_app_id), BuildConfig.DEBUG
    )
    //上报渠道信息
//    if (BuildConfigApp.isDeveloper(application)) {
//      //开发人员自己运行的异常渠道收集
//      CrashReport.setAppChannel(
//        application,
//        "开发人员AS运行-${if (BuildConfigApp.DEBUG) "Debug" else "Release"}"
//      )
//    } else {
//      //正常打包的异常渠道收集
//      CrashReport.setAppChannel(application, BuildConfigApp.getChannelName(application))
//    }
    //上报APP信息
    CrashReport.putUserData(application, "appInfo", AppInfoUtils.instance.getAppInfo())
    //用户信息
    setBuglyUserInfo(application)
  }

  //给bugly设置用户信息
  private fun setBuglyUserInfo(application: Application) {
//    UserLoginManager.getInstance()
//        .user?.let { user ->
//      CrashReport.setUserId(user.memid?.toString() ?: "memid=null")
//      //上报用户id
//      CrashReport.putUserData(application, "userId", user.memid?.toString() ?: "")
//      //上报用户手机号
//      CrashReport.putUserData(application, "phoneNumber", user.mobileno ?: "")
//    }
  }

  //上报指定的异常信息
  fun reportException(
    msg: String?,
    code: Int = 0
  ) {
    val message = if (TextUtils.isEmpty(msg)) "message == null" else msg
    CrashReport.setUserSceneTag(Utils.getApp(), code)
    CrashReport.postCatchedException(Throwable(message))
  }
}
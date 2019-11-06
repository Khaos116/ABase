package cc.abase.demo.utils

import cc.abase.demo.BuildConfig
import cc.abase.demo.R
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.StringUtils

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/6 17:29
 */
class AppInfoUtils private constructor() {
  private object SingletonHolder {
    val holder = AppInfoUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //获取APP版本信息
  fun getAppInfo(): String {
    val builder: StringBuilder = StringBuilder()
    builder.append("版本信息：")
        .append("\n")
        .append("DEBUG：")
        .append(BuildConfig.DEBUG)
        .append("\n")
        .append("BUILD_TIME：")
        .append(StringUtils.getString(R.string.build_time))
        .append("\n")
        .append("VERSION_NAME：")
        .append(AppUtils.getAppVersionName())
        .append("\n")
        .append("VERSION_CODE：")
        .append(AppUtils.getAppVersionCode())
    return builder.toString()
  }
}
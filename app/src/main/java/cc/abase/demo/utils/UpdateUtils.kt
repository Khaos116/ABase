package cc.abase.demo.utils

import android.content.Intent
import android.net.Uri
import cc.ab.base.ext.openOutLink
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils

/**
 * https://www.jianshu.com/p/b544810beac3
 * Author:Khaos
 * Date:2022/4/25
 * Time:11:09
 */
object UpdateUtils {
  private const val googlePlay = "com.android.vending"

  fun openGooglePlayByUpdate() {
    val packageName = AppUtils.getAppPackageName()
    val url = "https://play.google.com/store/apps/details?id=$packageName"
    if (AppUtils.isAppInstalled(googlePlay)) {
      try {
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage(googlePlay)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        Utils.getApp().startActivity(intent)
      } catch (e: Exception) {
        e.printStackTrace()
        url.openOutLink()
      }
    } else {
      url.openOutLink()
    }
  }
}
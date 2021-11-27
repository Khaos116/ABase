package cc.abase.demo.component.update

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.*
import cc.abase.demo.constants.StringConstants
import cc.abase.demo.utils.NetUtils
import com.blankj.utilcode.util.*
import java.io.File

/**
 * Description:
 * @author: Khaos
 * @date: 2019/10/30 10:09
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

  @SuppressLint("MissingPermission")
  override fun onReceive(context: Context, intent: Intent) {
    intent.action?.let {
      when (it) {
        //安装
        StringConstants.Update.INTENT_KEY_INSTALL_APP -> {
          //点击安装取消消息
          if (ActivityUtils.getActivityList().isNullOrEmpty()) (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .cancel(intent.getIntExtra(StringConstants.Update.INTENT_KEY_UPDATE_ID, 0))
          val path = intent.getStringExtra(StringConstants.Update.INTENT_KEY_INSTALL_PATH)
          if (path != null && File(path).exists()) {
            AppUtils.installApp(path)
            NotificationUtils.setNotificationBarVisibility(false) //收起通知栏
            if (ActivityUtils.getActivityList().isNullOrEmpty()) NotificationUtils.cancelAll()
          } else {
            val apkUrl = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH)
            val appName = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_NAME)
            val version = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_VERSION)
            CcUpdateService.startIntent(apkUrl ?: "", appName ?: "", version ?: "", true)
          }
        }
        //重试
        StringConstants.Update.INTENT_KEY_APK_DOWNLOAD_ERROR -> if (NetUtils.checkNetToast()) {
          if (ActivityUtils.getActivityList().isNullOrEmpty()) {
            NotificationUtils.cancelAll()
          } else {
            val apkUrl = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH)
            val appName = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_NAME)
            val version = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_VERSION)
            CcUpdateService.startIntent(apkUrl ?: "", appName ?: "", version ?: "", true)
          }
        }
      }
    }
  }
}

/**
 * ★★★--如果检测到文件已下载，则直接添加以下监听，监听APP安装时关闭APP--★★★
 *
//<editor-fold defaultstate="collapsed" desc="监听APP安装">
private var hasRegister = false
//检测文件已经是完整版本的，则直接监听安装并调用AppUtils.installApp(file)
private fun registerAppInstall() {
if (!hasRegister) {
hasRegister = true
val intentFilter = IntentFilter()
intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
intentFilter.addDataScheme("package")
Utils.getApp().registerReceiver(mInstallAppBroadcastReceiver, intentFilter)
}
}
//页面销毁时记得取消注册
fun unregisterAppInstall() {
if (hasRegister) {
hasRegister = false
Utils.getApp().unregisterReceiver(mInstallAppBroadcastReceiver)
}
}

private val mInstallAppBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
override fun onReceive(context: Context, intent: Intent?) {
if (intent != null && TextUtils.equals(Intent.ACTION_PACKAGE_ADDED, intent.action)) {
intent.data?.schemeSpecificPart?.let { packageName ->
if (packageName == AppUtils.getAppPackageName()) {
unregisterAppInstall()
} else if (packageName == downApkPackageName) { //兼容正式和测试包名不一样
//downApkPackageName = AppUtils.getApkInfo(fileApk.absolutePath)?.packageName ?: ""
unregisterAppInstall()
AppUtils.exitApp()
}
}
}
}
}
//</editor-fold>

 */
package cc.abase.demo.component.update

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.*
import cc.abase.demo.constants.IntConstants
import cc.abase.demo.constants.StringConstants
import cc.abase.demo.utils.NetUtils
import com.blankj.utilcode.util.AppUtils
import java.lang.reflect.Method

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/30 10:09
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

  override fun onReceive(
    context: Context,
    intent: Intent
  ) {
    intent.action?.let {
      when (it) {
        //安装
        StringConstants.Update.INTENT_KEY_INSTALL_APP -> {
          collapsingNotification(context)
          (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
              .cancel(IntConstants.Notification.UPDATE_ID)
          val path = intent.getStringExtra(StringConstants.Update.INTENT_KEY_INSTALL_PATH)
          AppUtils.installApp(path)
        }
        //重试
        StringConstants.Update.INTENT_KEY_APK_DOWNLOAD_ERROR -> if (NetUtils.instance.checkToast()) {
          val apkUrl = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH)
          val mApkVersion = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_NAME)
          CcUpdateService.startIntent(apkUrl ?: "", mApkVersion ?: "", true)
        }
      }
    }
  }

  /**
   * 折叠通知栏
   */
  @SuppressLint("PrivateApi")
  private fun collapsingNotification(context: Context) {
    try {
      var serviceName = Context.VIBRATOR_SERVICE
      serviceName = "statusbar"
      val service = context.getSystemService(serviceName) ?: return
      val clazz = Class.forName("android.app.StatusBarManager")
      val sdkVersion = android.os.Build.VERSION.SDK_INT
      val collapse: Method
      if (sdkVersion <= 16) {
        collapse = clazz.getMethod("collapse")
      } else {
        collapse = clazz.getMethod("collapsePanels")
      }
      collapse.isAccessible = true
      collapse.invoke(service)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
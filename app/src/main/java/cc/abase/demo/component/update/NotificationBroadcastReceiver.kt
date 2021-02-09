package cc.abase.demo.component.update

import android.app.NotificationManager
import android.content.*
import cc.abase.demo.constants.StringConstants
import cc.abase.demo.utils.NetUtils
import com.blankj.utilcode.util.*
import java.io.File

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/30 10:09
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    intent.action?.let {
      when (it) {
        //安装
        StringConstants.Update.INTENT_KEY_INSTALL_APP -> {
          //点击安装关闭通知栏
          if (ActivityUtils.getActivityList().isNullOrEmpty()) (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
              .cancel(intent.getIntExtra(StringConstants.Update.INTENT_KEY_UPDATE_ID, 0))
          val path = intent.getStringExtra(StringConstants.Update.INTENT_KEY_INSTALL_PATH)
          if (path != null && File(path).exists()) {
            AppUtils.installApp(path)
            NotificationUtils.setNotificationBarVisibility(false) //收起通知栏
          } else {
            val apkUrl = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH)
            val appName = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_NAME)
            val version = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_VERSION)
            CcUpdateService.startIntent(apkUrl ?: "", appName ?: "", version ?: "", true)
          }
        }
        //重试
        StringConstants.Update.INTENT_KEY_APK_DOWNLOAD_ERROR -> if (NetUtils.checkNetToast()) {
          val apkUrl = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH)
          val appName = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_NAME)
          val version = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_VERSION)
          CcUpdateService.startIntent(apkUrl ?: "", appName ?: "", version ?: "", true)
        }
      }
    }
  }
}
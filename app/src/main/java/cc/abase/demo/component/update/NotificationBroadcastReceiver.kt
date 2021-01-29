package cc.abase.demo.component.update

import android.content.*
import cc.abase.demo.constants.StringConstants
import cc.abase.demo.utils.NetUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.NotificationUtils

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
          //收起通知栏
          NotificationUtils.setNotificationBarVisibility(false)
          //点击安装关闭通知栏
          //(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
          //    .cancel(intent.getIntExtra(StringConstants.Update.INTENT_KEY_UPDATE_ID, 0))
          val path = intent.getStringExtra(StringConstants.Update.INTENT_KEY_INSTALL_PATH)
          AppUtils.installApp(path)
        }
        //重试
        StringConstants.Update.INTENT_KEY_APK_DOWNLOAD_ERROR -> if (NetUtils.checkNetToast()) {
          val apkUrl = intent.getStringExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH)
          CcUpdateService.startIntent(apkUrl ?: "", true)
        }
      }
    }
  }
}
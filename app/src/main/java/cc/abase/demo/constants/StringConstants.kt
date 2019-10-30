package cc.abase.demo.constants

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/30 10:10
 */
interface StringConstants {
  interface Update {
    companion object {
      //安装APP的intent key
      const val INTENT_KEY_INSTALL_APP = "INTENT_KEY_INSTALL_APP"
      //下载app失败
      const val INTENT_KEY_APK_DOWNLOAD_ERROR = "INTENT_KEY_APK_DOWNLOAD_ERROR"
      //需要安装的APK路径
      const val INTENT_KEY_INSTALL_PATH = "INTENT_KEY_INSTALL_PATH"
      const val INTENT_KEY_RETRY_PATH = "INTENT_KEY_RETRY_PATH"
      const val INTENT_KEY_RETRY_NAME = "INTENT_KEY_RETRY_NAME"
    }
  }
}
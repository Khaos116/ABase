package cc.abase.demo.constants

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/30 10:10
 */
object StringConstants {
  object Update {
    //安装APP的intent key
    const val INTENT_KEY_INSTALL_APP = "INTENT_KEY_INSTALL_APP"

    //更新的消息id
    const val INTENT_KEY_UPDATE_ID = "INTENT_KEY_UPDATE_ID"

    //下载app失败
    const val INTENT_KEY_APK_DOWNLOAD_ERROR = "INTENT_KEY_APK_DOWNLOAD_ERROR"

    //需要安装的APK路径
    const val INTENT_KEY_INSTALL_PATH = "INTENT_KEY_INSTALL_PATH"
    const val INTENT_KEY_RETRY_PATH = "INTENT_KEY_RETRY_PATH"
    const val INTENT_KEY_RETRY_NAME = "INTENT_KEY_RETRY_NAME"
    const val INTENT_KEY_RETRY_VERSION = "INTENT_KEY_RETRY_VERSION"
  }

  object Tag {
    //悬浮窗播放Tag
    const val FLOAT_PLAY = "float_play"
  }
}
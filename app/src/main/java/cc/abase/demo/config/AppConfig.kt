package cc.abase.demo.config

/**
 * @author: CASE
 * @date: 2020/3/5 9:41
 */
class AppConfig {
  companion object {
    //是否需要自动登录
    val NEE_AUTO_LOGIN: Boolean = System.currentTimeMillis() > 0L
  }
}
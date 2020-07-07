package cc.abase.demo.config

/**
 * Description: 动态配置
 * @see AppConfig.defaultAppName 代码设置APP名称
 * @author: caiyoufei
 * @date: 2020/3/5 9:41
 */
class AppConfig {
  companion object {
    //默认APP的名称，通过Gradle读取，勿删
    val defaultAppName = "2020ABase"

    //是否需要自动登录
    val NEE_AUTO_LOGIN: Boolean = System.currentTimeMillis() > 0L
  }
}
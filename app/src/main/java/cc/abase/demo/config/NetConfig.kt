package cc.abase.demo.config

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/5 9:41
 */
class NetConfig {
  companion object {
    //相关接口使用Rxhttp还是Fuel(登录注册和主页三个tab)
    val USE_RXHTTP: Boolean = System.currentTimeMillis() > 0L

    //是否需要自动登录
    val NEE_AUTO_LOGIN: Boolean = !USE_RXHTTP
  }
}
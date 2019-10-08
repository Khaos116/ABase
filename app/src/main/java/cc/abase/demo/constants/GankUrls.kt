package cc.abase.demo.constants

/**
 * Description:干货集中营相关开发api地址
 * @author: caiyoufei
 * @date: 2019/10/8 18:48
 */
interface GankUrls {
  companion object {
    const val BASE_URL = "https://gank.io/api/"
    const val ANDROID = "data/Android/%s/%s"//{pageSize}/{page}
    const val FULI = "data/福利/%s/%s"//{pageSize}/{page}"
    const val TODAY = "today"
  }
}
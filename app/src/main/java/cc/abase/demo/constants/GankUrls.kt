package cc.abase.demo.constants

/**
 * Description:干货集中营相关开发api地址
 * @author: caiyoufei
 * @date: 2019/10/8 18:48
 */
interface GankUrls {
  companion object {
    const val ANDROID = "https://gank.io/api/data/Android/%s/%s"//{pageSize}/{page}
    const val FULI = "https://gank.io/api/data/福利/%s/%s"//{pageSize}/{page}"
    const val TODAY = "https://gank.io/api/today"
  }
}
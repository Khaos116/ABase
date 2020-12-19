package cc.abase.demo.constants

import rxhttp.wrapper.annotation.DefaultDomain
import rxhttp.wrapper.annotation.Domain




/**
 * Description:
 * 域名配置https://github.com/liujingxing/okhttp-RxHttp/wiki/%E9%85%8D%E7%BD%AEBaseUrl
 * @author: CASE
 * @date: 2020/2/19 17:03
 */
open class BaseUrl {
  companion object {
    @DefaultDomain //设置为默认域名
    const val baseUrl = "https://www.wanandroid.com/"

    @Domain(name = "Gank") //非默认域名，并取别名为Gank
    const val gankUrl = "https://gank.io/api/"

    @Domain(name = "Wan") //非默认域名，并取别名为Wan
    const val wanUrl = "https://www.wanandroid.com/"
  }
}
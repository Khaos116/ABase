package cc.abase.demo.constants

import rxhttp.wrapper.annotation.DefaultDomain

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/2/19 17:03
 */
open class BaseUrl {
  companion object {
    @DefaultDomain //设置为默认域名
    const val baseUrl = "https://www.wanandroid.com/"
  }
}
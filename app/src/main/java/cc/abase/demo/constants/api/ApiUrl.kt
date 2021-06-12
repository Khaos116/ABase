package cc.abase.demo.constants.api

import cc.abase.demo.utils.MMkvUtils

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/12
 * @Time：16:42
 */
object ApiUrl {
  const val baseUrlDebug = "https://www.wanandroid.com/" //测试版域名地址
  const val baseUrlRelease = "https://www.wanandroid.com/" //正式版域名地址

  //APP使用的域名地址
  var appBaseUrl = ""
    get() {
      if (field.isBlank()) field = MMkvUtils.getBaseUrl()
      return field
    }
}
package cc.abase.demo.rxhttp.config

import cc.abase.demo.constants.api.ApiUrl
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import rxhttp.HttpSender
import rxhttp.wrapper.cookie.ICookieJar

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/4 18:08
 */
object RxCookie {
  //保存cookie
  fun setCookie(cookie: String, url: String = ApiUrl.appBaseUrl) {
    // HttpUrl.parse(url)
    url.toHttpUrlOrNull()
        ?.let { http ->
          Cookie.parse(http, cookie)
              ?.let { cookie ->
                HttpSender.getOkHttpClient().cookieJar.saveFromResponse(http, mutableListOf(cookie))
              }
        }
  }

  //读取cookie
  fun getCookie(url: String = ApiUrl.appBaseUrl): MutableList<Cookie>? {
    // HttpUrl.parse(url)
    url.toHttpUrlOrNull()
        ?.let {
          return HttpSender.getOkHttpClient().cookieJar.loadForRequest(it)
              .toMutableList()
        }
    return null
  }

  //移除对于地址的cookie
  fun removeCookie(url: String = ApiUrl.appBaseUrl) {
    removeCookie(url, false)
  }

  //移除所有cookie
  fun removeAllCookie(url: String = ApiUrl.appBaseUrl) {
    removeCookie(url, true)
  }

  //移除cookie
  private fun removeCookie(url: String = ApiUrl.appBaseUrl, all: Boolean) {
    (HttpSender.getOkHttpClient().cookieJar as ICookieJar).let {
      if (all) it.removeAllCookie()
      // else it.removeCookie(HttpUrl.parse(url))
      else it.removeCookie(url.toHttpUrlOrNull())
    }
  }
}
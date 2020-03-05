package cc.abase.demo.rxhttp.config

import cc.abase.demo.constants.BaseUrl
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import rxhttp.wrapper.cookie.ICookieJar
import rxhttp.wrapper.param.RxHttp

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/4 18:08
 */
class RxCookie private constructor() {
  private object SingletonHolder {
    val holder = RxCookie()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //保存cookie
  fun setCookie(
    cookie: String,
    url: String = BaseUrl.baseUrl
  ) {
    url.toHttpUrlOrNull()
        ?.let { http ->
          Cookie.parse(http, cookie)
              ?.let { cookie ->
                RxHttp.getOkHttpClient()
                    .cookieJar.saveFromResponse(http, mutableListOf(cookie))
              }
        }
  }

  //读取cookie
  fun getCookie(url: String = BaseUrl.baseUrl): MutableList<Cookie>? {
    val cookieJar =
      url.toHttpUrlOrNull()
          ?.let {
            return RxHttp.getOkHttpClient()
                .cookieJar.loadForRequest(it)
                .toMutableList()
          }
    return null
  }

  //移除对于地址的cookie
  fun removeCookie(url: String = BaseUrl.baseUrl) {
    removeCookie(url, false)
  }

  //移除所有cookie
  fun removeAllCookie(url: String = BaseUrl.baseUrl) {
    removeCookie(url, true)
  }

  //移除cookie
  private fun removeCookie(
    url: String = BaseUrl.baseUrl,
    all: Boolean
  ) {
    (RxHttp.getOkHttpClient()
        .cookieJar as ICookieJar).let {
      if (all) it.removeAllCookie()
      else it.removeCookie(url.toHttpUrlOrNull())
    }
  }
}
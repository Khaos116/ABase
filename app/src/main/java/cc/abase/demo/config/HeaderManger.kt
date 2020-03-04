package cc.abase.demo.config

import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import java.util.HashMap

/**
 * Description:统一请求头拦截处理，如添加header和token等
 * @author: caiyoufei
 * @date: 2019/9/30 21:01
 */
class HeaderManger private constructor() {
  private object SingletonHolder {
    val holder = HeaderManger()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //获取固定header
  fun getStaticHeaders(): Map<String, String> {
    val headers = HashMap<String, String>()
    val map = HashMap<String, String>()
    map["os"] = "0"  //1：ios    0:Android
    map["clientVersion"] = AppUtils.getAppVersionName()
    map["channel"] = "10000"//BuildConfigApp.getChannelId()
    headers["Connection"] = "close"
    headers["Accept"] = "*/*"
    headers["Content-Type"] = "application/x-www-form-urlencoded;charset=utf-8"
    headers["Charset"] = "UTF-8"
    headers["aimy-drivers"] = GsonUtils.toJson(map)
    return headers
  }

  //获取动态header
  fun getTokenPair(): Pair<String, String>? {
    val token = MMkvUtils.instance.getToken()
    return if (token.isNullOrBlank()) null else Pair("Cookie", token)
  }
}
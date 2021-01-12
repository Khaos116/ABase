package cc.abase.demo.config

import cc.abase.demo.constants.api.GankUrls
import cc.abase.demo.constants.api.WanUrls
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import java.util.HashMap

/**
 * Description:统一请求头拦截处理，如添加header和token等
 * @author: CASE
 * @date: 2019/9/30 21:01
 */
object HeaderManger {
  //获取固定header
  fun getStaticHeaders(): Map<String, String> {
    val headers = HashMap<String, String>()
    val map = HashMap<String, String>()
    map["os"] = "Android"  //1：ios    0:Android
    map["clientVersion"] = AppUtils.getAppVersionName()
    map["channel"] = "10000" //BuildConfigApp.getChannelId()
    headers["Connection"] = "close"
    headers["Accept"] = "*/*"
    headers["Content-Type"] = "application/x-www-form-urlencoded;charset=utf-8"
    headers["Charset"] = "UTF-8"
    headers["app-info"] = GsonUtils.toJson(map)
    return headers
  }

  //获取动态header
  fun getTokenPair(): Pair<String, String>? {
    val token = MMkvUtils.getToken()
    return if (token.isNullOrBlank()) null else Pair("Cookie", token)
  }

  //不需要Token的接口
  val noTokenUrls = listOf(
      WanUrls.User.LOGIN,
      WanUrls.User.REGISTER,
      WanUrls.Home.BANNER,
      WanUrls.Home.ARTICLE,
      GankUrls.ANDROID,
  )
}
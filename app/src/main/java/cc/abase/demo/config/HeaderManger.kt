package cc.abase.demo.config

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import com.github.kittinunf.fuel.core.FoldableRequestInterceptor
import com.github.kittinunf.fuel.core.RequestTransformer
import java.util.HashMap

/**
 * Description:请求头和token等
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
//  fun getDynamicHeaders(): Map<String, String> {
//    val headers = HashMap<String, String>()
//    headers["Authorization"] = OauthRepo.getAuthorization()
//    return headers
//  }

  //获取fuel添加的header
  fun fuelHeader(): FoldableRequestInterceptor {
    return object : FoldableRequestInterceptor {
      override fun invoke(next: RequestTransformer): RequestTransformer {
        return { request ->
          request.header(getStaticHeaders())
          next(request)
        }
      }
    }
  }
}
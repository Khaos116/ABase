package cc.abase.demo.config

import cc.abase.demo.constants.WanUrls
import cc.abase.demo.repository.UserRepository
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import com.github.kittinunf.fuel.core.*
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

  //不需要Token的接口
  private val noTokenUrls = listOf(
      FuelManager.instance.basePath ?: "" + WanUrls.User.LOGIN,
      FuelManager.instance.basePath ?: "" + WanUrls.User.REGISTER
  )

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
    val token = UserRepository.instance.getToken()
    return if (token.isNullOrBlank()) null else Pair("Cookie", token)
  }

  //获取fuel添加的header
  fun fuelHeader(): FoldableRequestInterceptor {
    return object : FoldableRequestInterceptor {
      override fun invoke(next: RequestTransformer): RequestTransformer {
        return { request ->
          //为了防止原本的header被覆盖，所以判断没有的才添加(否则会出现文件上传失败的情况)
          val temp = request.headers
          getStaticHeaders().entries.forEach { map ->
            if (!temp.containsKey(map.key)) request.header(Pair(map.key, map.value))
          }
          getTokenPair()?.let { pair ->
            //没有在去除token的接口就添加token
            if (!noTokenUrls.contains(request.url.toString())) {
              request.appendHeader(pair)
            }
          }
          next(request)
        }
      }
    }
  }
}
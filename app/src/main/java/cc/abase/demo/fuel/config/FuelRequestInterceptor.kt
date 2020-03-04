package cc.abase.demo.fuel.config

import cc.abase.demo.config.HeaderManger
import cc.abase.demo.constants.WanUrls
import com.github.kittinunf.fuel.core.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/4 15:17
 */
class FuelRequestInterceptor private constructor() {
  private object SingletonHolder {
    val holder = FuelRequestInterceptor()
  }

  companion object {
    val instance =
      SingletonHolder.holder
  }

  //不需要Token的接口
  private val noTokenUrls = listOf(
      FuelManager.instance.basePath ?: "" + WanUrls.User.LOGIN,
      FuelManager.instance.basePath ?: "" + WanUrls.User.REGISTER
  )

  //获取fuel添加的header
  fun fuelHeader(): FoldableRequestInterceptor {
    return object : FoldableRequestInterceptor {
      override fun invoke(next: RequestTransformer): RequestTransformer {
        return { request ->
          //为了防止原本的header被覆盖，所以判断没有的才添加(否则会出现文件上传失败的情况)
          val temp = request.headers
          HeaderManger.instance.getStaticHeaders()
              .entries.forEach { map ->
            if (!temp.containsKey(map.key)) request.header(Pair(map.key, map.value))
          }
          HeaderManger.instance.getTokenPair()
              ?.let { pair ->
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
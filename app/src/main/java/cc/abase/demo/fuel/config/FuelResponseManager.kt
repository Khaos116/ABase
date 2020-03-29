package cc.abase.demo.fuel.config

import cc.abase.demo.fuel.repository.UserRepositoryFuel
import com.github.kittinunf.fuel.core.FoldableResponseInterceptor
import com.github.kittinunf.fuel.core.ResponseTransformer

/**
 * Description:统一响应拦截处理，如token、cookie
 * @author: caiyoufei
 * @date: 2019/10/12 23:19
 */
class FuelResponseManager private constructor() {
  private object SingletonHolder {
    val holder = FuelResponseManager()
  }

  companion object {
    val instance =
      SingletonHolder.holder
  }

  //响应拦截
  fun fuelResponse(): FoldableResponseInterceptor {
    return object : FoldableResponseInterceptor {
      override fun invoke(next: ResponseTransformer): ResponseTransformer {
        return { request, response ->
          if (response.headers.containsKey("Set-Cookie")) {
              //更新cookie
            for (s in response.header("Set-Cookie")) {
              if (s.contains("SESSIONID", true)) {
                UserRepositoryFuel.instance.setToken(s.split(";")[0],request.url.toString())
                break
              }
            }
          }
          val result = String(response.body().toByteArray())
          next(request, response)
        }
      }
    }
  }
}
package cc.ab.base.net.http

import android.util.Log
import com.github.kittinunf.fuel.core.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/8 10:58
 */
object FuelHelper {
  fun initFuel(
    baseUrl: String,
    timeOut: Int = 30,
    headerInterceptor: FoldableRequestInterceptor? = null
  ) {
    //服务器接口地址
    FuelManager.instance.basePath = baseUrl
    //超时时间
    FuelManager.instance.timeoutInMillisecond = timeOut * 1000
    FuelManager.instance.timeoutReadInMillisecond = timeOut * 1000
    //添加header拦截器
    headerInterceptor?.let { FuelManager.instance.addRequestInterceptor(it) }
    //添加请求日志拦截器
    FuelManager.instance.addRequestInterceptor(cUrlLoggingRequestInterceptor())
  }

  private fun cUrlLoggingRequestInterceptor() = { next: (Request) -> Request ->
    { r: Request ->
      val logging = StringBuffer()
      logging.append("\n-----Method = ${r.method}")
      logging.append("\n-----headers = ${r.headers}")
      logging.append("\n-----url---->${r.url}")
      when (r.method) {
        Method.POST -> {
          logging.append("\n-----request parameters:")
          r.parameters.forEach {
            logging.append("\n-----${it.first}=${it.second}")
          }
        }
        else -> {

        }
      }
      Log.e("CASE", logging.toString())
      next(r)
    }
  }
}
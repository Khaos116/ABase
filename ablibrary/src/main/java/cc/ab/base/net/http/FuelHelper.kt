package cc.ab.base.net.http

import android.util.Log
import cc.ab.base.net.http.ssl.SSLManager
import cc.ab.base.utils.CharlesUtils
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
    requestInterceptor: FoldableRequestInterceptor? = null,
    responseInterceptor: FoldableResponseInterceptor? = null
  ) {
    //服务器接口地址
    FuelManager.instance.basePath = baseUrl
    //超时时间
    FuelManager.instance.timeoutInMillisecond = timeOut * 1000
    FuelManager.instance.timeoutReadInMillisecond = timeOut * 1000
    //添加拦截器
    requestInterceptor?.let { FuelManager.instance.addRequestInterceptor(it) }
    responseInterceptor?.let { FuelManager.instance.addResponseInterceptor(it) }
    //添加请求日志拦截器
    FuelManager.instance.addResponseInterceptor(CCResponseInterceptor)
    FuelManager.instance.addRequestInterceptor(CCRequestInterceptor)
    val ssl = CharlesUtils.getInstance()
        .getFuelCharlesSSL(
      CharlesUtils.getInstance().getCharlesInputStream("charles.pem")
        )
    if (ssl != null) {
      FuelManager.instance.socketFactory = ssl
    } else {
      SSLManager.createSSLSocketFactory()
          ?.let { FuelManager.instance.socketFactory = it }
    }
  }

  private object CCRequestInterceptor : FoldableRequestInterceptor {
    override fun invoke(next: RequestTransformer): RequestTransformer {
      return { request ->
        val logging = StringBuffer()
        logging.append("\n ")
        logging.append("\n┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━请求参数拦截开始━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━>>>")
        logging.append("\n┃")
        logging.append("\n┣━━━   Method = ${request.method}")
        logging.append("\n┃")
        logging.append("\n┣━━━   headers = ${request.headers}")
        logging.append("\n┃")
        logging.append("\n┣━━━   url = ${request.url}")
        when (request.method) {
          Method.POST -> {
            if (!request.parameters.isNullOrEmpty()) {
              logging.append("\n┃")
              logging.append("\n┣━━━   request parameters:")
            }
            request.parameters.forEach {
              logging.append("\n┣━━━      [${it.first}]=${it.second}")
            }
          }
          else -> {
          }
        }
        logging.append("\n┃")
        logging.append("\n┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━请求参数拦截结束━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<<<\n")
        Log.i("request", logging.toString())
        next(request)
      }
    }
  }

  private object CCResponseInterceptor : FoldableResponseInterceptor {
    override fun invoke(next: ResponseTransformer): ResponseTransformer {
      return { request, response ->
        val logging = StringBuffer()
        logging.append("\n ")
        logging.append("\n┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━响应结果拦截开始━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━>>>")
        logging.append("\n┃")
        logging.append("\n┣━━━   Method = ${request.method}")
        logging.append("\n┃")
        logging.append("\n┣━━━   headers = ${request.headers}")
        when (request.method) {
          Method.POST -> {
            if (!request.parameters.isNullOrEmpty()) {
              logging.append("\n┃")
              logging.append("\n┣━━━   request parameters:")
            }
            request.parameters.forEach {
              logging.append("\n┣━━━      [${it.first}]=${it.second}")
            }
          }
          else -> {
          }
        }
        logging.append("\n┃")
        logging.append(
          "\n┣━━━   response = ${response.toString().trim().replace(
            "\n",
            "\n┃                     "
          )}"
        )
        logging.append("\n┃")
        logging.append("\n┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━响应结果拦截结束━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<<<\n")
        Log.i("response", logging.toString())
        next(request, response)
      }
    }
  }
}
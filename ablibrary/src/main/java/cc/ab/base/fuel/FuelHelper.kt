package cc.ab.base.fuel

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
    FuelManager.instance.apply {
      //服务器接口地址
      basePath = baseUrl
      //超时时间
      timeoutInMillisecond = timeOut * 1000
      timeoutReadInMillisecond = timeOut * 1000
      //添加拦截器
      requestInterceptor?.let { addRequestInterceptor(it) }
      responseInterceptor?.let { addResponseInterceptor(it) }
      //添加请求日志拦截器
      addResponseInterceptor(CCResponseInterceptor)
      addRequestInterceptor(CCRequestInterceptor)
      val ssl = CharlesUtils.getInstance()
        .getFuelCharlesSSL(CharlesUtils.getInstance().getCharlesInputStream("charles.pem"))
      if (ssl != null) {
        socketFactory = ssl
      } else {
        SSLManager.createSSLSocketFactory()?.let { socketFactory = it }
      }
    }
  }

  //换行符
  private var line = "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
  //空格符
  private var space = "\t"
  //每行最大数量
  private var lineCount = 150

  private object CCRequestInterceptor : FoldableRequestInterceptor {
    override fun invoke(next: RequestTransformer): RequestTransformer {
      return { request ->
        val logging = StringBuffer()
        logging.append("↓↓↓\n ")
        logging.append("${line}┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━请求参数拦截开始━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━>>>")
        logging.append("${line}┃")
        logging.append("${line}┣━━━   Method = ${request.method}")
        logging.append("${line}┃")
        logging.append("${line}┣━━━   headers = ${request.headers}")
        logging.append("${line}┃")
        logging.append("${line}┣━━━   url = ${request.url}")
        when (request.method) {
          Method.POST -> {
            if (!request.parameters.isNullOrEmpty()) {
              logging.append("${line}┃")
              logging.append("${line}┣━━━   request parameters:")
            }
            request.parameters.forEach {
              logging.append("${line}┣━━━      [${it.first}]=${it.second}")
            }
          }
          else -> {
          }
        }
        logging.append("${line}┃")
        logging.append("${line}┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━请求参数拦截结束━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<<<\n")
        Log.i("request", logging.toString())
        next(request)
      }
    }
  }

  private object CCResponseInterceptor : FoldableResponseInterceptor {
    override fun invoke(next: ResponseTransformer): ResponseTransformer {
      return { request, response ->
        val logging = StringBuffer()
        logging.append("↓↓↓\n ")
        logging.append("${line}┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━响应结果拦截开始━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━>>>")
        logging.append("${line}┃")
        logging.append("${line}┣━━━   Method = ${request.method}")
        logging.append("${line}┃")
        logging.append("${line}┣━━━   headers = ${request.headers}")
        when (request.method) {
          Method.POST -> {
            if (!request.parameters.isNullOrEmpty()) {
              logging.append("${line}┃")
              logging.append("${line}┣━━━   request parameters:")
            }
            request.parameters.forEach {
              logging.append("${line}┣━━━      [${it.first}]=${it.second}")
            }
          }
          else -> {
          }
        }
        logging.append("${line}┃")
        logging.append(
          "${line}┣━━━   response = ${response.toString().trim()
            .replace(
              "\n",
              "${line}┃                     "
            )
          }"
        )
        logging.append("${line}┃")
        logging.append("${line}┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━响应结果拦截结束━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<<<\n")
        val size =
          logging.toString().length / lineCount + if (logging.toString().length % lineCount > 0) 1 else 0
        for (i in 0 until size) {
          val msg = logging.toString().substring(
            i * lineCount,
            Math.min((i + 1) * lineCount, logging.toString().length)
          ).trim()
          Log.i(
            "response",
            (if (i > 0 && !msg.startsWith("┃") && !msg.startsWith("┣") && !msg.startsWith("┗")) "${space}┃                     "
            else if (i > 0 && (msg.startsWith("┃") || msg.startsWith("┣") || msg.startsWith("┗"))) space
            else "") + msg

          )
        }
        next(request, response)
      }
    }
  }
}
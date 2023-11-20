package cc.abase.demo.rxhttp.interceptor

import cc.abase.demo.rxhttp.parser.isParsable
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.zip.InflaterInputStream

/**
 * @Description 解码deflate
 * @Author：Khaos
 * @Date：2023/11/20
 * @Time：10:28
 */
class DeflateInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request: Request = chain.request()
    val response = chain.proceed(request)
    val l1 = response.headers("Content-Encoding")
    val l2 = response.headers("content-encoding")
    val isDeflate = l1.any { a -> a.lowercase() == "deflate" } || l2.any { a -> a.lowercase() == "deflate" }
    return if (isDeflate) {
      val resBody = response.body
      if (resBody?.contentType()?.isParsable() == true) {
        val contentType = resBody.contentType()
        val result = InflaterInputStream(resBody.byteStream()).bufferedReader().use { it.readText() }
        response.newBuilder()
          .removeHeader("Content-Encoding")
          .removeHeader("content-encoding")
          .body(result.trim().toResponseBody(contentType))
          .build()
      } else {
        response
      }
    } else {
      response
    }
  }
}
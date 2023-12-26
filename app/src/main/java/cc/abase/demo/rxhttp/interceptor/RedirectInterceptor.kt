package cc.abase.demo.rxhttp.interceptor

import okhttp3.*

import java.io.IOException


/**
 * https://www.jianshu.com/p/61a60859a317
 *
 * okhttp重定向存在两个缺陷：
 * 1.okhttp处理301,302重定向时，会把请求方式设置为GET
 * 这样会丢失原来Post请求中的参数。
 *
 * 2.okhttp默认不支持跨协议的重定向，比如http重定向到https
 *
 * 为了解决这两个问题写了这个拦截器
 * Created by zhuguohui on 2017/11/9.
 */
internal class RedirectInterceptor : Interceptor {
  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val request: Request = chain.request()
    val beforeUrl: HttpUrl = request.url
    var response: Response = chain.proceed(request)
    val afterUrl: HttpUrl = response.request.url
    //1.根据url判断是否是重定向
    if (beforeUrl != afterUrl) {
      //处理两种情况 1、跨协议 2、原先不是GET请求。
      if (beforeUrl.scheme != afterUrl.scheme || request.method != "GET") {
        //重新请求
        val newRequest: Request = request.newBuilder().url(response.request.url).build()
        response = chain.proceed(newRequest)
      }
    }
    return response
  }
}
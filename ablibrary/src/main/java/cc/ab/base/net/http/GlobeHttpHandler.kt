package cc.ab.base.net.http

import okhttp3.*

/**
 * Description:全局网络请求的拦截.
 * @author: caiyoufei
 * @date: 2019/9/20 18:34
 */
interface GlobeHttpHandler {

  companion object {
    val EMPTY: GlobeHttpHandler = object : GlobeHttpHandler {
      override fun onHttpResultResponse(
        httpResult: String, chain: Interceptor.Chain,
        response: Response
      ): Response {
        return response
      }

      override fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request {
        return request
      }
    }
  }

  fun onHttpResultResponse(
    httpResult: String,
    chain: Interceptor.Chain,
    response: Response
  ): Response

  fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request

}
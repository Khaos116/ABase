package cc.abase.demo.rxhttp.interceptor

import com.blankj.utilcode.constant.TimeConstants
import okhttp3.*
import okhttp3.Interceptor.Chain

/** https://www.jianshu.com/p/dbda0bb8d541
 * @Description 图片网络缓存设置时长
 * @Author：CASE
 * @Date：2021/2/20
 * @Time：20:01
 */
class NetCacheInterceptor : Interceptor {
  override fun intercept(chain: Chain): Response {
    val request: Request = chain.request()
    val response = chain.proceed(request)
    val onlineCacheTime = 30L * TimeConstants.DAY / TimeConstants.SEC //在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
    return response.newBuilder()
        .header("Cache-Control", "public, max-age=$onlineCacheTime")
        .removeHeader("Pragma")
        .build()
  }
}
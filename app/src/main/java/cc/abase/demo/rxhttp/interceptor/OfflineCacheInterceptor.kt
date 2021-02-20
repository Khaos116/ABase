package cc.abase.demo.rxhttp.interceptor

import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.NetworkUtils
import okhttp3.*
import okhttp3.Interceptor.Chain

/** https://www.jianshu.com/p/dbda0bb8d541
 * @Description 图片访问缓存
 * @Author：CASE
 * @Date：2021/2/20
 * @Time：16:39
 */
class OfflineCacheInterceptor : Interceptor {
  override fun intercept(chain: Chain): Response {
    var request: Request = chain.request()
    if (!NetworkUtils.isConnected()) {
      val offlineCacheTime = 30L * TimeConstants.DAY / TimeConstants.SEC //离线的时候的缓存的过期时间(单位秒)
      request = request.newBuilder()
          //.cacheControl(CacheControl.Builder()
          //    .maxStale(60, TimeUnit.DAYS)
          //    .onlyIfCached()
          //    .build())//两种方式结果是一样的，写法不同
          .header("Cache-Control", "public, only-if-cached, max-stale=$offlineCacheTime")
          .build()
    }
    return chain.proceed(request)
  }
}
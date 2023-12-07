package cc.ab.base.config

import okhttp3.OkHttpClient
import rxhttp.wrapper.ssl.HttpsUtils
import java.util.concurrent.TimeUnit

/**
 * Author:Khaos116
 * Date:2023/7/18
 * Time:12:21
 */
object BaseConfig {
  private val 禁止代理 = false
  private val sslParams = HttpsUtils.getSslSocketFactory()
  fun getMyOkBuilder(seconds: Long, bySocket: Boolean, retry: Boolean): OkHttpClient.Builder {
    return OkHttpClient.Builder().apply {
      connectTimeout(seconds, TimeUnit.SECONDS)
      readTimeout(seconds, TimeUnit.SECONDS)
      writeTimeout(seconds, TimeUnit.SECONDS)
      if (bySocket) pingInterval(seconds, TimeUnit.SECONDS)
      if (retry) retryOnConnectionFailure(true)
      if (!禁止代理) {
        sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
        hostnameVerifier { _, _ -> true }
      }
    }
  }
}
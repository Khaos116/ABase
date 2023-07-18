package cc.ab.base.config

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Author:Khaos116
 * Date:2023/7/18
 * Time:12:21
 */
object BaseConfig {
  fun getMyOkBuilder(): OkHttpClient.Builder {
    return OkHttpClient.Builder()
      .connectTimeout(60, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(60, TimeUnit.SECONDS)
  }
}
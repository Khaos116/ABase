package cc.abase.demo.rxhttp.config

import cc.ab.base.config.PathConfig
import cc.ab.base.utils.CharlesUtils
import cc.abase.demo.config.HeaderManger
import cc.abase.demo.rxhttp.interceptor.TokenInterceptor
import com.ayvytr.okhttploginterceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.Param
import rxhttp.wrapper.ssl.HttpsUtils
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/4 15:13
 */
object RxHttpConfig {
  private var hasInit = false

  fun init() {
    if (hasInit) return
    hasInit = true
    initRxHttp()
  }

  //去除无意义的参数key，这里把header的共同参数剔除
  private val noCacheKeys = arrayOf(
    "Connection",
    "Accept",
    "Content-Type",
    "Charset",
    "request_time",
  )

  //初始化RxHttp https://github.com/liujingxing/rxhttp/wiki
  private fun initRxHttp() {
    val mode = CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE
    RxHttpPlugins.init(getDefaultOkHttpClient()) //自定义OkHttpClient对象
      .setDebug(false) //是否开启调试模式，开启后，logcat过滤RxHttp，即可看到整个请求流程日志
      .setCache(File(PathConfig.API_CACHE_DIR), 10 * 1024 * 1024L, mode, 1 * 60 * 60 * 1000L)  //配置缓存目录，最大size及缓存模式 (设置最大缓存为10M，缓存有效时长为1小时)
      .setExcludeCacheKeys(*noCacheKeys) //设置一些key，不参与cacheKey的组拼
      //.setResultDecoder(Function)//设置数据解密/解码器，非必须
      //.setConverter(IConverter)//设置全局的转换器，非必须
      .setOnParamAssembly { p: Param<*> -> //设置公共参数/请求头回调
        p.add("platform", "RxHttp")
        p.addAllHeader(HeaderManger.getStaticHeaders()) //添加公共参数
        //添加Token
        if (HeaderManger.noTokenUrls.filter { u ->
            (p.httpUrl).toString().contains(u, true)
          }.isNullOrEmpty()) {
          HeaderManger.getTokenPair()?.let { p.addHeader(it.first, it.second) }
        }
        p.add("request_time", System.currentTimeMillis()) //添加请求时间，方便更新token
        p
      }
  }

  //OkHttpClient
  private fun getDefaultOkHttpClient(): OkHttpClient {
    val builder = getOkHttpClient()
    builder.addInterceptor(TokenInterceptor())
    builder.addInterceptor(LoggingInterceptor(isShowAll = true))
    return builder.build()
  }

  //其他配置获取Okhttp对象
  fun getOkHttpClient(): Builder {
    val sslParams = HttpsUtils.getSslSocketFactory()
    val builder = Builder()
      //.cookieJar(CookieStore())//如果启用自动管理，则不需要在TokenInterceptor中进行保存和initRxHttp()进行读取
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
      .hostnameVerifier { _, _ -> true } //忽略host验证
    val util = CharlesUtils.getInstance()
    util.setOkHttpCharlesSSL(builder, util.getCharlesInputStream("charles.pem"))
    return builder
  }
}
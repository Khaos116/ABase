package cc.abase.demo.rxhttp.config

import cc.ab.base.utils.CharlesUtils
import cc.abase.demo.BuildConfig
import cc.abase.demo.config.HeaderManger
import com.blankj.utilcode.util.Utils
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.cookie.CookieStore
import rxhttp.wrapper.param.Param
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.ssl.SSLSocketFactoryImpl
import rxhttp.wrapper.ssl.X509TrustManagerImpl
import java.io.File
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/4 15:13
 */
class RxHttpConfig private constructor() {
  private object SingletonHolder {
    val holder = RxHttpConfig()
  }

  companion object {
    val instance =
      SingletonHolder.holder
  }

  private var hasInit = false

  fun init() {
    if (hasInit) return
    hasInit = true
    initRxHttp()
    initRxHttpCahce()
    /**
     * 去除无意义的参数key，这里把header的共同参数剔除
     * @see cc.abase.demo.config.HeaderManger.getStaticHeaders
     */
    deleteCacheParam(
        "Connection",
        "Accept",
        "Content-Type",
        "Charset"
    )
  }

  //初始化RxHttp https://github.com/liujingxing/okhttp-RxHttp/wiki/%E5%88%9D%E5%A7%8B%E5%8C%96
  private fun initRxHttp() {
    //设置debug模式，默认为false，设置为true后，发请求，过滤"RxHttp"能看到请求日志
    RxHttp.setDebug(BuildConfig.DEBUG)
    //非必须,只能初始化一次，第二次将抛出异常
    RxHttp.init(getDefaultOkHttpClient())
    //添加公共参数 https://github.com/liujingxing/okhttp-RxHttp/blob/486c7bc9e4554b4604f29c726e3e58714e2de6ee/app/src/main/java/com/example/httpsender/RxHttpManager.java
    RxHttp.setOnParamAssembly { p: Param<*> ->
      p.add("platform", "RxHttp")
      p.addAll(HeaderManger.instance.getStaticHeaders())//添加公共参数
      HeaderManger.instance.getTokenPair()
          ?.let { p.addHeader(it.first, it.second) /*添加公共请求头*/ }
      p
    }
  }

  //OkHttpClient
  private fun getDefaultOkHttpClient(): OkHttpClient {
    val trustAllCert: X509TrustManager = X509TrustManagerImpl()
    val sslSocketFactory: SSLSocketFactory = SSLSocketFactoryImpl(trustAllCert)
    val builder = Builder()
        .cookieJar(CookieStore())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .sslSocketFactory(sslSocketFactory, trustAllCert) //添加信任证书
        .hostnameVerifier(
            HostnameVerifier { hostname: String?, session: SSLSession? -> true }
        ) //忽略host验证
    val util = CharlesUtils.getInstance()
    util.setOkHttpCharlesSSL(builder, util.getCharlesInputStream("charles.pem"))
    return builder.build()
  }

  //缓存配置
  private fun initRxHttpCahce() {
    //设置缓存目录为：Android/data/{app包名目录}/cache/RxHttpCache
    val cacheDir = File(Utils.getApp().externalCacheDir, "RxHttpCache")
    //if (!cacheDir.exists()) cacheDir.mkdirs()
    //设置最大缓存为10M，缓存有效时长为1小时
    RxHttpPlugins.setCache(
        cacheDir, 10 * 1024 * 1024L, CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE,
        1 * 60 * 60 * 1000L
    )
  }

  /*
   * https://github.com/liujingxing/okhttp-RxHttp/wiki/%E7%BC%93%E5%AD%98%E7%AD%96%E7%95%A5
   * 缓存时需要生成由参数构成的key，如果参数中有当前时间的参数，则会导致缓存变得无意义，所以需要把这个会导致缓存无意义的参数剔除掉
   */
  private fun deleteCacheParam(vararg params: String) {
    RxHttpPlugins.setExcludeCacheKeys(*params)  //可变参数，可传入多个key
  }
}
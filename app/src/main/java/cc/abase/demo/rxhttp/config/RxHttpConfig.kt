package cc.abase.demo.rxhttp.config

import cc.ab.base.utils.CharlesUtils
import cc.abase.demo.BuildConfig
import cc.abase.demo.config.HeaderManger
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import rxhttp.wrapper.param.Param
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.ssl.SSLSocketFactoryImpl
import rxhttp.wrapper.ssl.X509TrustManagerImpl
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

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
  }

  //初始化RxHttp https://github.com/liujingxing/okhttp-RxHttp/wiki/%E5%88%9D%E5%A7%8B%E5%8C%96
  private fun initRxHttp() {
    //设置debug模式，默认为false，设置为true后，发请求，过滤"RxHttp"能看到请求日志
    RxHttp.setDebug(BuildConfig.DEBUG)
    //非必须,只能初始化一次，第二次将抛出异常
    RxHttp.init(getDefaultOkHttpClient())
    //添加公共参数 https://github.com/liujingxing/okhttp-RxHttp/blob/486c7bc9e4554b4604f29c726e3e58714e2de6ee/app/src/main/java/com/example/httpsender/RxHttpManager.java
    RxHttp.setOnParamAssembly { p: Param<*> ->
      p.addAll(HeaderManger.instance.getStaticHeaders())//添加公共参数
      HeaderManger.instance.getTokenPair()
          ?.let { p.addHeader(it.first, it.second) /*添加公共请求头*/ }
    }
  }

  //OkHttpClient
  private fun getDefaultOkHttpClient(): OkHttpClient {
    val trustAllCert: X509TrustManager = X509TrustManagerImpl()
    val sslSocketFactory: SSLSocketFactory = SSLSocketFactoryImpl(trustAllCert)
    val builder = Builder()
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
}
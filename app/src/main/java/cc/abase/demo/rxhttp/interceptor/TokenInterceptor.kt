package cc.abase.demo.rxhttp.interceptor

import cc.abase.demo.config.*
import cc.abase.demo.constants.ErrorCode
import cc.abase.demo.constants.api.WanUrls
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import okhttp3.*
import org.json.JSONObject
import rxhttp.RxHttp
import rxhttp.wrapper.parse.SimpleParser
import java.io.IOException
import java.nio.charset.Charset

/**
 * token 失效，自动刷新token，然后再次发送请求，用户无感知
 * Description: https://github.com/liujingxing/okhttp-RxHttp/blob/a7b311098e6c36bf5764db027cbb855b628b8d0a/app/src/main/java/com/example/httpsender/interceptor/TokenInterceptor.java
 * @author: CASE
 * @date: 2020/3/6 13:31
 */
class TokenInterceptor : Interceptor {
  //上次token刷新时间
  private var SESSION_KEY_REFRESH_TIME = 0L

  override fun intercept(chain: Interceptor.Chain): Response {
    //请求
    val request: Request = chain.request()
    //响应
    val originalResponse = chain.proceed(request)
    //Cookies
    val cookies = originalResponse.headers.toMultimap()
        .filter { it.key.contains("cookie", true) }
    //需要重新登录的判断
    originalResponse.body?.source()?.apply { request(Long.MAX_VALUE) }?.buffer?.let { buffer ->
      val jsonObject = JSONObject(buffer.clone().readString(Charset.forName("UTF-8")))
      //需要的登录
      if (jsonObject.has("errorCode") && jsonObject.optInt("errorCode") == ErrorCode.NO_LOGIN) {
        if (AppConfig.NEE_AUTO_LOGIN) {
          originalResponse.close()
          return handleTokenInvalid(chain, request)
        } else {
          GlobalErrorHandle.dealGlobalErrorCode(jsonObject.optInt("errorCode"))
        }
      }
    }
    //登录接口保存cookie
    if (originalResponse.isSuccessful && request.url.toString().contains(WanUrls.User.LOGIN, true)) {
      cookies.forEach { map ->
        for (value in map.value) {
          if (value.contains("JSESSIONID", true)) {
            UserManager.setToken(value, request.url.toString())
          }
        }
      }
    }
    return originalResponse
  }

  //处理token失效问题
  @Throws(IOException::class)
  private fun handleTokenInvalid(chain: Interceptor.Chain, request: Request): Response {
    val rxHttp1 = RxHttp.postForm(request.url.toString())
    val rxHttp2 = RxHttp.get(request.url.toString())
    val post = request.method.equals("POST", true)
    //2、根据自己的业务修改
    val body = request.body
    if (body is FormBody) {
      for (i in 0 until body.size) {
        if (post) rxHttp1.add(body.name(i), body.value(i))
        else rxHttp2.add(body.name(i), body.value(i))
      }
    }
    //同步刷新token
    //3、发请求前需要add("request_time",System.currentTimeMillis())
    //val requestTime = if (post) rxHttp1.queryValue("request_time")
    val requestTime = if (post) rxHttp1.param.bodyParam.first { p -> p.key == "request_time" }.value
    else rxHttp2.param.toString().split("&").first { it.contains("request_time") }.split("=")[1]
    val success: Boolean = refreshToken(requestTime)
    val newRequest: Request
    newRequest = if (success) { //刷新成功，重新签名
      //拿到最新的token,重新发起请求 4、根据自己的业务修改
      if (post) {
        rxHttp1.removeAllHeader("Cookie")
        rxHttp1.addHeader("Cookie", UserManager.getToken())
        rxHttp1.buildRequest()
      } else {
        rxHttp2.removeAllHeader("Cookie")
        rxHttp2.addHeader("Cookie", UserManager.getToken())
        rxHttp2.buildRequest()
      }
    } else {
      request
    }
    return chain.proceed(newRequest)
  }

  //刷新token
  private fun refreshToken(value: Any): Boolean {
    var requestTime: Long = 0
    try {
      requestTime = value.toString().toLong()
    } catch (ignore: Exception) {
      ignore.printStackTrace()
    }
    //请求时间小于token刷新时间，说明token已经刷新，则无需再次刷新
    if (requestTime <= SESSION_KEY_REFRESH_TIME) return true
    synchronized(this) {
      //再次判断是否已经刷新
      return if (requestTime <= SESSION_KEY_REFRESH_TIME) true else try {
        //获取到最新的token，这里需要同步请求token,千万不能异步  5、根据自己的业务修改
        RxHttp.postForm(WanUrls.User.LOGIN)
            .add("username", MMkvUtils.getAccount())
            .add("password", EncryptUtils.encryptMD5ToString(MMkvUtils.getPassword()))
            .execute(SimpleParser[String::class.java])
        LogUtils.e("CASE:自动登录登录刷新Token")
        SESSION_KEY_REFRESH_TIME = System.currentTimeMillis()
        true
      } catch (e: IOException) {
        false
      }
    }
  }
}
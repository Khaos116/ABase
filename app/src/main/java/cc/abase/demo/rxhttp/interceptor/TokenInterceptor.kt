package cc.abase.demo.rxhttp.interceptor

import cc.abase.demo.config.*
import cc.abase.demo.constants.ErrorCode
import cc.abase.demo.constants.api.WanUrls
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.EncryptUtils
import okhttp3.*
import okhttp3.internal.readBomAsCharset
import org.json.JSONObject
import rxhttp.RxHttp
import rxhttp.wrapper.parse.OkResponseParser

/**
 * token 失效，自动刷新token，然后再次发送请求，用户无感知
 * Description: https://github.com/liujingxing/okhttp-RxHttp/blob/a7b311098e6c36bf5764db027cbb855b628b8d0a/app/src/main/java/com/example/httpsender/interceptor/TokenInterceptor.java
 * @author: Khaos
 * @date: 2020/3/6 13:31
 */
class TokenInterceptor : Interceptor {
  //<editor-fold defaultstate="collapsed" desc="拦截判断是否需要自动登录">
  override fun intercept(chain: Interceptor.Chain): Response {
    //请求
    val request: Request = chain.request()
    //防止不需要token的请求走到下面去
    if (request.url.host != WanUrls.HOST) return chain.proceed(request)
    //响应
    val response = chain.proceed(request)
    //判断是否需要自动登录(变量需要+用户名不为空+密码不为空+请求成功)
    val needAutoLogin = if (AppConfig.NEE_AUTO_LOGIN) {
      val username = MMkvUtils.getAccount()
      val password = MMkvUtils.getPassword()
      username.isNotBlank() && password.isNotBlank() && response.isSuccessful
    } else {
      false
    }
    //确定需要自动登录才进行数据处理
    if (needAutoLogin) {
      //处理登录过期
      response.body?.let { body ->
        body.source().let { source ->
          source.request(Long.MAX_VALUE)
          val charset = source.readBomAsCharset(body.contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8)
          source.buffer.clone().readString(charset).let { json ->
            val jsonObject = try {
              JSONObject(json)
            } catch (e: Exception) {
              JSONObject("{}")
            }
            //判断Token过期
            if (jsonObject.has("errorCode") && jsonObject.optInt("errorCode") == ErrorCode.NO_LOGIN) {
              val username = MMkvUtils.getAccount()
              val password = MMkvUtils.getPassword()
              val suc = autoLogin(username, password)
              //如果自动登录成功，则需要更新旧请求的token，重新发起请求
              if (suc) return chain.proceed(request.newBuilder().also { b -> HeaderManger.getTokenPair()?.let { p -> b.header(p.first, p.second) } }.build())
            }
          }
        }
      }
    }
    //只有登录接口才保存Token
    if (response.isSuccessful && request.url.toString().contains(WanUrls.User.LOGIN, true)) saveToken(response)
    return response
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="自动登录刷新Token">
  private fun autoLogin(username: String, password: String): Boolean {
    //防止出现多个同时执行登录
    synchronized(this) {
      val response = RxHttp.postForm(WanUrls.User.LOGIN)
        .add("username", username)
        .add("password", EncryptUtils.encryptMD5ToString(password))
        .execute(OkResponseParser())
      return if (response.isSuccessful) {
        saveToken(response)
        true
      } else {
        false
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="保存Token">
  private fun saveToken(response: Response) {
    //登录接口保存cookie
    response.headers("Set-Cookie").firstOrNull { f -> f.contains("JSESSIONID") }?.let { token ->
      UserManager.setToken(token)
    }
  }
  //</editor-fold>
}
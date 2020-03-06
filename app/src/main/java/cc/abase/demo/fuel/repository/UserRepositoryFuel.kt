package cc.abase.demo.fuel.repository

import android.annotation.SuppressLint
import android.util.Log
import cc.ab.base.net.http.response.ApiException
import cc.ab.base.utils.RxUtils
import cc.abase.demo.bean.wan.IntegralBean
import cc.abase.demo.bean.wan.UserBean
import cc.abase.demo.constants.BaseUrl
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.fuel.repository.base.BaseRepository
import cc.abase.demo.fuel.repository.request.WanRequest
import cc.abase.demo.rxhttp.config.RxCookie
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.rx.rxString
import io.reactivex.Single

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/9 21:37
 */
class UserRepositoryFuel private constructor() : BaseRepository() {
  private object SingletonHolder {
    val holder = UserRepositoryFuel()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //注册
  fun register(
    username: String,
    password: String,
    repassword: String
  ): Single<Boolean> {
    //创建请求
    val request = WanUrls.User.REGISTER.httpPost(
        listOf(
            "username" to username,
            "password" to EncryptUtils.encryptMD5ToString(password),
            "repassword" to EncryptUtils.encryptMD5ToString(repassword)
        )
    )
    //执行请求
    return WanRequest.instance.startRequest<UserBean>(request)
        //保存登录数据
        .flatMap {
          it.data?.let { user ->
            setUid(user.id)
            this.user = user
          }
          if (it.errorCode == 0) {
            Single.just(it.data != null)
          } else {
            Single.error(ApiException(code = it.errorCode, msg = it.errorMsg))
          }
        }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }

  //登录
  fun login(
    username: String,
    password: String
  ): Single<Boolean> {
    //创建请求
    val request = WanUrls.User.LOGIN.httpPost(
        listOf(
            "username" to username,
            "password" to EncryptUtils.encryptMD5ToString(password)
        )
    )
    //执行请求
    return WanRequest.instance.startRequest<UserBean>(request)
        //保存登录数据
        .flatMap {
          it.data?.let { user ->
            setUid(user.id)
            MMkvUtils.instance.setAccount(username)
            MMkvUtils.instance.setPassword(password)
            this.user = user
          }
          if (it.errorCode == 0) {
            Single.just(it.data != null)
          } else {
            Single.error(ApiException(code = it.errorCode, msg = it.errorMsg))
          }
        }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }

  //登出
  @SuppressLint("CheckResult")
  fun logOut() {
    clearUserInfo()
    WanUrls.User.LOGOUT.httpGet()
        .rxString()
        .map { LogUtils.e("CASE:退出成功:${it.component2() == null}") }
        .subscribe({}, {})
  }

  //我的积分
  fun myIntegral(): Single<IntegralBean> {
    val request = WanUrls.User.INTEGRAL.httpGet()
    return WanRequest.instance.startRequest<IntegralBean>(request)
        .flatMap { justRespons(it) }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }

  //======================用户登录相关信息======================//
  private var user: UserBean? = null
  private var uid: Long = 0
  private var token: String? = null
  fun isLogin(): Boolean {
    if (uid == 0L) getUid()
    if (token.isNullOrBlank()) getToken()
    return uid > 0 && !token.isNullOrBlank()
  }

  fun getUid(): Long {
    if (uid == 0L) uid = MMkvUtils.instance.getUid()
    return uid
  }

  fun getToken(): String? {
    if (token.isNullOrBlank()) token = MMkvUtils.instance.getToken()
    return token
  }

  fun getUser(): UserBean? {
    return user
  }

  fun clearUserInfo() {
    uid = 0
    token = null
    user = null
    MMkvUtils.instance.clearUserInfo()
  }

  private fun setUid(uid: Long) {
    this.uid = uid
    MMkvUtils.instance.setUid(uid)
  }

  internal fun setToken(
    token: String,
    url: String = BaseUrl.gankUrl
  ) {
    if (token.isNotBlank() && token != this.token) {
      LogUtils.e("CASE:更新Token为:${token}")
      this.token = token
      MMkvUtils.instance.setToken(token)
      RxCookie.instance.setCookie(token, url)
    }
  }
}

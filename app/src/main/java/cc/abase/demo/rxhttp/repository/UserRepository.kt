package cc.abase.demo.rxhttp.repository

import android.annotation.SuppressLint
import cc.ab.base.utils.RxUtils
import cc.abase.demo.bean.wan.IntegralBean
import cc.abase.demo.bean.wan.UserBean
import cc.abase.demo.constants.BaseUrl
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.rxhttp.config.RxCookie
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.RxHttp

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/4 16:03
 */
object UserRepository {
  //注册
  fun register(
      username: String,
      password: String,
      repassword: String
  ): Observable<UserBean> {
    return RxHttp.postForm(WanUrls.User.REGISTER)
        .setDomainToWanIfAbsent()
        .add("username", username)
        .add("password", EncryptUtils.encryptMD5ToString(password))
        .add("repassword", EncryptUtils.encryptMD5ToString(repassword))
        .setCacheMode(CacheMode.ONLY_NETWORK) //不使用缓存
        .asResponseWan(UserBean::class.java)
        .map {
          setUid(it.id)
          this.user = it
          it
        }
        .compose(RxUtils.rx2SchedulerHelperODelay())
  }

  //登录
  fun login(
      username: String,
      password: String
  ): Observable<UserBean> {
    return RxHttp.postForm(WanUrls.User.LOGIN)
        .setDomainToWanIfAbsent()
        .add("username", username)
        .add("password", EncryptUtils.encryptMD5ToString(password))
        .setAssemblyEnabled(true) //添加公共参数/头部
        .setCacheMode(CacheMode.ONLY_NETWORK) //不使用缓存
        .asResponseWan(UserBean::class.java)
        .observeOn(AndroidSchedulers.mainThread()) //指定在主线程回调
        .map {
          setUid(it.id)
          MMkvUtils.setAccount(username)
          MMkvUtils.setPassword(password)
          this.user = it
          /**
           * 采用自动管理Cookie的方式可以采用下面的方式保存Token
           * @see cc.abase.demo.rxhttp.config.RxHttpConfig.getDefaultOkHttpClient
           */
          //RxCookie.getCookie()?.forEach { cookie ->
          //    if (cookie.toString().contains("SESSIONID")) setToken(cookie.toString())
          //}
          it
        }
        .compose(RxUtils.rx2SchedulerHelperODelay())
  }

  //登出
  @SuppressLint("CheckResult")
  fun logOut() {
    RxHttp.get(WanUrls.User.LOGOUT)
        .setCacheMode(CacheMode.ONLY_NETWORK) //不使用缓存
        .setDomainToWanIfAbsent()
        .asString()
        .map { LogUtils.e("退出成功:$it") }
        .subscribe({}, { LogUtils.e("退出失败:$it") })
  }

  //我的积分
  fun myIntegral(): Observable<IntegralBean> {
    return RxHttp.get(WanUrls.User.INTEGRAL)
        .setDomainToWanIfAbsent()
        .asResponseWan(IntegralBean::class.java)
        .compose(RxUtils.rx2SchedulerHelperODelay())
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
    if (uid == 0L) uid = MMkvUtils.getUid()
    return uid
  }

  fun getToken(): String? {
    if (token.isNullOrBlank()) token = MMkvUtils.getToken()
    return token
  }

  fun getUser(): UserBean? {
    return user
  }

  fun clearUserInfo() {
    uid = 0
    token = null
    user = null
    MMkvUtils.clearUserInfo()
  }

  private fun setUid(uid: Long) {
    this.uid = uid
    MMkvUtils.setUid(uid)
  }

  fun setToken(
      token: String,
      url: String = BaseUrl.gankUrl
  ) {
    if (token.isNotBlank() && token != this.token) {
      LogUtils.e("CASE:更新Token为:${token}")
      this.token = token
      MMkvUtils.setToken(token)
      RxCookie.setCookie(token, url)
    }
  }
}
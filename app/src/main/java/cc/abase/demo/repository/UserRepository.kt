package cc.abase.demo.repository

import android.annotation.SuppressLint
import android.util.Log
import cc.ab.base.net.http.response.ApiException
import cc.ab.base.utils.RxUtils
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.repository.base.BaseRepository
import cc.abase.demo.repository.bean.wan.IntegralBean
import cc.abase.demo.repository.bean.wan.UserBean
import cc.abase.demo.repository.request.WanRequest
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.EncryptUtils
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.rx.rxString
import io.reactivex.Single

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/9 21:37
 */
class UserRepository private constructor() : BaseRepository() {
  private object SingletonHolder {
    val holder = UserRepository()
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
        .map { Log.e("CASE", "退出成功:${it.component2() == null}") }
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

  internal fun setToken(token: String) {
    if (token.isNotBlank() && token != this.token) {
      Log.e("CASE", "更新Token为:${token}")
      this.token = token
      MMkvUtils.instance.setToken(token)
    }
  }
}

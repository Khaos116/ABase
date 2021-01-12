package cc.abase.demo.rxhttp.repository

import android.annotation.SuppressLint
import cc.ab.base.utils.RxUtils
import cc.abase.demo.bean.wan.IntegralBean
import cc.abase.demo.bean.wan.UserBean
import cc.abase.demo.config.UserManager
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import rxhttp.RxHttp
import rxhttp.wrapper.cahce.CacheMode

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/4 16:03
 */
object UserRepository {
  //注册
  fun register(username: String, password: String, repassword: String): Observable<UserBean> {
    return RxHttp.postForm(WanUrls.User.REGISTER)
        .add("username", username)
        .add("password", EncryptUtils.encryptMD5ToString(password))
        .add("repassword", EncryptUtils.encryptMD5ToString(repassword))
        .setCacheMode(CacheMode.ONLY_NETWORK) //不使用缓存
        .asResponseWan(UserBean::class.java)
        .map {
          UserManager.setUid(it.id)
          MMkvUtils.setAccount(username)
          MMkvUtils.setPassword(password)
          it
        }
        .compose(RxUtils.rx2SchedulerHelperODelay())
  }

  //登录
  fun login(username: String, password: String): Observable<UserBean> {
    return RxHttp.postForm(WanUrls.User.LOGIN)
        .add("username", username)
        .add("password", EncryptUtils.encryptMD5ToString(password))
        .setAssemblyEnabled(true) //添加公共参数/头部
        .setCacheMode(CacheMode.ONLY_NETWORK) //不使用缓存
        .asResponseWan(UserBean::class.java)
        .observeOn(AndroidSchedulers.mainThread()) //指定在主线程回调
        .map {
          UserManager.setUid(it.id)
          MMkvUtils.setAccount(username)
          MMkvUtils.setPassword(password)
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
        .asString()
        .map { LogUtils.e("退出成功:$it") }
        .subscribe({}, { LogUtils.e("退出失败:$it") })
  }

  //我的积分
  fun myIntegral(): Observable<IntegralBean> {
    return RxHttp.get(WanUrls.User.INTEGRAL)
        .asResponseWan(IntegralBean::class.java)
        .compose(RxUtils.rx2SchedulerHelperODelay())
  }
}
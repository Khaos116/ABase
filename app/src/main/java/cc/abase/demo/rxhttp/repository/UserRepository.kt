package cc.abase.demo.rxhttp.repository

import android.annotation.SuppressLint
import cc.ab.base.ext.logI
import cc.abase.demo.bean.wan.IntegralBean
import cc.abase.demo.bean.wan.UserBean
import cc.abase.demo.config.UserManager
import cc.abase.demo.constants.api.WanUrls
import cc.abase.demo.utils.BuglyManager
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.EncryptUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import rxhttp.*
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.coroutines.Await

/**
 * Description:
 * @author: Khaos
 * @date: 2020/3/4 16:03
 */
object UserRepository {
  //注册
  suspend fun register(username: String, password: String, repassword: String): Await<UserBean> {
    return RxHttp.postForm(WanUrls.User.REGISTER)
      .add("username", username)
      .add("password", EncryptUtils.encryptMD5ToString(password))
      .add("repassword", EncryptUtils.encryptMD5ToString(repassword))
      .setCacheMode(CacheMode.ONLY_NETWORK) //不使用缓存
      .toResponseWan<UserBean>()
      .map { u ->
        UserManager.setUid(u.id)
        MMkvUtils.setAccount(username)
        MMkvUtils.setPassword(password)
        BuglyManager.updateUserInfo()
        u
      }
  }

  //登录
  suspend fun login(username: String, password: String): Await<UserBean> {
    return RxHttp.postForm(WanUrls.User.LOGIN)
      .add("username", username)
      .add("password", EncryptUtils.encryptMD5ToString(password))
      .setAssemblyEnabled(true) //添加公共参数/头部
      .setCacheMode(CacheMode.ONLY_NETWORK) //不使用缓存
      .toResponseWan<UserBean>()
      .map {
        UserManager.setUid(it.id)
        MMkvUtils.setAccount(username)
        MMkvUtils.setPassword(password)
        BuglyManager.updateUserInfo()
        /**
         * 采用自动管理Cookie的方式可以采用下面的方式保存Token
         * @see cc.abase.demo.rxhttp.config.RxHttpConfig.getDefaultOkHttpClient
         */
        //RxCookie.getCookie()?.forEach { cookie ->
        //    if (cookie.toString().contains("SESSIONID")) setToken(cookie.toString())
        //}
        it
      }
  }

  //我的积分
  suspend fun myIntegral(): Await<IntegralBean> {
    return RxHttp.get(WanUrls.User.INTEGRAL)
      .toResponseWan<IntegralBean>()
  }

  //登出
  @SuppressLint("CheckResult")
  fun logOut() {
    RxHttp.get(WanUrls.User.LOGOUT)
      .setCacheMode(CacheMode.ONLY_NETWORK) //不使用缓存
      .asString()
      .observeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread()) //指定在主线程回调
      .subscribe({ "退出成功:$it".logI() }, { "退出失败:$it".logI() }, {})
  }
}
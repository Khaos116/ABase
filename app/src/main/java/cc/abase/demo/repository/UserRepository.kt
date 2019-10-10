package cc.abase.demo.repository

import cc.abase.demo.constants.WanAndroidUrls
import cc.abase.demo.repository.request.WanUserRequest
import com.blankj.utilcode.util.EncryptUtils
import com.github.kittinunf.fuel.httpPost
import io.reactivex.Single

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/9 21:37
 */
class UserRepository private constructor() {
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
  ): Single<String> {
    val request = WanAndroidUrls.User.REGISTER.httpPost(
      listOf(
        "username" to username,
        "password" to EncryptUtils.encryptMD5ToString(password),
        "repassword" to EncryptUtils.encryptMD5ToString(repassword)
      )
    )
    return WanUserRequest.instance.register(request)
  }
}

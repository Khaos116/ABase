package cc.abase.demo.config

import cc.ab.base.ext.toast
import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.repository.UserRepository
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.github.kittinunf.fuel.core.FoldableResponseInterceptor
import com.github.kittinunf.fuel.core.ResponseTransformer
import com.google.gson.reflect.TypeToken

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/12 23:19
 */
class ResponseManager private constructor() {
  private object SingletonHolder {
    val holder = ResponseManager()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //响应拦截
  fun fuelResponse(): FoldableResponseInterceptor {
    return object : FoldableResponseInterceptor {
      override fun invoke(next: ResponseTransformer): ResponseTransformer {
        return { request, response ->
          var str = response.toString()
          if (str.isNotEmpty()) {
            //判断未登录
            if (str.contains("-1001")) {
              val index = str.indexOf("Body")
              val start = str.indexOf("{", index)
              val end = str.indexOf("}", index)
              str = str.substring(start, end + 1)
              try {
                val result = GsonUtils.fromJson<BaseResponse<String>>(
                    str, object : TypeToken<BaseResponse<String>>() {}.type
                )
                if (result.errorCode == -1001) {
                  ActivityUtils.getTopActivity()
                      ?.let { ac ->
                        ac.runOnUiThread {
                          ac.toast(result.errorMsg)
                          LoginActivity.startActivity(ac)
                        }
                      }
                }
              } catch (e: Exception) {
                e.printStackTrace()
              }
            } else if (response.headers.containsKey("Set-Cookie")) {
              //更新cookie
              val cookie = response.header("Set-Cookie").toMutableList()[0].split(";")[0]
              UserRepository.instance.setToken(cookie)
            }
          }
          next(request, response)
        }
      }
    }
  }
}
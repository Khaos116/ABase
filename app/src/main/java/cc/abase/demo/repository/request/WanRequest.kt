package cc.abase.demo.repository.request

import cc.ab.base.ext.toast
import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.R
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.constants.ErrorCode
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.rx.rxString
import com.github.kittinunf.result.Result
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import java.lang.reflect.Type

/**
 * Description:统一执行请求，并处理非业务的code和需要全局处理的code
 * @author: caiyoufei
 * @date: 2019/10/9 21:38
 */
class WanRequest private constructor() : BaseRequest() {
  private object SingletonHolder {
    val holder = WanRequest()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //需要全局处理的ErrorCode
  private val globalErrorCode = listOf(
      ErrorCode.NO_LOGIN//未登录
  )

  //执行请求
  internal fun <T> startRequest(
    request: Request,
    type: TypeToken<BaseResponse<T>>
  ):
      Single<BaseResponse<T>> {
    return request.rxString()
        .flatMap { flatMapSingle(it, type) }
  }

  //区分成功和失败
  private fun <T> flatMapSingle(
    result: Result<String, FuelError>,
    type: TypeToken<BaseResponse<T>>
  ): Single<BaseResponse<T>> {
    return if (result.component2() == null) {
      Single.just(converWanData(result.component1(), type.type))
    } else {
      Single.error(converFuelError(result.component2()))
    }
  }

  //数据转换(可能抛出异常)
  @Throws
  private fun <T> converWanData(
    response: String?,
    type: Type
  ): BaseResponse<T> {
    if (response.isNullOrBlank()) throw converDataError()
    try {
      val result: BaseResponse<T> = GsonUtils.fromJson(response, type)
      if (globalErrorCode.contains(result.errorCode)) {
        result.errorMsg = null
        dealGlobalErrorCode(result.errorCode)
      }
      return result
    } catch (e: Exception) {
      e.printStackTrace()
    }
    throw converDataError()
  }

  //统一处理
  private fun dealGlobalErrorCode(errorCode: Int) {
    val activity = ActivityUtils.getTopActivity()
    activity?.let { ac ->
      when (errorCode) {
        //未登录
        ErrorCode.NO_LOGIN -> {
          ac.runOnUiThread {
            ac.toast(R.string.need_login)
            LoginActivity.startActivity(ac)
          }
        }
        else -> {
        }
      }
    }
  }
}
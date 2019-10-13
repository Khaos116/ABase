package cc.abase.demo.repository.request

import cc.ab.base.ext.toast
import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.R
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.constants.ErrorCode
import cc.abase.demo.repository.base.BaseRequest
import cc.abase.demo.repository.cache.CacheRepository
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.rx.rxString
import com.github.kittinunf.result.Result
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictProvider
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

  //请求数据，如果有缓存则返回缓存，没有则进行请求
  internal fun <T> startRequestWithCache(
    request: Request,
    page: Int = 1,
    size: Int = 20,
    type: TypeToken<BaseResponse<T>>
  ): Single<BaseResponse<T>> {
    return CacheRepository.instance.getCacheData(
        request.rxString(),//请求结果以string返回
        DynamicKey("url=${request.url}page=${page},size=${size}"),//缓存相关的key
        update = EvictProvider(false)//false不强制清除缓存,true强制清除缓存
    )
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
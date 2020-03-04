package cc.abase.demo.fuel.repository.request

import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.config.GlobalErrorHandle
import cc.abase.demo.fuel.repository.base.BaseRequest
import cc.abase.demo.fuel.repository.cache.CacheRepository
import cc.abase.demo.fuel.repository.parse.BaseParameterizedTypeImpl
import com.blankj.utilcode.util.GsonUtils
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.rx.rxString
import com.github.kittinunf.result.Result
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

  //执行请求
  internal inline fun <reified T> startRequest(request: Request):
      Single<BaseResponse<T>> {
    return request.rxString()
        .flatMap { flatMapSingle<T>(it) }
  }

  internal inline fun <reified T> startRequestList(request: Request):
      Single<BaseResponse<MutableList<T>>> {
    return request.rxString()
        .flatMap { flatMapSingleList<T>(it) }
  }

  //请求数据，如果有缓存则返回缓存，没有则进行请求
  internal inline fun <reified T> startRequestWithCache(
    request: Request,
    page: Int = 1,
    size: Int = 20
  ): Single<BaseResponse<T>> {
    return CacheRepository.instance.getCacheData(
        request.rxString(),//请求结果以string返回
        DynamicKey("url=${request.url}page=${page},size=${size}"),//缓存相关的key
        update = EvictProvider(false)//false不强制清除缓存,true强制清除缓存
    )
        .flatMap { flatMapSingle<T>(it) }
  }

  internal inline fun <reified T> startRequestWithCacheList(
    request: Request,
    page: Int = 1,
    size: Int = 20
  ): Single<BaseResponse<MutableList<T>>> {
    return CacheRepository.instance.getCacheData(
        request.rxString(),//请求结果以string返回
        DynamicKey("url=${request.url}page=${page},size=${size}"),//缓存相关的key
        update = EvictProvider(false)//false不强制清除缓存,true强制清除缓存
    )
        .flatMap { flatMapSingleList<T>(it) }
  }

  //区分成功和失败
  private inline fun <reified T> flatMapSingle(
    result: Result<String, FuelError>
  ): Single<BaseResponse<T>> {
    return if (result.component2() == null) {
      Single.just(
          converWanData(result.component1(), BaseParameterizedTypeImpl.typeOne(T::class.java))
      )
    } else {
      Single.error(converFuelError(result.component2()))
    }
  }

  private inline fun <reified T> flatMapSingleList(
    result: Result<String, FuelError>
  ): Single<BaseResponse<MutableList<T>>> {
    return if (result.component2() == null) {
      Single.just(
          converWanData(result.component1(), BaseParameterizedTypeImpl.typeList(T::class.java))
      )
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
      if (GlobalErrorHandle.instance.globalErrorCodes.contains(result.errorCode)) {
        result.errorMsg = null
        GlobalErrorHandle.instance.dealGlobalErrorCode(result.errorCode)
      }
      return result
    } catch (e: Exception) {
      e.printStackTrace()
    }
    throw converDataError()
  }
}
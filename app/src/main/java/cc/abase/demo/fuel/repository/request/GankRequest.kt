package cc.abase.demo.fuel.repository.request

import cc.ab.base.net.http.response.GankResponse
import cc.abase.demo.fuel.repository.base.BaseRequest
import cc.abase.demo.fuel.repository.cache.CacheRepository
import cc.abase.demo.fuel.repository.parse.GankParameterizedTypeImpl
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
 * Description:Gank的网络请求，通过Repository调用
 * Repository-->Request-->CacheRepository
 * @author: caiyoufei
 * @date: 2019/10/8 11:32
 */
internal class GankRequest private constructor() : BaseRequest() {
  private object SingletonHolder {
    val holder = GankRequest()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //直接请求数据，不走缓存-->结果非列表
  internal inline fun <reified T> startRequest(request: Request): Single<GankResponse<T>> {
    return request.rxString().flatMap { flatMapSingle<T>(it) }
  }

  //直接请求数据，不走缓存-->结果列表
  internal inline fun <reified T> startRequestList(request: Request): Single<GankResponse<MutableList<T>>> {
    return request.rxString().flatMap { flatMapSingleList<T>(it) }
  }

  //请求数据，如果有缓存则返回缓存，没有则进行请求-->结果非列表
  internal inline fun <reified T> startRequestWithCache(
      request: Request, page: Int = 1, size: Int = 20): Single<GankResponse<T>> {
    return CacheRepository.instance.getCacheData(request.rxString(),//请求结果以string返回
        DynamicKey("url=${request.url},page=${page},size=${size}"),//缓存相关的key
        update = EvictProvider(false))//false不强制清除缓存,true强制清除缓存
      .flatMap { flatMapSingle<T>(it) }
  }

  //请求数据，如果有缓存则返回缓存，没有则进行请求-->结果列表
  internal inline fun <reified T> startRequestWithCacheList(
      request: Request, page: Int = 1, size: Int = 20): Single<GankResponse<MutableList<T>>> {
    return CacheRepository.instance.getCacheData(
        request.rxString(),//请求结果以string返回
        DynamicKey("url=${request.url},page=${page},size=${size}"),//缓存相关的key
        update = EvictProvider(false))//false不强制清除缓存,true强制清除缓存
      .flatMap { flatMapSingleList<T>(it) }
  }

  //======================================下面是统一处理======================================//
  //成功和失败的处理,非列表
  private inline fun <reified T> flatMapSingle(result: Result<String, FuelError>): Single<GankResponse<T>> {
    return if (result.component2() == null) {
      Single.just(converGankData(result.component1(), GankParameterizedTypeImpl.typeOne(T::class.java)))
    } else {
      Single.error(converFuelError(result.component2()))
    }
  }

  //成功和失败的处理，列表
  private inline fun <reified T> flatMapSingleList(result: Result<String, FuelError>): Single<GankResponse<MutableList<T>>> {
    return if (result.component2() == null) {
      Single.just(converGankData(result.component1(), GankParameterizedTypeImpl.typeList(T::class.java)))
    } else {
      Single.error(converFuelError(result.component2()))
    }
  }

  //数据转换，可能抛出异常
  @Throws
  private fun <T> converGankData(response: String?, type: Type): GankResponse<T> {
    if (response.isNullOrBlank()) throw converDataError()
    try {
      return GsonUtils.fromJson(response, type)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    throw converDataError()
  }
}
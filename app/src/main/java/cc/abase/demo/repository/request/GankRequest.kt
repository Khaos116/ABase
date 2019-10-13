package cc.abase.demo.repository.request

import cc.ab.base.utils.RxUtils
import cc.abase.demo.repository.CacheRepository
import cc.abase.demo.repository.bean.gank.GankResponse
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

  //直接请求数据，不走缓存
  internal fun <T> startRequest(
    request: Request,
    type: TypeToken<GankResponse<T>>
  ): Single<GankResponse<T>> {
    return request.rxString()
        .flatMap { flatMapSingle(it, type) }
  }

  //请求数据，如果有缓存则返回缓存，没有则进行请求
  internal fun <T> startRequestWithCache(
    request: Request,
    page: Int = 1,
    size: Int = 20,
    type: TypeToken<GankResponse<T>>
  ): Single<GankResponse<T>> {
    return CacheRepository.instance.androidList(
        request.rxString(),//请求结果以string返回
        DynamicKey("page=${page},size=${size}"),//缓存相关的key
        update = EvictProvider(false)//false不强制清除缓存,true强制清除缓存
    )
        .flatMap { flatMapSingle(it, type) }
  }

  //======================================下面是统一处理======================================//
  //成功和失败的处理
  private fun <T> flatMapSingle(
    result: Result<String, FuelError>,
    type: TypeToken<GankResponse<T>>
  ): Single<GankResponse<T>> {
    return if (result.component2() == null) {
      Single.just(converGankData(result.component1(), type.type))
    } else {
      Single.error(converFuelError(result.component2()))
    }
  }

  //数据转换，可能抛出异常
  @Throws
  private fun <T> converGankData(
    response: String?,
    type: Type
  ): GankResponse<T> {
    if (response.isNullOrBlank()) throw converDataError()
    try {
      return GsonUtils.fromJson(response, type)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    throw converDataError()
  }
}
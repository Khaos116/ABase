package cc.abase.demo.repository.request

import cc.ab.base.utils.RxUtils
import cc.abase.demo.repository.CacheRepository
import cc.abase.demo.repository.bean.gank.GankAndroidBean
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

/**
 * Description:Gank的网络请求，通过Repository调用
 * Repository-->Request-->CacheRepository
 * @author: caiyoufei
 * @date: 2019/10/8 11:32
 */
internal class GankRequest private constructor() {
  private object SingletonHolder {
    val holder = GankRequest()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //直接请求数据，不走缓存
  internal fun requestGank(request: Request): Single<MutableList<GankAndroidBean>> {
    return request.rxString()
        .flatMap { flatMapSingle(it) }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }

  //请求数据，如果有缓存则返回缓存，没有则进行请求
  internal fun requestGankByCache(
    request: Request,
    page: Int = 1,
    size: Int = 20
  ): Single<MutableList<GankAndroidBean>> {
    return CacheRepository.instance.androidList(
        request.rxString(),//请求结果以string返回
        DynamicKey("page=${page},size=${size}"),//缓存相关的key
        update = EvictProvider(false)//false不强制清除缓存,true强制清除缓存
    )
        .flatMap { flatMapSingle(it) }
        .compose(
            if (page <= 1) {//由于第一次加载的时候是loading，所以不能让接口请求的太快
              RxUtils.instance.rx2SchedulerHelperSDelay()
            } else {
              RxUtils.instance.rx2SchedulerHelperS()
            }
        )
  }

  //======================================下面是统一处理======================================//
  //成功和失败的处理
  private fun flatMapSingle(result: Result<String, FuelError>): Single<MutableList<GankAndroidBean>> {
    return if (result.component2() == null) {
      Single.just(converGankData(result.component1()))
    } else {
      Single.error(result.component2()?.exception ?: Throwable("null"))
    }
  }

  //数据转换，可能抛出异常
  @Throws
  private fun converGankData(response: String?): MutableList<GankAndroidBean> {
    if (response.isNullOrBlank()) throw Throwable("response is null or empty")
    val result: GankResponse<MutableList<GankAndroidBean>> = GsonUtils.fromJson(
        response, object : TypeToken<GankResponse<MutableList<GankAndroidBean>>>() {
    }.type
    )
    return result.results ?: mutableListOf()
  }
}
package cc.abase.demo.fuel.repository.cache

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import io.reactivex.Single
import io.rx_cache2.*
import java.util.concurrent.TimeUnit

/**
 * Description:缓存相关api，非缓存直接使用request，调用时统一使用Repository调用
 * @author: caiyoufei
 * @date: 2019/10/8 11:23
 */
interface CacheApi {
  @ProviderKey("gankAndroidList")
  @LifeCache(duration = 1, timeUnit = TimeUnit.HOURS)
  fun requestWithCache(
    single: Single<Result<String, FuelError>>,
    pageSize: DynamicKey? = null,
    update: EvictProvider
  ): Single<Result<String, FuelError>>
}
package cc.abase.demo.fuel.repository.cache

import cc.abase.demo.fuel.repository.base.BaseCacheRepository
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import io.reactivex.Single
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictProvider

/**
 * Description:不直接访问本类，通过对应的非缓存类进行访问
 * Repository-->Request-->CacheRepository
 * @author: caiyoufei
 * @date: 2019/10/8 15:48
 */
internal class CacheRepository private constructor() : BaseCacheRepository<CacheApi>(
    CacheApi::class.java
) {

  private object SingletonHolder {
    val holder = CacheRepository()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //获取缓存数据，如果不存在，则进行请求
  internal fun getCacheData(
    single: Single<Result<String, FuelError>>,
    pageSizeUrl: DynamicKey,
    update: EvictProvider
  ): Single<Result<String, FuelError>> {
    return cacheApi.requestWithCache(single, pageSizeUrl, update)
  }
}
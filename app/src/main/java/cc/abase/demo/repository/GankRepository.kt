package cc.abase.demo.repository

import androidx.annotation.IntRange
import cc.ab.base.utils.RxUtils
import cc.abase.demo.constants.GankUrls
import cc.abase.demo.repository.base.BaseRepository
import cc.abase.demo.repository.bean.gank.GankAndroidBean
import cc.abase.demo.repository.request.GankRequest
import com.github.kittinunf.fuel.httpGet
import io.reactivex.Single

/**
 * Description:通过本类调用接口
 * Repository-->Request-->CacheRepository
 * @author: caiyoufei
 * @date: 2019/10/8 17:58
 */
class GankRepository private constructor(): BaseRepository() {
  private object SingletonHolder {
    val holder = GankRepository()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //获取安卓信息列表,返回Single<MutableList<...>>需要访问Request...List
  fun androidList(
    @IntRange(from = 1) page: Int, size: Int,
    readCache: Boolean = true
  ): Single<MutableList<GankAndroidBean>> {
    val request = String.format(GankUrls.ANDROID, size, page)
        .httpGet()
    return if (readCache) {
      GankRequest.instance.startRequestWithCacheList(request, page = page, size = size)
    } else {
      GankRequest.instance.startRequestList<GankAndroidBean>(request)
    }.flatMap { justRespons(it) }
        .compose(
            if (page <= 1) {//由于第一次加载的时候是loading，所以不能让接口请求的太快
              RxUtils.instance.rx2SchedulerHelperSDelay()
            } else {
              RxUtils.instance.rx2SchedulerHelperS()
            }
        )
  }
}
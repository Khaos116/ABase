package cc.abase.demo.rxhttp.repository

import androidx.annotation.IntRange
import cc.ab.base.utils.RxUtils
import cc.abase.demo.bean.gank.GankAndroidBean
import cc.abase.demo.constants.GankUrls
import cc.abase.demo.constants.TimeConstants
import io.reactivex.Observable
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/5 18:09
 */
class GankRepository private constructor() {
  private object SingletonHolder {
    val holder = GankRepository()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //安卓列表
  fun androidList(@IntRange(from = 1) page: Int, size: Int, readCache: Boolean = true): Observable<MutableList<GankAndroidBean>> {
    return RxHttp.get(String.format(GankUrls.ANDROID, page, size))
      .setDomainToGankIfAbsent()
      .setCacheValidTime(TimeConstants.DYN_CACHE)//设置缓存时长
      .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK
      else CacheMode.ONLY_NETWORK)//先读取缓存，失败再请求数据
      .asResponseGankList(GankAndroidBean::class.java)
      .compose(RxUtils.instance.rx2SchedulerHelperODelay())
  }
}
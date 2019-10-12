package cc.abase.demo.repository

import androidx.annotation.IntRange
import cc.abase.demo.constants.GankUrls
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

  //获取安卓信息列表
  fun androidList(
    @IntRange(from = 1) page: Int, size: Int,
    byCache: Boolean = true
  ): Single<MutableList<GankAndroidBean>> {
    val request = String.format(GankUrls.ANDROID, size, page)
        .httpGet()
    return if (byCache) {
      GankRequest.instance.requestGankByCache(request, page = page, size = size)
    } else {
      GankRequest.instance.requestGank(request)
    }
  }
}
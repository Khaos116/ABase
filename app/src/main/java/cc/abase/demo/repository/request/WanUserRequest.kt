package cc.abase.demo.repository.request

import android.util.Log
import cc.ab.base.utils.RxUtils
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.rx.rxString
import com.github.kittinunf.result.Result
import io.reactivex.Single

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/9 21:38
 */
class WanUserRequest private constructor() {
  private object SingletonHolder {
    val holder = WanUserRequest()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  fun register(request: Request): Single<String> {
    return request.rxString()
        .flatMap { flatMapSingle(it) }
        .compose(RxUtils.instance.rx2SchedulerHelperSDelay())
  }

  private fun flatMapSingle(result: Result<String, FuelError>): Single<String> {
    return if (result.component2() == null) {
      Single.just(converWanData(result.component1()))
    } else {
      Single.error(result.component2()?.exception ?: Throwable("null"))
    }
  }

  //数据转换，可能抛出异常
  @Throws
  private fun converWanData(response: String?): String {
    if (response.isNullOrBlank()) throw Throwable("response is null or empty")
    return response
  }
}
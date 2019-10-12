package cc.abase.demo.repository.request

import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.repository.bean.wan.UserBean
import com.blankj.utilcode.util.GsonUtils
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.rx.rxString
import com.github.kittinunf.result.Result
import com.google.gson.reflect.TypeToken
import io.reactivex.Single

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/9 21:38
 */
class WanUserRequest private constructor() : BaseRequest() {
  private object SingletonHolder {
    val holder = WanUserRequest()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  fun register(request: Request): Single<BaseResponse<UserBean>> {
    return request.rxString()
        .flatMap { flatMapSingle(it) }
  }

  fun login(request: Request): Single<BaseResponse<UserBean>> {
    return request.rxString()
        .flatMap { flatMapSingle(it) }
  }

  //======================================下面是统一处理======================================//
  private fun flatMapSingle(result: Result<String, FuelError>): Single<BaseResponse<UserBean>> {
    return if (result.component2() == null) {
      Single.just(converWanData(result.component1()))
    } else {
      Single.error(converFuelError(result.component2()))
    }
  }

  //数据转换，可能抛出异常
  @Throws
  private fun converWanData(response: String?): BaseResponse<UserBean> {
    if (response.isNullOrBlank()) throw converDataError()
    try {
      return GsonUtils.fromJson(response, object : TypeToken<BaseResponse<UserBean>>() {}.type)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    throw converDataError()
  }
}
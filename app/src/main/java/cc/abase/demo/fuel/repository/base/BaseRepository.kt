package cc.abase.demo.fuel.repository.base

import cc.ab.base.net.http.response.ApiException
import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.R.string
import cc.abase.demo.bean.gank.GankResponse
import com.blankj.utilcode.util.StringUtils
import io.reactivex.Single

/**
 * Description:统一处理业务异常
 * @author: caiyoufei
 * @date: 2019/10/12 20:26
 */
abstract class BaseRepository {
  //统一处理base的数据
  fun <T> justRespons(response: BaseResponse<T>): Single<T> {
    return if (response.errorCode == 0 && response.data != null) {
      Single.just(response.data)
    } else {
      Single.error(
          if (response.errorCode == 0 && response.data == null) {
            ApiException(
                msg = StringUtils.getString(string.service_no_data)
            )
          } else {
            ApiException(code = response.errorCode, msg = response.errorMsg)
          }
      )
    }
  }

  //统一处理base的数据
  fun <T> justRespons(response: GankResponse<T>): Single<T> {
    return if (!response.error && response.results != null) {
      Single.just(response.results)
    } else {
      Single.error(
          if (!response.error && response.results == null) {
            ApiException(msg = StringUtils.getString(string.service_no_data))
          } else {
            ApiException(msg = response.message)
          }
      )
    }
  }
}
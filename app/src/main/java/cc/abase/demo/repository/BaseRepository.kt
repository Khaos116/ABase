package cc.abase.demo.repository

import cc.ab.base.net.http.response.ApiException
import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.R.string
import com.blankj.utilcode.util.Utils
import io.reactivex.Single

/**
 * Description:
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
                msg = Utils.getApp().getString(
                    string.service_no_data
                )
            )
          } else {
            ApiException(code = response.errorCode, msg = response.errorMsg)
          }
      )
    }
  }
}
package cc.abase.demo.repository.request

import cc.ab.base.net.http.response.ApiException
import cc.abase.demo.R
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils
import com.github.kittinunf.fuel.core.FuelError

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/12 15:34
 */
abstract class BaseRequest {
  //数据转换异常
  fun converDataError(): Throwable {
    return ApiException(msg = Utils.getApp().getString(R.string.data_conver_error))
  }

  //请求异常
  fun converFuelError(error: FuelError?): Throwable {
    if (error == null || error.exception.message.isNullOrEmpty()) {
      return ApiException(msg = Utils.getApp().getString(R.string.data_request_error))
    } else {
      val code = error.response.statusCode
      val comtext = Utils.getApp()
      val throwable: Throwable = when {//https://blog.csdn.net/ddhsea/article/details/79405996
        //添加特殊异常
        // val apiException = ApiException()

        //网络问题
        code < 0 -> {
          Throwable(
              message = if (NetworkUtils.isConnected()) {
                comtext.getString(R.string.data_request_error)
              } else {
                comtext.getString(R.string.no_network)
              }, cause = error.exception
          )
        }
        //客户端参数错误
        code in 400..499 -> {
          Throwable(
              message = comtext.getString(R.string.client_request_error), cause = error.exception
          )
        }
        //服务器响应错误
        code in 500..599 -> {
          Throwable(
              message = comtext.getString(R.string.service_response_error), cause = error.exception
          )
        }
        //未知错误
        else -> {
          Throwable(message = comtext.getString(R.string.unknown_error), cause = error.exception)
        }
      }
      return throwable
    }
  }
}
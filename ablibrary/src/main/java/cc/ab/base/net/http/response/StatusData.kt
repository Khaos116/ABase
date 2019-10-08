package cc.ab.base.net.http.response

/**
 * Description:请求结果的data.
 * @author: caiyoufei
 * @date: 2019/9/22 18:57
 */
sealed class StatusData<out T> {
  /**
   * 请求成功
   */
  data class Success<out T>(val data: T?) : StatusData<T>()

  /**
   * 请求失败，无论哪种失败都会返回
   */
  data class Failure<out T>(val throwable: Throwable) : StatusData<T>()
}
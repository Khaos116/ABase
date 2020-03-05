package cc.ab.base.net.http.response

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/5 19:27
 */
data class GankResponse<T>(
  var error: Boolean = false,
  val results: T? = null
)
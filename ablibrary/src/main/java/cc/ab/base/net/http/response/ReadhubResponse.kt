package cc.ab.base.net.http.response

/**
 * Description:
 * @author: Khaos
 * @date: 2022年3月8日16:05:57
 */
data class ReadhubResponse<T>(
  var pageSize: Int = 1,
  var totalItems: Int = 1,
  var totalPages: Int = 1,
  val data: T? = null
)
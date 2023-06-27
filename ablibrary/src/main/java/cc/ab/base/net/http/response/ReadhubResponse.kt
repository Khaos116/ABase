package cc.ab.base.net.http.response

/**
 * Description:
 * @author: Khaos
 * @date: 2022年3月8日16:05:57
 */
data class ReadhubResponse<T>(
  val data: T? = null
)
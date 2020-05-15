package cc.ab.base.net.http.response

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/5 19:27
 */
data class GankResponse<T>(
    var status: Int = 100,
    var page: Int = 1,
    var page_count: Int = 1,
    var total_counts: Int = 1,
    val data: T? = null) {
  fun error(): Boolean {
    return status != 100
  }
}
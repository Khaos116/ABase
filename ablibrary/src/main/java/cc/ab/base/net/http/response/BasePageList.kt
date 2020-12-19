package cc.ab.base.net.http.response

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/5 19:12
 */
data class BasePageList<out T>(
  var curPage: Int = 0, //当前页数
  var pageCount: Int = 0, //总页数
  var total: Int = 0, //总条数
  val datas: List<T>? = null
)
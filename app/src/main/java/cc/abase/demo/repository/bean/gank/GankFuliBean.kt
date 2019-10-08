package cc.abase.demo.repository.bean.gank

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 20:31
 */
data class GankFuliBean(
  var _id: String,
  var createdAt: String?,
  var desc: String?,
  var publishedAt: String?,
  var source: String?,
  var type: String?,
  var url: String?,
  var used: Boolean?,
  var who: String?
)
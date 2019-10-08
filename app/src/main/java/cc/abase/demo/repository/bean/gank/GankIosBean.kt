package cc.abase.demo.repository.bean.gank

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 20:33
 */
data class GankIosBean(
  var _id: String,
  var createdAt: String?,
  var desc: String?,
  var images: List<String?>?,
  var publishedAt: String?,
  var source: String?,
  var type: String?,
  var url: String?,
  var used: Boolean?,
  var who: String?
)
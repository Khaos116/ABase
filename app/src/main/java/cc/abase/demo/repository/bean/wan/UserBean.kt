package cc.abase.demo.repository.bean.wan

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/11 10:59
 */
data class UserBean(
  var admin: Boolean = false,
  var email: String? = null,
  var icon: String? = null,
  var id: Long = 0,
  var nickname: String? = null,
  var password: String? = null,
  var publicName: String? = null,
  var type: Int = 0,
  var username: String? = null,
  val chapterTops: List<Any>? = null,
  val collectIds: List<Any>? = null
)
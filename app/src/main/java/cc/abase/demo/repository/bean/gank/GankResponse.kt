package cc.abase.demo.repository.bean.gank

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 20:34
 */
data class GankResponse<out T>(
  val error: Boolean = true,
  var message: String? = null,
  val results: T? = null
)
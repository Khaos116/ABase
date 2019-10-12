package cc.ab.base.net.http.response

/**
 * Description:网络请求异常的Exception
 * @author: caiyoufei
 * @date: 2019/9/22 18:53
 */
open class ApiException constructor(
  var code: Int = 0,
  var msg: String? = null,
  val fields: Map<String, Any>? = null
) : Exception(msg)
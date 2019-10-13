package cc.abase.demo.repository.bean.gank

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 20:34
 */
data class GankResponse<out T>(
  val error: Boolean = true,
  var message: String? = null,
  val results: T?
)
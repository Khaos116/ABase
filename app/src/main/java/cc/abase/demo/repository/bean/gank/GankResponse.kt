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
  val message: String? = null,
  val results: T?
) {
  val rawType: Type by lazy {
    getSuperclassTypeParameter(javaClass)
  }

  companion object {
    fun getSuperclassTypeParameter(cls: Class<*>): Type {
      val superclass = cls.genericSuperclass
      if (superclass is Class<*>) {
        throw RuntimeException("Missing type parameter.")
      }
      val parameterized = superclass as ParameterizedType
      return `$Gson$Types`.canonicalize(parameterized.actualTypeArguments[0])
    }
  }
}
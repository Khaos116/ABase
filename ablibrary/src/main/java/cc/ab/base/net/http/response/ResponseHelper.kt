package cc.ab.base.net.http.response

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Description:https://blog.csdn.net/quan648997767/article/details/86537930
 * @author: caiyoufei
 * @date: 2019/10/12 20:15
 */
abstract class ResponseHelper<T> {
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
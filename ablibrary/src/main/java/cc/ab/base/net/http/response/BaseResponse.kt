package cc.ab.base.net.http.response

import android.util.Log
import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Description:网络请求返回的基类,接口文档没有不确定.
 * @author: caiyoufei
 * @date: 2019/9/22 18:54
 */
data class BaseResponse<out T>(
  val errorCode: Int = -1,//正常接口使用的状态码
  val errorMsg: String? = null,//code异常对应的信息提示
  val throwType: String? = null,//code异常对应的服务端出错信息
  val data: T? = null,//正常返回的数据信息
  val fields: Map<String, Any>? = null//异常中返回的一些参数
)
{

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
      val type = `$Gson$Types`.canonicalize(parameterized.actualTypeArguments[0])
      Log.e("CASE", "type=${type}")
      return type
    }
  }
}
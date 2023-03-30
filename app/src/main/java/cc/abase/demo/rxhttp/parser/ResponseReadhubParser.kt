package cc.abase.demo.rxhttp.parser

import cc.ab.base.ext.readBodyMyString
import cc.ab.base.ext.xmlToString
import cc.ab.base.net.http.response.ReadhubResponse
import cc.abase.demo.R
import com.blankj.utilcode.util.GsonUtils
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.entity.ParameterizedTypeImpl
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import java.io.IOException
import java.lang.reflect.Type

/**
 * Description:
 * @author: Khaos
 * @date: 2022年3月8日16:00:42
 */
@Parser(name = "ResponseReadhub")
open class ResponseReadhubParser<T> : TypeParser<T> {
  //该构造方法是必须的
  protected constructor() : super()

  //如果依赖了RxJava，该构造方法也是必须的/
  constructor(type: Type) : super(type)

  @Throws(IOException::class)
  override fun onParse(response: okhttp3.Response): T {
    if (response.isSuccessful && response.body?.contentType()?.isParsable() == true) {
      val result = response.readBodyMyString()
      if (result.isNullOrEmpty()) {
        throw ParseException(response.code.toString(), "body is blank", response)
      }
      val type: Type = ParameterizedTypeImpl[ReadhubResponse::class.java, types.first()]
      val responseGank: ReadhubResponse<T> = GsonUtils.fromJson(result, type)
      return responseGank.data ?: throw ParseException("-1", R.string.数据返回错误.xmlToString(), response)
    } else {
      throw ParseException(response.code.toString(), "fail or type error", response)
    }
  }
}
package cc.abase.demo.rxhttp.parser

import cc.ab.base.ext.readBodyMyString
import cc.ab.base.ext.xmlToString
import cc.ab.base.net.http.response.GankResponse
import cc.abase.demo.R
import com.blankj.utilcode.util.GsonUtils
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.entity.ParameterizedTypeImpl
import rxhttp.wrapper.exception.HttpStatusCodeException
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import java.io.IOException
import java.lang.reflect.Type

/**
 * Description:
 * @author: Khaos
 * @date: 2020/6/26 11:13
 */
@Parser(name = "ResponseGank")
open class ResponseGankParser<T> : TypeParser<T> {
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
      val type: Type = ParameterizedTypeImpl[GankResponse::class.java, types.first()]
      val responseGank: GankResponse<T> = GsonUtils.fromJson(result, type)
      val data = responseGank.data
      //code不等于0，说明数据不正确，抛出异常
      if (responseGank.error() || data == null) {
        throw ParseException("-1", if (responseGank.error()) R.string.接口error.xmlToString() else R.string.数据返回错误.xmlToString(), response)
      }
      return data
    } else if (response.body?.contentType()?.isParsable() != true) {
      throw ParseException(response.code.toString(), "ContentType Parsing Exception", response)
    } else {
      throw HttpStatusCodeException(response)
    }
  }
}
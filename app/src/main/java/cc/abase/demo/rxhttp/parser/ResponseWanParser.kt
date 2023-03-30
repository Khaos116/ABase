package cc.abase.demo.rxhttp.parser

import cc.ab.base.ext.readBodyMyString
import cc.ab.base.net.http.response.BasePageList
import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.config.GlobalErrorHandle
import com.blankj.utilcode.util.GsonUtils
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.entity.ParameterizedTypeImpl
import rxhttp.wrapper.exception.HttpStatusCodeException
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import java.io.IOException
import java.lang.reflect.Type

/**
 * Description: 支持data解析为
 * 1.T
 * 2.MutableList<T>
 * 3.BasePageList<T>
 * @author: Khaos
 * @date: 2020/6/26 16:01
 */
@Parser(name = "ResponseWan", wrappers = [BasePageList::class])
open class ResponseWanParser<T> : TypeParser<T> {
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
      val type: Type = ParameterizedTypeImpl[BaseResponse::class.java, types.first()]
      val responseWan: BaseResponse<T> = GsonUtils.fromJson(result, type)
      val data = responseWan.data
      //code不等于0，说明数据不正确，抛出异常
      if (data == null || responseWan.errorCode != 0) {
        val dealCode = GlobalErrorHandle.dealGlobalErrorCode(responseWan.errorCode)
        throw ParseException(dealCode.toString(), responseWan.errorMsg, response)
      }
      return data
    } else if (response.body?.contentType()?.isParsable() != true) {
      throw ParseException(response.code.toString(), "ContentType Parsing Exception", response)
    } else {
      throw HttpStatusCodeException(response)
    }
  }
}
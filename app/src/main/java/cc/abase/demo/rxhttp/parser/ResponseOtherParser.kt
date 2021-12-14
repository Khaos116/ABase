package cc.abase.demo.rxhttp.parser

import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convert
import java.io.IOException
import java.lang.reflect.Type

/**
 * Description:
 * @author: Khaos
 * @date: 2021年6月2日21:03:55
 */
@Parser(name = "ResponseOther")
open class ResponseOtherParser<T> : TypeParser<T> {
  //该构造方法是必须的
  protected constructor() : super()

  //如果依赖了RxJava，该构造方法也是必须的/
  constructor(type: Type) : super(type)

  @Throws(IOException::class)
  override fun onParse(response: okhttp3.Response): T {
    //------------------------自己处理Start(能处理结果不影响response.body的后续调用)------------------------//
    ////读取返回结果
    //var result: String? = null
    //if (response.body?.contentType()?.isParsable() == true) {
    //  result = response.peekBody(Long.MAX_VALUE).string()
    //}
    ////判断结果
    //if (result == null || result.isEmpty()) throw ParseException("500", "服务器没有数据", response)
    //val responseOther: T = GsonUtils.fromJson(result, mType)
    //------------------------自己处理End(能处理结果不影响response的后续调用)------------------------//

    //---------------------------------交给框架处理Start(后续无法再使用response.body)---------------------------------//
    //转换类型
    val responseOther: T = response.convert(types.first())
    //---------------------------------交给框架处理End---------------------------------//

    return responseOther
  }
}
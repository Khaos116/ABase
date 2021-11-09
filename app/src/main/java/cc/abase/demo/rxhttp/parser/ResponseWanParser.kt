package cc.abase.demo.rxhttp.parser

import cc.ab.base.net.http.response.BasePageList
import cc.ab.base.net.http.response.BaseResponse
import cc.abase.demo.config.GlobalErrorHandle
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
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
@Parser(name = "ResponseWan", wrappers = [MutableList::class, BasePageList::class])
open class ResponseWanParser<T> : TypeParser<T> {
  //该构造方法是必须的
  protected constructor() : super()

  //如果依赖了RxJava，该构造方法也是必须的/
  constructor(type: Type) : super(type)

  @Throws(IOException::class)
  override fun onParse(response: okhttp3.Response): T {
    //------------------------自己处理Start(能处理结果不影响response.body的后续调用)------------------------//
    ////获取泛型类型
    //val type: Type = ParameterizedTypeImpl[BaseResponse::class.java, mType]
    ////读取返回结果
    //var result: String? = null
    //if (response.body?.contentType()?.isParsable() == true) {
    //  result = response.peekBody(Long.MAX_VALUE).string()
    //}
    ////判断结果
    //if (result == null || result.isEmpty()) throw ParseException("500", "服务器没有数据", response)
    ////转换类型
    //val responseWan: BaseResponse<T> = GsonUtils.fromJson(result, type)
    //------------------------自己处理End(能处理结果不影响response的后续调用)------------------------//

    //---------------------------------交给框架处理Start(后续无法再使用response.body)---------------------------------//
    val responseWan: BaseResponse<T> = response.convertTo(BaseResponse::class.java, *types)
    //---------------------------------交给框架处理End---------------------------------//

    //获取data字段
    val data = responseWan.data
    //code不等于0，说明数据不正确，抛出异常
    if (data == null || responseWan.errorCode != 0) {
      val dealCode = GlobalErrorHandle.dealGlobalErrorCode(responseWan.errorCode)
      throw ParseException(dealCode.toString(), responseWan.errorMsg, response)
    }
    return data
  }
}
package cc.abase.demo.rxhttp.parser

import cc.abase.demo.config.GlobalErrorHandle
import com.blankj.utilcode.util.GsonUtils
import org.json.JSONObject
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convert
import timber.log.Timber
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

    //---------------------------------强制进行统一异常处理(不同项目可能code和msg需要替换)Start---------------------------------//
    val codeKey = "code"//接口正常与否的code码key
    val msgKey = "msg"//接口正常和异常的提示信息key
    val successMsg = "success"//接口正常返回的提示消息
    val successCode = 200//接口正常的code值
    val noLoginCode = -154//未登录对应的code值
    try {
      val jsonStr = GsonUtils.toJson(responseOther)
      if (jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
        val jsonObject = JSONObject(jsonStr)
        if (jsonObject.has(codeKey)) {
          val code = jsonObject.optInt(codeKey, 0)
          if (code != successCode) {
            val msg = jsonObject.optString(msgKey, "")
            val codeNew = if (msg.contains("未登录")) noLoginCode else code
            val dealCode = GlobalErrorHandle.dealGlobalErrorCode(codeNew)
            if (successMsg == msg) Timber.e("ResponseLive解析异常:${response.request.url}")
            throw ParseException(dealCode.toString(), msg, response)
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    //---------------------------------强制进行统一异常处理(不同项目可能code和msg需要替换)End---------------------------------//
    return responseOther
  }
}
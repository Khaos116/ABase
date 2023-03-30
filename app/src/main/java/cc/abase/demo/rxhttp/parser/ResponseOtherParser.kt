package cc.abase.demo.rxhttp.parser

import cc.ab.base.ext.readBodyMyString
import cc.abase.demo.config.GlobalErrorHandle
import com.blankj.utilcode.util.GsonUtils
import org.json.JSONObject
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type

/**
 * Description: 其他通用结构解析
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
    if (response.isSuccessful && response.body?.contentType()?.isParsable() == true) {
      val result = response.readBodyMyString()
      if (result.isNullOrEmpty()) {
        throw ParseException(response.code.toString(), "body is blank", response)
      }
      val codeKey = "code"//接口正常与否的code码key
      val msgKey = "msg"//接口正常和异常的提示信息key
      val successMsg = "success"//接口正常返回的提示消息
      val successCode = 200//接口正常的code值
      val noLoginCode = -154//未登录对应的code值
      try {
        val jsonStr = GsonUtils.toJson(result)
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
      return GsonUtils.fromJson(result, types.first())
    } else {
      throw ParseException(response.code.toString(), "fail or type error", response)
    }
  }
}
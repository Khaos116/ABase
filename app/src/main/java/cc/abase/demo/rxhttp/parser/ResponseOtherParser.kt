package cc.abase.demo.rxhttp.parser

import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.parse.AbstractParser
import rxhttp.wrapper.utils.convert
import java.io.IOException
import java.lang.reflect.Type

/**
 * Description:
 * @author: CASE
 * @date: 2021年6月2日21:03:55
 */
@Parser(name = "ResponseOther", wrappers = [MutableList::class])
open class ResponseOtherParser<T> : AbstractParser<T> {
  /**
   * 此构造方法适用于任意Class对象，但更多用于带泛型的Class对象，如：List<Student>
   *
   * 用法:
   * Java: .asParser(new ResponseParser<List<Student>>(){})
   * Kotlin: .asParser(object : ResponseParser<List<Student>>() {})
   *
   * 注：此构造方法一定要用protected关键字修饰，否则调用此构造方法将拿不到泛型类型
   */
  protected constructor() : super()

  /**
   * 此构造方法仅适用于不带泛型的Class对象，如: Student.class
   *
   * 用法
   * Java: .asParser(new ResponseParser<>(Student.class))   或者  .asResponse(Student.class)
   * Kotlin: .asParser(ResponseParser(Student::class.java)) 或者  .asResponse<Student>()
   */
  constructor(type: Type) : super(type)

  @Throws(IOException::class)
  override fun onParse(response: okhttp3.Response): T {
    //------------------------自己处理Start(能处理结果不影响response.body的后续调用)------------------------//
    ////读取返回结果
    //var result: String? = null
    //val body = response.body
    //if (body != null) {
    //  val source = body.source()
    //  source.request(Long.MAX_VALUE)
    //  result = source.buffer.clone().readString(StandardCharsets.UTF_8)
    //}
    ////判断结果
    //if (result == null || result.isEmpty()) throw ParseException("500", "服务器没有数据", response)
    //val responseOther: T = GsonUtils.fromJson(result, mType)
    //------------------------自己处理End(能处理结果不影响response的后续调用)------------------------//

    //---------------------------------交给框架处理Start(后续无法再使用response.body)---------------------------------//
    //转换类型
    val responseOther: T = response.convert(mType)
    //---------------------------------交给框架处理End---------------------------------//

    return responseOther
  }
}
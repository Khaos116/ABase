package cc.abase.demo.rxhttp.parser

import cc.ab.base.ext.xmlToString
import cc.ab.base.net.http.response.ReadhubResponse
import cc.abase.demo.R
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
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
        //------------------------自己处理Start(能处理结果不影响response.body的后续调用)------------------------//
        ////获取泛型类型
        //val type: Type = ParameterizedTypeImpl[GankResponse::class.java, mType]
        ////读取返回结果
        //var result: String? = null
        //if (response.body?.contentType()?.isParsable() == true) {
        //  result = response.peekBody(Long.MAX_VALUE).string()
        //}
        ////判断结果
        //if (result == null || result.isEmpty()) throw ParseException("500", "服务器没有数据", response)
        ////转换类型
        //val responseGank: GankResponse<T> = GsonUtils.fromJson(result, type)
        //------------------------自己处理End(能处理结果不影响response的后续调用)------------------------//

        //---------------------------------交给框架处理Start(后续无法再使用response.body)---------------------------------//
        val responseGank: ReadhubResponse<T> = response.convertTo(ReadhubResponse::class.java, *types)
        //---------------------------------交给框架处理End---------------------------------//

        //获取data字段
        return responseGank.data ?: throw ParseException("-1", R.string.数据返回错误.xmlToString(), response)
    }
}
//package cc.ab.base.net.http.converter
//
//import cc.ab.base.app.BaseApplication
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import okhttp3.RequestBody
//import okhttp3.ResponseBody
//import org.kodein.di.generic.instance
//import retrofit2.Converter
//import retrofit2.Retrofit
//import java.lang.reflect.Type
//
///**
// * Description:
// * @author: caiyoufei
// * @date: 2019/9/22 19:01
// */
//class AbnormalConverterFactory private constructor(private val gson: Gson) : Converter.Factory() {
//
//  override fun responseBodyConverter(
//    type: Type, annotations: Array<Annotation>,
//    retrofit: Retrofit
//  ): Converter<ResponseBody, *> {
//    val adapter = gson.getAdapter(TypeToken.get(type))
//    return AbnormalResponseBodyConverter(gson, adapter)
//  }
//
//  override fun requestBodyConverter(
//    type: Type,
//    parameterAnnotations: Array<Annotation>,
//    methodAnnotations: Array<Annotation>, retrofit: Retrofit
//  ): Converter<*, RequestBody> {
//    val adapter = gson.getAdapter(TypeToken.get(type))
//    return AbnormalRequestBodyConverter(gson, adapter)
//  }
//
//  companion object {
//    fun create(): AbnormalConverterFactory {
//      val instance: Gson by BaseApplication.getApp().kodein.instance()
//      return AbnormalConverterFactory(instance)
//    }
//  }
//}
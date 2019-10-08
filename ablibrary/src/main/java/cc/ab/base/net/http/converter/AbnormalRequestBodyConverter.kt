//package cc.ab.base.net.http.converter
//
//import com.google.gson.Gson
//import com.google.gson.TypeAdapter
//import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import okio.Buffer
//import retrofit2.Converter
//import java.io.OutputStreamWriter
//import java.nio.charset.Charset
//
///**
// * Description:请求转换
// * @author: caiyoufei
// * @date: 2019/9/22 19:02
// */
//class AbnormalRequestBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) :
//  Converter<T, RequestBody> {
//  private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
//  private val UTF_8 = Charset.forName("UTF-8")
//
//  override fun convert(value: T): RequestBody {
//    val buffer = Buffer()
//    val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
//    val jsonWriter = gson.newJsonWriter(writer)
//    adapter.write(jsonWriter, value)
//    jsonWriter.close()
//    return buffer.readByteString().toRequestBody(MEDIA_TYPE)
//  }
//}
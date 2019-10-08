//package cc.ab.base.net.http.converter
//
//import android.util.Log
//import com.google.gson.Gson
//import com.google.gson.TypeAdapter
//import okhttp3.ResponseBody
//import org.json.JSONObject
//import retrofit2.Converter
//
///**
// * Description:
// * @author: caiyoufei
// * @date: 2019/9/22 19:05
// */
//class AbnormalResponseBodyConverter<T> constructor(
//  private val gson: Gson,
//  private val adapter: TypeAdapter<T>
//) : Converter<ResponseBody, T> {
//  private val KEY_DATA = "data"
//  private val KEY_CODE = "code"
//  private val EMPTY_DATA_LIST_CONVER = "{\"data\":[]}"
//  private val EMPTY_DATA_OBJECT_CONVER = "{\"data\":{}}"
//  private val EMPTY_DATA_LIST_ADD = "{\"data\":[],"
//  private val EMPTY_DATA_OBJECT_ADD = "{\"data\":{},"
//  override fun convert(value: ResponseBody): T {
//    if (value.contentLength() > Int.MAX_VALUE) {
//      //超出String字符串的长度
//      value.use {
//        val jsonReader = gson.newJsonReader(it.charStream())
//        return adapter.read(jsonReader)
//      }
//    }
//
//    var resStr = String(value.bytes())
//    val resJsonOb = JSONObject(resStr)
//    if (resStr.isNotEmpty()
//      && resStr.startsWith("{")
//      && resJsonOb.has(KEY_CODE)
//      && resJsonOb.getInt(KEY_CODE) == 200
//      && !resJsonOb.has(KEY_DATA)
//    ) {
//      try {
//        adapter.fromJson(EMPTY_DATA_OBJECT_CONVER)
//        resStr = EMPTY_DATA_OBJECT_ADD + resStr.substring(1)
//      } catch (e: Exception) {
//        try {
//          adapter.fromJson(EMPTY_DATA_LIST_CONVER)
//          resStr = EMPTY_DATA_LIST_ADD + resStr.substring(1)
//        } catch (e: Exception) {
//          e.printStackTrace()
//          Log.e("CASE", "BodyConverter:data is not list and object")
//        }
//      }
//    }
//    return adapter.fromJson(resStr)
//  }
//}
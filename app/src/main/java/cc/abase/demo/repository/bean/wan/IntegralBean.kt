package cc.abase.demo.repository.bean.wan

import cc.abase.demo.constants.WanAndroidUrls
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Description:积分
 * @author: caiyoufei
 * @date: 2019/10/12 20:06
 */
data class IntegralBean(
  var coinCount: Int = 0,
  var rank: Int = 0,
  var userId: Long = 0,
  var username: String?
){
  //User Deserializer
  class Deserializer : ResponseDeserializable<IntegralBean> {
    override fun deserialize(content: String) = Gson().fromJson(content, IntegralBean::class.java)
  }
}
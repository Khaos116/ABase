package cc.abase.demo.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @Description https://github.com/YeEeck/NetworkTool/blob/master/app/src/main/java/com/yeck/networktool/MainActivity.java
 * @Author：Khaos
 * @Date：2021-06-02
 * @Time：20:53
 */
data class IpBean(
  var status: String? = null, // "success",
  var country: String? = null, // "香港",
  var countryCode: String? = null, // "HK",
  var region: String? = null, // "NSK",
  var regionName: String? = null, // "Sai Kung District",
  var city: String? = null, // "Tseung Kwan O",
  var zip: String? = null, // "",
  var lat: String? = null, // 22.3119,
  var lon: String? = null, // 114.257,
  var timezone: String? = null, // "Asia/Hong_Kong",
  var isp: String? = null, // "Aofei Data International Company Limited",
  var org: String? = null, // "PT Abhinawa Sumberdaya Asia",
  @SerializedName(value = "as")
  var company: String? = null, // "AS135391 AOFEI DATA INTERNATIONAL COMPANY LIMITED",
  var query: String? = null, // "61.29.252.251"
)
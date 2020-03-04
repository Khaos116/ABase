package cc.abase.demo.bean.local

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/26 18:17
 */
data class CityBean(
  var id: Long = 0,// 10,
  var level: Int = 0,// 2,
  var regionCode: String? = null,// "0310",
  var regionName: String? = null,// "邯郸",
  var parentId: Long = 0,// 6,
  var longitude: Double? = 0.0,// 114.538959,
  var latitude: Double? = 0.0,// 36.625594,
  var pinYin: String? = null,// "handan",
  var pinYinFirst: String? = null,// "HD",
  var regionFullName: String? = null,// "邯郸市"
  var fromTag: String? = ""//从哪个页面进行的选择
)
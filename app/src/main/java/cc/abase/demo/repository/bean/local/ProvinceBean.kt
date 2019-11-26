package cc.abase.demo.repository.bean.local

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/26 18:17
 */
data class ProvinceBean(
  var id: Long = 0,// 6,
  var level: Int = 0,// 1,
  var regionCode: String? = null,// "13",
  var regionName: String? = null,// "河北",
  var parentId: Long = 0,// 1,
  var longitude: Double? = 0.0,// 114.530235,
  var latitude: Double? = 0.0,// 38.037433,
  var pinYin: String? = null,// "hebei",
  var pinYinFirst: String? = null,// "HB",
  var cmsRegionDtoList: MutableList<CityBean>? = null//
)
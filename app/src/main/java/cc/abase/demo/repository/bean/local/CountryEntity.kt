package cc.abase.demo.repository.bean.local

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/28 15:26
 */
data class CountryEntity(
  var id: Int = 0,//主键Id
  var name_en: String,//国家英文全名
  var name_zh: String,//国家中文名
  var name_international_abbr: String,//国际域名缩写
  var country_phone_code: String,//MMENT
  var zone: String? = null,//国家所属范围圈,例中东，亚洲，非洲...
  var zone_alias: String? = null,//所属分区别名字段，如果有，则以该zone为准
  var lat: String? = null,//国家所处纬度'
  var lon: String? = null,//国家所处经度'
  var pinyinFirst: String? = null,
  var pinyinFull: String? = null
)
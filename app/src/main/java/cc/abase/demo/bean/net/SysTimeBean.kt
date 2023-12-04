package cc.abase.demo.bean.net

/**
 * Author:Khaos
 * Date:2023/12/4
 * Time:10:05
 */
data class SysTimeBean(
  //http://worldclockapi.com/api/json/utc/now
  val currentDateTime: String? = "",//2023-12-04T03:02Z
  //https://timeapi.io/api/Time/current/zone?timeZone=UTC
  val dateTime: String? = "",//2023-12-04T03:02:15.835507
)
package cc.abase.demo.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Description:时区转换工具
 * UTC(Universal Time/Temps Cordonné 世界标准时间)
 * GMT(Greenwich Mean Time，格林威治标准时间)
 * CST各个翻译版本：
 *      美国中部时间：Central Standard Time (USA) UT-6:00
 *      澳大利亚中部时间：Central Standard Time (Australia) UT+9:30
 *      中国标准时间：China Standard Time UT+8:00
 *      古巴标准时间：Cuba Standard Time UT-4:00
 * @author: CASE
 * @date: 2019/10/3 18:20
 */
object TimeUtils {
  //UTC时间格式
  private val UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  //本地时间格式
  private val LOCAL_FORMAT = "yyyy-MM-dd HH:mm:ss"

  //当地时间 ---> UTC时间
  fun local2UTC(time: Long? = null): String {
    val sdf = SimpleDateFormat(UTC_FORMAT, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(if (time == null) Date() else Date(time))
  }

  /**
   * UTC时间 ---> 当地时间
   * 如：2019-07-24T06:57:06.548Z ---> 2019-07-24 14:57:06
   * @param utcTime  UTC时间
   */
  fun utc2Local(
      utcTime: String,
      offSet: Int? = null //当前时区偏移，如:北京+8，纽约-5，默认为手机自带时区
  ): String {
    val utcFormatter = SimpleDateFormat(UTC_FORMAT, Locale.getDefault()) //UTC时间格式
    utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
    try {
      val utcDate = utcFormatter.parse(utcTime)
      val localFormatter = SimpleDateFormat(LOCAL_FORMAT, Locale.getDefault()) //当地时间格式
      if (offSet != null && offSet >= 0) {
        localFormatter.timeZone = TimeZone.getTimeZone("GMT+${min(offSet, 11)}")
      } else if (offSet != null && offSet < 0) {
        localFormatter.timeZone = TimeZone.getTimeZone("GMT${max(-11, offSet)}")
      } else {
        localFormatter.timeZone = TimeZone.getDefault()
      }
      return localFormatter.format(utcDate.time)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return ""
  }
}
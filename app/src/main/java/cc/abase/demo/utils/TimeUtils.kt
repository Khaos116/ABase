package cc.abase.demo.utils

import cc.ab.base.ext.logE
import com.google.gson.internal.bind.util.ISO8601Utils
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

/**
 * Description:时区转换工具
 * UTC(Universal Time/Temps Cordonné 世界标准时间)
 * GMT(Greenwich Mean Time，格林威治标准时间)
 * CST各个翻译版本：
 *      美国中部时间：Central Standard Time (USA) UT-6:00
 *      澳大利亚中部时间：Central Standard Time (Australia) UT+9:30
 *      中国标准时间：China Standard Time UT+8:00
 *      古巴标准时间：Cuba Standard Time UT-4:00
 * @author: Khaos
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

  //<editor-fold defaultstate="collapsed" desc="2021年4月5日22:46:47新增转换">
  /**
   * 带时区的时间转换为本地时间显示
   * @author CASE
   * @param time GTM带时区的时间格式(格式为[yyyy-MM-dd|yyyyMMdd][T(hh:mm[:ss[.sss]]|hhmm[ss[.sss]])]?[Z|[+-]hh[:mm]]])
   * @return 返回yyyy-MM-dd HH:mm:ss格式的时间
   */
  fun iso8601ToLocal(time: String/*2021-04-03T00:00:00-04:00*/): String {
    val localFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return localFormatter.format(ISO8601Utils.parse(time, ParsePosition(0)))
  }

  /**
   * GMT时间转本地时间
   * @author CASE
   * @param time GMT时间(格式为格式为yyyy-MM-dd HH:mm:ss)
   * @param offSet GMT的时区(默认为0时区)
   * @return 返回yyyy-MM-dd HH:mm:ss格式的时间
   */
  fun gmt2Local(time: String/*2021-04-05 21:34:46*/, offSet: Int = 0): String {
    val split = time.split(" ")
    if (split.size != 2) return time
    val offsetStr = if (offSet < 0) {
      when {
        offSet > -10 -> "-0${abs(offSet)}:00"
        offSet > -12 -> "-${abs(offSet)}:00"
        else -> "-11:00"
      }
    } else {
      when {
        offSet < 10 -> "+0${offSet}:00"
        offSet < 12 -> "+${offSet}:00"
        else -> "+11:00"
      }
    }
    return iso8601ToLocal("${split[0]}T${split[1]}$offsetStr")
  }

  /**
   * 本地时间转指定GMT时区时间
   * @author CASE
   * @param time 本地时间(格式为yyyy-MM-dd HH:mm:ss)
   * @param offSet GMT时区(如:北京+8，纽约-5，默认为0时区)
   * @return 返回yyyy-MM-dd HH:mm:ss格式的时间
   */
  fun local2Gmt(time: String/*2021-04-05 21:34:46*/, offSet: Int = 0): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = try {
      format.parse(time)
    } catch (e: Exception) {
      return time
    }
    return local2Gmt(date, offSet)
  }

  /**
   * 本地时间转指定GMT时区时间
   * @author CASE
   * @param date 本地时间
   * @param offSet GMT时区(如:北京+8，纽约-5，默认为0时区)
   * @param offSet GMT时区(如:北京+8，纽约-5，默认为0时区)
   * @param endSSS 是否返回带毫秒的时间
   * @return 返回yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd HH:mm:ss.SSS格式的时间
   */
  fun local2Gmt(date: Date = Date(), offSet: Int = 0, endSSS: Boolean = false): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss${if (endSSS) ".SSS" else ""}", Locale.getDefault())
    if (offSet < 0) {
      format.timeZone = TimeZone.getTimeZone("GMT${min(offSet, 11)}")
    } else {
      format.timeZone = TimeZone.getTimeZone("GMT+${min(offSet, 11)}")
    }
    return format.format(date)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="时差计算">
  private fun getOffset(): Long {
    val date = Date()
    val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    sdf1.timeZone = TimeZone.getTimeZone("UTC")
    val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date1 = sdf1.format(date)
    val date2 = sdf2.format(date)
    val off = (sdf2.parse(date2)?.time ?: 0) - (sdf2.parse(date1)?.time ?: 0)
    "本地和0时区时差为：${(off / 3600000f)}小时".logE()
    return off
  }
  //</editor-fold>
}
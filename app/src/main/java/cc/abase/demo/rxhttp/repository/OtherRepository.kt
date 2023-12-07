package cc.abase.demo.rxhttp.repository

import cc.abase.demo.bean.net.IpBean
import cc.abase.demo.bean.net.SysTimeBean
import com.blankj.utilcode.constant.TimeConstants
import rxhttp.cc.RxHttp
import rxhttp.cc.toAwaitResponseOther
import rxhttp.map
import rxhttp.onErrorReturn
import rxhttp.wrapper.cache.CacheMode

/**
 * @Description
 * @Author：Khaos
 * @Date：2021-06-02
 * @Time：20:58
 */
object OtherRepository : BaseRepository() {
  private const val TIME_OUT = 30L * TimeConstants.SEC

  //只获取外网IP的地址
  private var netIpUrls = mutableListOf(
    "http://checkip.amazonaws.com",
    "http://www.icanhazip.com",
    "http://icanhazip.com",
    "http://ipinfo.io/ip",
    "https://api.ip.sb/ip",
    "https://ifconfig.co/ip",
    "https://ipv4.icanhazip.com",
    "http://www.trackip.net/ip",
    "https://ip-api.io/json",
  )

  //{
  //    "status": "success",
  //    "country": "越南",
  //    "countryCode": "VN",
  //    "region": "SG",
  //    "regionName": "胡志明市",
  //    "city": "胡志明市",
  //    "zip": "",
  //    "lat": 10.822,
  //    "lon": 106.6257,
  //    "timezone": "Asia/Ho_Chi_Minh",
  //    "isp": "VIETELftth",
  //    "org": "",
  //    "as": "AS7552 Viettel Group",
  //    "query": "115.78.12.119"
  //}
  suspend fun getNetIp(): IpBean {
    //备用地址 https://www.cloudflare.com/cdn-cgi/trace
    return RxHttp.get("http://ip-api.com/json?lang=zh-CN")
      .connectTimeout(TIME_OUT)
      .readTimeout(TIME_OUT)
      .writeTimeout(TIME_OUT)
      .setCacheMode(CacheMode.ONLY_NETWORK)
      .toAwaitResponseOther<IpBean>()
      .await()
  }

  //{
  //    "$id": "1",
  //    "currentDateTime": "2023-12-04T03:02Z",
  //    "utcOffset": "00:00:00",
  //    "isDayLightSavingsTime": false,
  //    "dayOfTheWeek": "Monday",
  //    "timeZoneName": "UTC",
  //    "currentFileTime": 133461325337280270,
  //    "ordinalDate": "2023-338",
  //    "serviceResponse": null
  //}
  suspend fun getSysTime1(): String {
    return RxHttp.get("http://worldclockapi.com/api/json/utc/now")
      .connectTimeout(TIME_OUT)
      .readTimeout(TIME_OUT)
      .writeTimeout(TIME_OUT)
      .setCacheMode(CacheMode.ONLY_NETWORK)
      .toAwaitResponseOther<SysTimeBean>()
      .map { it.currentDateTime ?: "" }
      .onErrorReturn { "" }
      .await()
  }

  //{
  //    "year": 2023,
  //    "month": 12,
  //    "day": 4,
  //    "hour": 3,
  //    "minute": 2,
  //    "seconds": 15,
  //    "milliSeconds": 835,
  //    "dateTime": "2023-12-04T03:02:15.835507",
  //    "date": "12/04/2023",
  //    "time": "03:02",
  //    "timeZone": "UTC",
  //    "dayOfWeek": "Monday",
  //    "dstActive": false
  //}
  suspend fun getSysTime2(): String {
    return RxHttp.get("https://timeapi.io/api/Time/current/zone?timeZone=UTC")
      .connectTimeout(TIME_OUT)
      .readTimeout(TIME_OUT)
      .writeTimeout(TIME_OUT)
      .setCacheMode(CacheMode.ONLY_NETWORK)
      .toAwaitResponseOther<SysTimeBean>()
      .map { it.dateTime ?: "" }
      .onErrorReturn { "" }
      .await()
  }
}
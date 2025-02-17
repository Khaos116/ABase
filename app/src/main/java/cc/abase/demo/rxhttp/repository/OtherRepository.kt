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
    "https://api.ip.sb/ip",//只返回ip
    "https://api.ipify.org",//只返回ip
    "https://api64.ipify.org/",//只返回ip
    "https://ipapi.co/ip",//只返回ip
    "https://ipapi.io/ip",//只返回ip
    "https://ifconfig.co/ip",//只返回ip
    "https://ifconfig.io/ip",//只返回ip
    "https://ipinfo.io/ip",//只返回ip
    "https://ipv4.icanhazip.com",//只返回ip
    "https://icanhazip.com",//只返回ip
    "https://checkip.amazonaws.com",//只返回ip
    "https://www.icanhazip.com",//只返回ip
    "https://www.trackip.net/ip",//只返回ip
    "https://ip.42.pl/raw",//只返回ip

    "https://ip-api.org/json",//返回Json包含国家、城市、ip【次数太多也会被限制】
    "https://ipinfo.io/json",//返回Json包含国家、城市、ip 【出现过无法访问的情况】
    "https://api.ip2location.io",//返回Json包含国家、城市、ip【每天限制500次】
    "https://freeipapi.com/api/json",//返回Json包含国家、城市、ip【国内可能无法访问】
    "https://api.wolfx.jp/geoip.php",//返回Json包含国家、城市、ip【可能没有城市】
    "https://am.i.mullvad.net/json",//返回Json包含国家、城市、ip【国内可能无法访问】
    "https://airvpn.org/api/whatismyip",//返回Json包含国家、城市、ip【国内可能无法访问】
    "https://ipwho.is",//返回Json包含国家、城市、ip
    "https://ip.nf/me.json",//返回Json包含国家、城市、ip【可能没有省份】
    "https://www.ip.cn/api/index?ip=&type=0",//返回Json包含国家、城市、ip
    "https://ip-api.io/json",//返回Json包含国家、ip
    "https://ifconfig.co/json",//返回Json包含国家、ip
    "https://api.myip.com",//返回Json包含国家、ip
    "https://api.ip.sb/geoip",//返回Json包含国家、ip
    "https://ifconfig.me/all.json",//返回Json只有ip有用
    "https://jsonip.com",//返回Json只有ip
    "https://httpbin.org/ip",//返回Json只有ip
    "http://ip-api.com/json",//HTTP->返回Json包含国家、城市、ip
    "http://ipv4.iplocation.net",//HTTP->返回Json只有ip有用
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
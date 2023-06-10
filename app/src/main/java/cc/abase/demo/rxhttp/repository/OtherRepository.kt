package cc.abase.demo.rxhttp.repository

import cc.abase.demo.bean.net.IpBean
import rxhttp.cc.RxHttp
import rxhttp.cc.toAwaitResponseOther
import rxhttp.wrapper.cache.CacheMode

/**
 * @Description
 * @Author：Khaos
 * @Date：2021-06-02
 * @Time：20:58
 */
object OtherRepository {
  //只获取外网IP的地址
  private var netIpUrls = mutableListOf(
    "http://bot.whatismyipaddress.com/",
    "http://checkip.amazonaws.com",
    "https://ipv4.icanhazip.com",
    "http://www.icanhazip.com",
    "http://icanhazip.com",
    "http://ipinfo.io/ip",
    "https://api.ip.sb/ip",
    "https://ifconfig.co/ip",
    "http://www.trackip.net/ip",
  )

  suspend fun getNetIp(): IpBean {
    //备用地址 https://www.cloudflare.com/cdn-cgi/trace
    return RxHttp.get("http://ip-api.com/json/?lang=zh-CN")
      .setCacheMode(CacheMode.ONLY_NETWORK)
      .toAwaitResponseOther<IpBean>()
      .await()
  }
}
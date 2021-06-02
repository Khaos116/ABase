package cc.abase.demo.rxhttp.repository

import cc.abase.demo.bean.net.IpBean
import rxhttp.RxHttp
import rxhttp.toResponseOther
import rxhttp.wrapper.cahce.CacheMode

/**
 * @Description
 * @Author：CASE
 * @Date：2021-06-02
 * @Time：20:58
 */
object OtherRepository {
  suspend fun getNetIp(): IpBean {
    return RxHttp.get("http://ip-api.com/json/?lang=zh-CN")
      .setCacheMode(CacheMode.ONLY_NETWORK)
      .toResponseOther<IpBean>()
      .await()
  }
}
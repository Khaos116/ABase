package cc.ab.base.utils

import cc.ab.base.ext.launchError
import cc.ab.base.ext.logI
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * @Description 获取响应速度最快的Host
 * @Author：CASE
 * @Date：2021-05-26
 * @Time：11:40
 */
object HostUtils {
  //获取响应最快的host
  fun getFastestHost(callBack: (host: String) -> Unit, vararg hosts: String) {
    GlobalScope.launchError { select<String> { for (host in hosts) async { pingHost(host) }.onAwait { it } }.let(callBack) }
  }

  //如果可以ping通则返回当前Host，否则抛出错误，防止拿到不可用的host
  @Throws(Exception::class)
  private suspend fun pingHost(host: String): String {
    return suspendCancellableCoroutine { con ->
      GlobalScope.launchError(Dispatchers.IO, handler = { _, e -> con.resumeWithException(e) }) {
        val time = System.currentTimeMillis()
        //val response = RxHttp.get("https://${host}").setCacheMode(CacheMode.ONLY_NETWORK).awaitOkResponse()
        //if (response.isSuccessful) {
        if (NetworkUtils.isAvailableByPing(host)) {
          "{$host} 耗时=${System.currentTimeMillis() - time}".logI()
          con.resume(host)
        } else con.resumeWithException(Exception("host is not available:$host"))
      }
    }
  }

  //测试方法
  fun test() {
    getFastestHost(
      { "【${Thread.currentThread().name}】最快的Host:$it".logI() },
      "www.baidu.com",
      "www.google.com",
      "www.youtube.com",
      "www.tmall.com",
      "www.jianshu.com",
      "www.jikedaohang.com",
    )
    //结果打印：
    //2021-05-26 13:06:03.597 27039-27168/com.kokvn.app2 I/YM-: {www.baidu.com} 耗时=93
    //2021-05-26 13:06:03.598 27039-27039/com.kokvn.app2 I/YM-: 【main】最快的Host:www.baidu.com
    //2021-05-26 13:06:03.600 27039-27418/com.kokvn.app2 I/YM-: {www.youtube.com} 耗时=91
    //2021-05-26 13:06:03.705 27039-27336/com.kokvn.app2 I/YM-: {www.google.com} 耗时=200
    //2021-05-26 13:06:03.731 27039-27218/com.kokvn.app2 I/YM-: {www.jikedaohang.com} 耗时=223
    //2021-05-26 13:06:03.802 27039-27172/com.kokvn.app2 I/YM-: {www.jianshu.com} 耗时=295
    //2021-05-26 13:06:04.401 27039-27165/com.kokvn.app2 I/YM-: {www.tmall.com} 耗时=895
  }
}
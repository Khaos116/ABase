package cc.abase.demo.startup

import android.content.Context
import cc.ab.base.config.PathConfig
import cc.ab.base.ext.*
import cc.abase.demo.rxhttp.repository.OtherRepository
import com.blankj.utilcode.util.NetworkUtils
import com.rousetime.android_startup.AndroidStartup
import com.rousetime.android_startup.Startup
import kotlinx.coroutines.GlobalScope

/**
 * 由于发现AndroidX的StartUp在三星手机上出现了不会走初始化的情况，所以改为了第三方的StartUp
 * Author:CASE https://github.com/idisfkj/android-startup/blob/master/README-ch.md
 * Date:2020/12/19
 * Time:15:03
 */
class AppLastInit : AndroidStartup<Int>() {
  //<editor-fold defaultstate="collapsed" desc="初始化线程问题">
  //create()方法调时所在的线程：如果callCreateOnMainThread返回true，则表示在主线程中初始化，会导致waitOnMainThread返回值失效；当返回false时，才会判断waitOnMainThread的返回值
  override fun callCreateOnMainThread(): Boolean = true

  //是否需要在主线程进行等待其完成:如果返回true，将在主线程等待，并且阻塞主线程
  override fun waitOnMainThread(): Boolean = true
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun create(context: Context): Int {
    try {
      PathConfig.initCacheDir()
    } catch (e: Exception) {
      e.logE()
    }
    //ApiErrorForat.realApiErrorImp = ApiErrorConfig
    if (NetworkUtils.isConnected()) getNetIp()
    //防止打开APP没有网络
    NetworkUtils.registerNetworkStatusChangedListener(object : NetworkUtils.OnNetworkStatusChangedListener {
      override fun onDisconnected() {}

      override fun onConnected(networkType: NetworkUtils.NetworkType?) = getNetIp()
    })
    "初始化完成".logI()
    return 0
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="依赖">
  override fun dependencies(): List<Class<out Startup<*>>> {
    return mutableListOf(DKPlayerInit::class.java)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取外网ip">
  private fun getNetIp() {
    GlobalScope.launchError {
      val ipBean = OtherRepository.getNetIp()
      "外网IP=${ipBean.query};地址=${ipBean.country}".logI()
    }
  }
  //</editor-fold>
}
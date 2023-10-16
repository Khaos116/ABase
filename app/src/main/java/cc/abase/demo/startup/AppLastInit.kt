package cc.abase.demo.startup

import android.content.Context
import android.os.Build
import android.webkit.WebView
import cc.ab.base.config.PathConfig
import cc.ab.base.ext.*
import cc.abase.demo.config.AppLiveData
import cc.abase.demo.rxhttp.repository.OtherRepository
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ProcessUtils
import com.rousetime.android_startup.AndroidStartup

/**
 * 由于发现AndroidX的StartUp在三星手机上出现了不会走初始化的情况，所以改为了第三方的StartUp
 * Author:Khaos https://github.com/idisfkj/android-startup/blob/master/README-ch.md
 * Date:2020/12/19
 * Time:15:03
 */
class AppLastInit : AndroidStartup<Int>() {
  //<editor-fold defaultstate="collapsed" desc="初始化线程问题">
  //create()方法调时所在的线程：如果callCreateOnMainThread返回true，则表示在主线程中初始化，会导致waitOnMainThread返回值失效；当返回false时，才会判断waitOnMainThread的返回值
  override fun callCreateOnMainThread(): Boolean = false

  //是否需要在主线程进行等待其完成:如果返回true，将在主线程等待，并且阻塞主线程
  override fun waitOnMainThread(): Boolean = true //为保证初始化完成后才使用，最后一个尽量返回true
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun create(context: Context): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) WebView.setDataDirectorySuffix(ProcessUtils.getCurrentProcessName())//Android 9.0及以上版本，多进程使用WebView会引发应用程序崩溃
    if (ProcessUtils.isMainProcess()) {
      //修复WebView导致的语言切换失效，要放到设置语言前
      try {
        WebView(context).destroy()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
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
  override fun dependenciesByName(): List<String> {
    return mutableListOf(DKPlayerInit::class.java.name)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取外网ip">
  private fun getNetIp() {
    if (AppLiveData.ipLiveData.value != null) return
    launchError {
      val ipBean = OtherRepository.getNetIp()
      AppLiveData.ipLiveData.postValue(ipBean)
      "外网IP=${ipBean.query};地址=${ipBean.country}".logI()
    }
  }
  //</editor-fold>
}
package cc.abase.demo.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.config.PathConfig
import cc.ab.base.ext.*
import cc.abase.demo.rxhttp.repository.OtherRepository
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.coroutines.GlobalScope

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:03
 */
class AppLastInit : Initializer<Int> {
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

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(DKPlayerInit::class.java)
  }

  //<editor-fold defaultstate="collapsed" desc="获取外网ip">
  private fun getNetIp() {
    GlobalScope.launchError {
      val ipBean = OtherRepository.getNetIp()
      "外网IP=${ipBean.query};地址=${ipBean.country}".logI()
    }
  }
  //</editor-fold>
}
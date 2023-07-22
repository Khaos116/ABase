package cc.abase.demo.startup

import android.content.Context
import cc.ab.base.ext.logI
import cc.abase.demo.rxhttp.config.RxHttpConfig
import com.rousetime.android_startup.AndroidStartup

/**
 * Author:Khaos
 * Date:2020/12/19
 * Time:15:57
 */
class RxHttpInit : AndroidStartup<Int>() {
  //<editor-fold defaultstate="collapsed" desc="初始化线程问题">
  //create()方法调时所在的线程：如果callCreateOnMainThread返回true，则表示在主线程中初始化，会导致waitOnMainThread返回值失效；当返回false时，才会判断waitOnMainThread的返回值
  override fun callCreateOnMainThread(): Boolean = false

  //是否需要在主线程进行等待其完成:如果返回true，将在主线程等待，并且阻塞主线程
  override fun waitOnMainThread(): Boolean = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun create(context: Context): Int {
    //初始化RxHttp
    RxHttpConfig.init()
    "初始化完成".logI()
    return 0
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="依赖">
  override fun dependenciesByName(): List<String> {
    return mutableListOf(PinyinInit::class.java.name)
  }
  //</editor-fold>
}
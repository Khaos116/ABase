package cc.abase.demo.app

import android.app.Application
import android.content.pm.PackageManager
import cc.abase.demo.BuildConfig
import com.blankj.utilcode.util.AppUtils
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader

/**
 * Description:
 * @author: Khaos
 * @date: 2019/10/8 10:02
 */
open class TestApplication : Application() {
  //是否需要走修改后的包信息(只能修改APP运行后的读取)
  var needChangePackageManager = false
  override fun getPackageManager(): PackageManager {
    //根据需要修改PackageManager信息获取
    return if (needChangePackageManager) MyPackageManager(super.getPackageManager()) else super.getPackageManager()
  }

  companion object {
    var networkFlipperPlugin: NetworkFlipperPlugin = NetworkFlipperPlugin()
  }

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this, false)
    if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
      val client = AndroidFlipperClient.getInstance(this)
      client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
      client.addPlugin(networkFlipperPlugin)
      client.start()
    }
  }

  override fun onTrimMemory(level: Int) {
    super.onTrimMemory(level)
    //https://paul.pub/android-process-recycle/#id-%E5%BC%80%E5%8F%91%E8%80%85%E7%9A%84%E5%86%85%E5%AD%98%E5%9B%9E%E6%94%B6
    when (level) {
      //应用处于Runnig状态可能收到的级别
      TRIM_MEMORY_RUNNING_MODERATE -> {} // 表示系统内存已经稍低
      TRIM_MEMORY_RUNNING_LOW -> {} //表示系统内存已经相当低
      TRIM_MEMORY_RUNNING_CRITICAL -> {} // 表示系统内存已经非常低，你的应用程序应当考虑释放部分资源
      //应用的可见性发生变化时收到的级别
      TRIM_MEMORY_UI_HIDDEN -> {} // 表示应用已经处于不可见状态，可以考虑释放一些与显示相关的资源
      //应用处于后台时可能收到的级别
      TRIM_MEMORY_BACKGROUND -> {} // 表示系统内存稍低，你的应用被杀的可能性不大。但可以考虑适当释放资源
      TRIM_MEMORY_MODERATE -> {} // 表示系统内存已经较低，当内存持续减少，你的应用可能会被杀死
      TRIM_MEMORY_COMPLETE -> { // 表示系统内存已经非常低，你的应用即将被杀死，请释放所有可能释放的资源
        AppUtils.exitApp()
      }
    }
  }
}
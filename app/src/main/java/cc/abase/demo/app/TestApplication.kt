package cc.abase.demo.app

import android.app.Application
import android.content.pm.PackageManager
import cc.abase.demo.BuildConfig
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
}
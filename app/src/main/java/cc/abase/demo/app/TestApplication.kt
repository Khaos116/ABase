package cc.abase.demo.app

import android.app.Application
import android.content.pm.PackageManager

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
}
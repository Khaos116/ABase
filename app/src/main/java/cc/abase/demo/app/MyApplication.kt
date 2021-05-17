package cc.abase.demo.app

import android.content.pm.PackageManager
import cc.ab.base.app.BaseApplication

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/8 10:02
 */
open class MyApplication : BaseApplication() {
  //是否需要走修改后的包信息(只能修改APP运行后的读取)
  var needChangePackageManager = false
  override fun getPackageManager(): PackageManager {
    //根据需要修改PackageManager信息获取
    return if (needChangePackageManager) MyPackageManager(super.getPackageManager()) else super.getPackageManager()
  }
}
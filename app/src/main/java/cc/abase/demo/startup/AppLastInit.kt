package cc.abase.demo.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:03
 */
class AppLastInit : Initializer<Int> {
  override fun create(context: Context): Int {
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(RxHttpInit::class.java)
  }
}
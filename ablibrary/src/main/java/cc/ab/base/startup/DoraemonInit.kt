package cc.ab.base.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import com.didichuxing.doraemonkit.DoraemonKit

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:47
 */
class DoraemonInit : Initializer<Int> {
  override fun create(context: Context): Int {
    DoraemonKit.install(context.applicationContext as Application)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(MmkvInit::class.java)
  }
}
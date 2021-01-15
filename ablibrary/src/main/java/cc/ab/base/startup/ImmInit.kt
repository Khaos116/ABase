package cc.ab.base.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import cc.ab.base.utils.IMMLeaks

/**
 * @Description 输入法内存泄漏
 * @Author：CASE
 * @Date：2021/1/15
 * @Time：16:45
 */
class ImmInit:Initializer<Int> {
  override fun create(context: Context): Int {
    IMMLeaks.fixFocusedViewLeak(context.applicationContext as Application)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf()
  }
}
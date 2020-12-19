package cc.abase.demo.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import cc.abase.demo.rxhttp.config.RxHttpConfig

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:57
 */
class RxHttpInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //初始化RxHttp
    RxHttpConfig.init()
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(PinyinInit::class.java)
  }
}
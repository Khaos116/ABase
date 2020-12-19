package cc.abase.demo.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import cc.abase.demo.utils.BuglyManager

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:54
 */
class BuglyInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //初始化Bugly
    BuglyManager.instance.initBugly(context.applicationContext as Application)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(EmojiInit::class.java)
  }
}
package cc.ab.base.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import me.jessyan.autosize.AutoSizeConfig

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:38
 */
class AutoSizeInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //字体sp不跟随系统大小变化
    AutoSizeConfig.getInstance().isExcludeFontScale = true
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf()
  }
}
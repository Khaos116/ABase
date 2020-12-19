package cc.ab.base.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import com.tencent.mmkv.MMKV

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:45
 */
class MmkvInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //MMKV也在所有进程中
    MMKV.initialize(context)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(UtilsInit::class.java)
  }
}
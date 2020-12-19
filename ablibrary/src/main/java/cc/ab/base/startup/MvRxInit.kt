package cc.ab.base.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import com.airbnb.mvrx.mock.MvRxMocks

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:41
 */
class MvRxInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //MvRx2.0起需要配置 https://github.com/airbnb/MvRx/wiki/Integrating-MvRx-In-Your-App
    MvRxMocks.install(context)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(SketchInit::class.java)
  }
}
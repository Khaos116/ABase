package cc.ab.base.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.Utils

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/8 9:32
 */
abstract class BaseApplication : Application() {
  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

  companion object {
    fun getApp(): BaseApplication {
      return Utils.getApp() as BaseApplication
    }
  }
}
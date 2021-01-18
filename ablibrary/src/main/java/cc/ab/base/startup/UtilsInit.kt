package cc.ab.base.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.config.PathConfig
import cc.ab.base.ext.logI
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.Utils

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:08
 */
class UtilsInit : Initializer<Int> {
    override fun create(context: Context): Int {
        Utils.init(context.applicationContext as Application)
        CrashUtils.init(PathConfig.CRASH_CACHE_DIR)
        "初始化完成".logI()
        return 0
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(TimberInit::class.java)
    }
}
package cc.ab.base.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.BuildConfig
import cc.ab.base.ext.logI
import timber.log.Timber

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:11
 */
class TimberInit : Initializer<Int> {
    override fun create(context: Context): Int {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        "初始化完成".logI()
        return 0
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(AutoSizeInit::class.java)
    }
}
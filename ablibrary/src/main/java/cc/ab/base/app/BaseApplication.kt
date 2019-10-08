package cc.ab.base.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import cc.ab.base.BuildConfig
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import com.didichuxing.doraemonkit.DoraemonKit
import com.tencent.mmkv.MMKV
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.autosize.AutoSizeConfig

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/8 9:32
 */
abstract class BaseApplication : Application() {
    //子进程中的初始化是否完成,有的必须要子进程中的初始化完成后才能调用
    var initFinishInChildThread = false

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        //优先初始化工具类
        Utils.init(this)
        //所有进程中需要的初始化
        initInAllProcess()
        //主进程中的初始化(一般都在这里初始化)
        if (ProcessUtils.isMainProcess()) {
            initInMainProcess()
        }
    }

    //主进程中的初始化
    @SuppressLint("CheckResult")
    private fun initInMainProcess() {
        LogUtils.getConfig()
            .setLogSwitch(BuildConfig.DEBUG)//log开关
            .setGlobalTag("BaseLib")
            .stackDeep = 3//log栈
        //主线程中的初始化(必要的放在这,不然APP打开会比较慢)
        initInMainThread()
        //子线程中的初始化(为了防止APP打开太慢,将不必要的放在子线程中初始化)
        Observable.create(ObservableOnSubscribe<Boolean> { emitter ->
            DoraemonKit.install(this)
            initInChildThread()
            emitter.onNext(true)
            emitter.onComplete()
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                initFinishInChildThread = true
            }, { Log.e("CASE", "$it") }, {})
        //字体sp不跟随系统大小变化
        AutoSizeConfig.getInstance().isExcludeFontScale = true
    }

    //所有进程中的初始化
    fun initInAllProcess() {
        //MMKV也在所有进程中
        MMKV.initialize(this)
    }

    //主线程中的初始化(只在主进程中调用)
    abstract fun initInMainThread()

    //子线程中的初始化(只在主进程中调用)
    abstract fun initInChildThread()

    companion object {
        fun getApp(): BaseApplication {
            return Utils.getApp() as BaseApplication
        }
    }
}
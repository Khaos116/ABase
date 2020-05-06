package cc.ab.base.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import cc.ab.base.BuildConfig
import cc.ab.base.utils.RxUtils
import cc.ab.base.widget.sketch.VideoThumbnailUriModel
import com.airbnb.mvrx.mock.MvRxMocks
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import com.didichuxing.doraemonkit.DoraemonKit
import com.tencent.mmkv.MMKV
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import me.jessyan.autosize.AutoSizeConfig
import me.panpf.sketch.Configuration
import me.panpf.sketch.Sketch


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
      if (BuildConfig.DEBUG) initDebugUtils()
      initInChildThread()
      emitter.onNext(true)
      emitter.onComplete()
    })
        .compose(RxUtils.instance.rx2SchedulerHelperO())
        .subscribe({
          initFinishInChildThread = true
        }, { LogUtils.e(it) }, {})
    //字体sp不跟随系统大小变化
    AutoSizeConfig.getInstance()
        .isExcludeFontScale = true
    //Sketch配置视频封面加载
    val configuration: Configuration = Sketch.with(this).configuration
    configuration.uriModelManager.add(VideoThumbnailUriModel())
    //MvRx2.0起需要配置 https://github.com/airbnb/MvRx/wiki/Integrating-MvRx-In-Your-App
    MvRxMocks.install(this)
  }

  //所有进程中的初始化
  fun initInAllProcess() {
    //MMKV也在所有进程中
    MMKV.initialize(this)
  }

  //初始化调试工具
  private fun initDebugUtils() {
    if (PermissionUtils.isGrantedDrawOverlays()) {
      DoraemonKit.install(this)//可能log会打印异常，但不影响使用
    }
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
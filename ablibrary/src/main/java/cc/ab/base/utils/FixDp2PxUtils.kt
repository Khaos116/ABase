package cc.ab.base.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.fragment.app.*

/**
 * Description:修复dp2px转换
 * @author: Khaos
 * @date: 2019/11/11 21:40
 */
object FixDp2PxUtils {
  private var hasFix = false

  //监听Activity生命周期
  fun fix(application: Application) {
    if (hasFix) return //只执行一次
    hasFix = true
    application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
      //在页面的onCreate方法super.onCreate(savedInstanceState)后调用
      // override fun onCreate(savedInstanceState: Bundle?) {
      //    super.onCreate(savedInstanceState)
      //    onActivityCreated 调用位置在这里
      //    ***这里是页面代码***
      // }
      override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        fixDp2Px(activity)
        if (activity is FragmentActivity) {
          //第二个参数true表示是对于子Fragment的FragmentManagers也注册回调
          activity.supportFragmentManager.registerFragmentLifecycleCallbacks(mLifeCallbacks, true)
        }
      }

      override fun onActivityStarted(activity: Activity) {
      }

      override fun onActivityResumed(activity: Activity) {
        fixDp2Px(activity)
      }

      override fun onActivityPaused(activity: Activity) {
      }

      override fun onActivityStopped(activity: Activity) {
      }

      override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
      }

      override fun onActivityDestroyed(activity: Activity) {
      }
    })
  }

  //监听fragment生命周期
  private val mLifeCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
      super.onFragmentCreated(fm, f, savedInstanceState)
      f.context?.let { c -> fixDp2Px(c) }
    }

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
      super.onFragmentViewCreated(fm, f, v, savedInstanceState)
      f.context?.let { c -> fixDp2Px(c) }
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
      super.onFragmentResumed(fm, f)
      f.context?.let { c -> fixDp2Px(c) }
    }
  }

  //修复dp转px
  private fun fixDp2Px(context: Context) {
    //获取参数
    val displayMetrics = context.applicationContext.resources.displayMetrics
    val width = kotlin.math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)
    val targetDensity: Float = width / 360f
    val appScaleDensity = displayMetrics.scaledDensity
    val appDensity = displayMetrics.density
    //计算目标值 density，scaleDensity，densityDpi
    val targetScaleDensity: Float = targetDensity * (appScaleDensity / appDensity)
    val targetDensityDpi = (targetDensity * 160).toInt()
    //替换Resources的density scaleDensity，densityDpi
    mutableListOf(displayMetrics, Resources.getSystem().displayMetrics, context.resources.displayMetrics).forEach { dm ->
      dm.density = targetDensity
      dm.scaledDensity = targetScaleDensity
      dm.densityDpi = targetDensityDpi
    }
  }
}
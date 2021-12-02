package cc.ab.base.utils

import android.app.Activity
import android.app.Application
import android.content.res.Resources
import kotlin.math.min

/**
 * ☆☆☆☆由于在第一个页面无法获取正确的值，所以最好不要直接在第一页面的变量直接使用SizeUtils.dp2px为变量赋值☆☆☆☆
 * ☆☆☆横屏时注意：TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,xx)可能存在转换问题，尽量使用TypedValue.COMPLEX_UNIT_PX☆☆☆
 * 部分手机在AutoSizeConfig适配后下面2个值会获取的不一样(第一个页面onCreate时在Application拿到正确的，Resources拿到是错误的)，
 * 导致SizeUtils.dp2px转换的值不一样，所以SizeUtils.dp2px不能直接使用，需要纠正使用
 * Resources.getSystem().displayMetrics.density
 * Application.resources.displayMetrics.density
 * Author:CASE
 * Date:2021/11/25
 * Time:14:43
 */
object FixResources {
  //测试发现在第一页面的OnCreate方法中都无法获取正常的值，所以为了保证拿到正确的值，在每个页面都执行以下
  @Synchronized fun fixInActivityOnCreate(activity: Activity) {
    //获取参数
    val displayMetrics = activity.applicationContext.resources.displayMetrics
    val width = min(displayMetrics.widthPixels, displayMetrics.heightPixels)
    val targetDensity: Float = width / 360f
    val appScaleDensity = displayMetrics.scaledDensity
    val appDensity = displayMetrics.density
    //计算目标值 density，scaleDensity，densityDpi
    val targetScaleDensity: Float = targetDensity * (appScaleDensity / appDensity)
    val targetDensityDpi = (targetDensity * 160).toInt()
    //替换Resources的density scaleDensity，densityDpi
    mutableListOf(displayMetrics, Resources.getSystem().displayMetrics, activity.resources.displayMetrics).forEach { dm ->
      dm.density = targetDensity
      dm.scaledDensity = targetScaleDensity
      dm.densityDpi = targetDensityDpi
    }
  }

  //尽量保证在application中也初始化一次
  @Synchronized fun fixInApplicationOnCreate(application: Application) {
    //获取参数
    val displayMetrics = application.resources.displayMetrics
    val width = min(displayMetrics.widthPixels, displayMetrics.heightPixels)
    val targetDensity: Float = width / 360f
    val appScaleDensity = displayMetrics.scaledDensity
    val appDensity = displayMetrics.density
    //计算目标值 density，scaleDensity，densityDpi
    val targetScaleDensity: Float = targetDensity * (appScaleDensity / appDensity)
    val targetDensityDpi = (targetDensity * 160).toInt()
    //替换Resources的density scaleDensity，densityDpi
    mutableListOf(displayMetrics, Resources.getSystem().displayMetrics).forEach { dm ->
      dm.density = targetDensity
      dm.scaledDensity = targetScaleDensity
      dm.densityDpi = targetDensityDpi
    }
  }
}
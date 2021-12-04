package cc.ab.base.utils

import android.app.Application
import android.content.Context
import android.content.res.Resources
import cc.ab.base.ext.dp2px
import cc.ab.base.ext.logI
import com.blankj.utilcode.util.SizeUtils
import me.jessyan.autosize.utils.AutoSizeUtils
import kotlin.math.min

/**
 * ☆☆☆☆由于在第一个页面无法获取正确的值，所以最好不要直接在第一页面的变量直接使用SizeUtils.dp2px为变量赋值☆☆☆☆
 * ☆☆☆横屏时注意：TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,xx)可能存在转换问题，尽量使用TypedValue.COMPLEX_UNIT_PX☆☆☆
 * 部分手机在AutoSizeConfig适配后下面2个值会获取的不一样(第一个页面onCreate时在Application拿到正确的，Resources拿到是错误的)，
 * 导致SizeUtils.dp2px转换的值不一样，所以SizeUtils.dp2px不能直接使用，需要纠正使用
 * Resources.getSystem().displayMetrics.density
 * Application.resources.displayMetrics.density
 * 注：需要测试一下变量直接采用dp2px拿到的值对不对(调用fixInApplicationOnCreate之后应该是对了的)
 * Author:CASE
 * Date:2021/11/25
 * Time:14:43
 */
object FixResources {
  //1.部分手机在OnCreate中都无法获取正常的值，所以在OnCreate中需要修复一下
  //2.横屏情况下，在setContentView之后会导致变化，所以在setContentView后再调用一次
  //3.横屏情况下，在View完成回执之后会导致变化，所以有需要的话，在绘制完成后可以调用一次
  @Synchronized fun fixDp2Px(context: Context) {
    //获取参数
    val displayMetrics = context.applicationContext.resources.displayMetrics
    val width = min(displayMetrics.widthPixels, displayMetrics.heightPixels)
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

  //测试转换
  fun testDp2px(context: Context) {
    val dp20 = 20.dp2px()
    val dp201 = SizeUtils.dp2px(20f)
    val dp202 = AutoSizeUtils.dp2px(context, 20f)
    val dp203 = AutoSizeUtils.dp2px(context.applicationContext, 20f)
    "Ext的dp20=$dp20,Resources的dp20=$dp201,Activity的dp20=$dp202,application的dp20=$dp203".logI()
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
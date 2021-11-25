package cc.ab.base.utils

import android.app.Application
import android.content.res.Resources

/**
 * 部分手机在AutoSizeConfig适配后下面2个值会获取的不一样(第一个页面onCreate时在Application拿到正确的，Resources拿到是错误的)，
 * 导致SizeUtils.dp2px转换的值不一样，所以SizeUtils.dp2px不能直接使用，需要纠正使用
 * Resources.getSystem().displayMetrics.density
 * Application.resources.displayMetrics.density
 * Author:CASE
 * Date:2021/11/25
 * Time:14:43
 */
object FixResources {
  //需要在第一个页面onCreate时调用(Application中测试还没获取到正常的值)
  fun fixResources(application: Application) {
    Resources.getSystem().displayMetrics.density = application.resources.displayMetrics.density
    Resources.getSystem().displayMetrics.densityDpi = application.resources.displayMetrics.densityDpi
    Resources.getSystem().displayMetrics.scaledDensity = application.resources.displayMetrics.scaledDensity
    Resources.getSystem().displayMetrics.xdpi = application.resources.displayMetrics.xdpi
    Resources.getSystem().displayMetrics.ydpi = application.resources.displayMetrics.ydpi
    Resources.getSystem().displayMetrics.heightPixels = application.resources.displayMetrics.heightPixels
    Resources.getSystem().displayMetrics.widthPixels = application.resources.displayMetrics.widthPixels
  }
}
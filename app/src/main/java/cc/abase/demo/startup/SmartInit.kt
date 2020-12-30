package cc.abase.demo.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import cc.ab.base.startup.DoraemonInit
import cc.abase.demo.component.gallery.GalleryActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.component.splash.GuideActivity
import cc.abase.demo.component.splash.SplashActivity
import cc.abase.demo.widget.CCRefreshHeader
import com.billy.android.swipe.SmartSwipeBack
import com.billy.android.swipe.SmartSwipeRefresh

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:52
 */
class SmartInit : Initializer<Int> {
  //不需要侧滑的页面
  private val list = listOf(
      SplashActivity::class.java.name,
      MainActivity::class.java.name,
      GuideActivity::class.java.name,
      LoginActivity::class.java.name,
      GalleryActivity::class.java.name,
      "com.didichuxing.doraemonkit.ui.UniversalActivity")

  override fun create(context: Context): Int {
    //仿手机QQ的手势滑动返回
    //SmartSwipeBack.activityStayBack(application, null);
    //仿微信带联动效果的透明侧滑返回
    //SmartSwipeBack.activitySlidingBack(application, null);
    //侧滑开门样式关闭activity
    //SmartSwipeBack.activityDoorBack(application, null);
    //侧滑百叶窗样式关闭activity
    //SmartSwipeBack.activityShuttersBack(application, null);
    //仿小米MIUI系统的贝塞尔曲线返回效果
    //SmartSwipeBack.activityBezierBack(application, null);
    SmartSwipeBack.activitySlidingBack(context.applicationContext as Application) { activity -> !list.contains(activity.javaClass.name) }
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(DoraemonInit::class.java)
  }

  //静态代码段可以防止内存泄露
  companion object {
    init {
      //设置全局的Header构建器
      SmartSwipeRefresh.setDefaultRefreshViewCreator(object :
          SmartSwipeRefresh.SmartSwipeRefreshViewCreator {
        override fun createRefreshHeader(context: Context): SmartSwipeRefresh.SmartSwipeRefreshHeader {
          return CCRefreshHeader(context)
        }

        override fun createRefreshFooter(context: Context): SmartSwipeRefresh.SmartSwipeRefreshFooter {
          return cc.abase.demo.widget.swipe.ClassicFooter(context)
        }
      })
    }
  }
}
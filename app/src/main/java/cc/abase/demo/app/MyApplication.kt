package cc.abase.demo.app

import android.content.Context
import cc.ab.base.app.BaseApplication
import cc.ab.base.net.http.FuelHelper
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.component.splash.GuideActivity
import cc.abase.demo.component.splash.SplashActivity
import cc.abase.demo.config.HeaderManger
import cc.abase.demo.config.ResponseManager
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.widget.CCRefreshHeader
import cc.abase.demo.widget.imgpreview.PreviewImgLoader
import com.billy.android.swipe.SmartSwipeBack
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.refresh.ClassicFooter
import com.billy.android.swipe.refresh.ClassicHeader
import com.previewlibrary.ZoomMediaLoader

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/8 10:02
 */
open class MyApplication : BaseApplication() {
  override fun initInMainThread() {
  }

  override fun initInChildThread() {
    //侧滑返回
    initSmartSwipeBack()
    //图片预览
    ZoomMediaLoader.getInstance()
      .init(PreviewImgLoader())
    //网络请求
    FuelHelper.initFuel(WanUrls.BASE,
        requestInterceptor = HeaderManger.instance.fuelHeader(),
        responseInterceptor= ResponseManager.instance.fuelResponse()
        )
  }
  //静态代码段可以防止内存泄露
  companion object {
    init {
      //设置全局的Header构建器
      SmartSwipeRefresh.setDefaultRefreshViewCreator(object : SmartSwipeRefresh.SmartSwipeRefreshViewCreator {
        override fun createRefreshHeader(context: Context?): SmartSwipeRefresh.SmartSwipeRefreshHeader {
          return if (context == null) ClassicHeader(context) else CCRefreshHeader(context)
        }

        override fun createRefreshFooter(context: Context?): SmartSwipeRefresh.SmartSwipeRefreshFooter {
          return ClassicFooter(context)
        }
      })
    }
  }

  //侧滑返回
  private fun initSmartSwipeBack() {
    /*
    //仿手机QQ的手势滑动返回
    SmartSwipeBack.activityStayBack(application, null);
    //仿微信带联动效果的透明侧滑返回
    SmartSwipeBack.activitySlidingBack(application, null);
    //侧滑开门样式关闭activity
    SmartSwipeBack.activityDoorBack(application, null);
    //侧滑百叶窗样式关闭activity
    SmartSwipeBack.activityShuttersBack(application, null);
    //仿小米MIUI系统的贝塞尔曲线返回效果
    SmartSwipeBack.activityBezierBack(application, null);
     */
    SmartSwipeBack.activitySlidingBack(
        this
    ) { activity -> !list.contains(activity.javaClass.name) }
  }

  //不需要侧滑的页面
  private val list = listOf(
      SplashActivity::class.java.name,
      MainActivity::class.java.name,
      GuideActivity::class.java.name,
      LoginActivity::class.java.name
  )
}
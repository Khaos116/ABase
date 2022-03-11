package cc.abase.demo.startup

import android.app.Application
import android.content.Context
import android.graphics.Color
import cc.ab.base.ext.logI
import cc.ab.base.startup.MmkvInit
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.component.splash.GuideActivity
import cc.abase.demo.component.splash.SplashActivity
import cc.abase.demo.widget.smart.MidaMusicHeader
import com.billy.android.swipe.SmartSwipeBack
import com.rousetime.android_startup.AndroidStartup
import com.rousetime.android_startup.Startup
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Author:Khaos
 * Date:2020/12/19
 * Time:15:52
 */
class SmartInit : AndroidStartup<Int>() {
  //<editor-fold defaultstate="collapsed" desc="配置">
  //静态代码段可以防止内存泄露
  companion object {
    init {
      //设置全局默认配置（优先级最低，会被其他设置覆盖）
      SmartRefreshLayout.setDefaultRefreshInitializer { _, layout -> //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
        layout.setPrimaryColors(Color.parseColor("#f3f3f3")) //header和footer的背景色(多传一个参数代表文字的颜色)
        layout.setEnableLoadMoreWhenContentNotFull(false) //是否在列表不满一页时候开启上拉加载功能
        layout.setEnableScrollContentWhenLoaded(true) //true时数据在加载更多位置,加载更多隐藏；false时加载成功整体下移，手动上滑显示更多内容
        layout.setEnableRefresh(false) //默认不可下拉
        layout.setEnableLoadMore(false) //默认不可上拉
        layout.setNoMoreData(true) //默认没有更多数据
        layout.setEnableFooterFollowWhenNoMoreData(true) //是否在全部加载结束之后Footer跟随内容
      }
      //设置全局的Header构建器
      SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> MidaMusicHeader(context) }
      //设置全局的Footer构建器
      SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> cc.abase.demo.widget.smart.ClassicsFooter(context) }
    }
  }

  //不需要侧滑的页面
  private val list = listOf(
    SplashActivity::class.java.name,
    MainActivity::class.java.name,
    GuideActivity::class.java.name,
    LoginActivity::class.java.name,
  )
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化线程问题">
  //create()方法调时所在的线程：如果callCreateOnMainThread返回true，则表示在主线程中初始化，会导致waitOnMainThread返回值失效；当返回false时，才会判断waitOnMainThread的返回值
  override fun callCreateOnMainThread(): Boolean = false

  //是否需要在主线程进行等待其完成:如果返回true，将在主线程等待，并且阻塞主线程
  override fun waitOnMainThread(): Boolean = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="依赖">
  override fun dependenciesByName(): List<String> {
    return mutableListOf(MmkvInit::class.java.name)
  }
  //</editor-fold>
}
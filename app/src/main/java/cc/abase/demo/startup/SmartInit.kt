package cc.abase.demo.startup

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import cc.ab.base.startup.DoraemonInit
import cc.abase.demo.component.gallery.GalleryActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.component.splash.GuideActivity
import cc.abase.demo.component.splash.SplashActivity
import cc.abase.demo.widget.CCRefreshHeader
import cc.abase.demo.widget.smart.MidaMusicHeader
import com.billy.android.swipe.SmartSwipeBack
import com.billy.android.swipe.SmartSwipeRefresh
import com.scwang.smart.refresh.layout.SmartRefreshLayout

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
      //设置全局默认配置（优先级最低，会被其他设置覆盖）
      SmartRefreshLayout.setDefaultRefreshInitializer { _, layout -> //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
        layout.setPrimaryColors(Color.parseColor("#f3f3f3")) //header和footer的背景色(多传一个参数代表文字的颜色)
        layout.setEnableLoadMoreWhenContentNotFull(false) //是否在列表不满一页时候开启上拉加载功能
        layout.setEnableScrollContentWhenLoaded(true) //是否在加载完成时滚动列表显示新的内容(false相当于加载更多是一个item)
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
}
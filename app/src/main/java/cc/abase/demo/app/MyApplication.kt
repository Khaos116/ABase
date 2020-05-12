package cc.abase.demo.app

import android.content.Context
import cc.ab.base.app.BaseApplication
import cc.abase.demo.component.gallery.GalleryActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.component.splash.GuideActivity
import cc.abase.demo.component.splash.SplashActivity
import cc.abase.demo.fuel.config.FuelConfig
import cc.abase.demo.rxhttp.config.RxHttpConfig
import cc.abase.demo.utils.BuglyManager
import cc.abase.demo.widget.CCRefreshHeader
import cc.abase.demo.widget.video.ExoVideoCacheUtils
import com.billy.android.swipe.SmartSwipeBack
import com.billy.android.swipe.SmartSwipeRefresh
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory
import com.dueeeke.videoplayer.player.*
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider

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
    //表情
    EmojiManager.install(IosEmojiProvider())
    //视频播放全局配置
    VideoViewManager.setConfig(
        VideoViewConfig.newBuilder()
            //使用ExoPlayer解码
            .setPlayerFactory(ExoMediaPlayerFactory.create())
            .setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT)
            .build()
    )
    //初始化Bugly
    BuglyManager.instance.initBugly(this)
    // 添加中文城市词典
    Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this)))
    //清理没有缓存完成的视频
    ExoVideoCacheUtils.instance.openAappClearNoCacheComplete()
    //初始化Fuel
    FuelConfig.instance.init()
    //初始化RxHttp
    RxHttpConfig.instance.init()
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
      LoginActivity::class.java.name,
      GalleryActivity::class.java.name,
      "com.didichuxing.doraemonkit.ui.UniversalActivity"
                           )
}
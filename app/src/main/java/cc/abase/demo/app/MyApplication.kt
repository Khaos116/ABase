package cc.abase.demo.app

import android.content.Context
import cc.ab.base.app.BaseApplication
import cc.ab.base.net.http.FuelHelper
import cc.ab.base.utils.CharlesUtils
import cc.abase.demo.BuildConfig
import cc.abase.demo.component.gallery.GalleryActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.component.splash.GuideActivity
import cc.abase.demo.component.splash.SplashActivity
import cc.abase.demo.config.HeaderManger
import cc.abase.demo.config.ResponseManager
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.utils.BuglyManager
import cc.abase.demo.widget.CCRefreshHeader
import cc.abase.demo.widget.video.ExoVideoCacheUtils
import com.billy.android.swipe.SmartSwipeBack
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.refresh.ClassicFooter
import com.billy.android.swipe.refresh.ClassicHeader
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory
import com.dueeeke.videoplayer.player.*
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider
import io.microshow.rxffmpeg.RxFFmpegInvoke
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import rxhttp.wrapper.param.Param
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.ssl.SSLSocketFactoryImpl
import rxhttp.wrapper.ssl.X509TrustManagerImpl
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

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
    //网络请求
    FuelHelper.initFuel(
        WanUrls.BASE,
        requestInterceptor = HeaderManger.instance.fuelHeader(),
        responseInterceptor = ResponseManager.instance.fuelResponse()
    )
    //表情
    EmojiManager.install(IosEmojiProvider())
    //RxFFmpeg
    RxFFmpegInvoke.getInstance()
        .setDebug(true)
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
    //初始化RxHttp
    initRxHttp()
  }

  //静态代码段可以防止内存泄露
  companion object {
    init {
      //设置全局的Header构建器
      SmartSwipeRefresh.setDefaultRefreshViewCreator(object :
          SmartSwipeRefresh.SmartSwipeRefreshViewCreator {
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
      LoginActivity::class.java.name,
      GalleryActivity::class.java.name,
      "com.didichuxing.doraemonkit.ui.UniversalActivity"
  )

  //初始化RxHttp https://github.com/liujingxing/okhttp-RxHttp/wiki/%E5%88%9D%E5%A7%8B%E5%8C%96
  private fun initRxHttp() {
    //设置debug模式，默认为false，设置为true后，发请求，过滤"RxHttp"能看到请求日志
    RxHttp.setDebug(BuildConfig.DEBUG)
    //非必须,只能初始化一次，第二次将抛出异常
    RxHttp.init(getDefaultOkHttpClient())
    //添加公共参数 https://github.com/liujingxing/okhttp-RxHttp/blob/486c7bc9e4554b4604f29c726e3e58714e2de6ee/app/src/main/java/com/example/httpsender/RxHttpManager.java
    RxHttp.setOnParamAssembly { p: Param<*> ->
      p.addAll(HeaderManger.instance.getStaticHeaders())//添加公共参数
      HeaderManger.instance.getTokenPair()
          ?.let { p.addHeader(it.first, it.second) /*添加公共请求头*/ }
    }
  }

  //OkHttpClient
  private fun getDefaultOkHttpClient(): OkHttpClient {
    val trustAllCert: X509TrustManager = X509TrustManagerImpl()
    val sslSocketFactory: SSLSocketFactory = SSLSocketFactoryImpl(trustAllCert)
    val builder = Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .sslSocketFactory(sslSocketFactory, trustAllCert) //添加信任证书
        .hostnameVerifier(
            HostnameVerifier { hostname: String?, session: SSLSession? -> true }
        ) //忽略host验证
    val util = CharlesUtils.getInstance()
    util.setOkHttpCharlesSSL(builder, util.getCharlesInputStream("charles.pem"))
    return builder.build()
  }
}
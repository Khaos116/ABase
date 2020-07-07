# ABase(采用AndroidX)
##### 准备实践框架
使用了[干货集中营开源API](http://gank.io/api)(2020年发现图片无法访问了)和[玩Android开源API](https://www.wanandroid.com/blog/show/2)  
Build Scan分析情况:[https://scans.gradle.com/s/4fbw3ftizzorg](https://scans.gradle.com/s/4fbw3ftizzorg)
  
三方库|描述
:-:|:-:
**[MvRx](https://github.com/airbnb/MvRx)**|[真响应式架构](https://www.jianshu.com/p/53240a44ec49)
**[epoxy](https://github.com/airbnb/epoxy)**|[积木堆的方式加载RecyclerView](https://www.jianshu.com/p/d62ade6077c9)
**[ImmersionBar](https://github.com/gyf-dev/ImmersionBar)**|状态栏和虚拟导航栏、全面屏+刘海屏适配、键盘监听
**[AndroidAutoSize](https://github.com/JessYanCoding/AndroidAutoSize)**|今日头条UI适配方案
**[SmartSwipe](https://qibilly.com/SmartSwipe-tutorial/)**|仿微信、小米侧滑返回、替代SmartRefresh下拉刷新
**[lottie-android](https://github.com/airbnb/lottie-android)**|AE动画库
**[sketch](https://github.com/panpf/sketch)**|图片加载(兼容圆角、圆形、高斯模糊、图片按压效果，图片边框)
**[AgentWeb](https://github.com/Justson/AgentWeb)**|简化WebView加载
**[RxHttp](https://github.com/liujingxing/okhttp-RxHttp)**|新一代OkHttp请求神器
**[RxCache](https://github.com/VictorAlbertos/RxCache)**|接口数据缓存方案，自定义缓存时长
**[MMKV](https://github.com/Tencent/MMKV)**|替代SharedPreferences的更高效方案
**[leakcanary](https://github.com/square/leakcanary/releases)**|内存泄漏检测工具
**[pandora](https://github.com/whataa/pandora/blob/master/README_CN.md)**|潘多拉开发辅助
**[DoraemonKit](https://github.com/didi/DoraemonKit/blob/master/Doc/android_cn_guide.md)**|哆啦A梦开发辅助
**[utilcode](https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md)**|超强工具合集
**[RxAndroid](https://github.com/ReactiveX/RxAndroid)**|Android线程切换+Rxjava
**[LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)**|跨进程通信、跨APP通信、自动取消订阅
**[DiscreteScrollView](https://github.com/yarolegovich/DiscreteScrollView)**|实现无限轮播Banner、ViewPager横竖屏滑动等
**[Emoji](https://github.com/vanniktech/Emoji)**|聊天Emoji表情选择面板
  
================================================================     
##  Banner效果图  
![横向Banner](https://github.com/caiyoufei/ABase/blob/master/image/horizontal_banner.gif)![纵向Banner](https://github.com/caiyoufei/ABase/blob/master/image/vertical_banner.gif)  

================================================================    
##  Banner代码配置
~~~
val banner: DiscreteBanner<BannerBean> = itemView.findViewById(R.id.itemBanner)
banner.setOrientation(if (vertical) DSVOrientation.VERTICAL else DSVOrientation.HORIZONTAL)
    .setLooper(true)//无限循环
    .setAutoPlay(true)//自动播放
    .setOnItemClick { _, t ->  }//banner点击
    .also {
      if (!vertical) {//由于默认是横向原点居底部(引导页使用)，所以banner处修改为底部居右
        it.setIndicatorGravity(Gravity.BOTTOM or Gravity.END)
        it.setIndicatorOffsetY(-it.defaultOffset / 2f)
        it.setIndicatorOffsetX(-it.defaultOffset)
      }
    }
    .setPages(object : DiscreteHolderCreator {
      override fun createHolder(view: View) = HomeBannerHolderView(view)
      override fun getLayoutId() = R.layout.item_banner_child
    }, data)//BannerBean的数据列表MutableList<BannerBean>
    
//需要自定义ViewHolder  
class HomeBannerHolderView(view: View?) : DiscreteHolder<BannerBean>(view) {
  private var imageView: SketchImageView? = null
  override fun updateUI(
    data: BannerBean,
    position: Int,
    count: Int
  ) {
    imageView?.load(data.imagePath)
  }

  override fun initView(view: View) {
    this.imageView = view.itemBannerIV
  }
}
~~~

================================================================
##  所有效果
![所有效果](https://github.com/caiyoufei/ABase/blob/master/image/all_effect.jpg)

================================================================
##  聊天Emoji效果图
![聊天Emoji](https://github.com/caiyoufei/ABase/blob/master/image/emoji_chat.gif)

================================================================
##  FFmpeg获取视频封面和压缩视频
![封面和压缩](https://github.com/caiyoufei/ABase/blob/master/image/video_cover_compress.gif)

================================================================
##  控件高斯模糊效果
![高斯模糊](https://github.com/caiyoufei/ABase/blob/master/image/blur.png)

================================================================
##  Epoxy吸顶效果
![吸顶效果](https://github.com/caiyoufei/ABase/blob/master/image/sticky.gif)![吸顶效果](https://github.com/caiyoufei/ABase/blob/master/image/sticky2.gif)

================================================================
##  学生成绩单滑动吸顶效果
![成绩单吸顶效果](https://github.com/caiyoufei/ABase/blob/master/image/sticky_score.gif)

================================================================
##  @或#效果
![高斯模糊](https://github.com/caiyoufei/ABase/blob/master/image/at_topic.gif)

================================================================
##  Coordinator嵌套刷新
![Coordinator嵌套刷新](https://github.com/caiyoufei/ABase/blob/master/image/coordinator_refresh.gif)

================================================================
##  Epoxy展开和收缩效果
![Epoxy展开和收缩效果](https://github.com/caiyoufei/ABase/blob/master/image/epoxy_expand.gif)

================================================================
##  Epoxy列表视频播放
![列表视频播放](https://github.com/caiyoufei/ABase/blob/master/image/video_list_play.gif)

================================================================
##  VerticalViewPager抖音播放效果
![抖音播放效果](https://github.com/caiyoufei/ABase/blob/master/image/play_pager.gif)
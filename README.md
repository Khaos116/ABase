# ABase(采用AndroidX)
### <table><tr><td bgcolor=#FF00FF>使用了[干货集中营开源API](http://gank.io/api)+[玩Android开源API](https://www.wanandroid.com/blog/show/2)+[ReadHub新闻API](https://readhub.cn/topics)</td></tr></table>
### <table><tr><td bgcolor=#FF0000>2021年11月9日发现干货集中营暂时无法访问</td></tr></table>
### <table><tr><td bgcolor=#FF0000>2022年3月8日第二个页面暂时替换为ReadHub新闻</td></tr></table>
可以使用[Build Scan](https://scans.gradle.com/)(Terminal下执行gradlew build --scan)分析[构建情况](https://scans.gradle.com/s/htlaaoofzhtz6)(相关配置在settings.gradle中)
注：测试发现多渠道Build Scan貌似无法成功，可以关闭多渠道到进行Build Scan

三方库|描述  
:---------------------------:|:---------------------------:
**[Timber](https://github.com/JakeWharton/timber)**|JakeWharton大神的Log打印工具
**[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md)**|超强工具合集
**[AndroidAutoSize](https://github.com/JessYanCoding/AndroidAutoSize)**|今日头条UI适配方案
**[ImmersionBar](https://github.com/gyf-dev/ImmersionBar)**|状态栏和虚拟导航栏、全面屏+刘海屏适配、键盘监听
**[MMKV](https://github.com/Tencent/MMKV)**|腾讯推出的替代SharedPreferences的更高效方案
**[Coroutines](https://github.com/Kotlin/kotlinx.coroutines)**|Kotlin协程,替代Rxjava
**[OkhttpLog](https://github.com/Ayvytr/OKHttpLogInterceptor)**|美化OKhttp请求和响应打印日志
**[RxHttp](https://github.com/liujingxing/okhttp-RxHttp)**|新一代OkHttp请求神器,自带缓存策略,兼容Kotlin协程
**[RxAndroid](https://github.com/ReactiveX/RxAndroid)**|Android链式调用Rxjava
**[Coil](https://github.com/coil-kt/coil)**|Kotlin版本的图片加载(内置高斯模糊和黑白化功能)
**[MultiType](https://github.com/drakeet/MultiType)**|多类型适配器与Epoxy类似(积木搭建式)
**[XXpermissions](https://github.com/getActivity/XXPermissions)**|兼容最新安卓11的权限请求框架
**[LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)**|跨进程通信、跨APP通信、自动取消订阅
**[AgentWeb](https://github.com/Justson/AgentWeb)**|简化WebView加载
**[Itemanimators](https://github.com/mikepenz/ItemAnimators)**|RecyclerView动画局部刷新动画
**[MagicIndicator](https://github.com/hackware1993/MagicIndicator/tree/androidx)**|样式齐全的ViewPager指示器
**[SmartSwipe](https://github.com/luckybilly/SmartSwipe)**|仿微信、小米侧滑返回
**[SmartRefresh](https://github.com/scwang90/SmartRefreshLayout)**|下拉刷新，兼容性和效果比SmartSwipe好
**[CacheEmulatorChecker](https://github.com/happylishang/CacheEmulatorChecker)**|模拟器检测工具
**[Leakcanary](https://github.com/square/leakcanary/releases)**|内存泄漏检测工具
**[Uinspector](https://github.com/YvesCheung/UInspector)**|取代LayoutInspector的UI调试工具(仅限Debug模式)
**[PictureSelector](https://github.com/LuckSiege/PictureSelector)**|强悍的图片选择，大图预览工具
**[Emoji](https://github.com/vanniktech/Emoji)**|聊天Emoji表情选择面板
**[SideBar](https://github.com/D10NGYANG/DL10SideBar)**|通讯录侧边栏SideBar
**[FlexboxLayout](https://github.com/google/flexbox-layout)**|谷歌推出的标签展示控件(Recycler流式布局)
**[DKPlayer](https://github.com/Doikki/DKVideoPlayer/wiki)**|UI封装比较美观的播放器
**[Lottie](https://github.com/airbnb/lottie-android)**|AE动画库
**[TinyPinyin](https://github.com/promeG/TinyPinyin)**|中文转拼音工具
**[DatePickerView](https://github.com/limxing/DatePickerView)**|基于RecyclerView的滚轮选择器(Koltin编写)
**[SpEditTool](https://github.com/sunhapper/SpEditTool)**|微博@和#变色效果
**[DiscreteScrollView](https://github.com/yarolegovich/DiscreteScrollView)**|实现无限轮播Banner、ViewPager横竖屏滑动等
**[Walle](https://github.com/Meituan-Dianping/walle)**|美团瓦力打包([Bat批量打包](https://github.com/khaos116/ABase/tree/master/bat_channel)+[说明](https://www.jianshu.com/p/d41dad812048))
===============================|===============================

//================================================================//
##  Banner效果图  
![横向Banner](https://github.com/khaos116/ABase/blob/master/image/horizontal_banner.gif)![纵向Banner](https://github.com/khaos116/ABase/blob/master/image/vertical_banner.gif)  

//================================================================//
##  BannerItem代码配置
~~~
override fun fillData(holder: BaseViewHolder<ItemBannerBinding>, item: MutableList<BannerBean>) {
  if (holder.itemView.getTag(R.id.tag_banner) != item) {
    holder.itemView.setTag(R.id.tag_banner, item)
    val banner = holder.itemView.findViewById<DiscreteBanner<BannerBean, ItemBannerImgBinding>>(R.id.itemBanner)
    banner.layoutParams.height = (ScreenUtils.getScreenWidth() * 500f / 900).toInt()
    banner.setLooper(true) //无限循环
      .setAutoPlay(true) //自动播放
      .setOrientation(if (System.currentTimeMillis() % 2 == 0L) DSVOrientation.HORIZONTAL else DSVOrientation.VERTICAL)
      .setOnItemClick { position, t -> onItemBannerClick?.invoke(t, position) } //banner点击
      .apply {
        getIndicator()?.needSpecial = false //去除引导页的特殊指示器
        if (getOrientation() == DSVOrientation.HORIZONTAL.ordinal) { //由于默认是横向原点居底部(引导页使用)，所以banner处修改为底部居右
          setIndicatorGravity(Gravity.BOTTOM or Gravity.END)
          setIndicatorOffsetY(-defaultOffset / 2f)
          setIndicatorOffsetX(-defaultOffset)
        }
      }
      .setPages(object : DiscretePageAdapter<BannerBean, ItemBannerImgBinding>(item) {
        override fun fillData(data: BannerBean, binding: ItemBannerImgBinding, position: Int, count: Int) {
          binding.itemBannerImg.loadImgHorizontal(data.imagePath, 900f / 500)
        }
      }, item) //BannerBean的数据列表MutableList<BannerBean>
  }
}
~~~

//================================================================//
##  所有效果
![所有效果](https://github.com/khaos116/ABase/blob/master/image/all_effect.png)

//================================================================//
##  非均分Grid分割线效果
![分割线效果](https://github.com/khaos116/ABase/blob/master/image/decoration.png)

//================================================================//
##  聊天Emoji效果图
![聊天Emoji](https://github.com/khaos116/ABase/blob/master/image/emoji_chat.gif)

//================================================================//
##  Coil加载视频封面和压缩视频
![封面和压缩](https://github.com/khaos116/ABase/blob/master/image/video_cover_compress.gif)

//================================================================//
##  控件高斯模糊效果
![高斯模糊](https://github.com/khaos116/ABase/blob/master/image/blur.png)

//================================================================//
##  Sticky吸顶效果
![吸顶效果](https://github.com/khaos116/ABase/blob/master/image/sticky.gif)![吸顶效果](https://github.com/khaos116/ABase/blob/master/image/sticky2.gif)

//================================================================//
##  学生成绩单滑动吸顶效果
![成绩单吸顶效果](https://github.com/khaos116/ABase/blob/master/image/sticky_score.gif)

//================================================================//
##  @或#效果
![高斯模糊](https://github.com/khaos116/ABase/blob/master/image/at_topic.gif)

//================================================================//
##  Coordinator嵌套刷新
![Coordinator嵌套刷新](https://github.com/khaos116/ABase/blob/master/image/coordinator_refresh.gif)

//================================================================//
##  MultiType展开和收缩效果
![MultiType展开和收缩效果](https://github.com/khaos116/ABase/blob/master/image/epoxy_expand.gif)

//================================================================//
##  MultiType列表视频播放
![列表视频播放](https://github.com/khaos116/ABase/blob/master/image/video_list_play.gif)

//================================================================//
##  VerticalViewPager抖音播放效果
![抖音播放效果](https://github.com/khaos116/ABase/blob/master/image/play_pager.gif)
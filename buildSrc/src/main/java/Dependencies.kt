object Versions {
  internal const val sdkMin = 23
  internal const val sdkTarget = 30
  internal const val kotlin = "1.4.21"
  internal const val okHttp = "4.9.0"
  internal const val rxHttp = "2.5.3"
  internal const val doraemonkit = "3.3.5"
  internal const val coil = "1.1.1"
}

object Deps {
  //根目录gradle https://maven.aliyun.com/mvn/search
  const val plugin_android_gradle = "com.android.tools.build:gradle:3.5.4"
  const val plugin_kotlin_gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
  const val plugin_r8_gradle = "com.android.tools:r8:2.1.67" //更新R8版本，解决正式版无法打包的问题 https://github.com/square/okhttp/issues/4604
  const val plugin_and_res = "com.tencent.mm:AndResGuard-gradle-plugin:1.2.19" //https://github.com/shwenzhang/AndResGuard/blob/master/README.zh-cn.md
  const val plugin_bugly = "com.tencent.bugly:symtabfileuploader:2.2.1" //bugly的mapping上传

  //使用kotlin
  const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"

  //系统相关 https://maven.aliyun.com/mvn/search
  const val core_ktx = "androidx.core:core-ktx:1.5.0-alpha05"
  const val activity_ktx = "androidx.activity:activity-ktx:1.2.0-rc01"
  const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha02"
  const val fragment = "androidx.fragment:fragment:1.3.0-rc01"
  const val material = "com.google.android.material:material:1.3.0-beta01"
  const val constraint = "androidx.constraintlayout:constraintlayout:2.1.0-alpha2"
  const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:2.3.0-rc01"

  //启动初始化 https://developer.android.google.cn/topic/libraries/app-startup
  const val startup = "androidx.startup:startup-runtime:1.0.0"

  //log打印 https://github.com/JakeWharton/timber
  const val timber = "com.jakewharton.timber:timber:4.7.1"

  //分包 https://developer.android.google.cn/studio/build/multidex?hl=zh_cn#mdex-gradle
  const val multidex = "androidx.multidex:multidex:2.0.1"

  //内存泄漏检测 https://square.github.io/leakcanary/getting_started/
  const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.6"

  //工具类 https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md
  const val utilcodex = "com.blankj:utilcodex:1.30.5"

  //UI适配 https://github.com/JessYanCoding/AndroidAutoSize
  const val autosize = "me.jessyan:autosize:1.2.1"

  //状态栏适配 https://github.com/gyf-dev/ImmersionBar
  const val immersionbar = "com.gyf.immersionbar:immersionbar:3.0.0"
  const val immersionbar_ktx = "com.gyf.immersionbar:immersionbar-ktx:3.0.0"

  //数据存储 https://github.com/Tencent/MMKV
  const val mmkv = "com.tencent:mmkv-static:1.2.7"

  //数据解析 https://github.com/google/gson
  const val gson = "com.google.code.gson:gson:2.8.6"

  //日志json打印 https://github.com/Ayvytr/OKHttpLogInterceptor
  const val okhttp_log = "com.ayvytr:okhttploginterceptor:3.0.4"

  //协程 https://github.com/Kotlin/kotlinx.coroutines
  const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
  const val coroutine_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2"

  //网络请求 https://github.com/square/okhttp
  const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"

  //RxHttp https://github.com/liujingxing/okhttp-RxHttp
  const val rxhttp = "com.ljx.rxhttp:rxhttp:${Versions.rxHttp}"
  const val rxhttp_kapt = "com.ljx.rxhttp:rxhttp-compiler:${Versions.rxHttp}"
  const val rxlife_coroutine = "com.ljx.rxlife:rxlife-coroutine:2.0.1" //管理协程生命周期，页面销毁，关闭请求
  const val rxlife_rxjava = "com.ljx.rxlife2:rxlife-rxjava:2.0.0" //管理RxJava2生命周期，页面销毁，关闭请求

  //RxAndroid https://github.com/ReactiveX/RxAndroid
  const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.1.1"

  //EventBus https://github.com/JeremyLiao/LiveEventBus
  const val eventBus = "com.jeremyliao:live-event-bus-x:1.7.3"

  //WebView https://github.com/Justson/AgentWeb
  const val agentweb = "com.just.agentweb:agentweb:4.1.4"

  //侧滑 https://github.com/luckybilly/SmartSwipe
  const val swipe = "com.billy.android:smart-swipe:1.1.2"
  const val swipex = "com.billy.android:smart-swipe-x:1.1.0"

  //下拉刷新 https://github.com/scwang90/SmartRefreshLayout
  const val smart_refresh = "com.scwang.smart:refresh-layout-kernel:2.0.3"
  const val smart_header = "com.scwang.smart:refresh-header-classics:2.0.3"
  const val smart_footer = "com.scwang.smart:refresh-footer-classics:2.0.3"

  //图片选择器 https://github.com/LuckSiege/PictureSelector
  const val pic_select = "com.github.LuckSiege.PictureSelector:picture_library:v2.6.0"

  //https://github.com/trello/RxLifecycle   更新后RxUtils存在问题
  //api 'com.trello.rxlifecycle4:rxlifecycle-android-lifecycle:4.0.0'
  const val rxlife_android = "com.trello.rxlifecycle3:rxlifecycle-android-lifecycle:3.1.0"

  //https://github.com/airbnb/lottie-android
  const val lottie = "com.airbnb.android:lottie:3.6.0"

  //多类型适配器 https://github.com/drakeet/MultiType
  const val multitype = "com.drakeet.multitype:multitype:4.2.0"

  //辅助工具 https://github.com/didi/DoraemonKit/blob/master/Doc/android_cn_guide.md
  const val doraemonkit = "com.didichuxing.doraemonkit:dokitx:${Versions.doraemonkit}"
  const val doraemonkit_no = "com.didichuxing.doraemonkit:dokitx-no-op:${Versions.doraemonkit}"

  //表情 https://github.com/vanniktech/Emoji
  const val emoji = "com.vanniktech:emoji-ios:0.7.0"

  //SideBar https://github.com/D10NGYANG/DL10SideBar
  const val side_bar = "com.github.D10NGYANG:DL10SideBar:1.0.0"

  //bugly https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20190916214340
  const val bugly_crash = "com.tencent.bugly:crashreport:3.2.422"
  const val bugly_native = "com.tencent.bugly:nativecrashreport:3.7.5"

  //中文转拼音 https://github.com/promeG/TinyPinyin
  const val pinyin = "com.github.promeg:tinypinyin:2.0.3" // TinyPinyin核心包，约80KB
  const val pinyin_android = "com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3" // 可选，适用于Android的中国地区词典

  //@和#效果 https://github.com/sunhapper/SpEditTool
  const val spedit_tool = "com.github.sunhapper.SpEditTool:SpEditText:1.0.4"

  //日期选择 https://github.com/zyyoona7/WheelPicker
  const val wheelview = "com.github.zyyoona7:wheelview:1.0.9"
  const val pickerview = "com.github.zyyoona7:pickerview:1.1.1"

  //FlexboxLayout https://github.com/google/flexbox-layout
  const val flexbox = "com.google.android:flexbox:2.0.1"

  //动态权限 https://github.com/getActivity/XXPermissions
  const val xxpermissions = "com.hjq:xxpermissions:9.6"

  //视频播放相关 https://github.com/Doikki/DKVideoPlayer/wiki
  const val dk_java = "com.github.dueeeke.dkplayer:dkplayer-java:3.2.6"
  const val dk_ui = "com.github.dueeeke.dkplayer:dkplayer-ui:3.2.6"
  const val dk_exo = "com.github.dueeeke.dkplayer:player-exo:3.2.6"

  //图片加载 https://github.com/coil-kt/coil
  const val coil = "io.coil-kt:coil:${Versions.coil}"
  const val coil_gif = "io.coil-kt:coil-gif:${Versions.coil}"
  const val coil_video_file = "io.coil-kt:coil-video:${Versions.coil}"

  //RecyclerView动画 https://github.com/mikepenz/ItemAnimators
  const val rv_anim = "com.mikepenz:itemanimators:1.1.0"

  //指示器 https://github.com/hackware1993/MagicIndicator/tree/androidx
  const val indicator = "com.github.hackware1993:MagicIndicator:1.7.0"

  //腾讯PAG动画 https://pag.io/docs/sdk.html
  const val pag = "com.tencent.tav:libpag:3.2.5.1"
}
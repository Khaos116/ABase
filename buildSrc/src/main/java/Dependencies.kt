object Versions {
  internal const val sdkMin = 23
  internal const val sdkTarget = 30
  internal const val kotlin = "1.4.21"
  internal const val okHttp = "4.9.0"
  internal const val rxHttp = "2.5.4"
  internal const val coil = "1.1.1"
}

object Deps {
  //<editor-fold defaultstate="collapsed" desc="Studio基础配置">
  //根目录gradle https://maven.aliyun.com/mvn/search
  const val plugin_android_gradle = "com.android.tools.build:gradle:3.5.4"
  const val plugin_kotlin_gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
  const val plugin_r8_gradle = "com.android.tools:r8:2.1.75" //更新R8版本，解决正式版无法打包的问题 https://github.com/square/okhttp/issues/4604

  //使用kotlin
  const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Studio-UI基础配置">
  //系统相关 https://maven.aliyun.com/mvn/search
  const val core_ktx = "androidx.core:core-ktx:1.5.0-alpha05"
  const val activity_ktx = "androidx.activity:activity-ktx:1.2.0-rc01"
  const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha02"
  const val fragment = "androidx.fragment:fragment:1.3.0-rc01"
  const val material = "com.google.android.material:material:1.3.0-beta01"
  const val constraint = "androidx.constraintlayout:constraintlayout:2.1.0-alpha2"
  const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:2.3.0-rc01"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="AndroidX基础配置">
  //启动初始化 https://developer.android.google.cn/topic/libraries/app-startup
  const val startup = "androidx.startup:startup-runtime:1.0.0"

  //分包 https://developer.android.google.cn/studio/build/multidex?hl=zh_cn#mdex-gradle
  const val multidex = "androidx.multidex:multidex:2.0.1"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="三方必须基础配置"
  //log打印 https://github.com/JakeWharton/timber
  const val timber = "com.jakewharton.timber:timber:4.7.1"

  //工具类 https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md
  const val utilcodex = "com.blankj:utilcodex:1.30.5"

  //UI适配 https://github.com/JessYanCoding/AndroidAutoSize
  const val autosize = "me.jessyan:autosize:1.2.1"

  //状态栏适配 https://github.com/gyf-dev/ImmersionBar
  const val immersionbar = "com.gyf.immersionbar:immersionbar:3.0.0"
  const val immersionbar_ktx = "com.gyf.immersionbar:immersionbar-ktx:3.0.0"

  //数据存储 https://github.com/Tencent/MMKV
  const val mmkv = "com.tencent:mmkv-static:1.2.7"
  //</editor-fold>"

  //<editor-fold defaultstate="collapsed" desc="三方基础配置">
  //数据解析 https://github.com/google/gson
  const val gson = "com.google.code.gson:gson:2.8.6"

  //协程 https://github.com/Kotlin/kotlinx.coroutines
  const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
  const val coroutine_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2"

  //网络请求 https://github.com/square/okhttp
  const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"

  //日志json打印 https://github.com/Ayvytr/OKHttpLogInterceptor
  const val okhttp_log = "com.ayvytr:okhttploginterceptor:3.0.4"

  //RxHttp https://github.com/liujingxing/okhttp-RxHttp
  const val rxhttp = "com.ljx.rxhttp:rxhttp:${Versions.rxHttp}"
  const val rxhttp_kapt = "com.ljx.rxhttp:rxhttp-compiler:${Versions.rxHttp}"
  const val rxlife_coroutine = "com.ljx.rxlife:rxlife-coroutine:2.0.1" //管理协程生命周期，页面销毁，关闭请求
  const val rxlife_rxjava = "com.ljx.rxlife3:rxlife-rxjava:3.0.0" //管理RxJava3生命周期，页面销毁，关闭请求

  //RxAndroid https://github.com/ReactiveX/RxAndroid
  const val rxandroid = "io.reactivex.rxjava3:rxandroid:3.0.0"

  //图片加载 https://github.com/coil-kt/coil
  const val coil = "io.coil-kt:coil:${Versions.coil}"
  const val coil_gif = "io.coil-kt:coil-gif:${Versions.coil}"
  const val coil_video_file = "io.coil-kt:coil-video:${Versions.coil}"

  //多类型适配器 https://github.com/drakeet/MultiType
  const val multitype = "com.drakeet.multitype:multitype:4.2.0"

  //动态权限 https://github.com/getActivity/XXPermissions
  const val xxpermissions = "com.hjq:xxpermissions:9.8"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="三方常用配置">
  //EventBus https://github.com/JeremyLiao/LiveEventBus
  const val eventBus = "com.jeremyliao:live-event-bus-x:1.7.3"

  //WebView https://github.com/Justson/AgentWeb
  const val agentweb = "com.just.agentweb:agentweb:4.1.4"

  //RecyclerView动画 https://github.com/mikepenz/ItemAnimators
  const val rv_anim = "com.mikepenz:itemanimators:1.1.0"

  //指示器 https://github.com/hackware1993/MagicIndicator/tree/androidx
  const val indicator = "com.github.hackware1993:MagicIndicator:1.7.0"

  //侧滑 https://github.com/luckybilly/SmartSwipe
  const val swipe = "com.billy.android:smart-swipe:1.1.2"
  const val swipex = "com.billy.android:smart-swipe-x:1.1.0"

  //下拉刷新 https://github.com/scwang90/SmartRefreshLayout
  const val smart_refresh = "com.scwang.smart:refresh-layout-kernel:2.0.3"
  const val smart_header = "com.scwang.smart:refresh-header-classics:2.0.3"
  const val smart_footer = "com.scwang.smart:refresh-footer-classics:2.0.3"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="调试统计工具">
  const val plugin_bugly = "com.tencent.bugly:symtabfileuploader:2.2.1" //bugly的mapping上传

  //模拟器检测 https://github.com/happylishang/CacheEmulatorChecker
  const val emulator = "com.snail:antifake:1.4"

  //Google分析统计工具 https://console.firebase.google.com/
  //const val gms_gradle = "com.google.gms:google-services:4.3.4"
  //const val crashlytics_gradle = "com.google.firebase:firebase-crashlytics-gradle:2.4.1"
  //const val firebase_bom = "com.google.firebase:firebase-bom:26.2.0"
  //const val analytics_ktx = "com.google.firebase:firebase-analytics-ktx"
  //const val crashlytics_ktx = "com.google.firebase:firebase-crashlytics-ktx"

  //内存泄漏检测 https://square.github.io/leakcanary/getting_started/
  const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.6"

  //UI调试 https://github.com/YvesCheung/UInspector
  const val uinspector = "com.huya.mobile:Uinspector:1.0.10"

  //bugly https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20190916214340
  const val bugly_crash = "com.tencent.bugly:crashreport:3.2.422"
  const val bugly_native = "com.tencent.bugly:nativecrashreport:3.7.5"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="三方不常用配置">
  //图片选择器 https://github.com/LuckSiege/PictureSelector
  const val pic_select = "com.github.LuckSiege.PictureSelector:picture_library:v2.6.0"

  //Emoji表情 https://github.com/vanniktech/Emoji
  const val emoji = "com.vanniktech:emoji-twitter:0.7.0"

  //SideBar https://github.com/D10NGYANG/DL10SideBar
  const val side_bar = "com.github.D10NGYANG:DL10SideBar:1.0.0"

  //FlexboxLayout https://github.com/google/flexbox-layout
  const val flexbox = "com.google.android:flexbox:2.0.1"

  //视频播放相关 https://github.com/Doikki/DKVideoPlayer/wiki
  const val dk_java = "com.github.dueeeke.dkplayer:dkplayer-java:3.2.6"
  const val dk_ui = "com.github.dueeeke.dkplayer:dkplayer-ui:3.2.6"
  const val dk_exo = "com.github.dueeeke.dkplayer:player-exo:3.2.6"

  //https://github.com/airbnb/lottie-android
  const val lottie = "com.airbnb.android:lottie:3.6.0"

  //中文转拼音 https://github.com/promeG/TinyPinyin
  const val pinyin = "com.github.promeg:tinypinyin:2.0.3" // TinyPinyin核心包，约80KB
  const val pinyin_android = "com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3" // 可选，适用于Android的中国地区词典

  //日期选择器 https://github.com/limxing/DatePickerView
  const val picker_view = "com.github.limxing:DatePickerView:1.0.3"

  //@和#效果 https://github.com/sunhapper/SpEditTool
  const val spedit_tool = "com.github.sunhapper.SpEditTool:SpEditText:1.0.4"

  //远距离识别二维码 https://github.com/devilsen/CZXing
  const val czxing = "me.devilsen:czxing:1.0.17"

  //多渠道读取 https://github.com/Meituan-Dianping/walle
  const val walle = "com.meituan.android.walle:library:1.1.7"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="其他">
  //</editor-fold>
}
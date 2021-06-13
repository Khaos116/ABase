object Versions {
  internal const val sdkMin = 23
  internal const val sdkTarget = 30
  internal const val kotlin = "1.5.10"
  internal const val okHttp = "4.9.1"
  internal const val rxHttp = "2.6.1"
  internal const val coil = "1.2.2"
}

object Deps {
  //<editor-fold defaultstate="collapsed" desc="Studio基础配置">
  //根目录gradle https://maven.aliyun.com/mvn/search 使用ViewBinding最低3.6.0
  const val plugin_android_gradle = "com.android.tools.build:gradle:4.2.1"
  const val plugin_kotlin_gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
  const val plugin_r8_gradle = "com.android.tools:r8:2.2.60" //更新R8版本，解决正式版无法打包的问题 https://github.com/square/okhttp/issues/4604

  //使用kotlin
  const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Studio-UI基础配置">
  //系统相关 https://maven.aliyun.com/mvn/search
  const val core_ktx = "androidx.core:core-ktx:1.6.0-beta02"
  const val activity_ktx = "androidx.activity:activity-ktx:1.3.0-beta01"
  const val appcompat = "androidx.appcompat:appcompat:1.4.0-alpha02"
  const val fragment = "androidx.fragment:fragment:1.4.0-alpha02"
  const val material = "com.google.android.material:material:1.4.0-rc01"
  const val constraint = "androidx.constraintlayout:constraintlayout:2.1.0-beta02"
  const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:2.4.0-alpha01"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="AndroidX基础配置">
  //启动初始化 https://developer.android.google.cn/topic/libraries/app-startup【注:由于出现在三星手机上不进入初始化，所以改为三方的android-startup】
  const val startup = "androidx.startup:startup-runtime:1.1.0-beta01"

  //https://github.com/idisfkj/android-startup/blob/master/README-ch.md【由于在三星手机上，AndroidX自带的startup存在不初始化的问题，所以改用这个】
  const val android_startup = "io.github.idisfkj:android-startup:1.0.62"

  //如果 minSdkVersion 设置为 21 或更高值则直接在build.gradle配置multiDexEnabled true即可，小于21的才需要配置到Application
  //分包 https://developer.android.google.cn/studio/build/multidex?hl=zh_cn#mdex-gradle
  const val multidex = "androidx.multidex:multidex:2.0.1"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="三方必须基础配置"
  //log打印 https://github.com/JakeWharton/timber
  const val timber = "com.jakewharton.timber:timber:4.7.1"

  //工具类 https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md
  const val utilcodex = "com.blankj:utilcodex:1.30.6"

  //UI适配 https://github.com/JessYanCoding/AndroidAutoSize
  const val autosize = "me.jessyan:autosize:1.2.1"

  //状态栏适配 https://github.com/gyf-dev/ImmersionBar
  const val immersionbar = "com.gyf.immersionbar:immersionbar:3.0.0"
  const val immersionbar_ktx = "com.gyf.immersionbar:immersionbar-ktx:3.0.0"

  //数据存储 https://github.com/Tencent/MMKV
  const val mmkv = "com.tencent:mmkv-static:1.2.9"
  //</editor-fold>"

  //<editor-fold defaultstate="collapsed" desc="三方基础配置">
  //数据解析 https://github.com/google/gson
  const val gson = "com.google.code.gson:gson:2.8.7"

  //协程 https://github.com/Kotlin/kotlinx.coroutines
  const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0"
  const val coroutine_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0"

  //网络请求 https://github.com/square/okhttp
  const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"

  //日志json打印 https://github.com/Ayvytr/OKHttpLogInterceptor
  const val okhttp_log = "com.ayvytr:okhttploginterceptor:3.0.5"

  //RxHttp https://github.com/liujingxing/okhttp-RxHttp
  const val rxhttp = "com.github.liujingxing.rxhttp:rxhttp:${Versions.rxHttp}"
  const val rxhttp_kapt = "com.github.liujingxing.rxhttp:rxhttp-compiler:${Versions.rxHttp}"
  const val rxlife_coroutine = "com.github.liujingxing.rxlife:rxlife-coroutine:2.1.0" //管理协程生命周期，页面销毁，关闭请求
  const val rxlife_rxjava = "com.github.liujingxing.rxlife:rxlife-rxjava3:2.1.0" //管理RxJava3生命周期，页面销毁，关闭请求

  //RxAndroid https://github.com/ReactiveX/RxAndroid
  const val rxandroid = "io.reactivex.rxjava3:rxandroid:3.0.0"

  //图片加载 https://github.com/coil-kt/coil
  const val coil = "io.coil-kt:coil:${Versions.coil}"
  const val coil_gif = "io.coil-kt:coil-gif:${Versions.coil}"
  const val coil_video_file = "io.coil-kt:coil-video:${Versions.coil}"

  //多类型适配器 https://github.com/drakeet/MultiType
  const val multitype = "com.drakeet.multitype:multitype:4.2.0"

  //动态权限 https://github.com/getActivity/XXPermissions
  const val xxpermissions = "com.github.getActivity:XXPermissions:11.2"

  //ViewBinding https://github.com/DylanCaiCoding/ViewBindingKtx/blob/master/README_CN.md
  const val binding_ktx = "com.github.DylanCaiCoding.ViewBindingKTX:viewbinding-ktx:1.2.1"
  const val binding_base = "com.github.DylanCaiCoding.ViewBindingKTX:viewbinding-base:1.2.1"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="三方常用配置">
  //EventBus https://github.com/JeremyLiao/LiveEventBus
  const val eventBus = "io.github.jeremyliao:live-event-bus-x:1.8.0"

  //WebView https://github.com/Justson/AgentWeb
  const val agentweb = "com.github.Justson.AgentWeb:agentweb-core:v4.1.9-androidx"
  const val agentweb_file = "com.github.Justson.AgentWeb:agentweb-filechooser:v4.1.9-androidx"
  const val agentweb_down = "com.github.Justson:Downloader:v4.1.9-androidx"

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

  //手机号判断 https://github.com/google/libphonenumber
  const val libphonenumber = "com.googlecode.libphonenumber:libphonenumber:8.12.24"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="调试统计工具">
  //模拟器检测 https://github.com/happylishang/CacheEmulatorChecker
  const val emulator = "io.github.happylishang:antifake:1.5.0"

  //Google分析统计工具 https://console.firebase.google.com/
  //const val gms_gradle = "com.google.gms:google-services:4.3.4"
  //const val crashlytics_gradle = "com.google.firebase:firebase-crashlytics-gradle:2.4.1"
  //const val firebase_bom = "com.google.firebase:firebase-bom:26.2.0"
  //const val analytics_ktx = "com.google.firebase:firebase-analytics-ktx"
  //const val crashlytics_ktx = "com.google.firebase:firebase-crashlytics-ktx"

  //内存泄漏检测 https://square.github.io/leakcanary/getting_started/
  const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.7"

  //UI调试 https://github.com/YvesCheung/UInspector
  const val uinspector = "com.github.YvesCheung.UInspector:Uinspector:2.0.5"

  //bugly https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20190916214340
  const val bugly_crash = "com.tencent.bugly:crashreport:3.3.92"
  const val bugly_native = "com.tencent.bugly:nativecrashreport:3.9.1"
  const val plugin_bugly = "com.tencent.bugly:symtabfileuploader:2.2.1" //bugly的mapping上传
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="三方不常用配置">
  //图片选择器 https://github.com/LuckSiege/PictureSelector
  const val pic_select = "io.github.lucksiege:pictureselector:v2.7.3-rc02"

  //Emoji表情 https://github.com/vanniktech/Emoji
  const val emoji = "com.vanniktech:emoji-twitter:0.7.0"

  //SideBar https://github.com/D10NGYANG/DL10SideBar
  const val side_bar = "com.github.D10NGYANG:DL10SideBar:1.0.0"

  //FlexboxLayout https://github.com/google/flexbox-layout
  const val flexbox = "com.google.android.flexbox:flexbox:3.0.0"

  //视频播放相关 https://github.com/Doikki/DKVideoPlayer/wiki
  const val dk_java = "xyz.doikki.android.dkplayer:dkplayer-java:3.3.3"
  const val dk_ui = "xyz.doikki.android.dkplayer:dkplayer-ui:3.3.3"
  const val dk_exo = "xyz.doikki.android.dkplayer:player-exo:3.3.3"

  //https://github.com/airbnb/lottie-android
  const val lottie = "com.airbnb.android:lottie:3.7.0"

  //中文转拼音 https://github.com/promeG/TinyPinyin
  const val pinyin = "com.github.promeg:tinypinyin:2.0.3" // TinyPinyin核心包，约80KB
  const val pinyin_android = "com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3" // 可选，适用于Android的中国地区词典

  //日期选择器 https://github.com/limxing/DatePickerView
  const val picker_view = "com.github.limxing:DatePickerView:1.1.1"

  //@和#效果 https://github.com/sunhapper/SpEditTool
  const val spedit_tool = "com.github.sunhapper.SpEditTool:SpEditText:1.0.4"

  //远距离识别二维码 https://github.com/devilsen/CZXing
  const val czxing = "io.github.devilsen:czxing:1.1.0"

  //多渠道读取 https://github.com/Meituan-Dianping/walle
  const val walle = "com.meituan.android.walle:library:1.1.7"

  //手势解锁 https://github.com/ihsg/PatternLocker
  const val pattern_locker = "com.github.ihsg:PatternLocker:2.5.7"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="其他">
  //</editor-fold>
}
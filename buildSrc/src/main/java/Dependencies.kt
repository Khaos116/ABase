object Versions {
  internal const val sdkMin = 23
  internal const val sdkTarget = 30
  internal const val kotlin = "1.4.10"
  internal const val okHttp = "4.8.1"
  internal const val rxHttp = "2.4.1"
  internal const val doraemonkit = "3.2.0"
  internal const val dkplayer = "3.2.6"
  internal const val epoxy = "4.1.0"
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
  const val core_ktx = "androidx.core:core-ktx:1.5.0-alpha03"
  const val activity_ktx = "androidx.activity:activity-ktx:1.2.0-alpha08"
  const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha02"
  const val fragment = "androidx.fragment:fragment:1.3.0-alpha08"
  const val material = "com.google.android.material:material:1.3.0-alpha02"
  const val constraint = "androidx.constraintlayout:constraintlayout:2.0.1"
  const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:2.3.0-alpha07"

  //分包 https://developer.android.google.cn/studio/build/multidex?hl=zh_cn#mdex-gradle
  const val multidex = "androidx.multidex:multidex:2.0.1"

  //内存泄漏检测 https://square.github.io/leakcanary/getting_started/
  const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.4"

  //工具类 https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md
  const val utilcodex = "com.blankj:utilcodex:1.29.0"

  //UI适配 https://github.com/JessYanCoding/AndroidAutoSize
  const val autosize = "me.jessyan:autosize:1.2.1"

  //状态栏适配 https://github.com/gyf-dev/ImmersionBar
  const val immersionbar = "com.gyf.immersionbar:immersionbar:3.0.0"
  const val immersionbar_ktx = "com.gyf.immersionbar:immersionbar-ktx:3.0.0"

  //数据存储 https://github.com/Tencent/MMKV
  const val mmkv = "com.tencent:mmkv-static:1.2.2"

  //数据解析 https://github.com/google/gson
  const val gson = "com.google.code.gson:gson:2.8.6"

  //网络请求 https://github.com/square/okhttp
  const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"

  //协程 https://github.com/Kotlin/kotlinx.coroutines
  const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9"
  const val coroutine_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"

  //RxHttp https://github.com/liujingxing/okhttp-RxHttp
  const val rxhttp = "com.ljx.rxhttp:rxhttp:${Versions.rxHttp}"
  const val rxhttp_kapt = "com.ljx.rxhttp:rxhttp-compiler:${Versions.rxHttp}" //生成RxHttp类
  const val rxlife_coroutine = "com.ljx.rxlife:rxlife-coroutine:2.0.1" //管理协程生命周期，页面销毁，关闭请求
  const val rxlife_rxjava = "com.ljx.rxlife2:rxlife-rxjava:2.0.0" //管理RxJava2生命周期，页面销毁，关闭请求

  //RxAndroid https://github.com/ReactiveX/RxAndroid
  const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.1.1"

  //EventBus https://github.com/JeremyLiao/LiveEventBus
  const val eventBus = "com.jeremyliao:live-event-bus-x:1.7.2"

  //WebView https://github.com/Justson/AgentWeb
  const val agentweb = "com.just.agentweb:agentweb:4.1.4"

  //侧滑 https://github.com/luckybilly/SmartSwipe
  const val swipe = "com.billy.android:smart-swipe:1.1.2"
  const val swipex = "com.billy.android:smart-swipe-x:1.1.0"

  //图片选择器 https://github.com/LuckSiege/PictureSelector
  const val pic_select = "com.github.LuckSiege.PictureSelector:picture_library:v2.5.9"

  //https://github.com/airbnb/MvRx TODO 更新后没有BaseMvRxViewModel
  const val mvrx = "com.airbnb.android:mvrx:2.0.0-alpha2"

  //https://github.com/trello/RxLifecycle  TODO 更新后RxUtils存在问题
  //api 'com.trello.rxlifecycle4:rxlifecycle-android-lifecycle:4.0.0'
  const val rxlife_android = "com.trello.rxlifecycle3:rxlifecycle-android-lifecycle:3.1.0"

  //https://github.com/airbnb/lottie-android
  const val lottie = "com.airbnb.android:lottie:3.4.2"

  //https://github.com/airbnb/epoxy
  const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
  const val epoxy_processor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"

  //图片加载 https://github.com/panpf/sketch
  const val sketch = "me.panpf:sketch:2.7.1"

  //封面加载 https://github.com/wseemann/FFmpegMediaMetadataRetriever
  const val ffm_mmr_core = "com.github.wseemann:FFmpegMediaMetadataRetriever-core:1.0.15"
  const val ffm_mmr_native = "com.github.wseemann:FFmpegMediaMetadataRetriever-native-armeabi-v7a:1.0.15"

  //辅助工具 https://github.com/didi/DoraemonKit/blob/master/Doc/android_cn_guide.md
  const val doraemonkit = "com.didichuxing.doraemonkit:doraemonkit:${Versions.doraemonkit}"
  const val doraemonkit_no = "com.didichuxing.doraemonkit:doraemonkit-no-op:${Versions.doraemonkit}"

  //https://github.com/VictorAlbertos/RxCache
  const val rxcache = "com.github.VictorAlbertos.RxCache:runtime:1.8.3-2.x"
  const val rxcache_gson = "com.github.VictorAlbertos.Jolyglot:gson:0.0.4"

  //表情 https://github.com/vanniktech/Emoji
  const val emoji = "com.vanniktech:emoji-ios:0.7.0"

  //必选 https://github.com/dueeeke/DKVideoPlayer/wiki
  const val dkplayer_java = "com.github.dueeeke.dkplayer:dkplayer-java:${Versions.dkplayer}"
  const val dkplayer_ui = "com.github.dueeeke.dkplayer:dkplayer-ui:${Versions.dkplayer}"
  const val dkplayer_exo = "com.github.dueeeke.dkplayer:player-exo:${Versions.dkplayer}"

  //弹幕 https://github.com/bilibili/DanmakuFlameMaster
  const val danma_master = "com.github.ctiao:DanmakuFlameMaster:0.9.25"
  const val danma_bitmap = "com.github.ctiao:ndkbitmap-armv7a:0.9.21"

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
  const val wheelview = "com.github.zyyoona7:wheelview:1.0.8"
  const val pickerview = "com.github.zyyoona7:pickerview:1.1.0"

  //FlexboxLayout https://github.com/google/flexbox-layout
  const val flexbox = "com.google.android:flexbox:2.0.1"
}
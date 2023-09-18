#noinspection ShrinkerUnresolvedReference
#-------1.基本指令-------
# 设置混淆的压缩比率 0 ~ 7
-optimizationpasses 5
# 混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共库的成员
-dontskipnonpubliclibraryclassmembers
# 混淆时不做预校验
-dontpreverify
# 混淆时不记录日志
-verbose
# 忽略警告
-ignorewarnings
# 代码优化
-dontshrink
# 不优化输入的类文件
-dontoptimize
# 保留注解不混淆
-keepattributes *Annotation*,InnerClasses
# 避免混淆泛型
-keepattributes Signature
# 将.class信息中的类名重新定义为"Proguard"字符串
-renamesourcefileattribute SourceFile
# 并保留源文件名为"Proguard"字符串，而非原始的类名 并保留行号
-keepattributes SourceFile,LineNumberTable
# 混淆采用的算法
-optimizations !code/simplification/cast,!field/*,!class/merging/*
# dump.txt文件列出apk包内所有class的内部结构
-dump class_files.txt
# seeds.txt文件列出未混淆的类和成员
-printseeds seeds.txt
# usage.txt文件列出从apk中删除的代码
-printusage unused.txt
# mapping.txt文件列出混淆前后的映射
-printmapping mapping.txt
#-------2.不需混淆的Android类-------
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
#-------3.support-v4包-------
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
#-------4.support-v7包-------
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }
#-------5.support design-------
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
#-------6.androidx的混淆-------
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
#-------7.避免混淆自定义控件类的 get/set 方法和构造函数-------
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#-------8.关闭Log日志-------
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
    public static *** w(...);
}
#-------9.避免资源混淆-------
-keep class **.R$* {*;}
#-------10.layout中onclick方法(android:onclick="onClick")混淆-------
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
#-------11.onXXEvent 混淆-------
-keepclassmembers class * {
    void *(*Event);
}
#-------12.避免混淆枚举类-------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#-------13.Natvie方法不混淆-------
-keepclasseswithmembernames class * {
    native <methods>;
}
#-------14.避免Parcelable混淆-------
-keep class * implements android.os.Parcelable {
  #noinspection ShrinkerUnresolvedReference
  public static final android.os.Parcelable$Creator *;
}
#-------15.Serializable接口的子类中指定的某些成员变量和方法混淆-------
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#-------16.WebView混淆配置-------
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    #noinspection ShrinkerUnresolvedReference
    public void *(android.webkit.webView, jav.lang.String);
}
#############################################
#            项目中特殊处理部分               #
#############################################
-keep class cc.ab.base.net.http.response.** { *; }
-keep class cc.ab.base.widget.engine.** { *; }
-keep class cc.abase.demo.bean.** { *; }
-keep class cc.abase.demo.widget.** { *; }

#内部成员和方法不混淆

#内部类不混淆

#############################################

#-----------处理反射类---------------
-keep class xyz.doikki.videoplayer.exo.ExoMediaSourceHelper { *; }

#......

#-----------处理js交互---------------

#......

#-----------处理实体类---------------

#......

#-----------处理第三方依赖库---------

#----------retrofit--------------
#-keepclassmembernames,allowobfuscation interface * {
#    @retrofit2.http.* <methods>;
#}
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**
-dontwarn javax.annotation.**

#-------------------------

#-------------- okhttp3 -------------
# OkHttp3
# https://github.com/square/okhttp
# okhttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.* { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# okhttp 3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

#------------------

#----------- rxjava rxandroid----------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    #noinspection ShrinkerUnresolvedReference
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    #noinspection ShrinkerUnresolvedReference
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

#--------------------------

#----------- gson ----------------
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers enum * { *; }
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
#--------------------------

#----------- Rxcache ----------------
-dontwarn io.rx_cache.internal.**
-keepclassmembers enum io.rx_cache.Source { *; }
#--------------------------

#----------- LiveEventBus ----------------
-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }
#--------------------------

#----------- RxFFmpeg ----------------
-dontwarn io.microshow.rxffmpeg.**
-keep class io.microshow.rxffmpeg.**{*;}
#--------------------------

#----------- DKPlayer ----------------
-keep class xyz.doikki.videoplayer.** { *; }
-dontwarn xyz.doikki.videoplayer.**

# IjkPlayer
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**
#--------------------------

#----------- bugly ----------------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#--------------------------

#PictureSelector 3.0
-keep class com.luck.picture.lib.** { *; }

#如果引入了Camerax库请添加混淆
-keep class com.luck.lib.camerax.** { *; }

#如果引入了Ucrop库请添加混淆
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#FFmpegMediaMetadataRetriever
-keep class wseemann.media** { *; }

#SpEditTool
-keep class com.sunhapper.x.spedit.**{*;}

#协程
#在安卓上，你可以使用协程解决两个常见问题：
 #简化耗时任务的代码，例如网络请求，磁盘读写，甚至大量 JSON 的解析
 #提供准确的主线程安全，在不会让代码更加臃肿的情况下保证不阻塞主线程
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#RxHttp https://github.com/liujingxing/okhttp-RxHttp
# RxHttp
-keep class rxhttp.**{*;}
# OkHttp
-keep class okhttp3.**{*;}
-keep class okio.**{*;}

#GSYVideoPlayer https://github.com/CarGuo/GSYVideoPlayer
-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#腾讯PAG动画 https://pag.io/docs/sdk.html
-keep class org.libpag.* {*;}

#远距离识别二维码 https://github.com/devilsen/CZXing
-keep class me.devilsen.czxing.**
-keep class me.devilsen.czxing.** { *; }

#AgentWeb https://github.com/Justson/AgentWeb
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**
#############################################
#----------------丧心病狂的混淆----------------#
# 指定外部模糊字典 proguard-chinese.txt 改为混淆文件名，下同
-obfuscationdictionary proguard-sxbk.txt
## 指定class模糊字典
-classobfuscationdictionary proguard-sxbk.txt
## 指定package模糊字典
-packageobfuscationdictionary proguard-sxbk.txt
#############################################
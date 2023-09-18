# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#############################################
#            项目中特殊处理部分               #
#保留Hook入口
-keep class * implements de.robv.android.xposed.IXposedHookLoadPackage
#不混淆base基类
-keep class cc.abase.lsposed.base.* {}
#不混淆继承base基类的相关类
-keep class * extends cc.abase.lsposed.base.* { }
#############################################
#----------------丧心病狂的混淆----------------#
# 指定外部模糊字典 proguard-chinese.txt 改为混淆文件名，下同
-obfuscationdictionary proguard-sxbk.txt
## 指定class模糊字典
-classobfuscationdictionary proguard-sxbk.txt
## 指定package模糊字典
-packageobfuscationdictionary proguard-sxbk.txt
#############################################
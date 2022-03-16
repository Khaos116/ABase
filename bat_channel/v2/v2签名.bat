::设置编码方式(65001 UTF-8;936 GBK;437 英语)
chcp 65001

::双冒号表示注释(setlocal enabledelayedexpansion是延迟变量赋值使用)
@echo off&setlocal enabledelayedexpansion
echo ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆==Start==☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
echo=

::APK加固后需要重新写入V2签名，采用免费的"易固V1.0"进行加固(https://bbs.125.la/thread-14620441-1-1.html)
::apksigner.jar文件在SDK目录下，如E:\01StudioAndEclipseSdk\build-tools\30.0.3\lib\apksigner.jar
::签名文件信息，参考https://www.it610.com/article/1291216346465509376.htm
set jksFile=com_ab.jks

set keyAlias=com_cc

set storePassword=com_cc

set keyPassword=com_cc

set suffix=_已签名

set suffix2=_已对齐

::找到当前目录(%~dp0)下所有apk文件
for %%i in (*.apk) do (
	::打印签名信息
	echo %date%_%time%  签名文件路径=%~dp0%jksFile%
	echo=
	echo %date%_%time%  签名Alias=%keyAlias%
	echo %date%_%time%  KeyStore密码=%storePassword%
	echo %date%_%time%  Alias对应密码=%keyPassword%
	echo=
	echo %date%_%time%  原APK=%%~fi
	echo %date%_%time%  新APK=%~dp0%%~ni%suffix2%%suffix%.apk
	echo=
	::APK对齐，使APP在安卓系统中运行速度更快
  	zipalign -v 4 %%~fi %~dp0%%~ni%suffix2%.apk
	::执行V2签名写入
	java -jar apksigner.jar sign  --ks %jksFile%  --ks-key-alias %keyAlias%  --ks-pass pass:%storePassword%  --key-pass pass:%keyPassword%  --out %~dp0%%~ni%suffix2%%suffix%.apk  %~dp0%%~ni%suffix2%.apk
	echo=
	::删除签名后生成的临时文件
	if exist %~dp0%%~ni%suffix2%%suffix%.apk.idsig del %~dp0%%~ni%suffix2%%suffix%.apk.idsig
	if exist %~dp0%%~ni%suffix2%.apk del %~dp0%%~ni%suffix2%.apk
	echo=
)

echo ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆==End==☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
pause
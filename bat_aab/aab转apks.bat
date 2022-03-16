::设置编码方式(65001 UTF-8;936 GBK;437 英语)
chcp 65001

::双冒号表示注释(setlocal enabledelayedexpansion是延迟变量赋值使用)
@echo off&setlocal enabledelayedexpansion
echo ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆==Start==☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
echo=

set jksFile=walk.jks

set keyAlias=walk

set storePassword=walk2022

set keyPassword=walk2022

set suffix=_已签名

::找到当前目录(%~dp0)下所有aab文件
for %%i in (*.aab) do (
    ::打印签名信息
    echo %date%_%time%  签名文件路径=%~dp0%jksFile%
    echo=
    echo %date%_%time%  签名Alias=%keyAlias%
    echo %date%_%time%  KeyStore密码=%storePassword%
    echo %date%_%time%  Alias对应密码=%keyPassword%
    echo=
    echo %date%_%time%  原APK=%%~fi
    echo %date%_%time%  新APK=%~dp0%%~ni%suffix2%%suffix%.apks
    echo=
    ::执行aab转apks
    ::java -jar bundletool.jar build-apks --bundle="(需要安装的aab路径)xxx.aab" --output="(输出的apks路径)xxx.apks" --overwrite --ks="xxx.keystore" --ks-pass="pass:keystore密钥" --ks-key-alias="别名" --key-pass="pass:别名密钥"
    java -jar bundletool.jar build-apks --bundle=%%~fi --output=%~dp0%%~ni%suffix%.apks --overwrite --ks=%jksFile% --ks-pass=pass:%storePassword% --ks-key-alias=%keyAlias% --key-pass=pass:%keyPassword%
    echo=
)

echo ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆==End==☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
pause
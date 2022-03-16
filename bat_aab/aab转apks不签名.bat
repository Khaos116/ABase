::设置编码方式(65001 UTF-8;936 GBK;437 英语)
chcp 65001

::双冒号表示注释(setlocal enabledelayedexpansion是延迟变量赋值使用)
@echo off&setlocal enabledelayedexpansion
echo ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆==Start==☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
echo=

::找到当前目录(%~dp0)下所有aab文件
for %%i in (*.aab) do (
    ::打印签名信息
    echo=
    echo %date%_%time%  原aab=%%~fi
    echo=
    ::执行aab转apks
    ::bundletool build-apks --bundle=my.aab --output=my.apks
    bundletool.jar build-apks --bundle=%%~fi --output=%~dp0%%~ni%suffix%.apks
    echo=
)

echo ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆==End==☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
pause
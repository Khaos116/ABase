::设置编码方式(65001 UTF-8;936 GBK;437 英语)
chcp 65001

::双冒号表示注释(setlocal enabledelayedexpansion是延迟变量赋值使用)
@echo off&setlocal enabledelayedexpansion
echo ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆==Start==☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
echo=

::找到当前目录(%~dp0)下所有apks文件
for %%i in (*.apks) do (
    echo=
    echo %date%_%time%  需要安装的APKS=%%~fi
    echo=
    ::执行apks安装
    ::java -jar bundletool.jar install-apks --apks="(输出的apks路径)xxx.apks"
    java -jar bundletool.jar install-apks --apks=%%~fi
    echo=
)

echo ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆==End==☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
pause
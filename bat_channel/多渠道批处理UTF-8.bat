::双冒号表示注释(setlocal enabledelayedexpansion是延迟变量赋值使用)
@echo off&setlocal enabledelayedexpansion
::先删除文件夹，再创建文件夹
for %%i in (*.apk) do (
  ::~ni 表示无后缀文件名
  if exist %~dp0\%%~ni (
     echo %%~ni文件夹已存在,执行清空
     del /q /s %~dp0\%%~ni
  ) else (
     echo %%~ni文件夹不存在,执行创建
     md %~dp0\%%~ni
  )
)
::找到当前目录下所有apk文件
for %%i in (*.apk) do (
  ::读取当前所有渠道
  for /f "tokens=1,* delims=_" %%a in (config.txt) do (
     echo 创建渠道：%%~ni_%%a.apk
     ::~fi表示文件全路径
     java -jar walle-cli-all.jar put -c %%a -e count_key=%%b %%~fi %~dp0\%%~ni\%%~ni_%%a.apk
  )
)
pause
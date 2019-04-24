:: 关闭终端回显
@echo off

set JACOB_FILEPATH=%JAVA_HOME%\bin
set JACOB_FILE=%JAVA_HOME%\bin\jacob.dll
echo %JACOB_FILEPATH%

rem echo 当前盘符：%~d0
rem echo 当前盘符和路径：%~dp0
rem echo 当前批处理全路径：%~f0
rem echo 当前CMD默认目录：%cd%

if exist %JACOB_FILE% (
	echo %JACOB_FILE% exists!
) else (
	echo %JACOB_FILE% not exists!

	copy %cd%\doc\jacob.dll %JACOB_FILEPATH%\jacob.dll
	echo file copy!
)

pause
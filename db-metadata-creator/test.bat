:: �ر��ն˻���
@echo off

set JACOB_FILEPATH=%JAVA_HOME%\bin
set JACOB_FILE=%JAVA_HOME%\bin\jacob.dll
echo %JACOB_FILEPATH%

rem echo ��ǰ�̷���%~d0
rem echo ��ǰ�̷���·����%~dp0
rem echo ��ǰ������ȫ·����%~f0
rem echo ��ǰCMDĬ��Ŀ¼��%cd%

if exist %JACOB_FILE% (
	echo %JACOB_FILE% exists!
) else (
	echo %JACOB_FILE% not exists!

	copy %cd%\doc\jacob.dll %JACOB_FILEPATH%\jacob.dll
	echo file copy!
)

pause
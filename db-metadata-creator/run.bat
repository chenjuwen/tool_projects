@echo off

rem echo %JAVA_HOME%
rem echo %PROCESSOR_ARCHITECTURE% - %PROCESSOR_ARCHITEW6432%

set JACOB_FILEPATH=%JAVA_HOME%\bin
set JACOB_FILE=%JAVA_HOME%\bin\jacob.dll

if not exist %JACOB_FILE% (
	echo %JACOB_FILE% not exists!

	if PROCESSOR_ARCHITEW6432 == AMD64 or PROCESSOR_ARCHITECTURE == AMD64 ( 
		echo OS is 64bit 
		copy %cd%\doc\jacob\AMD64\jacob.dll %JACOB_FILEPATH%\jacob.dll
		echo file copy!
		goto end
	) else (
		echo OS is 32bit
		copy %cd%\doc\jacob\x86\jacob.dll %JACOB_FILEPATH%\jacob.dll
		echo file copy!
		goto end
	)
)

:end
%JAVA_HOME%\bin\java -Xms256m -Xmx512m -jar db-metadata-creator.jar
pause
 
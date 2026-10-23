@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ========= KIEM TRA KET NOI SQL SERVER =========
where javac >nul 2>nul
if errorlevel 1 (echo CHUA CO JAVAC. Cai JDK 17+.& pause & exit /b 1)
set "HAS_DRIVER="
for %%F in ("lib\mssql-jdbc-*.jar") do if exist "%%~fF" set "HAS_DRIVER=1"
if not defined HAS_DRIVER (echo THIEU JDBC DRIVER trong lib.& pause & exit /b 1)
if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java
if errorlevel 1 (echo Bien dich loi.& pause & exit /b 1)
java -Dfile.encoding=UTF-8 -Djava.library.path="%CD%\lib" -cp "out;lib\*" KiemTraSQL
pause

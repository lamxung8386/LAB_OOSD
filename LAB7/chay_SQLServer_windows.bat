@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ========= QUAN LY NHAN SU - SQL SERVER =========
where javac >nul 2>nul
if errorlevel 1 (
    echo CHUA CO JDK 17 TRO LEN. Cai JDK va them java/javac vao PATH.
    pause
    exit /b 1
)
set "HAS_DRIVER="
for %%F in ("lib\mssql-jdbc-*.jar") do if exist "%%~fF" set "HAS_DRIVER=1"
if not defined HAS_DRIVER (
    echo THIEU DRIVER: Tai mssql-jdbc-13.6.0.jre11.jar va dat trong folder lib.
    echo Xem file HUONG_DAN_SQL_SERVER.md
    pause
    exit /b 1
)
if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java
if errorlevel 1 (echo Bien dich loi.& pause & exit /b 1)
java -Dfile.encoding=UTF-8 -Djava.library.path="%CD%\lib" -cp "out;lib\*" Main
if errorlevel 1 (echo Co loi khi khoi dong. Xem thong bao phia tren.&pause)

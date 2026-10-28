@echo off
chcp 65001 >nul
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
    echo May chua co Java. Can cai JDK/JRE 17 tro len.
    pause
    exit /b 1
)
java -jar CuaHangNuocGiaiKhat.jar
if errorlevel 1 pause

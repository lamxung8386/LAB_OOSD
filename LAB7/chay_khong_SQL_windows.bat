@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java
if errorlevel 1 (echo Bien dich loi.& pause & exit /b 1)
java -Dfile.encoding=UTF-8 -Dqlyns.mode=file -cp out Main
if errorlevel 1 pause

@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo Dang khoi dong ung dung Quan ly ho so benh an...
where mvn >nul 2>&1
if errorlevel 1 (
  echo CHUA CO MAVEN trong PATH. Hay cai Maven va JDK 17+.
  echo Doc file ..\README.md
  pause
  exit /b 1
)
call mvn -q compile exec:java
if errorlevel 1 (
  echo Da xay ra loi. Kiem tra SQL Server, config.properties va Maven.
  pause
)

@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\Models.java src\BusinessService.java src\DataStore.java src\Main.java src\BusinessTests.java
if errorlevel 1 (pause & exit /b 1)
java -ea -cp bin ngk.BusinessTests
pause

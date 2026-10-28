@echo off
chcp 65001 >nul
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
    echo Chua co JDK Java. Vui long cai JDK 17 tro len.
    pause
    exit /b 1
)
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\Models.java src\BusinessService.java src\DataStore.java src\Main.java src\BusinessTests.java
if errorlevel 1 (
    echo Bien dich bi loi. Kiem tra cua so nay.
    pause
    exit /b 1
)
java -cp bin ngk.Main
if errorlevel 1 pause

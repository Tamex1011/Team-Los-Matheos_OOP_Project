@echo off
REM Build and run the LTO app (Windows). Double-click or run from cmd.
setlocal enabledelayedexpansion

cd /d "%~dp0\.."
set ROOT=%CD%
set OUT=%ROOT%\out
set LIB=%ROOT%\lib\mysql-connector-j-9.4.0.jar
set SRC=%ROOT%\src

if not exist "%OUT%" (
    mkdir "%OUT%"
)

echo [1/2] Compiling...
REM Collect all .java files recursively because cmd wildcards don't expand **.
set SOURCES=
for /r "%SRC%" %%f in (*.java) do (
    set SOURCES=!SOURCES! "%%f"
)

javac -d "%OUT%" -cp "%LIB%;%SRC%" !SOURCES!
if errorlevel 1 (
    echo Compile failed. Check errors above.
    pause
    exit /b 1
)

echo [2/2] Running...
java -cp "%OUT%;%LIB%" LTOSystem

echo Done.
pause


@echo off

setlocal

set MY_DIR=%~dp0
set APP_NAME=find-non-ascii

echo.
echo INSTALL SCRIPT FOR %APP_NAME% PROGRAM
echo.
echo Actions to be performed for install:
echo 1. Create %USERPROFILE%\bin if it does not exist
echo 2. Create %USERPROFILE%\lib if it does not exist
echo 3. Copy %APP_NAME%.bat to %USERPROFILE%\bin
echo 4. Copy %APP_NAME%.jar to %USERPROFILE%\lib
echo 5. Add %USERPROFILE%\bin to current PATH variable
echo 6. Add %USERPROFILE%\bin to permanent PATH

choice /c YN /m "Continue with install"

if %ERRORLEVEL%==2 goto :END

echo.
if exist "%USERPROFILE%\bin" (
    echo Directory %USERPROFILE%\bin already exists.
) else (
    echo Creating directory: %USERPROFILE%\bin.
    mkdir "%USERPROFILE%\bin"
)

if exist "%USERPROFILE%\lib" (
    echo Directory %USERPROFILE%\lib already exists.
) else (
    echo Creating directory: %USERPROFILE%\lib.
    mkdir "%USERPROFILE%\lib"
)

echo Copying %APP_NAME%.bat to %USERPROFILE%\bin
copy "%MY_DIR%%APP_NAME%.bat" "%USERPROFILE%\bin"
echo Copying %APP_NAME%.jar to %USERPROFILE%\lib
copy "%MY_DIR%%APP_NAME%.jar" "%USERPROFILE%\lib"

echo Adding %USERPROFILE%\bin to PATH.
"%MY_DIR%add-to-path" "%USERPROFILE%\bin"

rem Pause if started from Windows Explorer so that the Command Prompt
rem doesn't disappear before the user can read it.
for /f "tokens=2" %%a in ("%CMDCMDLINE%") do (
    if "%%a"=="/c" (
        echo Installation of %APP_NAME% has finished.
        pause
    )
)

:END
endlocal

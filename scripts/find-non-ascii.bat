@echo off

rem //////////////////////////////////////////////////////////////////////
rem
rem Java Run Script
rem
rem Author:         Bill Chatfield
rem
rem License:        Apache License 2.0
rem
rem Instructions:   Name this script the same as the jar file you want
rem                 to run and then run it.
rem
rem //////////////////////////////////////////////////////////////////////


rem Skip over subroutine definitions.
goto :main


rem //////////////////////////////////////////////////////////////////////
rem
rem Subroutine to write a message to the standard output stream.
rem
rem //////////////////////////////////////////////////////////////////////
:writeMessage
echo %programName%: %*
exit /b
rem The above "exit" returns from this subroutine.


rem //////////////////////////////////////////////////////////////////////
rem
rem Subroutine to write a debug message if DEBUG is turned on.
rem
rem //////////////////////////////////////////////////////////////////////
:writeDebug
if defined DEBUG echo %programName%: DEBUG: %*
exit /b
rem The above "exit" returns from this subroutine.


rem //////////////////////////////////////////////////////////////////////
rem
rem Subroutine to write an error message to the standard error output
rem stream.
rem
rem //////////////////////////////////////////////////////////////////////
:writeError
echo %programName%: ERROR: %* >&2
exit /b
rem The above "exit" returns from this subroutine.


rem //////////////////////////////////////////////////////////////////////
rem
rem Main Program
rem
rem //////////////////////////////////////////////////////////////////////
:main
setlocal EnableDelayedExpansion
set programName=%~nx0
title %programName%
rem Determine the directory this script is in and then remove the trailing
rem backslash.
set binDir=%~dp0
set binDir=%binDir:~0,-1%
set jarName=%~n0.jar
set jarSearchDirs="%binDir%" "%binDir%\..\lib" "%USERPROFILE%\lib" "%USERPROFILE%\Documents\lib" "%USERPROFILE%\Dropbox\lib"
set javaSearchDirs="C:\j2sdk1.4*" "C:\j2sdk1.5*" "C:\Program Files (x86)\Java\jre*" "C:\Program Files (x86)\Java\jdk*" "C:\Program Files\Java\jre*" "C:\Program Files\Java\jdk*"

rem //////////////////////////////////////////////////////////////////////
rem
rem Find application jar file to run
rem
rem //////////////////////////////////////////////////////////////////////
set jar=
for /d %%d in (%jarSearchDirs%) do (
    set checkJar=%%~d\%jarName%
    call :writeDebug Checking checkJar=!checkJar!
    if exist "!checkJar!" (
        set jar=!checkJar!
        call :writeDebug Found jar=!jar!
        rem No need to waste CPU time continuing this loop.
        goto :endOfJarSearch
    )
)
:endOfJarSearch

if not defined jar (
    call :writeError Failed to find %jarName% in any of these locations:
    for %%j in (%jarSearchDirs%) do (
        call :writeError + %%~j
    )
    call :writeError Please move %jarName% into one of the above locations
    call :writeError and try again.
    goto :end
)

rem //////////////////////////////////////////////////////////////////////
rem
rem Find java.exe, taking into account the JAVA_HOME setting.
rem
rem //////////////////////////////////////////////////////////////////////

rem Clear java variable so we can tell later if we set it to something.
set java=

rem Check for java.exe in JAVA_HOME\bin
if defined JAVA_HOME (
    if exist "!JAVA_HOME!" (
        set checkJava=!JAVA_HOME!\bin\java.exe
        if exist "!checkJava!" (
            set java=!checkJava!
        ) else (
            call :writeError JAVA_HOME is broken because !JAVA_HOME!\bin\java.exe is missing.
        )
    ) else (
        call :writeError The JAVA_HOME directory !JAVA_HOME! does not exist.
    )
    if not defined java (
        call :writeError Please fix your JAVA_HOME environment variable.
        call :writeError Continuing to search for a working Java installation.
    )
)

rem 
rem Check for java in the PATH. This should be the case for a modern
rem version of java, unless the user has modified their PATH variable.
rem
if not defined java (
    where java
    if %ERRORLEVEL%==0 (
        set java=java
    )
)

rem
rem Find the highest version of Java. The last one set will be the
rem highest version because they're processed in alphabetic order.
rem
if not defined java (
    for /d %%j in (%javaSearchDirs%) do (
        set checkJava=%%j\bin\java.exe
        call :writeDebug checkJava=!checkJava!
        if exist "!checkJava!" (
            set java=!checkJava!
        )
    )
)

rem 
rem Handle missing Java.
rem 
if not defined java (
    call :writeError Failed to find Java in any standard install directory.
    call :writeError Please install Java or set JAVA_HOME to the directory
    call :writeError where you have installed Java.
    choice /c YN /m "Do you want to download and install Java now"
    if !ERRORLEVEL!==1 (
        rem The user answered "Yes".
        call :writeMessage Opening Java download web site in web browser...
        start https://www.java.com/download
        call :writeMessage Java Installation Instructions:
        call :writeMessage 1. Click the download button in your browser window.
        call :writeMessage 2. Wait for the Java install file to download.
        call :writeMessage 3. Double-click the Java install file that was downloaded.
        call :writeMessage 4. Follow the prompts to install Java.
        call :writeMessage 5. Run %programName% again after Java is installed.
    )
    goto :end
)

rem //////////////////////////////////////////////////////////////////////
rem
rem All parts found. Run the app.
rem
rem //////////////////////////////////////////////////////////////////////

title %jar%

"%java%" -jar "%jar%" %*

title %programName%

:end


rem //////////////////////////////////////////////////////////////////////
rem
rem Pause if started from GUI
rem
rem //////////////////////////////////////////////////////////////////////

call :writeDebug CMDCMDLINE=%CMDCMDLINE%

for /f "tokens=2" %%a in ("%CMDCMDLINE%") do (
    if "%%a"=="/c" (
        if defined jar (
            if defined java (
                call :writeMessage Application %jarName% has finished.
            )
        )
        pause
    )
)

title Command Prompt
endlocal


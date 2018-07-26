@echo off
setlocal
set MY_DIR=%~dp0
set MY_NAME=%~n0
rem Search for our runnable jar in my directory and ..\lib.
for %%j in ("%MY_DIR%%MY_NAME%.jar" "%MY_DIR%..\lib\%MY_NAME%.jar") do (
    if exist %%j (
        java -jar %%j %*
        goto :EOF
    )
)
rem If we got here, we failed.
echo Couldn't find runnable jar file %MY_NAME%.jar.
endlocal

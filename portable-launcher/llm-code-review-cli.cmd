@echo off
setlocal

REM Set JAVA_HOME
set "JAVA_HOME=%~dp0jre"

REM Check if java.exe exists
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Java runtime not found at: "%JAVA_HOME%\bin\java.exe"
    exit /b 1
)

REM Find JAR file (take the first one matching the pattern)
for %%F in ("%~dp0llm-code-review-cli-*.jar") do (
    set "JAR_FILE=%%F"
    goto found_jar
)
echo JAR file not found: "%~dp0llm-code-review-cli-*.jar"
exit /b 1

:found_jar

REM Run the Java application
"%JAVA_HOME%\bin\java.exe" ^
 -Dlogback.configurationFile="%~dp0logback.xml" ^
 --enable-native-access=ALL-UNNAMED ^
 -jar "%JAR_FILE%" ^
 %*

endlocal

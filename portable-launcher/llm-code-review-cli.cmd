@echo off
setlocal

set "JAVA_HOME=%~dp0jre"

"%JAVA_HOME%\bin\java.exe" ^
 -Dlogback.configurationFile="%~dp0logback.xml" ^
 --enable-native-access=ALL-UNNAMED ^
 -jar "%~dp0llm-code-review-cli-0.2.0-SNAPSHOT.jar" ^
 %*

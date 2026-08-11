@echo off
setlocal
set "APP_HOME=%~dp0"
if defined JAVA_HOME (
  set "JAVA_EXEC=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_EXEC=java.exe"
)
"%JAVA_EXEC%" -Xmx256m -Xss256k -XX:MaxMetaspaceSize=64m -XX:CompressedClassSpaceSize=16m -classpath "%APP_HOME%gradle\wrapper\gradle-wrapper.jar;%APP_HOME%gradle\wrapper\gradle-wrapper-shared.jar;%APP_HOME%gradle\wrapper\gradle-cli.jar;%APP_HOME%gradle\wrapper\gradle-files.jar;%APP_HOME%gradle\wrapper\gradle-stdlib-java-extensions.jar" org.gradle.wrapper.GradleWrapperMain %*
endlocal

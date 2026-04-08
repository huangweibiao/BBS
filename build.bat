@echo off
chcp 65001 >nul
echo ========================================
echo   BBS Project Build Script
echo ========================================
echo.

echo [1/3] Building Backend (Spring Boot)...
cd /d "%~dp0bbs-backend"
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo Backend build FAILED!
    pause
    exit /b 1
)
echo Backend build SUCCESS!
echo.

echo [2/3] Building Frontend (Vue 3)...
cd /d "%~dp0bbs-frontend"
call npm install
if %ERRORLEVEL% neq 0 (
    echo npm install FAILED!
    pause
    exit /b 1
)

call npm run build
if %ERRORLEVEL% neq 0 (
    echo Frontend build FAILED!
    pause
    exit /b 1
)
echo Frontend build SUCCESS!
echo.

echo [3/3] Copying Frontend to Backend...
if not exist "%~dp0bbs-backend\target\classes\static" mkdir "%~dp0bbs-backend\target\classes\static"
xcopy /E /Y "%~dp0bbs-frontend\dist\*" "%~dp0bbs-backend\target\classes\static\"
echo Frontend copied to backend static folder!
echo.

echo ========================================
echo   Build Complete!
echo ========================================
echo Backend JAR: bbs-backend\target\bbs-backend-1.0.0.jar
echo Frontend:    bbs-frontend\dist\
echo.
pause

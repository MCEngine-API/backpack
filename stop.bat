@echo off
echo Stopping process on port 8080...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    echo Found PID: %%a
    taskkill /PID %%a /F
)

echo Done.
pause

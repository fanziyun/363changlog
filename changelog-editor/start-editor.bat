@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo 正在安装依赖...
call npm install --silent 2>nul
echo 正在启动编辑器和浏览器...
start http://localhost:5173
start /B npm run dev
exit

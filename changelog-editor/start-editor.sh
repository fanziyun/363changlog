#!/bin/bash
# 363Changelog Editor — 一键启动脚本
cd "$(dirname "$0")"
echo "正在安装依赖..."
npm install --silent 2>/dev/null
echo "正在启动编辑器和浏览器..."
xdg-open http://localhost:5173 2>/dev/null || open http://localhost:5173 2>/dev/null
npm run dev &
exit 0

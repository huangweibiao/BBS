#!/bin/bash

echo "========================================"
echo "  BBS Project Build Script"
echo "========================================"
echo

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "[1/3] Building Backend (Spring Boot)..."
cd "$SCRIPT_DIR/bbs-backend"
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "Backend build FAILED!"
    exit 1
fi
echo "Backend build SUCCESS!"
echo

echo "[2/3] Building Frontend (Vue 3)..."
cd "$SCRIPT_DIR/bbs-frontend"
npm install
if [ $? -ne 0 ]; then
    echo "npm install FAILED!"
    exit 1
fi

npm run build
if [ $? -ne 0 ]; then
    echo "Frontend build FAILED!"
    exit 1
fi
echo "Frontend build SUCCESS!"
echo

echo "[3/3] Copying Frontend to Backend..."
mkdir -p "$SCRIPT_DIR/bbs-backend/target/classes/static"
cp -r "$SCRIPT_DIR/bbs-frontend/dist/"* "$SCRIPT_DIR/bbs-backend/target/classes/static/"
echo "Frontend copied to backend static folder!"
echo

echo "========================================"
echo "  Build Complete!"
echo "========================================"
echo "Backend JAR: bbs-backend/target/bbs-backend-1.0.0.jar"
echo "Frontend:    bbs-frontend/dist/"
echo

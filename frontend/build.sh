#!/bin/bash
cd ../frontend
npm install
npm run build

# 🆕 Skapa statisk mapp om den inte finns
mkdir -p ../backend/src/main/resources/static

# Rensa och kopiera React-bygget
rm -rf ../backend/src/main/resources/static/*
cp -r build/* ../backend/src/main/resources/static/

cd ../backend
chmod +x gradlew  
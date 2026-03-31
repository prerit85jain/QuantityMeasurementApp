#!/usr/bin/env bash
# render-build.sh — called by Render during the build phase
set -e
echo "==> Building Spring Boot JAR (skipping tests for speed)..."
./mvnw clean package -DskipTests
echo "==> Build complete."

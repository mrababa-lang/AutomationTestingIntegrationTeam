#!/usr/bin/env bash
set -euo pipefail

echo "Running test suite..."
mvn clean test

echo "Opening Allure report..."
mvn allure:serve

#!/usr/bin/env bash
set -euo pipefail

echo "Running test suite..."
mvn clean test

echo "Extent report generated at target/extent-report/extent-report.html"

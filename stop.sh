#!/bin/bash
set -euo pipefail

moduleName="$1"

function cleanup() {
  docker-compose down &> /dev/null || true
}

trap cleanup EXIT

cd "$(dirname "$0")/$moduleName"
echo Stopping module "$PWD"
cleanup

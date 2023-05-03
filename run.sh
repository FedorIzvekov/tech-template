#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

moduleName="$1"

function cleanup() {
  docker compose down &> /dev/null || true
}

sh ./build.sh & wait $!

cleanup

cd "$moduleName"
echo Running module "$PWD"
docker compose up -d
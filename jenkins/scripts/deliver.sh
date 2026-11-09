#!/usr/bin/env bash

set -euo pipefail

echo 'Resultado.'
java -jar target/my-app-1.0-SNAPSHOT.jar --jenkins

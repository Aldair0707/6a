#!/usr/bin/env bash

set -euo pipefail

echo 'Calculating with the p and dof configured in App.java.'
java -jar target/my-app-1.0-SNAPSHOT.jar --jenkins

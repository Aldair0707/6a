#!/usr/bin/env bash

set -euo pipefail

CASE_TO_RUN=1

echo "Running required case ${CASE_TO_RUN} from the Maven-built application."
java -jar target/my-app-1.0-SNAPSHOT.jar --case "${CASE_TO_RUN}"

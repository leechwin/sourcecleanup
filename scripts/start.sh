#!/bin/sh
set -euo pipefail
IFS=$'\n\t'

java -jar source-cleanup.jar $@


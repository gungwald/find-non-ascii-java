#!/bin/sh
SCRIPT_DIR=$(cd "$(dirname "$0")" ; pwd)
JAR=$(ls "$SCRIPT_DIR"/find-non-ascii*.jar)
java -jar "$JAR" -i "$SCRIPT_DIR"

#!/bin/sh

JAVA_DIST=jdk
APP_JAR=sm2csv.jar

DIR="$(cd "$(dirname "$0")" ; pwd -P)"
JAVA_EXE=$DIR/$JAVA_DIST/bin/java
APP_JAR_PATH=$DIR/$APP_JAR

exec $JAVA_EXE -jar $APP_JAR_PATH $@

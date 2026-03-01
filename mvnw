#!/bin/sh
# Maven Wrapper script
# Downloads and runs Maven using the wrapper configuration

set -e

# Determine the project base directory
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
export MAVEN_PROJECTBASEDIR

MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

# Read distribution URL from properties
if [ -f "$MAVEN_WRAPPER_PROPERTIES" ]; then
    distributionUrl=$(grep 'distributionUrl' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
fi

MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6"

if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
    echo "Downloading Maven..."
    mkdir -p "$MAVEN_HOME"
    TMP_FILE=$(mktemp)
    curl -sL "$distributionUrl" -o "$TMP_FILE"
    unzip -qo "$TMP_FILE" -d "$HOME/.m2/wrapper/dists/"
    rm "$TMP_FILE"
    # The zip extracts to apache-maven-3.9.6/ directory
    if [ -d "$HOME/.m2/wrapper/dists/apache-maven-3.9.6" ]; then
        MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6"
    fi
fi

exec "$MAVEN_HOME/bin/mvn" "$@"

#!/bin/bash

set -e

VERSIONS_DIR="versions"
GENERIC_PROPERTIES="gradle.properties"
GENERIC_MOD_JSON="src/main/resources/fabric.mod.json"

for version in "$VERSIONS_DIR"/*; do
    if [ -d "$version" ]; then
        MC_VERSION=$(basename "$version")
        echo "== Building for Minecraft $MC_VERSION =="

        cp "$version/gradle.properties" "$GENERIC_PROPERTIES"
        cp "$version/fabric.mod.json" "$GENERIC_MOD_JSON"

        ./gradlew build

        echo "Done: $MC_VERSION"
    fi
done

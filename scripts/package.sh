#!/usr/bin/env bash
shopt -s expand_aliases
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

if [ -z "$DIST_CODEBASE_URL" ]; then
    echo "What is codebase url?  DIST_CODEBASE_URL must be set."
    exit 1
fi

CODEBASE_URL=$1

# build apps and launcher
gradle clean build

# copy apps + dependencies to cytoscape distribution
for app in `find "$DEV_APPS_DIR" -mindepth 1 -maxdepth 1 -type d`; do
    env -i DEV_CY3_BUNDLE_DIR="$DIST_CY3_BUNDLE_DIR" bash "$app/scripts/deploy.sh"
done

# zip cytoscape distribution
pushd "$DIST_TOOLS_DIR" > /dev/null
zip -r "$DEV_DIR/build/cytoscape.zip" cytoscape
popd > /dev/null

# package and sign launcher
DEV_LAUNCHER_JAR="$DEV_DIR/build/libs/sdp-cytoscape3.jar"
pushd "$DEV_DIR/build" > /dev/null
    # build secure manifest
    cp "$DIST_RESOURCES_DIR/MANIFEST.MF" "$DEV_DIR/build"
    sed -i "s#\$DIST_NAME#$DIST_NAME#g" "$DEV_DIR/build/MANIFEST.MF"
    sed -i "s#\$DIST_CODEBASE_URL#$DIST_CODEBASE_URL#g" "$DEV_DIR/build/MANIFEST.MF"

    # build secure jnlp
    cp "$DIST_RESOURCES_DIR"/jnlp/* "$DEV_DIR/build"
    sed -i "s#CODEBASE_URL#$DIST_CODEBASE_URL#g" "$DIST_JNLP_FILE"
    mkdir "$DEV_DIR/build/JNLP-INF"
    cp "$DIST_JNLP_FILE" "$DEV_DIR/build/JNLP-INF/APPLICATION.JNLP"

    # package
    jar -ufm "$DEV_LAUNCHER_JAR" "$DEV_DIR/build/MANIFEST.MF"
    jar -uf "$DEV_LAUNCHER_JAR" $(basename "$DEV_DIR/build/JNLP-INF")
    jar -uf "$DEV_LAUNCHER_JAR" "cytoscape.zip"

    # sign
    jarsigner -keystore  "$DIST_KEYSTORE_FILE" \
              -storepass "$DIST_KEYSTORE_PASS" \
              -tsa "http://tsa.starfieldtech.com" \
              "$DEV_LAUNCHER_JAR" "$DIST_SIGNING_ALIAS"

popd > /dev/null

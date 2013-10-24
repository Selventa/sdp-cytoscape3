#!/usr/bin/env bash
shopt -s expand_aliases
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

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
DEV_LAUNCHER_JAR="$DEV_DIR/build/libs/sdp-cytoscape.jar"
pushd "$DEV_DIR/build" > /dev/null
jar -uf "$DEV_LAUNCHER_JAR" "cytoscape.zip"
jarsigner -keystore  "$DIST_KEYSTORE_FILE" \
          -storepass "$DIST_KEYSTORE_PASS" \
          "$DEV_LAUNCHER_JAR" "$DIST_SIGNING_ALIAS"
popd > /dev/null

# build gzip for jnlp
tar cf "$DEV_DIR/build/sdp-cytoscape.tar" -C "$DEV_DIR/build/libs" .
tar uf "$DEV_DIR/build/sdp-cytoscape.tar" -C "$DIST_RESOURCES_DIR/jnlp" .
gzip "$DEV_DIR/build/sdp-cytoscape.tar"

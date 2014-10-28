#!/usr/bin/env bash
shopt -s expand_aliases
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

if [ -z "$DIST_CODEBASE_URL" ]; then
    echo "Set DIST_CODEBASE_URL." 1>&2;
    exit 1
fi

CODE_SHA=$(find . -type f \
    \( -name "*.java" -or -name "*.groovy" \) -and \
    \( -path "./launcher*" -or -path "./apps/kam-nav/modules*" -or \
    -path "./apps/model-builder/modules*" \) | \
    xargs cat | sha256sum | cut -d' ' -f1)

echo "Name: $DIST_NAME"
echo "Description: $DIST_DESCRIPTION"
echo "Version: $DIST_VERSION"
echo "URL: $DIST_CODEBASE_URL"
echo "SHA: $CODE_SHA"
echo "Cytoscape Distribution: $DIST_CY3_DIR"

# build apps and launcher
echo "...building project"
gradle -q clean build > /dev/null || exit 1

# copy apps + dependencies to cytoscape distribution
echo "...deploying project jars into cytoscape"
for app in `find "$DEV_APPS_DIR" -mindepth 1 -maxdepth 1 -type d`; do
    env -i DEV_CY3_BUNDLE_DIR="$DIST_CY3_BUNDLE_DIR" bash "$app/scripts/deploy.sh"
done

# make deploy dir
echo "...mkdir $DEV_BUILD_DIR/deploy"
mkdir "$DEV_BUILD_DIR/deploy"

# zip cytoscape distribution
echo "...packaging cytoscape zip (from $DIST_CY3_DIR)"
pushd "$DEV_BUILD_DIR" > /dev/null
    ln -s "$DIST_CY3_DIR" cytoscape
    zip -qr "$DEV_BUILD_DIR/cytoscape.zip" cytoscape
    unlink cytoscape
popd > /dev/null
echo "...adding zip to $DEV_BUILD_DIR/libs/sdp-cytoscape3.jar"
jar -uf "$DEV_BUILD_DIR/libs/sdp-cytoscape3.jar" -C "$DEV_BUILD_DIR" "cytoscape.zip"
echo "...copying $DEV_BUILD_DIR/libs/sdp-cytoscape3.jar to $DEV_BUILD_DIR/deploy"
cp "$DEV_BUILD_DIR/libs/sdp-cytoscape3.jar" "$DEV_BUILD_DIR/deploy"

# build getdown from tools dir
echo "...building samskivert + getdown"
pushd "$TOOLS_SAMSKIVERT_DIR" > /dev/null
    mvn -q clean install -DskipTests > /dev/null
    if [ "$?" != "0" ]; then
        echo "...samskivert build failed"
        exit 1
    fi
popd > /dev/null
pushd "$TOOLS_GETDOWN_DIR" > /dev/null
    mvn -q clean package > /dev/null
    if [ "$?" != "0" ]; then
        echo "...getdown build failed"
        exit 1
    fi
popd > /dev/null

# copy to build
echo "...copying $TOOLS_GETDOWN_JAR_FILE to $DIST_GETDOWN_BUILD_JAR"
cp "$TOOLS_GETDOWN_JAR_FILE" "$DIST_GETDOWN_BUILD_JAR"
echo "...copying $DIST_RESOURCES_DIR/deploy to $DEV_BUILD_DIR"
cp "$DIST_RESOURCES_DIR"/deploy/* "$DEV_BUILD_DIR/deploy"

echo "...filtering $DEV_BUILD_DIR/deploy/getdown.txt"
sed -i "s#\$DIST_NAME#$DIST_NAME#g" "$DEV_BUILD_DIR/deploy/getdown.txt"
sed -i "s#\$DIST_CODEBASE_URL#$DIST_CODEBASE_URL#g" "$DEV_BUILD_DIR/deploy/getdown.txt"
echo "...filtering $DEV_BUILD_DIR/deploy/cytoscape3.jnlp"
sed -i "s#\$DIST_NAME#$DIST_NAME#g" "$DEV_BUILD_DIR/deploy/cytoscape3.jnlp"
sed -i "s#\$DIST_DESCRIPTION#$DIST_DESCRIPTION#g" "$DEV_BUILD_DIR/deploy/cytoscape3.jnlp"
sed -i "s#\$DIST_CODEBASE_URL#$DIST_CODEBASE_URL#g" "$DEV_BUILD_DIR/deploy/cytoscape3.jnlp"

# add secure manifest
echo "...updating $DIST_GETDOWN_BUILD_JAR with manifest - $DEV_BUILD_DIR/MANIFEST.MF"
cp "$DIST_RESOURCES_DIR/MANIFEST.MF" "$DEV_BUILD_DIR"
sed -i "s#\$DIST_NAME#$DIST_NAME#g" "$DEV_BUILD_DIR/MANIFEST.MF"
sed -i "s#\$DIST_CODEBASE_URL#$DIST_CODEBASE_URL#g" "$DEV_BUILD_DIR/MANIFEST.MF"
jar -ufm "$DIST_GETDOWN_BUILD_JAR" "$DEV_BUILD_DIR/MANIFEST.MF"

# build secure jnlp
echo "...updating $DIST_GETDOWN_BUILD_JAR with dir - $DEV_BUILD_DIR/JNLP-INF"
mkdir "$DEV_BUILD_DIR/JNLP-INF"
cp "$DEV_BUILD_DIR/deploy/cytoscape3.jnlp" "$DEV_BUILD_DIR/JNLP-INF/APPLICATION.JNLP"
jar -uf "$DIST_GETDOWN_BUILD_JAR" -C "$DEV_BUILD_DIR" "JNLP-INF"

# sign
echo "...signing"
jarsigner -keystore  "$DIST_KEYSTORE_FILE" \
          -storepass "$DIST_KEYSTORE_PASS" \
          -tsa "http://tsa.starfieldtech.com" \
          "$DIST_GETDOWN_BUILD_JAR" "$DIST_SIGNING_ALIAS"
if [ "$?" != "0" ]; then
    exit 1
fi
echo "...verifying signature"
jarsigner -verify -strict "$DIST_GETDOWN_BUILD_JAR"
if [ "$?" != "0" ]; then
    exit 1
fi

echo "...copying $DIST_GETDOWN_BUILD_JAR to $DEV_BUILD_DIR/deploy"
cp "$DIST_GETDOWN_BUILD_JAR" "$DEV_BUILD_DIR/deploy"

echo "...generating changelog to include $DIST_NAME / $DIST_VERSION"
HAS_DOCUTILS=$(hash rst2html > /dev/null 2>&1)
if [ $? -ne 0 ]; then
    echo "rst2html cannot not be found; install docutils" 1>&2;
    exit 1
fi
rst2html --stylesheet-path="${PROJ_STYLE}" "${PROJ_CHANGELOG}" > "$DEV_BUILD_DIR/deploy/changelog.html"

echo "...generating local digest.txt"
java -cp "$DIST_GETDOWN_BUILD_JAR" com.threerings.getdown.tools.Digester "$DEV_BUILD_DIR/deploy"

echo "...writing version info to $DEV_BUILD_DIR/deploy/version"
cat << EOF > "$DEV_BUILD_DIR/deploy/version"
Name: $DIST_NAME
Description: $DIST_DESCRIPTION
Version: $DIST_VERSION
URL: $DIST_CODEBASE_URL
SHA: $CODE_SHA
EOF

#!/usr/bin/env bash
shopt -s expand_aliases
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1
. "${DEV_SCRIPTS_DIR}/util.sh"

if [ $# -ne 2 ]; then
    echo "usage: ${BASH_SOURCE[0]} [BUCKET] [CODEBASE URL]" 1>&2;
    exit 1
fi

BUCKET="$1"
CODEBASE_URL="$2"
if [ -n "$CODEBASE_URL" ]; then
    export DIST_CODEBASE_URL="$CODEBASE_URL"
fi

echo "S3 Bucket: $BUCKET"
echo "Codebase url: $DIST_CODEBASE_URL"
echo ""

echo "Packaging"
echo "---------"
"$DEV_SCRIPTS_DIR"/package.sh || exit 1
echo "---------"
echo ""

DEPLOY_FILES=(changelog.html cytoscape3.jnlp digest.txt \
              getdown-latest.jar getdown.txt index.html \
              java_linux_32.jar java_linux_64.jar java_macosx_64.jar \
              java_windows_32.jar java_windows_64.jar logo.png \
              sdp-cytoscape3.jar splash.png version)
if [ ! -d "$DEV_BUILD_DIR/deploy" ]; then
    echo "Deploy directory, $DEV_BUILD_DIR/deploy, does not exist." 1>&2;
    exit 1
else
    for file in ${DEPLOY_FILES[@]}; do
        if [ ! -f "$DEV_BUILD_DIR/deploy/$file" ]; then
            echo "$file does not exist in $DEV_BUILD_DIR/deploy, exiting..."
            exit 1
        fi
    done
fi

echo "Deploying to S3"
echo "---------------"

NOT_FOUND=22
OLD_IFS=$IFS
EXISTING=$(curl -sf $DIST_CODEBASE_URL/version)
if [ $? -eq $NOT_FOUND ]; then
    echo "...$DIST_CODEBASE_URL/version does not exist; deploy will continue"
else
    IFS=$':'
    NEW=$(cat "$DEV_BUILD_DIR"/deploy/version)
    echo -e "...$DIST_CODEBASE_URL/version exists; \
        \n\nexiting version is \n$EXISTING \
        \n\nnew version is \n$NEW"
    EXISTING_SHA=$(echo $EXISTING | grep SHA | tr -d 'SHA ')
    NEW_SHA=$(echo $NEW | grep SHA | tr -d 'SHA ')

    if [ "$EXISTING_SHA" == "$NEW_SHA" ]; then
        echo "No deploy needed.  SHAs match for $NEW_SHA."
        exit 0
    else
        echo "...$DIST_CODEBASE_URL/version is old; deploy will continue"
    fi
fi
IFS=$OLD_IFS

echo "...to $BUCKET"
for file in ${DEPLOY_FILES[@]}; do
    MIME_TYPE="$(readmime $(basename $file))"
    echo "Deploy $file to $BUCKET (mime: $MIME_TYPE)"
    s3 -q --no-progress -m "$MIME_TYPE" put "$DEV_BUILD_DIR/deploy/$file" "$BUCKET"
    if [ $? != 0 ]; then
        echo "Failure to send $DEV_BUILD_DIR/deploy/$file, exiting..." 1>&2;
        exit 1
    fi
done
echo "---------"

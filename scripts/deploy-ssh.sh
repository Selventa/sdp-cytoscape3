#!/usr/bin/env bash
shopt -s expand_aliases
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

if [ $# -ne 2 ] && [ $# -ne 3 ]; then
    echo "usage: ${BASH_SOURCE[0]} HOST REMOTE DIR [CODEBASE URL]" 1>&2;
    exit 1
fi

HOST="$1"
REMOTE_DIR="$2"
CODEBASE_URL="$3"
if [ -n "$CODEBASE_URL" ]; then
    export DIST_CODEBASE_URL="$CODEBASE_URL"
fi

echo "Host: $HOST"
echo "Remote dir: $REMOTE_DIR"
echo "Codebase url: $DIST_CODEBASE_URL"
echo ""

echo "Packaging"
echo "---------"
"$DEV_SCRIPTS_DIR"/package-getdown.sh || exit 1
echo "---------"
echo ""

if [ ! -d "$DEV_BUILD_DIR/deploy" ]; then
    echo "Deploy directory, $DEV_BUILD_DIR/deploy, does not exist." 1>&2;
    exit 1
fi

echo "Deploying"
echo "---------"

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

echo "...to $HOST:$REMOTE_DIR"
scp -r "$DEV_BUILD_DIR"/deploy/* $HOST:$REMOTE_DIR
echo "---------"

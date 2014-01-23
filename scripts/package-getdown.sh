#!/usr/bin/env bash
shopt -s expand_aliases
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

pushd "$TOOLS_GETDOWN_DIR" > /dev/null
    echo "Fetching getdown..."
    git pull -q origin master > /dev/null
    if [ "$?" != "0" ]; then
        echo "...fetch failed"
        exit 1
    fi

    echo "Building getdown..."
    mvn -q clean package > /dev/null
    if [ "$?" != "0" ]; then
        echo "...build failed"
        exit 1
    fi
popd > /dev/null

file "$TOOLS_GETDOWN_JAR_FILE"

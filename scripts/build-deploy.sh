#!/usr/bin/env bash
#
# build-deploy.sh: builds and deploys sdp-cytoscape project to a remote
# server.
#
# usage: build-deploy.sh <host> [user]

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../

case $# in
2)
    SERVER_HOST=$1
    SERVER_PATH=$2
    ;;
3)
    SERVER_HOST=$1
    SERVER_PATH=$2
    SERVER_USER=$3
    ;;
*)
    echo "usage: build-deploy.sh <remote host> <remote path> [remote user]"
    exit 1
esac 

echo "building sdp-cytoscape"
export URL_ROOT="http://$SERVER_HOST/"
buildr -s clean package
if [ $? != 0 ]; then
    echo "build failed"
    exit 2
fi

cat distribution/cytoscape.site/target/cytoscape.site-*.tgz | ssh "$SERVER_HOST" tar xzf - -C "$SERVER_PATH"

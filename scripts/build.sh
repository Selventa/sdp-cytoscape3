#!/usr/bin/env bash
shopt -s expand_aliases
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

# model-builder
gradle compileJava compileJarApps

# bel-nav
cd apps/bel-nav
mvn package

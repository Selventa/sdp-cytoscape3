#!/usr/bin/env bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

for app in `find "$DEV_APPS_DIR" -mindepth 1 -maxdepth 1 -type d`; do
    env -i DEV_CY3_BUNDLE_DIR="$DEV_CY3_BUNDLE_DIR" bash "$app/scripts/deploy.sh"
done

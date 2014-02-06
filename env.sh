#!/usr/bin/env bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# load custom environment if available
if [ -r "env.sh.custom" ]; then
    source env.sh.custom || exit 1
fi

# TODO Clean up this mess.  Why did I dictate DEV or DIST?  Break up
# environment vars by what they are not how they are used (e.g. tools,
# scripts, build, deploy).  Beginning to with -- Tools -- section.

# -- Development --
export DEV_DIR="${DEV_DIR:=$DIR}"
export DEV_BUILD_DIR="${DEV_BUILD_DIR:=$DEV_DIR/build}"
export DEV_APPS_DIR="${DEV_APPS_DIR:=$DEV_DIR/apps}"
export DEV_SCRIPTS_DIR="${DEV_SCRIPTS_DIR:=$DEV_DIR/scripts}"
export DEV_TOOLS_DIR="${DEV_TOOLS_DIR:=$DEV_DIR/tools}"
export DEV_TOOLS_GROOVY_DIR="${DEV_TOOLS_GROOVY_DIR:=$DEV_TOOLS_DIR/groovy}"
export DEV_CY3_DIR="${DEV_CY3_DIR:=$DEV_TOOLS_DIR/cytoscape}"
export DEV_CY3_LIBS_DIR="${DEV_CY3_LIBS_DIR:=$DEV_CY3_DIR/framework/system}"
export DEV_CY3_WORK_DIR="${DEV_CY3_WORK_DIR:=$DEV_CY3_DIR/work}"
export DEV_CY3_BUNDLE_DIR="${DEV_CY3_BUNDLE_DIR:=$DEV_CY3_WORK_DIR/bundles}"
export DEV_CY3_DATA_DIR="${DEV_CY3_DATA_DIR:=$DEV_CY3_WORK_DIR/data}"
export DEV_BUILD_FILE="${DEV_BUILD_FILE:=$DEV_DIR/build.gradle}"
export DEV_CY3_LOG_FILE="${DEV_CY3_LOG_FILE:=$DEV_CY3_WORK_DIR/cytoscape.log}"
alias gradle="${DEV_SCRIPTS_DIR}/gradlew --daemon"

# -- Distribution --
export DIST_DIR="${DIST_DIR:=$DIR}"
export DIST_RESOURCES_DIR="${DIST_RESOURCES_DIR:=$DIST_DIR/resources}"
export DIST_TOOLS_DIR="${DIST_TOOLS_DIR:=$DIST_DIR/tools/distribution}"
export DIST_CY3_DIR="${DIST_CY3_DIR:=$DIST_TOOLS_DIR/cytoscape}"
export DIST_CY3_BUNDLE_DIR="${DIST_CY3_BUNDLE_DIR:=$DIST_CY3_DIR/framework/plugins}"
export DIST_NAME="${DIST_NAME:=SDP Cytoscape}"
export DIST_DESCRIPTION="${DIST_DESCRIPTION:=SDP Model building an analysis on Cytoscape.}"
export DIST_VERSION="${DIST_VERSION:=1.0.0}"
export DIST_CODEBASE_URL="${DIST_CODEBASE_URL:=http://cytoscape.selventa.com/}"
export DIST_JNLP_FILE="${DIST_JNLP_FILE:=$DIST_RESOURCES_DIR/jnlp/cytoscape3.jnlp}"
export DIST_GETDOWN_BUILD_JAR="${DIST_GETDOWN_BUILD_JAR:=$DEV_BUILD_DIR/getdown-latest.jar}"
export DIST_SIGNING_ALIAS="signing"
export DIST_KEYSTORE_PASS="thisislooney"
export DIST_KEYSTORE_FILE="${DIST_KEYSTORE_FILE:=$DIST_RESOURCES_DIR/signing/keystore}"

# -- Tools --
export TOOLS_DIR="${TOOLS_DIR:=$DEV_DIR/tools}"
export TOOLS_GETDOWN_DIR="${TOOLS_GETDOWN_DIR:=$TOOLS_DIR/getdown}"
export TOOLS_GETDOWN_JAR_FILE="${TOOLS_GETDOWN_JAR_FILE:=$TOOLS_GETDOWN_DIR/target/getdown-1.4-SNAPSHOT.jar}"

# -- Project files --
export PROJ_CHANGELOG="${PROJ_CHANGELOG:=$DIR/CHANGELOG.rst}"
export PROJ_STYLE="${PROJ_STYLE:=$DIST_RESOURCES_DIR/css/style.css}"

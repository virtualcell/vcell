#!/usr/bin/env bash
#
# Dev launcher for the VCell desktop client from a source build.
#
# Mirrors the JVM options used by the install4j installer
# (docker/build/installers/VCell.install4j) so the from-source client behaves
# like a shipped one — in particular the `--add-exports` that Java 17 needs for
# image handling, and the server host / prefix / bioformats properties.
#
# Usage:
#   ./vcell.sh [--debug-bridge[=PORT]] [--field-viewer[=URL]] [extra VCellClientMain args...]
#
# Options:
#   --debug-bridge[=PORT]  Enable the dev-only Swing debug bridge (loopback HTTP,
#                          default port 9123). See
#                          vcell-client/src/main/java/org/vcell/client/debug/README.md
#   --field-viewer[=URL]   Enable the browser-based 3D field viewer (the loopback field
#                          server + the "View in 3D" button on the PDE results panel).
#                          The page is served from webapp-viewer/ in this checkout, so the
#                          page and its data share an origin. Pass URL to override that
#                          and point at a viewer you are serving yourself.
#
# Environment overrides:
#   VCELL_API_HOST          api server host[:port]  (default vcell.cam.uchc.edu:443)
#   VCELL_SOFTWARE_VERSION  version string          (default Rel_Version_8.0.0_build_00)
#
# Server hosts:
#   production  vcell.cam.uchc.edu:443       (default)
#   dev         vcell-dev.cam.uchc.edu:443
#   (vcellapi.cam.uchc.edu is defunct)
#
# Examples:
#   ./vcell.sh                                            # connect to production
#   VCELL_API_HOST=vcell-dev.cam.uchc.edu:443 ./vcell.sh  # connect to dev
#   ./vcell.sh --debug-bridge                             # bridge on :9123
#   ./vcell.sh --field-viewer                             # 3D field viewer on
#   ./vcell.sh --field-viewer=http://localhost:4400/      # serve the page yourself instead
#   ./vcell.sh --debug-bridge=9200 model.vcml            # bridge on :9200, open model

set -eo pipefail

VCELL_API_HOST="${VCELL_API_HOST:-vcell.cam.uchc.edu:443}"
VCELL_SOFTWARE_VERSION="${VCELL_SOFTWARE_VERSION:-Rel_Version_8.0.0_build_00}"

MAIN_CLASS=cbit.vcell.client.VCellClientMain
CLASSPATH="./vcell-client/target/maven-jars/*:./vcell-client/target/*"

# Separate our own flags from arguments passed through to the client.
bridge_props=()
viewer_props=()
passthrough=()
bridge_note=""
viewer_note=""
for arg in "$@"; do
  case "$arg" in
    --debug-bridge)
      bridge_props=(-Dvcell.debugBridge=true)
      bridge_note=" debug-bridge=on(:9123)"
      ;;
    --debug-bridge=*)
      port="${arg#*=}"
      bridge_props=(-Dvcell.debugBridge=true "-Dvcell.debugBridge.port=${port}")
      bridge_note=" debug-bridge=on(:${port})"
      ;;
    --field-viewer)
      # webapp-viewer has no build step — the source directory is the deployable — so point
      # the field server straight at it and let the page and its data share an origin.
      viewer_props=(-Dvcell.fieldViewer.enabled=true)
      viewer_note=" field-viewer=on"
      if [ -f ./webapp-viewer/index.html ]; then
        viewer_props+=("-Dvcell.fieldViewer.staticDir=${PWD}/webapp-viewer")
        viewer_note=" field-viewer=on(webapp-viewer/)"
        if ! ls ./webapp-viewer/assets/vtk-wasm/*.tar.gz >/dev/null 2>&1; then
          echo "NOTE: the vtk.wasm bundle is missing, so the viewer page will not render." >&2
          echo "      Fetch it once with:  ( cd webapp-viewer && npm run fetch:vtk-wasm )" >&2
        fi
      fi
      ;;
    --field-viewer=*)
      url="${arg#*=}"
      viewer_props=(-Dvcell.fieldViewer.enabled=true "-Dvcell.fieldViewer.url=${url}")
      viewer_note=" field-viewer=on(${url})"
      ;;
    *)
      passthrough+=("$arg")
      ;;
  esac
done

# Fail early with a helpful message if the source build hasn't produced jars.
if ! ls ./vcell-client/target/*.jar >/dev/null 2>&1 \
   && ! ls ./vcell-client/target/maven-jars/*.jar >/dev/null 2>&1; then
  echo "ERROR: no build artifacts under vcell-client/target/." >&2
  echo "Build the project first, e.g.:  mvn clean install -DskipTests" >&2
  exit 1
fi

# JVM options mirrored from the install4j launcher vmOptions.
vmopts=(
  --add-exports java.desktop/sun.awt.image=ALL-UNNAMED
  "-Dvcell.installDir=$PWD"
  -Dvcell.autoflushlog=true
  -Dvcell.thirdPartyLicense=thirdpartylicenses.txt
  "-Dvcell.softwareVersion=${VCELL_SOFTWARE_VERSION}"
  "-Dvcell.serverHost=${VCELL_API_HOST}"
  -Dvcell.serverPrefix.v0=/api/v0
  -Dvcell.serverPrefix.v1=/api/v1
  -Dvcell.onlineResourcesURL=http://vcell.org
  -Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.9-jar-with-dependencies.jar
  -Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.9-jar-with-dependencies.jar
  -Dvcell.imagej.plugin.url=http://vcell.org/webstart
)

echo "VCell client (dev): host=${VCELL_API_HOST} version=${VCELL_SOFTWARE_VERSION}${bridge_note}${viewer_note}" >&2

exec java "${vmopts[@]}" ${bridge_props[@]+"${bridge_props[@]}"} ${viewer_props[@]+"${viewer_props[@]}"} \
  -cp "$CLASSPATH" "$MAIN_CLASS" \
  --api-host="$VCELL_API_HOST" ${passthrough[@]+"${passthrough[@]}"}

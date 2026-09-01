#!/usr/bin/env bash
#
# Fast-iteration launcher for the VCell desktop client with the Swing debug
# bridge enabled — runs straight from the modules' target/classes so a
# `mvn compile -pl vcell-client -am` (or a single javac) is enough to test a
# change, no packaging step. Complements ./vcell.sh, which runs packaged jars.
#
# Classpath = each module's target/classes PREPENDED to vcell-client's
# dependency classpath, so freshly compiled classes shadow the jars from the
# last full build. Native libs / bundled JRE come from a local install4j
# install (default ~/Applications/VCell_Alpha).
#
# Usage:
#   tools/debug-bridge/launch-client.sh [options] [-- extra VCellClientMain args]
#
# Options:
#   --port=N        debug bridge port (default 9123)
#   --refresh-cp    regenerate the cached dependency classpath
#   --foreground    run in the foreground instead of detaching
#
# Environment overrides:
#   VCELL_INSTALL_DIR       local install4j install (default ~/Applications/VCell_Alpha)
#   VCELL_API_HOST          server host[:port]      (default vcell-dev.cam.uchc.edu:443)
#   VCELL_SOFTWARE_VERSION  version string          (default Alpha_Version_8.0.0_build_07)
#
# The client's stdout/stderr are redirected by the client itself to
# ~/.vcell/logs/vcellrun_<site>.log — read them there (or via the bridge's
# /log endpoint), not from this launcher's output.

set -eo pipefail

REPO="$(cd "$(dirname "$0")/../.." && pwd)"
INSTALL_DIR="${VCELL_INSTALL_DIR:-$HOME/Applications/VCell_Alpha}"
API_HOST="${VCELL_API_HOST:-vcell-dev.cam.uchc.edu:443}"
VERSION="${VCELL_SOFTWARE_VERSION:-Alpha_Version_8.0.0_build_07}"

PORT=9123
REFRESH_CP=false
FOREGROUND=false
passthrough=()
while [ $# -gt 0 ]; do
  case "$1" in
    --port=*)     PORT="${1#*=}" ;;
    --refresh-cp) REFRESH_CP=true ;;
    --foreground) FOREGROUND=true ;;
    --)           shift; passthrough+=("$@"); break ;;
    *)            passthrough+=("$1") ;;
  esac
  shift
done

# --- JRE: prefer the bundled install4j JRE (matches shipped runtime) --------
JAVA_BIN="$INSTALL_DIR/.install4j/jre.bundle/Contents/Home/bin/java"
if [ ! -x "$JAVA_BIN" ]; then
  JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}java"
  echo "note: no bundled JRE under $INSTALL_DIR, using $JAVA_BIN" >&2
fi

# --- classpath: module classes first, then the dependency jars --------------
MODULES=(vcell-client vcell-core vcell-util vcell-math vcell-api-types
         vcell-apiclient vcell-restclient vcell-server vcell-api)

# Under Git Bash / MSYS the java on PATH is a WINDOWS binary: it wants ';' between
# classpath entries and native C:\... paths, not the ':' and /c/... this shell uses.
# cygpath does that translation; everywhere else the POSIX form is already right.
CPSEP=":"
case "$(uname -s)" in
  MINGW*|MSYS*|CYGWIN*) CPSEP=";" ;;
esac
winpath() {
  if [ "$CPSEP" = ";" ] && command -v cygpath >/dev/null 2>&1; then
    cygpath -w "$1"
  else
    printf '%s' "$1"
  fi
}

CP=""
for m in "${MODULES[@]}"; do
  CP+="$(winpath "$REPO/$m/target/classes")$CPSEP"
done

if [ ! -d "$REPO/vcell-client/target/classes" ]; then
  echo "ERROR: $REPO/vcell-client/target/classes missing." >&2
  echo "Compile first, e.g.:  mvn compile -pl vcell-client -am -DskipTests" >&2
  exit 1
fi

DEP_CP_FILE="$REPO/vcell-client/target/debug-bridge-deps-cp.txt"
if $REFRESH_CP || [ ! -s "$DEP_CP_FILE" ]; then
  echo "resolving vcell-client dependency classpath (cached at $DEP_CP_FILE)..." >&2
  (cd "$REPO" && mvn -q dependency:build-classpath -pl vcell-client \
      -Dmdep.outputFile="$(winpath "$DEP_CP_FILE")")
fi
CP+="$(cat "$DEP_CP_FILE")"

# --- JVM options mirrored from the install4j launcher ------------------------
vmopts=(
  --add-exports java.desktop/sun.awt.image=ALL-UNNAMED
  "-Dvcell.installDir=$(winpath "$INSTALL_DIR")"
  -Dvcell.autoflushlog=true
  "-Dvcell.softwareVersion=$VERSION"
  "-Dvcell.serverHost=$API_HOST"
  -Dvcell.serverPrefix.v0=/api/v0
  -Dvcell.serverPrefix.v1=/api/v1
  -Dvcell.onlineResourcesURL=http://vcell.org
  -Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.9-jar-with-dependencies.jar
  -Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.9-jar-with-dependencies.jar
  -Dvcell.debugBridge=true
  "-Dvcell.debugBridge.port=$PORT"
)

echo "VCell client (target/classes): host=$API_HOST version=$VERSION bridge=:$PORT" >&2

if $FOREGROUND; then
  exec "$JAVA_BIN" "${vmopts[@]}" -cp "$CP" cbit.vcell.client.VCellClientMain \
    --api-host="$API_HOST" ${passthrough[@]+"${passthrough[@]}"}
fi

nohup "$JAVA_BIN" "${vmopts[@]}" -cp "$CP" cbit.vcell.client.VCellClientMain \
  --api-host="$API_HOST" ${passthrough[@]+"${passthrough[@]}"} \
  >/tmp/vcell-debug-launch.out 2>&1 &
PID=$!
echo "launched pid=$PID (pre-redirect output: /tmp/vcell-debug-launch.out)" >&2

# --- wait for the bridge to come up ------------------------------------------
for _ in $(seq 1 90); do
  if curl -fsS -m 1 "http://127.0.0.1:$PORT/health" >/dev/null 2>&1; then
    echo "bridge up: http://127.0.0.1:$PORT (try: tools/debug-bridge/bridge.sh windows)" >&2
    exit 0
  fi
  if ! kill -0 "$PID" 2>/dev/null; then
    echo "ERROR: client exited during startup; see /tmp/vcell-debug-launch.out" >&2
    exit 1
  fi
  sleep 1
done
echo "ERROR: bridge did not answer on :$PORT within 90s (client pid=$PID still running)" >&2
exit 1

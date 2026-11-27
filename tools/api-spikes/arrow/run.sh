#!/usr/bin/env bash
# Arrow part of the API spike (docs/api-style-guide.md §2.3, §2.4): an IPC stream with null, NaN and
# +-Infinity written by Python is read by Java, and one written by Java is read by Python.
# Needs JDK 17, Maven and Python 3 (pyarrow and numpy are installed into a temporary venv).
set -euo pipefail
here="$(cd "$(dirname "$0")" && pwd)"
work="$(mktemp -d)"
python3 -m venv "$work/venv"
"$work/venv/bin/pip" install -q pyarrow numpy
py="$work/venv/bin/python -I $here/arrow_check.py"
cd "$here/java"
mvn -q -B -DskipTests compile
cp="target/classes:$(mvn -q -B dependency:build-classpath -Dmdep.outputFile=/dev/stdout)"
java_check() { java --add-opens=java.base/java.nio=ALL-UNNAMED -cp "$cp" spike.ArrowCheck "$@"; }
echo "== Python writes, Java reads"
$py write "$work/py.arrows"
java_check read "$work/py.arrows"
echo "== Java writes, Python reads"
java_check write "$work/java.arrows"
$py read "$work/java.arrows"

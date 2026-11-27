#!/usr/bin/env bash
# Python + TypeScript part of the client spike (docs/api-style-guide.md §2.3): generate both clients from
# ../java-client/spike.yaml with the repo's generator version and Python settings, check that the strict
# Python client accepts null and the "NaN"/"Infinity"/"-Infinity" strings, and print the TypeScript types.
set -euo pipefail
here="$(cd "$(dirname "$0")" && pwd)"
work="$(mktemp -d)"
cp "$here/../java-client/spike.yaml" "$here/python-config.yaml" "$work/"
gen="openapitools/openapi-generator-cli:v7.1.0"
docker run --rm -v "$work:/w" $gen generate -g python -i /w/spike.yaml -o /w/py -c /w/python-config.yaml >/dev/null
docker run --rm -v "$work:/w" $gen generate -g typescript-angular -i /w/spike.yaml -o /w/ts >/dev/null
python3 -m venv "$work/venv"
"$work/venv/bin/pip" install -q "$work/py"
echo "--- Python, Float64 as generated (anyOf wrapper)"
"$work/venv/bin/python" -I "$here/check_python.py" || true
echo "--- Python, Float64 mapped onto float"
docker run --rm -v "$work:/w" $gen generate -g python -i /w/spike.yaml -o /w/py2 -c /w/python-config.yaml \
  --schema-mappings Float64=float --import-mappings Float64=float >/dev/null
python3 -m venv "$work/venv2"
"$work/venv2/bin/pip" install -q "$work/py2"
"$work/venv2/bin/python" -I "$here/check_python_mapped.py"
echo "--- TypeScript: generated Float64 and TimeSeries types"
grep -v -E '^ \*|^/\*\*|^$' "$work/ts/model/float64.ts"
grep -E 'times|values' "$work/ts/model/timeSeries.ts"
echo "(generated clients left in $work)"

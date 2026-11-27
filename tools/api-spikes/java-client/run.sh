#!/usr/bin/env bash
# Java-client spike (see docs/api-style-guide.md §2.2): generate the Java client with openapi-generator
# (the version vcell-restclient uses) mapped onto hand-written DTO records, compile, and run the checks
# against a stub server. Needs Docker and a JDK 17. Writes only into a temporary directory.
set -euo pipefail
here="$(cd "$(dirname "$0")" && pwd)"
work="$(mktemp -d)"
cp "$here/spike.yaml" "$here/java-config.yaml" "$work/"
maps=""
for n in Curation UserRef Application BioModelSummary BioModel BioModelCreate CsvExport N5Export ExportRequest ExportJob FieldError Problem TimeSeries; do
  maps="$maps,$n=org.vcell.api.dto.$n"
done
maps="${maps#,},Float64=java.lang.Double"
docker run --rm -v "$work:/w" openapitools/openapi-generator-cli:v7.1.0 generate -g java \
  -i /w/spike.yaml -o /w/client -c /w/java-config.yaml --schema-mappings "$maps" --import-mappings "$maps" >/dev/null
cp -R "$here/dto/org/vcell/api" "$work/client/src/main/java/org/vcell/"
cp -R "$here/runner/spike" "$work/client/src/main/java/"
cd "$work/client"
props="-Dmaven.compiler.release=17 -Dmaven.compiler.source=17 -Dmaven.compiler.target=17"
mvn -q -B $props -DskipTests compile
cp="$(mvn -q -B dependency:build-classpath -Dmdep.outputFile=/dev/stdout)"
java -cp "target/classes:$cp" spike.SpikeRun
echo "(generated client left in $work/client)"

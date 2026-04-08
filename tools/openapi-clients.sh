#!/usr/bin/env bash
set -e

#
# Regenerate OpenAPI spec and/or generate API clients (Java, Python, TypeScript-Angular).
#
# Usage:
#   ./tools/openapi-clients.sh                  # generate clients from existing tools/openapi.yaml
#   ./tools/openapi-clients.sh --update-spec    # rebuild vcell-rest, copy spec, then generate clients
#
# The --update-spec flag runs 'mvn clean install -DskipTests' to build vcell-rest,
# which produces the OpenAPI spec via SmallRye OpenAPI, then copies it to tools/openapi.yaml.
# Without the flag, it assumes tools/openapi.yaml is already up to date.
#
# After running, verify downstream compilation:
#   mvn compile test-compile -pl vcell-rest -am
#

generatorCliImage=openapitools/openapi-generator-cli:v7.1.0

scriptDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
parentDir="$(dirname "$scriptDir")"

# Parse arguments
UPDATE_SPEC=false
for arg in "$@"; do
    case $arg in
        --update-spec)
            UPDATE_SPEC=true
            ;;
        -h|--help)
            echo "Usage: $0 [--update-spec]"
            echo ""
            echo "  --update-spec    Build vcell-rest and regenerate tools/openapi.yaml before generating clients"
            echo "  (default)        Generate clients from existing tools/openapi.yaml"
            exit 0
            ;;
        *)
            echo "Unknown argument: $arg"
            echo "Usage: $0 [--update-spec]"
            exit 1
            ;;
    esac
done

# Step 1: Optionally rebuild and update the OpenAPI spec
if [ "$UPDATE_SPEC" = true ]; then
    echo "==> Building vcell-rest to generate OpenAPI spec..."
    pushd "$parentDir" > /dev/null
    mvn clean install dependency:copy-dependencies -DskipTests=true
    popd > /dev/null

    echo "==> Copying generated spec to tools/openapi.yaml"
    cp "$parentDir/vcell-rest/target/generated/openapi.yaml" "$scriptDir/openapi.yaml"
fi

# Step 2: Validate the OpenAPI spec
echo "==> Validating openapi.yaml..."
docker run --rm -v "${scriptDir}:/local" ${generatorCliImage} validate -i /local/openapi.yaml --recommend
if [ $? -ne 0 ]; then
    echo "openapi.yaml is not valid"
    exit 1
fi

# Step 3: Generate Java client
echo "==> Generating Java client (vcell-restclient)..."
docker run --rm -v "${parentDir}:/vcell" \
    ${generatorCliImage} \
    generate \
    -g java \
    -i /vcell/tools/openapi.yaml \
    -o /vcell/vcell-restclient \
    -c /vcell/tools/java-config.yaml

# Apply patch for FieldDataResourceApi
pushd "${parentDir}" > /dev/null || { echo "Failed to cd to ${parentDir}"; exit 1; }
if ! git apply "${scriptDir}/FieldDataResourceApi.patch"; then
    echo "Failed to apply FieldDataResourceApi.patch"
    exit 1
fi
popd > /dev/null || { echo "Failed to return to previous directory"; exit 1; }

# Step 4: Generate Python client
echo "==> Generating Python client (python-restclient)..."
docker run --rm -v "${parentDir}:/vcell" \
    ${generatorCliImage} generate \
    -g python \
    -i /vcell/tools/openapi.yaml \
    -o /vcell/python-restclient \
    -c /vcell/tools/python-config.yaml

# Apply Python import fixes
"${scriptDir}/python-fix.sh"

# Step 5: Generate TypeScript-Angular client
echo "==> Generating TypeScript-Angular client (webapp-ng)..."
docker run --rm -v "${parentDir}:/vcell" \
    ${generatorCliImage} generate \
    -g typescript-angular \
    -i /vcell/tools/openapi.yaml \
    -o /vcell/webapp-ng/src/app/core/modules/openapi \
    -c /vcell/tools/typescript-angular-config.yaml

echo "==> Done. Verify downstream compilation with:"
echo "    mvn compile test-compile -pl vcell-rest -am"

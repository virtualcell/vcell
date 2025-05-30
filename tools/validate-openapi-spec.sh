#!/bin/bash
RED='\033[0;31m'
NC='\033[0m' # No Color
GREEN='\033[0;32m'


pushd ../
FILE="./tools/openapi.yaml"

echo "This test should only be done after the entire codebase has been compiled."

if [[ -z "$FILE" ]]; then
  echo "Error: OpenAPI file does not exist in tools directory."
  exit 1
fi

cp ./vcell-rest/target/generated/openapi.yaml "$FILE"

# Check if the file has changes compared to HEAD
if ! git diff --quiet -- "$FILE"; then
  echo ""
  echo -e "${RED}Error: The OpenAPI specification file has changed!"
  echo -e "This can mean some endpoint was created and did not generate clients, or "
  echo -e "an object internal to VCell has changed which the server uses as a DTO.${NC}"
  exit 1
fi

echo -e "${GREEN}Success: OpenAPI spec is up to date.${NC}"

popd || exit 0

#!/bin/bash

# This script is used to fix the generated Python REST client code

# Order of operations matters here, so we need to ensure that the imports are correctly handled.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
pushd "${SCRIPT_DIR}" || { echo "Failed to change directory to ${SCRIPT_DIR}"; exit 1; }
INIT_FILE="../python-restclient/vcell_client/models/__init__.py"

sed -i '' '/^from vcell_client.models.analytic_curve import AnalyticCurve$/d' $INIT_FILE && \
echo 'from vcell_client.models.analytic_curve import AnalyticCurve' >> $INIT_FILE

sed -i '' '/^from vcell_client.models.composite_curve import CompositeCurve$/d' $INIT_FILE && \
echo 'from vcell_client.models.composite_curve import CompositeCurve' >> $INIT_FILE

sed -i '' '/^from vcell_client.models.control_point_curve import ControlPointCurve$/d' $INIT_FILE && \
echo 'from vcell_client.models.control_point_curve import ControlPointCurve' >> $INIT_FILE

# Fix missing ExternalDataIdentifier import in field_data_resource_api.py (OpenAPI generator bug)
FIELD_DATA_API="../python-restclient/vcell_client/api/field_data_resource_api.py"
if [ -f "$FIELD_DATA_API" ] && ! grep -q 'from vcell_client.models.external_data_identifier import ExternalDataIdentifier' "$FIELD_DATA_API"; then
  sed -i '' 's/^from vcell_client.models.extent import Extent$/from vcell_client.models.external_data_identifier import ExternalDataIdentifier\
from vcell_client.models.extent import Extent/' "$FIELD_DATA_API"
fi

popd

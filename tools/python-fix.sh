#!/bin/bash

# This script is used to fix the generated Python REST client code

# Order of operations matters here, so we need to ensure that the imports are correctly handled.
INIT_FILE="../python-restclient/vcell_client/models/__init__.py"

sed -i '' '/^from vcell_client.models.analytic_curve import AnalyticCurve$/d' $INIT_FILE && \
echo 'from vcell_client.models.analytic_curve import AnalyticCurve' >> $INIT_FILE

sed -i '' '/^from vcell_client.models.composite_curve import CompositeCurve$/d' $INIT_FILE && \
echo 'from vcell_client.models.composite_curve import CompositeCurve' >> $INIT_FILE

sed -i '' '/^from vcell_client.models.control_point_curve import ControlPointCurve$/d' $INIT_FILE && \
echo 'from vcell_client.models.control_point_curve import ControlPointCurve' >> $INIT_FILE



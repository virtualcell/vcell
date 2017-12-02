#!/usr/bin/env bash

THRIFT_0_10_0=thrift
THRIFT_0_9_2=

# Utility to (re-)generate Java and Python classes from thrift IDL specifications
# writes Java and Python source files as specified in the .thrift files.

# Calling thrift executable separately for Java and Python code generation.  Comment out either if suppression of one is desired.
$THRIFT_0_10_0 --out ../vcell-core/src/main/java --gen java:generated_annotations=undated pyVCell.thrift
$THRIFT_0_10_0 --out ../pythonScripts/VCell_VTK --gen py pyVCell.thrift
if [ ! -z "$THRIFT_0_9_2" ]; then
	$THRIFT_0_9_2 --out ../pythonScripts/VCell_VisIt --gen py pyVCell.thrift
else
	echo "skipping python visit binding, thrift 0.9.2 binary required for currently bundled thrift python module."
fi

# Done. Remember to refresh Eclipse and Visual Studio projects.
$THRIFT_0_10_0 --out ../vcell-core/src/main/java --gen java:generated_annotations=undated VisMesh.thrift
$THRIFT_0_10_0 --out ../pythonScripts/VCell_VTK --gen py VisMesh.thrift

# Copasi stuff
$THRIFT_0_10_0 --out ../vcell-core/src/main/java --gen java:generated_annotations=undated VCellOpt.thrift
$THRIFT_0_10_0 --out ../pythonScripts/VCell_Opt --gen py VCellOpt.thrift

# ImageDataset Service
$THRIFT_0_10_0 --out ../vcell-core/src/main/java --gen java:generated_annotations=undated ImageDataset.thrift

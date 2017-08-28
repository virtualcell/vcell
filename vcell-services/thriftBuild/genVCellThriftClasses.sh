#!/usr/bin/env bash

# Utility to (re-)generate Java and Python classes from thrift IDL specifications
# writes Java and Python source files as specified in the .thrift files.

# Calling thrift executable separately for Java and Python code generation.  Comment out either if suppression of one is desired.
thrift --out ../src/main/java --gen java:generated_annotations=undated pyVCell.thrift
thrift --out ../../visTool --gen py pyVCell.thrift

# Done. Remember to refresh Eclipse and Visual Studio projects.
thrift --out ../src/main/java --gen java:generated_annotations=undated VisMesh.thrift
thrift --out ../../visTool --gen py VisMesh.thrift

# Copasi stuff
thrift --out ../src/main/java --gen java:generated_annotations=undated VCellOpt.thrift
thrift --out ../../visTool --gen py VCellOpt.thrift

# ImageJ
thrift --out ../src/main/java --gen java:generated_annotations=undated VCellImageJ.thrift
thrift --out ../../visTool --gen py VCellImageJ.thrift

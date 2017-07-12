#!/usr/bin/env bash

# Utility to (re-)generate Java and Python classes from pyVCell.thrift IDL specification, write class source files to where they belong in the VCell source tree.

# Calling thrift executable separately for Java and Python code generation.  Comment out either if suppression of one is desired.
thrift --out ../src/main/java --gen java pyVCell.thrift
thrift --out ../visTool --gen py pyVCell.thrift

# Done. Remember to refresh Eclipse and Visual Studio projects.
thrift --out ../src/main/java --gen java VisMesh.thrift
thrift --out ../visTool --gen py VisMesh.thrift

# Copasi stuff
thrift --out ../src/main/java --gen java VCellOpt.thrift
thrift --out ../visTool --gen py VCellOpt.thrift

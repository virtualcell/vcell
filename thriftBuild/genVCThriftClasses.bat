REM Utility to (re-)generate Java and Python classes from thrift IDL specifications
REM writes Java and Python source files as specified in the .thrift files.

REM Calling thrift executable separately for Java and Python code generation.  Comment out either if suppression of one is desired.
thrift --out ..\vcell-core\src\main\java --gen java:generated_annotations=undated pyVCell.thrift
REM thrift_0.9.2.exe --out ..\visTool --gen py pyVCell.thrift

REM Done. Remember to refresh Eclipse and Visual Studio projects.
thrift --out ..\vcell-core\src\main\java --gen java:generated_annotations=undated VisMesh.thrift
thrift --out ..\visTool --gen py VisMesh.thrift

REM Copasi stuff
thrift --out ..\vcell-core\src\main\java --gen java:generated_annotations=undated VCellOpt.thrift
thrift --out ..\visTool --gen py VCellOpt.thrift

# ImageJ
thrift --out ..\vcell-core\src\main\java --gen java:generated_annotations=undated VCellImageJ.thrift
thrift --out ..\visTool --gen py VCellIma

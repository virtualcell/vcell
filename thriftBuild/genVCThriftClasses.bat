REM genVCThriftClasses.bat
REM First revision. Jan 5, 2015 by Ed Boyce
REM Utility to (re-)generate Java and Python classes from pyVCell.thrift IDL specification, write class source files to where they belong in the VCell source tree.

REM Calling thrift executable separately for Java and Python code generation.  Comment out either if suppression of one is desired.
.\thrift-0.9.2.exe --out ..\src\main\java --gen java pyVCell.thrift
.\thrift-0.9.2.exe --out ..\visTool --gen py pyVCell.thrift

REM Done. Remember to refresh Eclipse and Visual Studio projects.
.\thrift-0.9.2.exe --out ..\src\main\java --gen java VisMesh.thrift
.\thrift-0.9.2.exe --out ..\visTool --gen py VisMesh.thrift

REM Copasi stuff
.\thrift-0.9.2.exe --out ..\src\main\java --gen java VCellOpt.thrift
.\thrift-0.9.2.exe --out ..\visTool --gen py VCellOpt.thrift

# Example developer startup from command line (adjust paths accordingly for your machine):
# C:\Windows\system32>"C:\Program Files\LLNL\VisIt 2.9.0\visit.exe" -cli -pysideviewer -s C:\Developer\EclipseLuna\eclipse\workspace\VCell-5.4-trunk\visTool\visMainCLI.py
# > "D:\Program Files\LLNL\VisIt 2.9.2\visit.exe" -cli -pysideviewer -s D:\Developer\eclipse\workspace_refactor\VCell_5.4_clean\visTool\visMainCLI.py

import sys, os
sys.path.append(os.path.dirname(__file__))

import visgui.visQt as visQt

#import numpy

#print(dir(numpy))

visQt.initPyside()
QtCore = visQt.QtCore
QtGui = visQt.QtGui

import visContext
from visgui import visGui

visContext.init_with_Visit()
vis = visContext.visContext()
ex = visGui.VCellPysideApp(vis)
ex.show()
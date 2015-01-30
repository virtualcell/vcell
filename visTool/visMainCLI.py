# Example developer startup from command line (adjust paths accordingly for your machine):
# C:\Windows\system32>"C:\Program Files\LLNL\VisIt 2.8.1\visit.exe" -cli -pysideviewer -s C:\Developer\EclipseLuna\eclipse\workspace\VCell-5.4-trunk\visTool\visMainCLI.py
# > "C:\Program Files\LLNL\VisIt 2.8.2\visit.exe" -cli -pysideviewer -s D:\Developer\eclipse\workspace_refactor\VCell_5.4_clean\visTool\visMainCLI.py

import sys, os
sys.path.append(os.path.dirname(__file__))

import visQt
visQt.initPyside()
QtCore = visQt.QtCore
QtGui = visQt.QtGui

from visContextVisit import visContextVisit as visContext
import visGui

vis = visContext()
ex = visGui.VCellPysideApp(vis)
ex.show()
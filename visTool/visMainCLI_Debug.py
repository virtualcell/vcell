# Example developer startup from command line (adjust paths accordingly for your machine):
# C:\Windows\system32>"C:\Program Files\LLNL\VisIt 2.8.1\visit.exe" -cli -pysideviewer -s C:\Developer\EclipseLuna\eclipse\workspace\VCell-5.4-trunk\visTool\visMainCLI.py
# > "C:\Program Files\LLNL\VisIt 2.8.2\visit.exe" -cli -pysideviewer -s D:\Developer\eclipse\workspace_refactor\VCell_5.4_clean\visTool\visMainCLI.py

import sys, os
sys.path.append(os.path.dirname(__file__))

# Needed to attach to Visual Studio.  Adjust for your machine's paths.
#sys.path.append("C:\\Program Files (x86)\\Microsoft Visual Studio 11.0\\Common7\\IDE\\Extensions\\Microsoft\\Python Tools for Visual Studio\\2.0")
sys.path.append("C:\\Program Files (x86)\\Microsoft Visual Studio 11.0\\Common7\\IDE\\Extensions\\Microsoft\\Python Tools for Visual Studio\\2.1")

import visQt
visQt.initPyside()
QtCore = visQt.QtCore
QtGui = visQt.QtGui

from visContextVisit import visContextVisit as visContext
import visGui

import ptvsd


# Uncomment the following block to enable debug connection to Python Tools for Visual Studio. 
# To connect in VS, go to Debug --> Attach to Process, type "secret@localhost" in Qualifier field, 
# click Refresh, select process in process list and click Attach. 
print('before connect')
ptvsd.enable_attach(secret = None)
ptvsd.wait_for_attach(None)
ptvsd.break_into_debugger()
print('after connect')
 

vis = visContext()
ex = visGui.VCellPysideApp(vis)
ex.show()
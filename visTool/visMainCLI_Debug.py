# Example developer startup from command line (adjust paths accordingly for your machine):

# Ed's debug invocation
# C:\Windows\system32>"C:\Program Files\LLNL\VisIt 2.9.0\visit.exe" -cli -pysideviewer -s C:\Developer\EclipseLuna\eclipse\workspace\VCell-5.4-trunk\visTool\visMainCLI_Debug.py

# Jim's debug invocation (as of 6/3/2015) on NRCAMDEV5
#    > "D:\Program Files\LLNL\VisIt 2.9.2\visit.exe" -cli -pysideviewer -s D:\Developer\eclipse\workspace_refactor\VCell_6.1_clean\visTool\visMainCLI_Debug.py
# or
#    > "D:\Program Files\LLNL\VisIt 2.9.2\visit.exe" -cli -pysideviewer -s D:\Developer\eclipse\workspace_refactor\VCell_6.1_userdata\visTool\visMainCLI_Debug.py

import sys, os
sys.path.append(os.path.dirname(__file__))

# Needed to attach to Visual Studio.  Adjust for your machine's paths.

# Jim's path to ptvs on NRCAMDEV5 (uncomment)
sys.path.append("C:\\Program Files (x86)\\Microsoft Visual Studio 11.0\\Common7\\IDE\\Extensions\\Microsoft\\Python Tools for Visual Studio\\2.1")

# Ed's path to ptvs (uncomment)
#sys.path.append("C:\\Program Files (x86)\\Microsoft Visual Studio 12.0\\Common7\\IDE\\Extensions\\Microsoft\\Python Tools for Visual Studio\\2.2")
import visgui.visQt as visQt
visQt.initPyside()
QtCore = visQt.QtCore
QtGui = visQt.QtGui

import visContext
from visgui import visGui

import ptvsd

print('python version is '+sys.version);


# Uncomment the following block to enable debug connection to Python Tools for Visual Studio. 
# To connect in VS, go to Debug --> Attach to Process, type "secret@localhost" in Qualifier field, 
# click Refresh, select process in process list and click Attach. 
print('before connect')
ptvsd.enable_attach(secret = None)
ptvsd.wait_for_attach(None)
ptvsd.break_into_debugger()
print('after connect')
 

visContext.init_with_Visit()
vis = visContext.visContext()
ex = visGui.VCellPysideApp(vis)
ex.show()
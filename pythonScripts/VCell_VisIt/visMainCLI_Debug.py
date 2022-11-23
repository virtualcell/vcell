# Example developer startup from command line (adjust paths accordingly for your machine):

# Ed's debug invocation
# C:\Windows\system32>"C:\Program Files\LLNL\VisIt 2.9.0\visit.exe" -cli -pysideviewer -s C:\Developer\EclipseLuna\eclipse\workspace\VCell-5.4-trunk\visTool\visMainCLI_Debug.py

# Jim's debug invocation (as of 6/3/2015) on NRCAMDEV5
#    > "C:\Program Files\Visit292\LLNL\VisIt 2.9.2\visit.exe" -cli -pysideviewer -s D:\Developer\eclipse\workspace_refactor\VCell_6.2_fordeploy\visTool\visMainCLI_Debug.py

import sys, os
sys.path.append(os.path.dirname(__file__))
#sys.path.append(os.path.dirname("d:\\developer\\eclipse\\workspace_refactor\\VCell_6.2_fordeploy\\visTool"))

# Needed to attach to Visual Studio.  Adjust for your machine's paths.

# Jim's path to ptvs on NRCAMDEV5 (uncomment)
sys.path.append("C:\\Program Files (x86)\\Microsoft Visual Studio 11.0\\Common7\\IDE\\Extensions\\Microsoft\\Python Tools for Visual Studio\\2.1")

# Uncomment the following block to enable debug connection to Python Tools for Visual Studio. 
# To connect in VS, go to Debug --> Attach to Process, type "secret@localhost" in Qualifier field, 
# click Refresh, select process in process list and click Attach. 
print('before connect')
import ptvsd
ptvsd.enable_attach(secret = None)
ptvsd.wait_for_attach(None)
ptvsd.break_into_debugger()
print('after connect')



# Ed's path to ptvs (uncomment)
#sys.path.append("C:\\Program Files (x86)\\Microsoft Visual Studio 12.0\\Common7\\IDE\\Extensions\\Microsoft\\Python Tools for Visual Studio\\2.2")
import visgui.visQt as visQt
visQt.initPyside()
QtCore = visQt.QtCore
QtGui = visQt.QtGui

import visContext
from visgui import visGui

import argparse
from pyvcell.ttypes import SimulationDataSetRef
from thrift.TSerialization import TBinaryProtocol
from thrift.TSerialization import deserialize


print('python version is '+sys.version);


 

visContext.init_with_Visit()
vis = visContext.visContext()
ex = visGui.VCellPysideApp(vis)

parser = argparse.ArgumentParser()
#parser.add_argument("-resourcedir", help="???")
#parser.add_argument("-simreffile", help="simref of initial simulation dataset to load upon initialization")
args = parser.parse_args()

if (hasattr(args, 'resourcedir')):
    ex.resourcedir = args.resourcedir
else:
    print('resourcedir not specified')
    

if (hasattr(args, 'simreffile')):
    f_simref = open(args.simreffile, "rb")
    blob_simref = f_simref.read()
    print("read "+str(len(blob_simref))+" bytes from "+args.simreffile)
    f_simref.close()
    simref = SimulationDataSetRef()
    protocol_factory = TBinaryProtocol.TBinaryProtocolFactory
    #    deserialize(visMesh, blob_vismesh, protocol_factory = protocol_factory())
    print("starting deserialization")
    deserialize(simref, blob_simref, protocol_factory = protocol_factory())
    print("done with deserialization")

    ex.initialTimer = QtCore.QTimer(ex)
    ex.initialTimer.setSingleShot(True)

    def load():
        ex.loadSim(simref)

    ex.initialTimer.singleShot(4000,load)
else:
    print('simreffile not specified')



ex.show()
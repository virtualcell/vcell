# Example developer startup from command line (adjust paths accordingly for your machine):
# C:\Windows\system32>"D:\Program Files\LLNL\VisIt 2.9.0\visit.exe" -cli -pysideviewer -s C:\Developer\EclipseLuna\eclipse\workspace\VCell-5.4-trunk\visTool\visMainCLI.py
# > "C:\Program Files\Visit292\LLNL\VisIt 2.9.2\visit.exe" -cli -uifile "D:\Developer\eclipse\workspace_refactor\VCell_6.2_fordeploy\visTool\visMainCLI.py"

import sys, os
#import argparse

sys.path.append(os.path.dirname(__file__))

import visgui.visQt as visQt

import vcellProxy
import pyvcell
from pyvcell.ttypes import SimulationDataSetRef
from thrift.TSerialization import TBinaryProtocol
from thrift.TSerialization import deserialize

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

# parser = argparse.ArgumentParser()
# #parser.add_argument("resourcedir", help="???")
# parser.add_argument("-simreffile", help="simref of initial simulation dataset to load upon initialization")
# args = parser.parse_args()
simreffile = os.environ['INITIALSIMDATAREFFILE']
# i = 0
# simreffile = None
# while i < len(sys.argv):
#     print("parsing "+str(sys.argv[i]))
#     if sys.argv[i] == "-simreffile":
#         simreffile = sys.argv[i+1]
#         i = i + 1
#     i = i + 1


#if (args.resourcedif):
#    ex.resourcedir = args.resourcedif

if (simreffile):
    print("opening simreffile "+simreffile)
    f_simref = open(simreffile, "rb")
    blob_simref = f_simref.read()
    print("read "+str(len(blob_simref))+" bytes from "+simreffile)
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

    ex.initialTimer.singleShot(50,load)

ex.show()
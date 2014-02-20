
import sys, os
sys.path.append(os.path.dirname(__file__))

import visQt
visQt.initPyside()
QtCore = visQt.QtCore
QtGui = visQt.QtGui

from visContextVisit import visContextVisit as visContext
import visGui

#import ptvsd

#print('before connect')
#ptvsd.enable_attach(secret = None)
#ptvsd.wait_for_attach(None)
#ptvsd.break_into_debugger()
#print('after connect')
 

vis = visContext()
ex = visGui.VCellPysideApp(vis)
ex.show()

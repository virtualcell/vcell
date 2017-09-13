
import sys, os
sys.path.append(os.path.dirname(__file__))

import visgui.visQt as visQt
#visQt.initPyQt4()
visQt.initPyside()
QtCore = visQt.QtCore
QtGui = visQt.QtGui

import visContext
from visgui import visGui

def main():
    app = QtGui.QApplication(sys.argv)
    visContext.init_with_Fake()
#    visContext.init_with_PlainVTK()
    vis = visContext.visContext()
    ex = visGui.VCellPysideApp(vis)
    ex.show()
    sys.exit(app.exec_())


if __name__ == '__main__':
    main()
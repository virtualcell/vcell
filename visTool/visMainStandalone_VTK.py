
import sys, os
sys.path.append(os.path.dirname(__file__))

import visQt
visQt.initPyQt4()

QtCore = visQt.QtCore
QtGui = visQt.QtGui

import visContextVTK

import visGui

def main():
    app = QtGui.QApplication(sys.argv)
    vis = visContextVTK.visContextVTK()
    ex = visGui.VCellPysideApp(vis)
    #filename = "/Developer/eclipse/workspace/VCell_5.3_vis/uGrid_1.vtu"
    #var1 = "mesh_quality/aspect"
    #var1 = "s2.vol0"
    #vis.open(filename,var1)
    ex.show()
    sys.exit(app.exec_())


if __name__ == '__main__':
    main()
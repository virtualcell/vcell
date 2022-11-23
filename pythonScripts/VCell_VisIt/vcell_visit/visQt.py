

qtbinding = "Not Initialized"

QtGui = None
QtCore = None
uic = None

def isPyside():
    return qtbinding == "PySide"

def isPyQt4():
    return qtbinding == "PyQt4"

def initPyside():
    global QtGui
    global QtCore
    global qtbinding
    try:
        import PySide
        import PySide.QtUiTools as QtUiTools
        import PySide.QtGui
        import PySide.QtCore
        QtCore = PySide.QtCore 
        QtGui = PySide.QtGui
        qtbinding = "PySide"
        print('qtsupport using '+qtbinding)
    except ImportError as ex2:
        print("failed to import PySide: "+str(ex2))


def initPyQt4():
    global QtGui
    global QtCore
    global uic
    global qtbinding
    try:
        import PyQt4
        import PyQt4.QtCore as QtCore
        import PyQt4.QtGui as QtGui
        import PyQt4.uic as uic
        QtCore = PyQt4.QtCore
#        QtCore = PyQt4.QtCore.pyqtSignal
        QtGui = PyQt4.QtGui
        uic = PyQt4.uic
        qtbinding = "PyQt4"
        print('qtsupport using '+qtbinding)
    except ImportError as ex:
        print("failed to import PyQt4: "+str(ex))


def loadUI(filename):
    assert isinstance(filename,str)
    if qtbinding == 'PyQt4':
        widget = uic.loadUi(filename)
    elif qtbinding == 'PySide':
        file = QtCore.QFile(filename)
        loader = QtUiTools.QUiLoader()
        file.open(QtCore.QFile.ReadOnly)
        widget = loader.load(file, None)
        file.close()
    assert isinstance(widget,QtGui.QWidget)
    return widget

def print_signals_and_slots(obj):
    for i in xrange(obj.metaObject().methodCount()):
         m = obj.metaObject().method(i)
         if m.methodType() == QMetaMethod.MethodType.Signal:
             print "SIGNAL: sig=", m.signature(), "hooked to nslots=",obj.receivers(SIGNAL(m.signature()))
         elif m.methodType() == QMetaMethod.MethodType.Slot:
             print "SLOT: sig=", m.signature()    
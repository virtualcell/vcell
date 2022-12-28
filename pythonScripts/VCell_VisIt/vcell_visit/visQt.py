from PySide6 import QtCore, QtGui, QtUiTools, QtWidgets
from PySide6.QtCore import QMetaMethod, SIGNAL, SLOT, QIODeviceBase, QFile


def load_ui(filename: str) -> QtWidgets.QWidget:
    file = QtCore.QFile(filename)
    loader = QtUiTools.QUiLoader()
    if not file.open(QIODeviceBase.OpenModeFlag.ReadOnly):
        raise Exception("failed to open file "+filename)

    widget: QtWidgets.QWidget = loader.load(file, None)
    file.close()

    return widget


def print_signals_and_slots(obj):
    for i in range(obj.metaObject().methodCount()):
        m = obj.metaObject().method(i)
        if m.methodType() == QMetaMethod.MethodType.Signal:
            print("SIGNAL: sig=", m.signature(), "hooked to nslots=", obj.receivers(SIGNAL(m.signature())))
        elif m.methodType() == QMetaMethod.MethodType.Slot:
            print("SLOT: sig=", m.signature())

from numbers import Number

class VisClipOperatorContext(object):
    def __init__(self):
        print ("VisOperatorContext init")
        self._currentOperatorProject2d = None
        self._currentOperatorPrecent = None
        self._currentOperatorAxis = None
        self._currentOperatorEnabled = None

    def getCurrentOperatorProject2d(self):
        return self._currentOperatorProject2d

    def setCurrentProject2d(self, bProject2d):
        assert(isinstance(bProject2d, bool))
        self._currentOperatorProject2d = bProject2d

    def getCurrentOperatorPercent(self):
        return self._currentOperatorPercent

    def setCurrentOperatorPercent(self, percent):
        assert(isinstance(percent, Number))
        assert((percent >= 0) and (percent <=100)) 
        self._currentOperatorPercent = percent

    def getCurrentOperatorAxis(self):
        return self._currentOperatorAxis

    def setCurrentOperatorAxis(self, axis):
        self._currentOperatorAxis = axis

    def getCurrentOperatorEnabled(self):
        return self._currentOperatorEnabled

    def setCurrentOperatorEnabled(self, bEnabled):
        self._currentOperatorEnabled = bEnabled
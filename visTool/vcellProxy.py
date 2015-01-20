import sys
sys.path.append('./')
from pyvcell import VCellProxy
from pyvcell.ttypes import *
from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol




class VCellProxyHandler(object):

    def __init__(self):
        self._transport = TSocket.TSocket('localhost',	9090)
        self._transport = TTransport.TBufferedTransport(self._transport)
        protocol = TBinaryProtocol.TBinaryProtocol(self._transport)
        self._client = VCellProxy.Client(protocol)
     
    def __enter__(self):
        self.open()
        
    def __exit__(self, type, value, traceback):
        self.close()  
        
    def clientRequest(self, expression):
        try:
            self._vis.getVCellProxy().open()
            result = self._vis.getVCellProxy().getClient().eval(expression)
        except Exception as exc:
            print(exc.message)
        finally:
            self._vis.getVCellProxy().close()
        return result

    def getClient(self):
        return(self._client)

    def open(self):
        self._transport.open()

    def close(self):
        self._transport.close()
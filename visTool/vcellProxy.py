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
        
        


    def getClient(self):
        return(self._client)

    def open(self):
        self._transport.open()

    def close(self):
        self._transport.close()
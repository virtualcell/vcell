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
        transport = TSocket.TSocket('localhost',	9090)
        transport = TTransport.TBufferedTransport(transport)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)
        self._client = VCellProxy.Client(protocol)
        transport.open()


    def getClient(self):
        return(self._client)


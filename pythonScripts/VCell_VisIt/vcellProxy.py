import sys
#sys.path.append('./')
import thrift
import thrift.transport
import thrift.transport.TSocket
import thrift.protocol
import pyvcell
import pyvcell.VCellProxy



class VCellProxyHandler(object):

    def __init__(self):
        self._transport = thrift.transport.TSocket.TSocket('localhost',	9090)
        self._transport = thrift.transport.TTransport.TBufferedTransport(self._transport)
        protocol = thrift.protocol.TBinaryProtocol.TBinaryProtocol(self._transport)
        self._client = pyvcell.VCellProxy.Client(protocol)
     
    def getClient(self):
        return(self._client)

    def open(self):
        self._transport.open()

    def close(self):
        self._transport.close()
#sys.path.append('./')
from vcell_visit import thrift, pyvcell
import vcell_visit.thrift.transport
import vcell_visit.thrift.transport.TSocket
import vcell_visit.thrift.protocol
import vcell_visit.pyvcell.VCellProxy



class VCellProxyHandler(object):

    def __init__(self):
        self._transport = vcell_visit.thrift.transport.TSocket.TSocket('localhost', 9090)
        self._transport = vcell_visit.thrift.transport.TTransport.TBufferedTransport(self._transport)
        protocol = vcell_visit.thrift.protocol.TBinaryProtocol.TBinaryProtocol(self._transport)
        self._client = vcell_visit.pyvcell.VCellProxy.Client(protocol)
     
    def getClient(self):
        return(self._client)

    def open(self):
        self._transport.open()

    def close(self):
        self._transport.close()
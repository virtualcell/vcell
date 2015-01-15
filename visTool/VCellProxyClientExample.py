import sys
sys.path.append('./')
from pyvcell import VCellProxy
from pyvcell.ttypes import *
from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

transport = TSocket.TSocket('localhost',	9090)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = VCellProxy.Client(protocol)
transport.open()

simList = client.getSimsFromSelectedModel()
#print(simList.__len__)
print('\n')
print(simList)






  # spec=ExportRequestSpec()
  # spec.exportType = ExportType.VTU
  # spec.variables = ['C_cyt','Ran_cyt','RanC_cyt','RanC_nuc']
  # spec.SimID='Sim1'
  # spec.startTime=0
  # spec.endTime=x

  # print(client.exportRequest(spec))
  # transport.close()



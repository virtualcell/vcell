import sys

import vtk

sys.path.append('./')
import vcellProxy

vcellProxy2 = vcellProxy.VCellProxyHandler()
try:
    vcellProxy2.open()

    # transport = TSocket.TSocket('localhost', 9090)
    # transport = TTransport.TBufferedTransport(transport)
    # protocol = TBinaryProtocol.TBinaryProtocol(transport)
    # client = VCellProxy.Client(protocol)
    # transport.open()

    simList = vcellProxy2.getClient().getSimsFromOpenModels()
    print('\n')
    print(simList)
    sim = simList[0]
    variables = vcellProxy2.getClient().getVariableList(sim)
    timePoints = vcellProxy2.getClient().getTimePoints(sim)
    print(variables)
    print(timePoints)
    var = variables[0]
    timeIndex = len(timePoints)-1
    dataFileName = vcellProxy2.getClient().getDataSetFileOfVariableAtTimeIndex(sim,var,timeIndex)
    reader = vtk.vtkXMLUnstructuredGridReader()
    reader.SetFileName(dataFileName)
    reader.Update()
    output = reader.GetOutput()
    assert isinstance(output, vtk.vtkUnstructuredGrid)
    cellData = output.GetCellData()
    scalar_range = output.GetScalarRange()
    print('scalar range = '+str(scalar_range))
    print("done")
    # print(simList.__len__)
except:
    e = sys.exe_info()[0]
    print("error: "+str(e))
finally:
    vcellProxy2.close()
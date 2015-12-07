import argparse
import sys

from vtk import *

from vtkService import writeDataArrayToNewVtkFile

from thrift.TSerialization import TBinaryProtocol
from thrift.TSerialization import deserialize
from vcellvismesh.ttypes import VarData

import numpy as np




def main():
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument("vardatafile", help="filename of input VarData to be written into output vtk file (thrift serialization via TBinaryProtocol)")
        parser.add_argument("invtkfile",help="filename of input vtk mesh (VTK XML unstructured grid")
        parser.add_argument("outvtkfile",help="filename of output vtk mesh (VTK XML unstructured grid")
        args = parser.parse_args()

        # read raw vardata from file
        f_vardata = open(args.vardatafile, "rb")
        blob_vardata = f_vardata.read()
        print("read "+str(len(blob_vardata))+" bytes from "+args.vardatafile)
        f_vardata.close()

        # deserialize raw bytes into VarDataAdd
        varData = VarData()
        protocol_factory = TBinaryProtocol.TBinaryProtocolFactory
        print("starting deserialization")
        deserialize(varData, blob_vardata, protocol_factory = protocol_factory())
        print("done with deserialization")

        npdata = np.asarray(varData.varData)

        writeDataArrayToNewVtkFile(args.invtkfile, varData.varName, npdata, args.outvtkfile)

    except:
        e = sys.exc_info()[0]
        print("exception "+e)
        sys.exit(-1)
    else:
        sys.exit(0)



if __name__ == '__main__':
    main()

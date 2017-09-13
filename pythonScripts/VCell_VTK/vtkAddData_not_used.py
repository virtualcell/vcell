import argparse

import numpy as np
import vtk

from vtkService import writeDataArrayToNewVtkFile




def main():
#    try:
        parser = argparse.ArgumentParser()
        parser.add_argument("invtkfile",help="filename of input vtk mesh (VTK XML unstructured grid")
        parser.add_argument("varname",help="name of cell data variable")
        parser.add_argument("outvtkfile",help="filename of output vtk mesh (VTK XML unstructured grid")
        args = parser.parse_args()

        # deserialize raw bytes into VarDataAdd
        reader = vtk.vtkXMLUnstructuredGridReader()
        reader.SetFileName(args.invtkfile)
        reader.Update()
        mesh = reader.GetOutput()
        assert isinstance(mesh, vtk.vtkUnstructuredGrid)
        numcells = mesh.GetCells().GetSize()

        # write 0..N-1 into data
        npdata = np.arange(0,numcells,0.1)

        writeDataArrayToNewVtkFile(args.invtkfile, args.varname, npdata, args.outvtkfile)
        print("done")
    # except:
    #     e = sys.exc_info()[0]
    #     print("exception "+e)
    #     sys.exit(-1)
    # else:
    #     sys.exit(0)



if __name__ == '__main__':
    main()

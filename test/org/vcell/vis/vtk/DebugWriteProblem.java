package org.vcell.vis.vtk;

import java.util.Arrays;
import java.util.List;

import org.vcell.vis.vismesh.thrift.ChomboVolumeIndex;
import org.vcell.vis.vismesh.thrift.PolyhedronFace;
import org.vcell.vis.vismesh.thrift.Vect3D;
import org.vcell.vis.vismesh.thrift.VisIrregularPolyhedron;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;

public class DebugWriteProblem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			VisMesh visMesh = new VisMesh(3,new Vect3D(0,0,0),new Vect3D(3,1,1));
			VisPoint[] points0 = new VisPoint[] { 
					new VisPoint(0, 0, 0),
					new VisPoint(1, 0, 0),
					new VisPoint(0, 1, 0),
					new VisPoint(0, 0, 1)
			};
			
			visMesh.addToPoints(points0[0]);
			visMesh.addToPoints(points0[1]);
			visMesh.addToPoints(points0[2]);
			visMesh.addToPoints(points0[3]);
			
			List<PolyhedronFace> faces0 = Arrays.asList( 
					new PolyhedronFace(Arrays.asList( 0, 1, 3 )),
					new PolyhedronFace(Arrays.asList( 0, 3, 2 )),
					new PolyhedronFace(Arrays.asList( 0, 2, 1 )),
					new PolyhedronFace(Arrays.asList( 1, 3, 2 ))
					);

			int level = 0;
			int boxNumber = 0;
			int boxIndex = 0;
			int fraction = 0;

			VisIrregularPolyhedron genPolyhedra0 = new VisIrregularPolyhedron(faces0);
			genPolyhedra0.setChomboVolumeIndex(new ChomboVolumeIndex(level, boxNumber, boxIndex, fraction));
			
			visMesh.addToIrregularPolyhedra(genPolyhedra0);

			
//			VisPoint[] points1 = new VisPoint[] { 
//					new VisPoint(2+0, 0, 0),
//					new VisPoint(2+1, 0, 0),
//					new VisPoint(2+0, 1, 0),
//					new VisPoint(2+0, 0, 1)
//			};
//			
//			visMesh.addPoint(points1[0]);
//			visMesh.addPoint(points1[1]);
//			visMesh.addPoint(points1[2]);
//			visMesh.addPoint(points1[3]);
//			
			
		
//			vtkUnstructuredGrid vtkgrid = VtkGridUtils.getVolumeVtkGrid(visMesh);
//			String filenameASCII = "testASCII.vtk";
//			String filenameBinary = "testBinary.vtk";
//			VtkGridUtils.writeXML(vtkgrid, filenameASCII, true);
//			VtkGridUtils.writeXML(vtkgrid, filenameBinary, false);
//			vtkgrid = VtkGridUtils.read(filenameBinary);
//			vtkgrid.BuildLinks();
			
			
//			SimpleVTKViewer simpleViewer = new SimpleVTKViewer();
//			String[] varNames = visDomain.getVisMeshData().getVarNames();
//			simpleViewer.showGrid(vtkgrid, varNames[0], varNames[1]);
			
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}

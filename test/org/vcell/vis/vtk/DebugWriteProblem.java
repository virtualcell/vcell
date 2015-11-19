package org.vcell.vis.vtk;

import org.vcell.vis.core.Vect3D;
import org.vcell.vis.vismesh.VisDataset.VisDomain;
import org.vcell.vis.vismesh.VisIrregularPolyhedron;
import org.vcell.vis.vismesh.VisIrregularPolyhedron.PolyhedronFace;
import org.vcell.vis.vismesh.VisMesh;
import org.vcell.vis.vismesh.VisMeshData;
import org.vcell.vis.vismesh.VisPoint;

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
			
			visMesh.addPoint(points0[0]);
			visMesh.addPoint(points0[1]);
			visMesh.addPoint(points0[2]);
			visMesh.addPoint(points0[3]);
			
			PolyhedronFace[] faces0 = new PolyhedronFace[] { 
					new PolyhedronFace(new int[] { 0, 1, 3 }),
					new PolyhedronFace(new int[] { 0, 3, 2 }),
					new PolyhedronFace(new int[] { 0, 2, 1 }),
					new PolyhedronFace(new int[] { 1, 3, 2 })
			};

			int level = 0;
			int boxNumber = 0;
			int boxIndex = 0;
			int fraction = 0;

			VisIrregularPolyhedron genPolyhedra0 = new VisIrregularPolyhedron(level, boxNumber, boxIndex, fraction, -1);
			genPolyhedra0.addFace(faces0[0]);
			genPolyhedra0.addFace(faces0[1]);
			genPolyhedra0.addFace(faces0[2]);
			genPolyhedra0.addFace(faces0[3]);
			
			visMesh.addPolyhedron(genPolyhedra0);

			
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
			
			VisMeshData visData = new VisMeshData() {
				private String[] names = new String[] { "data1", "data2" };
				private double[] data1 = new double[] { 0.0 };
				private double[] data2 = new double[] { 1.0 };
				
				@Override
				public String[] getVarNames() {
					return new String[] { "data1", "data2" };
				}
				
				@Override
				public double getTime() {
					return 0.0;
				}
				
				@Override
				public double[] getData(String var) {
					if (var.equals("data1")){
						return data1;
					}else if (var.equals("data2")){
						return data2;
					}else{
						throw new RuntimeException("var "+var+" not found");
					}
				}
			};
			
			VisDomain visDomain = new VisDomain("domain1",visMesh,visData);
//			vtkUnstructuredGrid vtkgrid = vtkGridUtils.getVolumeVtkGrid(visDomain);
//			String filenameASCII = "testASCII.vtk";
//			String filenameBinary = "testBinary.vtk";
//			vtkGridUtils.writeXML(vtkgrid, filenameASCII, true);
//			vtkGridUtils.writeXML(vtkgrid, filenameBinary, false);
//			vtkgrid = vtkGridUtils.read(filenameBinary);
//			vtkgrid.BuildLinks();
			
			
//			SimpleVTKViewer simpleViewer = new SimpleVTKViewer();
//			String[] varNames = visDomain.getVisMeshData().getVarNames();
//			simpleViewer.showGrid(vtkgrid, varNames[0], varNames[1]);
			
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}

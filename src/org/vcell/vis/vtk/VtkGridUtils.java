package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.vis.vismesh.thrift.ChomboIndexData;
import org.vcell.vis.vismesh.thrift.FiniteVolumeIndexData;
import org.vcell.vis.vismesh.thrift.PolyhedronFace;
import org.vcell.vis.vismesh.thrift.VisIrregularPolyhedron;
import org.vcell.vis.vismesh.thrift.VisLine;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisPolygon;
import org.vcell.vis.vismesh.thrift.VisSurfaceTriangle;
import org.vcell.vis.vismesh.thrift.VisTetrahedron;
import org.vcell.vis.vismesh.thrift.VisVoxel;

import vtk.vtkCell;
import vtk.vtkCellData;
import vtk.vtkDelaunay3D;
import vtk.vtkDoubleArray;
import vtk.vtkGeometryFilter;
import vtk.vtkIdList;
import vtk.vtkIdTypeArray;
import vtk.vtkLine;
import vtk.vtkNativeLibrary;
import vtk.vtkObjectBase;
import vtk.vtkPointData;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolygon;
import vtk.vtkPolyhedron;
import vtk.vtkQuad;
import vtk.vtkReferenceInformation;
import vtk.vtkTetra;
import vtk.vtkTriangle;
import vtk.vtkUnstructuredGrid;
import vtk.vtkUnstructuredGridGeometryFilter;
import vtk.vtkUnstructuredGridReader;
import vtk.vtkUnstructuredGridWriter;
import vtk.vtkVoxel;
import vtk.vtkWindowedSincPolyDataFilter;
import vtk.vtkXMLFileReadTester;
import vtk.vtkXMLUnstructuredGridReader;
import vtk.vtkXMLUnstructuredGridWriter;
//import vtk.vtkXdmfWriter;
import cbit.vcell.resource.NativeLib;

public class VtkGridUtils {
	
	public static final Logger LG = Logger.getLogger(VtkGridUtils.class);
	private static final boolean bClipPolyhedra = true;

	// Load VTK library and print which library was not properly loaded
	static {
		try {
			//in case loader thread not complete
			NativeLib.VTK.load();
		}catch (Exception e){
			LG.warn("exception loading VTK NativeLib",e);
		}
		vtkNativeLibrary.DisableOutputWindow(null);
		vtkObjectBase.JAVA_OBJECT_MANAGER.getAutoGarbageCollector().SetAutoGarbageCollection(true);
	}
	
	private VtkGridUtils(){
		
	}

	private static int[] getVtkFaceStream(VisIrregularPolyhedron irregularPolyhedron) {
		ArrayList<Integer> faceStream = new ArrayList<Integer>();
		faceStream.add(irregularPolyhedron.polyhedronFaces.size());
		for (PolyhedronFace polyhedronFace : irregularPolyhedron.polyhedronFaces){
			faceStream.add(polyhedronFace.getVertices().size());
			for (int v : polyhedronFace.getVertices()){
				faceStream.add(v);
			}
		}
		int[] intFaceStream = new int[faceStream.size()];
		for (int i=0;i<intFaceStream.length;i++){
			intFaceStream[i] = faceStream.get(i);
		}
		return intFaceStream;
	}
	
	private static List<Integer> getPointIndices(VisIrregularPolyhedron irregularPolyhedron) {
		HashSet<Integer> pointIndicesSet = new HashSet<Integer>();
		for (PolyhedronFace face : irregularPolyhedron.polyhedronFaces){
			for (int pointIndex : face.vertices){
				pointIndicesSet.add(pointIndex);
			}
		}
		ArrayList<Integer> pointArray = new ArrayList<Integer>();
		for (Integer uniquePointIndex : pointIndicesSet){
			pointArray.add(uniquePointIndex);
		}
		return pointArray;
	}


	private static void writeLegacy_not_used(vtkUnstructuredGrid vtkgrid, String filename, boolean bASCII){
		vtkUnstructuredGridWriter writer = new vtkUnstructuredGridWriter();
		if (bASCII){
			writer.SetFileTypeToASCII();
		}else{
			writer.SetFileTypeToBinary();
		}
		
		writer.SetInputDataObject(vtkgrid);
		writer.SetFileName(filename);
		writer.Update();
		long length = new File(filename).length();
		if (LG.isInfoEnabled()) {
			LG.info("saved to legacy file: "+filename+" with "+((bASCII)?"ASCII":"Binary")+" data encoding, length="+length+" bytes");
		}
	}
	
	private static void writeXML(vtkUnstructuredGrid vtkgrid, String filename, boolean bASCII){
		vtkXMLUnstructuredGridWriter writer = new vtkXMLUnstructuredGridWriter();
		if (bASCII){
			writer.SetDataModeToAscii();
		}else{
			writer.SetCompressorTypeToNone();
			writer.SetDataModeToBinary();
		}
//		vtkEnSightWriter writer2 = new vtkEnSightWriter();
//		writer2.SetInputDataObject(id0, id1)
		
		writer.SetInputDataObject(vtkgrid);
		writer.SetFileName(filename);
		writer.Update();
		long length = new File(filename).length();
		if (LG.isInfoEnabled()) {
			LG.info("saved to XML file: "+filename+" with "+((bASCII)?"ASCII":"Binary")+" data encoding, length="+length+" bytes");
		}
	}
	
//	public void writeXDMF(vtkUnstructuredGrid vtkgrid, String filename){
//		vtkXdmfWriter writer = new vtkXdmfWriter();
//		writer.SetInputDataObject(vtkgrid);
//		writer.SetFileName(filename);
//		writer.Update();
//		long length = new File(filename).length();
//		System.out.println("saved to XDMF file: "+filename+", length="+length+" bytes");
//	}
//	
	static vtkUnstructuredGrid read(String filename){
		File file = new File(filename);
		if (!file.exists()){
			throw new RuntimeException("unstructured mesh file "+filename+" not found");
		}
		vtkXMLFileReadTester tester = new vtkXMLFileReadTester();
		tester.SetFileName(filename);
		vtkUnstructuredGrid vtkgrid = null;
		
		if (tester.TestReadFile() == 1){
			vtkXMLUnstructuredGridReader reader = new vtkXMLUnstructuredGridReader();
			reader.SetFileName(filename);
			reader.Update();
			vtkgrid = reader.GetOutput();
			if (LG.isInfoEnabled()) {
				LG.info("read from XML file "+filename+", of type "+tester.GetFileDataType());
			}
		}else{
			vtkUnstructuredGridReader reader = new vtkUnstructuredGridReader();
			reader.SetFileName(filename);
			reader.Update();
			vtkgrid = reader.GetOutput();
			if (LG.isInfoEnabled()) {
				LG.info("read from legacy file "+filename);
			}
		}
		//vtkgrid.BuildLinks();
		return vtkgrid;
	}

	private static void write(vtkUnstructuredGrid vtkgrid, String filename) {
		writeXML(vtkgrid,filename,false);		// default
		//writeXDMF(vtkgrid, filename);
//		writeXML(vtkgrid,filename,true);
//		writeLegacy(vtkgrid,filename,false);
//		writeLegacy(vtkgrid,filename,true);
	}

	private static List<VisTetrahedron> createTetrahedra(VisIrregularPolyhedron clippedPolyhedron, VisMesh visMesh){
		long beginTime = System.currentTimeMillis();
		try {
			vtkPolyData vtkpolydata = new vtkPolyData();
			vtkPoints vtkpoints = new vtkPoints();
			int polygonType = new vtkPolygon().GetCellType();
			List<Integer> uniquePointIndices = getPointIndices(clippedPolyhedron);
			for (int point : uniquePointIndices){
				VisPoint visPoint = visMesh.getPoints().get(point);
				vtkpoints.InsertNextPoint(visPoint.x, visPoint.y, visPoint.z);
			}
			vtkpolydata.Allocate(100, 100);
			vtkpolydata.SetPoints(vtkpoints);
			
			for (PolyhedronFace face : clippedPolyhedron.getPolyhedronFaces()){
				vtkIdList faceIdList = new vtkIdList();
				for (int visPointIndex : face.getVertices()){
					int vtkpointid = -1;
					for (int i=0;i<uniquePointIndices.size();i++){
						if (uniquePointIndices.get(i) == visPointIndex){
							vtkpointid = i;
						}
					}
					faceIdList.InsertNextId(vtkpointid);
				}
				vtkpolydata.InsertNextCell(polygonType, faceIdList);
			}
			
			vtkDelaunay3D delaunayFilter = new vtkDelaunay3D();
			delaunayFilter.SetInputData(vtkpolydata);
			delaunayFilter.Update();
			delaunayFilter.SetAlpha(0.1);
			vtkUnstructuredGrid vtkgrid2 = delaunayFilter.GetOutput();
			delaunayFilter.Delete();
			ArrayList<VisTetrahedron> visTets = new ArrayList<VisTetrahedron>();
			int numTets = vtkgrid2.GetNumberOfCells();
			if (numTets<1){
				LG.debug("found no tets");
			}
	//		System.out.println("numFaces = "+vtkpolydata.GetNumberOfCells()+", numTets = "+numTets);
			for (int cellIndex=0; cellIndex<numTets; cellIndex++){
				vtkCell cell = vtkgrid2.GetCell(cellIndex);
				if (cell instanceof vtkTetra){
					vtkTetra vtkTet = (vtkTetra)cell;
					vtkIdList tetPointIds = vtkTet.GetPointIds();
					//
					// translate from vtkgrid pointids to visMesh point ids
					//
					int numPoints = tetPointIds.GetNumberOfIds();
					ArrayList<Integer> visPointIds = new ArrayList<Integer>();
					for (int p=0; p<numPoints; p++){
						visPointIds.add(uniquePointIndices.get(tetPointIds.GetId(p)));
					}
					VisTetrahedron visTet = new VisTetrahedron(visPointIds);
					if (clippedPolyhedron.isSetChomboVolumeIndex()){
						visTet.setChomboVolumeIndex(clippedPolyhedron.getChomboVolumeIndex());
					}
					if (clippedPolyhedron.isSetFiniteVolumeIndex()){
						visTet.setFiniteVolumeIndex(clippedPolyhedron.getFiniteVolumeIndex());
					}
					visTets.add(visTet);
				}else{
					if (LG.isEnabledFor(Level.WARN)) {
						LG.warn("ChomboMeshMapping.createTetrahedra(): expecting a tet, found a "+cell.GetClassName());
					}
				}
			}
			return visTets;
		}finally{
			long beforeCleanupMS = System.currentTimeMillis();
//			cleanupVtk();
//			long afterCleanupMS = System.currentTimeMillis();
			System.out.println("createTetrahedra() worktime = "+(beforeCleanupMS-beginTime)+"ms");
		}
	}
	
	private static void cleanupVtk(){
		long beforeCleanupMS = System.currentTimeMillis();
		vtk.vtkObjectBase.JAVA_OBJECT_MANAGER.deleteAll();
		vtkReferenceInformation info = vtk.vtkObjectBase.JAVA_OBJECT_MANAGER.gc(false);
		long afterCleanupMS = System.currentTimeMillis();
		System.out.println("VtkGridUtils() cleanup time="+(afterCleanupMS-beforeCleanupMS)+"ms");
		System.out.println("vtk garbage collection : "+info.toString());
		if (info.getTotalNumberOfObjectsStillReferenced()>0){
			System.out.println("vtk total number of objects still referenced = "+info.getTotalNumberOfObjectsStillReferenced());
		}
	}

	private static vtkUnstructuredGrid smoothUnstructuredGridSurface(vtkUnstructuredGrid grid){
		
		vtkUnstructuredGridGeometryFilter ugGeometryFilter = new vtkUnstructuredGridGeometryFilter();
		ugGeometryFilter.PassThroughPointIdsOn();
		ugGeometryFilter.MergingOff();
		ugGeometryFilter.SetInputData(grid);
		ugGeometryFilter.Update();
		vtkUnstructuredGrid surfaceUnstructuredGrid = ugGeometryFilter.GetOutput();
		String originalPointsIdsName = ugGeometryFilter.GetOriginalPointIdsName();
		
		{
		vtkCellData cellData = surfaceUnstructuredGrid.GetCellData();
		int numCellArrays = cellData.GetNumberOfArrays();
		for (int i=0;i<numCellArrays;i++){
			String cellArrayName = cellData.GetArrayName(i);
			if (LG.isDebugEnabled()) {
				LG.debug("CellArray("+i+") \""+cellArrayName+"\"");
			}
		}
		vtkPointData pointData = surfaceUnstructuredGrid.GetPointData();
		int numPointArrays = pointData.GetNumberOfArrays();
		for (int i=0;i<numPointArrays;i++){
			String pointArrayName = pointData.GetArrayName(i);
			if (LG.isDebugEnabled()) {
				LG.debug("PointArray("+i+") \""+pointArrayName+"\"");
			}
		}
		}
		
		vtkGeometryFilter geometryFilter = new vtkGeometryFilter();
		geometryFilter.SetInputData(surfaceUnstructuredGrid);
		geometryFilter.Update();
		vtkPolyData polyData = geometryFilter.GetOutput();
		
		vtkWindowedSincPolyDataFilter filter = new vtkWindowedSincPolyDataFilter();
		filter.SetInputData(polyData);
		filter.SetNumberOfIterations(15);
		filter.BoundarySmoothingOff();
		filter.FeatureEdgeSmoothingOff();
		filter.SetFeatureAngle(120.0);
		filter.SetPassBand(0.001);
		filter.NonManifoldSmoothingOff();
		filter.NormalizeCoordinatesOn();
		filter.Update();
		
		vtkPolyData smoothedPolydata = filter.GetOutput();
		
		vtkPoints smoothedPoints = smoothedPolydata.GetPoints();
		
		vtkPointData smoothedPointData = smoothedPolydata.GetPointData();
		vtkIdTypeArray pointIdsArray = (vtkIdTypeArray)smoothedPointData.GetArray(originalPointsIdsName);
		int pointsIdsArraySize = pointIdsArray.GetSize();
		vtkPoints origPoints = grid.GetPoints();
		for (int i=0;i<pointsIdsArraySize;i++){
			int pointId = pointIdsArray.GetValue(i);
			double[] smoothedPoint = smoothedPoints.GetPoint(i);
			origPoints.SetPoint(pointId, smoothedPoint);
		}
	
		return grid;
	}

	static vtkUnstructuredGrid getVolumeVtkGrid(VisMesh vMesh) throws IOException{
		
		vtkPoints vtkpoints = new vtkPoints();
		for (VisPoint visPoint : vMesh.getPoints()) {
		    vtkpoints.InsertNextPoint(visPoint.x,visPoint.y,visPoint.z);
		}

		vtkUnstructuredGrid vtkgrid = new vtkUnstructuredGrid();
		vtkgrid.Allocate(vMesh.getPoints().size(), vMesh.getPoints().size());
		vtkgrid.SetPoints(vtkpoints);

		int quadType = new vtkQuad().GetCellType();
		//int lineType = new vtkLine().GetCellType();
		int polygonType = new vtkPolygon().GetCellType();
		int polyhedronType = new vtkPolyhedron().GetCellType();
		int triangleType = new vtkTriangle().GetCellType();
		int voxelType = new vtkVoxel().GetCellType();
		int tetraType = new vtkTetra().GetCellType();

		if (vMesh.isSetPolygons()){
			for (VisPolygon visPolygon : vMesh.getPolygons()) {
			    vtkIdList pts = new vtkIdList();
			    List<Integer> polygonPoints = visPolygon.getPointIndices();
			    for (int p : polygonPoints){
			        pts.InsertNextId(p);
			    }
	
			    int numPoints = polygonPoints.size();
			    if (numPoints == 4){
			        vtkgrid.InsertNextCell(quadType,pts);
			    }else if (numPoints == 3){
			        vtkgrid.InsertNextCell(triangleType,pts);
			    }else{
			        vtkgrid.InsertNextCell(polygonType,pts);
			    }
			}
		}
		//
		// replace any VisIrregularPolyhedron with a list of VisTetrahedron
		//
		if (vMesh.isSetVisVoxels()){
			for (VisVoxel voxel : vMesh.getVisVoxels()) {
			    vtkIdList pts = new vtkIdList();
			    List<Integer> polyhedronPoints = voxel.getPointIndices();
			    for (int p : polyhedronPoints){
			        pts.InsertNextId(p);
			    }
		        vtkgrid.InsertNextCell(voxelType,pts);
			}
		}
		if (vMesh.isSetTetrahedra()){
			for (VisTetrahedron visTet : vMesh.getTetrahedra()){
			    vtkIdList pts = new vtkIdList();
			    List<Integer> tetPoints = visTet.getPointIndices();
			    for (int p : tetPoints){
			        pts.InsertNextId(p);
			    }
		        vtkgrid.InsertNextCell(tetraType,pts);
			}
		}
		boolean bInitializedFaces = false;
		if (vMesh.isSetIrregularPolyhedra()){
			for (VisIrregularPolyhedron clippedPolyhedron : vMesh.getIrregularPolyhedra()) {
				if (bClipPolyhedra ){
					List<VisTetrahedron> tets = createTetrahedra(clippedPolyhedron, vMesh);
					for (VisTetrahedron visTet : tets){
					    vtkIdList pts = new vtkIdList();
					    List<Integer> tetPoints = visTet.getPointIndices();
					    for (int p : tetPoints){
					        pts.InsertNextId(p);
					    }
				        vtkgrid.InsertNextCell(tetraType,pts);
					}
				}else{
				    vtkIdList faceStreamList = new vtkIdList();
				    int[] faceStream = getVtkFaceStream(clippedPolyhedron);
				    for (int p : faceStream){
				        faceStreamList.InsertNextId(p);
				    }
				    if (!bInitializedFaces && vtkgrid.GetNumberOfCells()>0){
					    vtkgrid.InitializeFacesRepresentation(vtkgrid.GetNumberOfCells());
					}
					bInitializedFaces = true;
					vtkgrid.InsertNextCell(polyhedronType, faceStreamList);
				}
			}
		}		
		vtkgrid.BuildLinks();
		
		int numCells = vtkgrid.GetCells().GetNumberOfCells();
		
//		vtkgrid.Squeeze();

		return vtkgrid;
	}
	
	private static vtkUnstructuredGrid getMembraneVtkGrid(VisMesh visMesh) {
		vtkPoints vtkpoints = new vtkPoints();
		List<VisPoint> surfacePoints = visMesh.getSurfacePoints();
		for (VisPoint visPoint : surfacePoints) {
		    vtkpoints.InsertNextPoint(visPoint.x,visPoint.y,visPoint.z);
		}

		vtkUnstructuredGrid vtkgrid = new vtkUnstructuredGrid();
		vtkgrid.Allocate(surfacePoints.size(), surfacePoints.size());
		vtkgrid.SetPoints(vtkpoints);

		if (visMesh.getDimension() == 2)
		{
			int lineType = new vtkLine().GetCellType();
		
			for (VisLine line : visMesh.getVisLines()) 
			{
				vtkIdList pts = new vtkIdList();
				pts.InsertNextId(line.getP1());
				pts.InsertNextId(line.getP2());
				vtkgrid.InsertNextCell(lineType, pts);
			}
		}
		else
		{
			int triangleType = new vtkTriangle().GetCellType();
			for (VisSurfaceTriangle surfaceTriangle : visMesh.getSurfaceTriangles())
			{
				vtkIdList pts = new vtkIdList();
				for (int pi : surfaceTriangle.getPointIndices())
				{
					pts.InsertNextId(pi);
				}
				// each triangle is a cell
				vtkgrid.InsertNextCell(triangleType, pts);
			}
		}
		
		vtkgrid.BuildLinks();
		return vtkgrid;
	}

	public synchronized static void writeFiniteVolumeSmoothedVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtuFile, File indexFile) throws IOException {
		try {
			vtk.vtkUnstructuredGrid vtkgrid = getVolumeVtkGrid(visMesh);
			vtk.vtkUnstructuredGrid vtkgridSmoothed = VtkGridUtils.smoothUnstructuredGridSurface(vtkgrid);
			write(vtkgridSmoothed, vtuFile.getPath());
			FiniteVolumeIndexData finiteVolumeIndexData = new FiniteVolumeIndexData();
			finiteVolumeIndexData.setDomainName(domainName);
			if (visMesh.getDimension()==2){
				// if volume
				if (visMesh.isSetPolygons()){
					for (VisPolygon polygon : visMesh.getPolygons()){
						finiteVolumeIndexData.addToFiniteVolumeIndices(polygon.getFiniteVolumeIndex());
					}
				}
				// if membrane
				if (visMesh.isSetVisLines()){
					for (VisLine visLine : visMesh.getVisLines()){
						finiteVolumeIndexData.addToFiniteVolumeIndices(visLine.getFiniteVolumeIndex());
					}
				}
				if (finiteVolumeIndexData.finiteVolumeIndices==null || finiteVolumeIndexData.finiteVolumeIndices.size()==0){
					System.out.println("didn't find any indices ... bad");
				}
			}else if (visMesh.getDimension()==3){
				// if volume
				if (visMesh.isSetVisVoxels()){
					for (VisVoxel voxel : visMesh.getVisVoxels()){
						finiteVolumeIndexData.addToFiniteVolumeIndices(voxel.getFiniteVolumeIndex());
					}
				}
				if (visMesh.isSetIrregularPolyhedra()){
					throw new RuntimeException("unexpected irregular polyhedra in mesh, should have been replaced with tetrahedra");
				}
				if (visMesh.isSetTetrahedra()){
					for (VisTetrahedron tetrahedron : visMesh.getTetrahedra()){
						finiteVolumeIndexData.addToFiniteVolumeIndices(tetrahedron.getFiniteVolumeIndex());
					}
				}
				// if membrane
				if (visMesh.isSetPolygons()){
					for (VisPolygon polygon : visMesh.getPolygons()){
						finiteVolumeIndexData.addToFiniteVolumeIndices(polygon.getFiniteVolumeIndex());
					}
				}
				if (finiteVolumeIndexData.finiteVolumeIndices==null || finiteVolumeIndexData.finiteVolumeIndices.size()==0){
					System.out.println("didn't find any indices ... bad");
				}
			}
			VisMeshUtils.writeFiniteVolumeIndexData(indexFile, finiteVolumeIndexData);

			//vtkgridSmoothed.Delete();	// is needed for garbage collection?   //superfluous with prior delete according to runtime errors with this uncommented?
			vtkgrid.Delete(); 			// is needed for garbage collection?
		}finally{
			cleanupVtk();
		}
	}
	
	public synchronized static void writeChomboVolumeVtkGridAndIndexData(VisMesh visMesh, String domainName, File volumeMeshFile, File chomboIndexFile) throws IOException {
		try {
			VisMesh originalVisMesh = visMesh;
			VisMesh correctedVisMesh = originalVisMesh;
			if (originalVisMesh.isSetIrregularPolyhedra()){
				correctedVisMesh = new VisMesh(originalVisMesh);
				for (VisIrregularPolyhedron irregularPolyhedron : correctedVisMesh.getIrregularPolyhedra()){
					List<VisTetrahedron> tets = createTetrahedra(irregularPolyhedron, correctedVisMesh);
					for (VisTetrahedron tet : tets){
						correctedVisMesh.addToTetrahedra(tet);
					}
				}
				correctedVisMesh.setIrregularPolyhedra(null);
			}
//			if (originalVisMesh != correctedVisMesh){
//				write
//			}

			vtkUnstructuredGrid vtkgrid = getVolumeVtkGrid(correctedVisMesh);
			write(vtkgrid, volumeMeshFile.getPath());
			ChomboIndexData chomboIndexData = new ChomboIndexData();
			chomboIndexData.setDomainName(domainName);
			if (correctedVisMesh.getDimension()==2){
				if (correctedVisMesh.isSetPolygons()){
					for (VisPolygon polygon : correctedVisMesh.getPolygons()){
						chomboIndexData.addToChomboVolumeIndices(polygon.getChomboVolumeIndex());
					}
				}
				if (chomboIndexData.chomboVolumeIndices==null || chomboIndexData.chomboVolumeIndices.size()==0){
					System.out.println("didn't find any indices ... bad");
				}
			}else if (correctedVisMesh.getDimension()==3){
				// if volume
				if (correctedVisMesh.isSetVisVoxels()){
					for (VisVoxel voxel : correctedVisMesh.getVisVoxels()){
						chomboIndexData.addToChomboVolumeIndices(voxel.getChomboVolumeIndex());
					}
				}
				if (correctedVisMesh.isSetIrregularPolyhedra()){
					throw new RuntimeException("unexpected irregular polyhedra in mesh, should have been replaced with tetrahedra");
				}
				if (correctedVisMesh.isSetTetrahedra()){
					for (VisTetrahedron tetrahedron : correctedVisMesh.getTetrahedra()){
						chomboIndexData.addToChomboVolumeIndices(tetrahedron.getChomboVolumeIndex());
					}
				}
				if (chomboIndexData.chomboVolumeIndices==null || chomboIndexData.chomboVolumeIndices.size()==0){
					System.out.println("didn't find any indices ... bad");
				}
			}
			VisMeshUtils.writeChomboIndexData(chomboIndexFile, chomboIndexData);
			vtkgrid.Delete(); // if needed for garbage collection
		}finally{
			cleanupVtk();
		}
	}

	public synchronized static void writeChomboMembraneVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtuFile, File chomboIndexFile) throws IOException {
		try {
			vtkUnstructuredGrid vtkgrid = getMembraneVtkGrid(visMesh);
			write(vtkgrid, vtuFile.getPath());
			ChomboIndexData chomboIndexData = new ChomboIndexData();
			if (!domainName.toUpperCase().endsWith("MEMBRANE")){
				throw new RuntimeException("expecting domain name ending with membrane");
			}
			chomboIndexData.setDomainName(domainName);
			if (visMesh.getDimension()==3){
				if (visMesh.isSetSurfaceTriangles()){
					for (VisSurfaceTriangle surfaceTriangle : visMesh.getSurfaceTriangles()){
						chomboIndexData.addToChomboSurfaceIndices(surfaceTriangle.getChomboSurfaceIndex());
					}
				}
			}else if (visMesh.getDimension()==2){
				if (visMesh.isSetVisLines()){
					for (VisLine visLine : visMesh.getVisLines()){
						chomboIndexData.addToChomboSurfaceIndices(visLine.getChomboSurfaceIndex());
					}
				}
			}
			if (chomboIndexData.chomboSurfaceIndices==null || chomboIndexData.chomboSurfaceIndices.size()==0){
					System.out.println("didn't find any indices ... bad");
			}
			VisMeshUtils.writeChomboIndexData(chomboIndexFile, chomboIndexData);
			vtkgrid.Delete();
		}finally{
			cleanupVtk();
		}
	}

	public synchronized static void writeDataArrayToNewVtkFile(File emptyMeshFile, String varName, double[] data, File newMeshFile) throws IOException{
		try {
			vtkUnstructuredGrid vtkgrid = read(emptyMeshFile.getCanonicalPath());
			vtkgrid.BuildLinks();
			
			//
			// add cell data array to the empty mesh for this variable
			//
			vtkDoubleArray dataArray = new vtkDoubleArray();
			dataArray.SetName(varName);
			dataArray.SetJavaArray(data);
			vtkCellData cellData = vtkgrid.GetCellData();
			cellData.AddArray(dataArray);
			
			//
			// write mesh and data to the file for that domain and time
			//
			write(vtkgrid, newMeshFile.getAbsolutePath());
			vtkgrid.Delete();
		} finally {
			cleanupVtk();
		}
	}

	
}

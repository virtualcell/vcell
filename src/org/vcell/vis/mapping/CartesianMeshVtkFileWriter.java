package org.vcell.vis.mapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.vis.io.CartesianMeshFileReader;
import org.vcell.vis.io.DataSet;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vis.vcell.CartesianMesh;
import org.vcell.vis.vismesh.VisDataset;
import org.vcell.vis.vismesh.VisMesh;
import org.vcell.vis.vismesh.VisMeshData;
import org.vcell.vis.vismesh.VisPolygon;
import org.vcell.vis.vismesh.VisPolyhedron;
import org.vcell.vis.vtk.VtkGridUtils;

import vtk.vtkDoubleArray;
import vtk.vtkUnstructuredGrid;
import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.VCData;


public class CartesianMeshVtkFileWriter {
	
	private static final String REGION_ID_VAR = "regionId";
	public static final String GLOBAL_INDEX_VAR = "globalIndex";

	public interface ProgressListener {
		public void progress(double percentDone);
	}
	
	private boolean isMeshVar(DataIdentifier dataID){
		boolean bMeshVar = false;
		if (dataID.getName().startsWith(MathFunctionDefinitions.Function_regionArea_current.getFunctionName())){
			bMeshVar = true;
		}
		if (dataID.getName().startsWith(MathFunctionDefinitions.Function_regionVolume_current.getFunctionName())){
			bMeshVar = true;
		}
		if (dataID.getName().startsWith(DiffEquMathMapping.PARAMETER_K_FLUX_PREFIX)){
			bMeshVar = true;
		}
		if (dataID.getName().startsWith(DiffEquMathMapping.PARAMETER_SIZE_FUNCTION_PREFIX)){
			bMeshVar = true;
		}
		return bMeshVar;
	}

	public VtuVarInfo[] getVtuVarInfos(VCellSimFiles vcellFiles, OutputContext outputContext, VCData vcData) throws IOException, DataAccessException, MathException{
		CartesianMeshFileReader reader = new CartesianMeshFileReader();
		CartesianMesh mesh = reader.readFromFiles(vcellFiles);

		List<String> volumeDomainNames = mesh.getVolumeDomainNames();
		List<String> membraneDomainNames = mesh.getMembraneDomainNames();
		ArrayList<String> allDomains = new ArrayList<String>();
		allDomains.addAll(volumeDomainNames);
		allDomains.addAll(membraneDomainNames);
		
		DataIdentifier[] dataIdentifiers = vcData.getVarAndFunctionDataIdentifiers(outputContext);

		ArrayList<VtuVarInfo> varInfos = new ArrayList<VtuVarInfo>();
		for (String domainName : allDomains){
			VariableDomain varDomain = VariableDomain.VARIABLEDOMAIN_UNKNOWN;
			if (volumeDomainNames.contains(domainName)){
				varDomain = VariableDomain.VARIABLEDOMAIN_VOLUME;
			}else if (membraneDomainNames.contains(domainName)){
				varDomain = VariableDomain.VARIABLEDOMAIN_MEMBRANE;
			}
			for (DataIdentifier dataID : dataIdentifiers){
				if (dataID.getDomain()==null || dataID.getDomain().getName().equals(domainName)){
					boolean bMeshVar = isMeshVar(dataID);
					varInfos.add(new VtuVarInfo(dataID.getName(), "("+domainName+")  "+dataID.getDisplayName(), domainName, varDomain, bMeshVar));
				}
			}
			varInfos.add(new VtuVarInfo(GLOBAL_INDEX_VAR, "("+domainName+")  "+GLOBAL_INDEX_VAR, domainName, varDomain, true));
			varInfos.add(new VtuVarInfo(REGION_ID_VAR, "("+domainName+")  "+REGION_ID_VAR, domainName, varDomain, true));
		}
		return varInfos.toArray(new VtuVarInfo[0]); 
	}
	
	public File[] writeVtuExportFiles(VCellSimFiles vcellFiles, File destinationDirectory, ProgressListener progressListener) throws Exception{
		if (destinationDirectory==null || !destinationDirectory.isDirectory()){
			throw new RuntimeException("destinationDirectory '"+destinationDirectory+" not valid");
		}
		//
		// read the simplified cartesian mesh
		//
		CartesianMeshFileReader reader = new CartesianMeshFileReader();
		CartesianMesh mesh = reader.readFromFiles(vcellFiles);
		final int numVolumeElements = mesh.getSize().getXYZ();
		final int numVolumeRegions = mesh.getNumVolumeRegions();
		final int numMembraneRegions = mesh.getNumMembraneRegions();
		
		//
		// for each volume domain in mesh, extract the associated VisMesh
		//
		List<String> volumeDomainNames = mesh.getVolumeDomainNames();
		HashMap<String, VisMesh> domainMeshMap = new HashMap<String, VisMesh>();
		for (String volumeDomainName : volumeDomainNames){
			CartesianMeshMapping meshMapping = new CartesianMeshMapping();
			VisMesh visMesh = meshMapping.fromMeshData(mesh, volumeDomainName, true);
			domainMeshMap.put(volumeDomainName, visMesh);
		}
		
		//
		// for each membrane domain in mesh, extract the associated VisMesh
		//
		List<String> membraneDomainNames = mesh.getMembraneDomainNames();
		for (String membraneDomainName : membraneDomainNames){
			CartesianMeshMapping meshMapping = new CartesianMeshMapping();
			VisMesh visMesh = meshMapping.fromMeshData(mesh, membraneDomainName, false);
			domainMeshMap.put(membraneDomainName, visMesh);
		}
		
		ArrayList<String> allDomainNames = new ArrayList<String>();
		allDomainNames.addAll(volumeDomainNames);
		allDomainNames.addAll(membraneDomainNames);
		
		int numFiles = vcellFiles.getTimes().size() * allDomainNames.size();
		ArrayList<File> files = new ArrayList<File>();
		int filesProcessed = 0;
		int timeIndex = 0;
		for (final double time : vcellFiles.getTimes()){
			final File zipFile = vcellFiles.getZipFile(time);
			File pdeFile = vcellFiles.getZipEntry(time);

			final DataSet dataSet = new DataSet();
			dataSet.read(pdeFile, zipFile);
			String[] dataNames = dataSet.getDataNames();
			
			for (String anyDomain : allDomainNames){
				final boolean bVolume = volumeDomainNames.contains(anyDomain);
				//
				// find the globalIndexes for each domain element - use this to extract the domain-only data.
				//
				VisMesh visMesh = domainMeshMap.get(anyDomain);
				int tmpNumCells;
				int[] tmpRegionIndices;
				int[] tmpGlobalIndices;
				if (bVolume){
					if (visMesh.getDimension()==2){
						List<VisPolygon> polygons = visMesh.getPolygons();
						tmpNumCells = polygons.size();
						tmpGlobalIndices = new int[tmpNumCells];
						tmpRegionIndices = new int[tmpNumCells];
						int index = 0;
						for (VisPolygon poly : polygons){
							tmpGlobalIndices[index] = poly.getBoxIndex();
							tmpRegionIndices[index] = poly.getRegionIndex();
							index++;
						}
					}else if (visMesh.getDimension()==3){
						List<VisPolyhedron> polyhedra = visMesh.getPolyhedra();
						tmpNumCells = polyhedra.size();
						tmpGlobalIndices = new int[tmpNumCells];
						tmpRegionIndices = new int[tmpNumCells];
						int index = 0;
						for (VisPolyhedron poly : polyhedra){
							tmpGlobalIndices[index] = poly.getBoxIndex();
							tmpRegionIndices[index] = poly.getRegionIndex();
							index++;
						}
					}else{
						throw new RuntimeException("mesh dimension "+visMesh.getDimension()+" not supported for volume mesh VTK export");
					}
				}else{ // !bVolume
					if (visMesh.getDimension()==3){
						List<VisPolygon> polygons = visMesh.getPolygons();
						tmpNumCells = polygons.size();
						tmpGlobalIndices = new int[tmpNumCells];
						tmpRegionIndices = new int[tmpNumCells];
						int index = 0;
						for (VisPolygon poly : polygons){
							tmpGlobalIndices[index] = poly.getBoxIndex();
							tmpRegionIndices[index] = poly.getRegionIndex();
							index++;
						}
					}else if (visMesh.getDimension()==2){
						throw new RuntimeException("data for 2D mesh membranes not yet supported");
//						List<VisLine> lines = visMesh.getLines();
//						tmpNumCells = lines.size();
//						tmpGlobalIndices = new int[tmpNumCells];
//						tmpRegionIndices = new int[tmpNumCells];
//						int index = 0;
//						for (VisLine line : lines){
//							tmpGlobalIndices[index] = line.getBoxIndex();
//							tmpRegionIndices[index] = line.getRegionIndex();
//							index++;
//						}
					}else{
						throw new RuntimeException("mesh dimension "+visMesh.getDimension()+" not supported for volume mesh VTK export");
					}
				}
				final int[] volumeRegionIndices = tmpRegionIndices;
				final int[] globalIndices = tmpGlobalIndices;
				final int numCells = tmpNumCells;
				//
				// get the variables defined for this domain (always include the global index for now)
				//
				final ArrayList<String> domainVarNames = new ArrayList<String>();
				for (String dataName : dataNames){
					if (dataName.contains("::")){
						if (dataName.startsWith(anyDomain+"::")){
							domainVarNames.add(dataName);
						}else{
							System.out.println("skipping variable "+dataName);
						}
					}else{
						domainVarNames.add(dataName);
					}
				}
				final String globalIndexVarName = GLOBAL_INDEX_VAR;
				final String regionIdVarName = REGION_ID_VAR;
				domainVarNames.add(globalIndexVarName);
				domainVarNames.add(regionIdVarName);
				
				VisMeshData visMeshData = new VisMeshData(){
					@Override
					public String[] getVarNames() {
						return domainVarNames.toArray(new String[domainVarNames.size()]);
					}

					@Override
					public double getTime() {
						return time;
					}

					@Override
					public double[] getData(String var) throws IOException {
						int sizeDomain = globalIndices.length;
						double[] domainData = new double[sizeDomain];

						if (var.equals(globalIndexVarName)){
							for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
								domainData[domainIndex] = globalIndices[domainIndex];
							}
						}else if (var.equals(regionIdVarName)){
							for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
								domainData[domainIndex] = volumeRegionIndices[domainIndex];
							}
						}else{
							double[] globalData = dataSet.getData(var, zipFile);
							if (!bVolume && (globalData.length == numMembraneRegions)){
								// skip for now.
								for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
									domainData[domainIndex] = globalData[0];
								}
							}else if (bVolume && (globalData.length == numVolumeRegions)){
								for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
									domainData[domainIndex] = globalData[volumeRegionIndices[domainIndex]];
								}
							}else if (globalData.length == numCells || globalData.length == numVolumeElements){
								for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
									domainData[domainIndex] = globalData[globalIndices[domainIndex]];
								}
							}else{
								for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
									domainData[domainIndex] = -9999999.0;
								}
							}
						}
						return domainData;
					}
					
				};
				
				VisDataset.VisDomain visDomain = new VisDataset.VisDomain(anyDomain,visMesh,visMeshData);
				VtkGridUtils vtkGridUtils = new VtkGridUtils();
				vtkUnstructuredGrid vtkgrid = vtkGridUtils.getVolumeVtkGrid(visDomain);
				vtkUnstructuredGrid vtkgridSmoothed = VtkGridUtils.smoothUnstructuredGridSurface(vtkgrid);
				File file = getVtuMeshAndDataFileName(destinationDirectory, vcellFiles, visDomain.getName(), timeIndex);
				vtkGridUtils.write(vtkgridSmoothed, file.getPath());
				vtkgrid.Delete(); 			// is needed for garbage collection? 
				//vtkgridSmoothed.Delete();	// is needed for garbage collection?   //superfluous with prior delete according to runtime errors with this uncommented?
				files.add(file);
				filesProcessed++;
				if (progressListener!=null){
					progressListener.progress(((double)filesProcessed)/numFiles);
				}
			}
			timeIndex++;
		}
		return files.toArray(new File[0]);

	}
	
	private File getVtuMeshFileName(VCellSimFiles vcellFiles, String domainName) {
		File directory = vcellFiles.cartesianMeshFile.getParentFile();
		return new File(directory,vcellFiles.getCannonicalFilePrefix(domainName)+".vtu");
	}

	private File getVtuMeshAndDataFileName(File directory, VCellSimFiles vcellFiles, String domainName, int timeIndex) {
		return new File(directory,vcellFiles.getCannonicalFilePrefix(domainName,timeIndex)+".vtu");
	}


	public double[] getVtuMeshData(VCellSimFiles vcellFiles,  OutputContext outputContext, SimDataBlock simDataBlock, File destinationDirectory, VtuVarInfo var, final double time) throws Exception {
		//
		// read the "empty" vtk mesh from a file (create the files if necessary)
		// read the indicing arrays from this file to know how to reorder the data into the vtk cell data order.
		// return the vtk cell data
		//
		String domainName = var.domainName;
		File vtuMeshFile = getVtuMeshFileName(vcellFiles, var.domainName);
		
		vtkUnstructuredGrid vtkUnstructuredGrid = null;
		int numCells = -1;
		double[] globalIndexValues = null;
		int maxGlobalIndex = 0;
		double[] regionIndexValues = null;
		int maxRegionIndex = 0;
		try {
			if (!vtuMeshFile.exists()){
				writeEmptyMeshFiles(vcellFiles, destinationDirectory, null);
				if (!vtuMeshFile.exists()){
					throw new RuntimeException("failed to find vtk mesh file "+vtuMeshFile.getAbsolutePath());
				}
			}
			VtkGridUtils vtkGridUtils = new VtkGridUtils();
			vtkUnstructuredGrid = vtkGridUtils.read(vtuMeshFile.getAbsolutePath());
			vtkUnstructuredGrid.BuildLinks();
	
			final String globalIndexVarName = GLOBAL_INDEX_VAR;
			final String regionIdVarName = REGION_ID_VAR;
			
			int numberOfArrays = vtkUnstructuredGrid.GetCellData().GetNumberOfArrays();
			for (int i=0;i<numberOfArrays;i++){
				String arrayName = vtkUnstructuredGrid.GetCellData().GetArrayName(i);
				System.out.println("Array("+i+") named \""+arrayName+"\"");
			}
			vtkDoubleArray globalIndexArray = (vtkDoubleArray)vtkUnstructuredGrid.GetCellData().GetArray(globalIndexVarName);
			globalIndexValues = globalIndexArray.GetJavaArray();
			maxGlobalIndex = 0;
			for (double globalIndex : globalIndexValues){
				maxGlobalIndex = Math.max((int)globalIndex,maxGlobalIndex);
			}
			
			vtkDoubleArray regionIndexArray = (vtkDoubleArray)vtkUnstructuredGrid.GetCellData().GetArray(regionIdVarName);
			regionIndexValues = regionIndexArray.GetJavaArray();
			maxRegionIndex = 0;
			for (double regionIndex : regionIndexValues){
				maxRegionIndex = Math.max((int)regionIndex,maxRegionIndex);
			}
			numCells = vtkUnstructuredGrid.GetCells().GetNumberOfCells();
		} finally {
			if (vtkUnstructuredGrid!=null){
				vtkUnstructuredGrid.Delete();
			}
		}
		
		String vcellName = var.name;
		System.out.println("CartesianMeshVtkFileWriter.getVtuMeshData(): reading data for variable "+vcellName+" at time "+time);
		double[] cartesianMeshData = simDataBlock.getData();
		
		
		//
		// have to reorder the cartesian mesh data according to the vtk mesh cell indices (may not even be the same length)
		//
		double[] vtkData = new double[numCells];
		if (cartesianMeshData.length >= numCells){
			// data is not from region variable, uses global indices
			for (int vtkCellIndex=0; vtkCellIndex < numCells; vtkCellIndex++){
				int cartesianMeshGlobalIndex = (int)globalIndexValues[vtkCellIndex];
				vtkData[vtkCellIndex] = cartesianMeshData[cartesianMeshGlobalIndex];
			}
		}else{
			// data is from region variable, uses region indices
			for (int vtkCellIndex=0; vtkCellIndex < numCells; vtkCellIndex++){
				int cartesianMeshRegionIndex = (int)regionIndexValues[vtkCellIndex];
				vtkData[vtkCellIndex] = cartesianMeshData[cartesianMeshRegionIndex];
			}
		}
		
		return vtkData;
	}
	
	public VtuFileContainer getEmptyVtuMeshFiles(VCellSimFiles vcellFiles, File destinationDirectory) throws IOException, MathException, DataAccessException {
		//
		// read the simplified cartesian mesh
		//
		CartesianMeshFileReader reader = new CartesianMeshFileReader();
		CartesianMesh mesh = reader.readFromFiles(vcellFiles);
		
		//
		// for each domain in cartesian mesh, get the mesh file
		//
		ArrayList<String> domains = new ArrayList<String>();
		domains.addAll(mesh.getVolumeDomainNames());
		domains.addAll(mesh.getMembraneDomainNames());
		
		boolean bMeshFileMissing = false;
		ArrayList<File> meshFiles = new ArrayList<File>();
		for (String domainName : domains){
			File file = getVtuMeshFileName(vcellFiles, domainName);
			meshFiles.add(file);
			if (!file.exists()){
				bMeshFileMissing = true;
			}
		}
		
		if (bMeshFileMissing){
			writeEmptyMeshFiles(vcellFiles, destinationDirectory, null);
		}
		
		VtuFileContainer vtuFileContainer = new VtuFileContainer();
		for (int i=0;i<domains.size();i++){
			byte[] vtuMeshFileContents = FileUtils.readByteArrayFromFile(meshFiles.get(i));
			vtuFileContainer.addVtuMesh(new VtuFileContainer.VtuMesh(domains.get(i), 0.0, vtuMeshFileContents));
		}
		return vtuFileContainer;
	}
	
	public File[] writeEmptyMeshFiles(VCellSimFiles vcellFiles, File destinationDirectory, ProgressListener progressListener) throws IOException, MathException {
		if (destinationDirectory==null || !destinationDirectory.isDirectory()){
			throw new RuntimeException("destinationDirectory '"+destinationDirectory+" not valid");
		}
		//
		// read the simplified cartesian mesh
		//
		CartesianMeshFileReader reader = new CartesianMeshFileReader();
		CartesianMesh mesh = reader.readFromFiles(vcellFiles);
		
		//
		// for each volume domain in mesh, extract the associated VisMesh
		//
		List<String> volumeDomainNames = mesh.getVolumeDomainNames();
		HashMap<String, VisMesh> domainMeshMap = new HashMap<String, VisMesh>();
		for (String volumeDomainName : volumeDomainNames){
			CartesianMeshMapping meshMapping = new CartesianMeshMapping();
			VisMesh visMesh = meshMapping.fromMeshData(mesh, volumeDomainName, true);
			domainMeshMap.put(volumeDomainName, visMesh);
		}
		
		//
		// for each membrane domain in mesh, extract the associated VisMesh
		//
		List<String> membraneDomainNames = mesh.getMembraneDomainNames();
		for (String membraneDomainName : membraneDomainNames){
			CartesianMeshMapping meshMapping = new CartesianMeshMapping();
			VisMesh visMesh = meshMapping.fromMeshData(mesh, membraneDomainName, false);
			domainMeshMap.put(membraneDomainName, visMesh);
		}
		
		ArrayList<String> allDomainNames = new ArrayList<String>();
		allDomainNames.addAll(volumeDomainNames);
		allDomainNames.addAll(membraneDomainNames);
		
		int numFiles = vcellFiles.getTimes().size() * allDomainNames.size();
		ArrayList<File> files = new ArrayList<File>();
		int filesProcessed = 0;
			
		for (final String domainName : allDomainNames){
			final boolean bVolume = volumeDomainNames.contains(domainName);
			//
			// find the globalIndexes for each domain element - use this to extract the domain-only data.
			//
			VisMesh visMesh = domainMeshMap.get(domainName);
			int[] tmpRegionIndices;
			int[] tmpGlobalIndices;
			if (bVolume){
				if (visMesh.getDimension()==2){
					List<VisPolygon> polygons = visMesh.getPolygons();
					int numCells = polygons.size();
					tmpGlobalIndices = new int[numCells];
					tmpRegionIndices = new int[numCells];
					int index = 0;
					for (VisPolygon poly : polygons){
						tmpGlobalIndices[index] = poly.getBoxIndex();
						tmpRegionIndices[index] = poly.getRegionIndex();
						index++;
					}
				}else if (visMesh.getDimension()==3){
					List<VisPolyhedron> polyhedra = visMesh.getPolyhedra();
					int numCells = polyhedra.size();
					tmpGlobalIndices = new int[numCells];
					tmpRegionIndices = new int[numCells];
					int index = 0;
					for (VisPolyhedron poly : polyhedra){
						tmpGlobalIndices[index] = poly.getBoxIndex();
						tmpRegionIndices[index] = poly.getRegionIndex();
						index++;
					}
				}else{
					throw new RuntimeException("mesh dimension "+visMesh.getDimension()+" not supported for volume mesh VTK export");
				}
			}else{ // !bVolume
				if (visMesh.getDimension()==3){
					List<VisPolygon> polygons = visMesh.getPolygons();
					int numCells = polygons.size();
					tmpGlobalIndices = new int[numCells];
					tmpRegionIndices = new int[numCells];
					int index = 0;
					for (VisPolygon poly : polygons){
						tmpGlobalIndices[index] = poly.getBoxIndex();
						tmpRegionIndices[index] = poly.getRegionIndex();
						index++;
					}
				}else if (visMesh.getDimension()==2){
					throw new RuntimeException("data for 2D mesh membranes not yet supported");
//						List<VisLine> lines = visMesh.getLines();
//						int numCells = lines.size();
//						tmpGlobalIndices = new int[numCells];
//						tmpRegionIndices = new int[numCells];
//						int index = 0;
//						for (VisLine line : lines){
//							tmpGlobalIndices[index] = line.getBoxIndex();
//							tmpRegionIndices[index] = line.getRegionIndex();
//							index++;
//						}
				}else{
					throw new RuntimeException("mesh dimension "+visMesh.getDimension()+" not supported for volume mesh VTK export");
				}
			}
			final int[] volumeRegionIndices = tmpRegionIndices;
			final int[] globalIndices = tmpGlobalIndices;

			VisMeshData visMeshData = new VisMeshData(){
				@Override
				public String[] getVarNames() {
					return new String[] { GLOBAL_INDEX_VAR, REGION_ID_VAR };
				}

				@Override
				public double getTime() {
					return 0.0;
				}

				@Override
				public double[] getData(String var) throws IOException {
					int sizeDomain = globalIndices.length;
					double[] domainData = new double[sizeDomain];

					if (var.equals(GLOBAL_INDEX_VAR)){
						for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
							domainData[domainIndex] = globalIndices[domainIndex];
						}
					}else if (var.equals(REGION_ID_VAR)){
						for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
							domainData[domainIndex] = volumeRegionIndices[domainIndex];
						}
					}else{
						throw new RuntimeException("unexpected var");
					}
					return domainData;
				}
				
			};
			
			VisDataset.VisDomain visDomain = new VisDataset.VisDomain(domainName,visMesh,visMeshData);
			VtkGridUtils vtkGridUtils = new VtkGridUtils();
			vtkUnstructuredGrid vtkgrid = vtkGridUtils.getVolumeVtkGrid(visDomain);
			vtkUnstructuredGrid vtkgridSmoothed = VtkGridUtils.smoothUnstructuredGridSurface(vtkgrid);
			File file = getVtuMeshFileName(vcellFiles, visDomain.getName());
			vtkGridUtils.write(vtkgridSmoothed, file.getPath());
			vtkgrid.Delete(); 			// is needed for garbage collection? 
			//vtkgridSmoothed.Delete();	// is needed for garbage collection?   //superfluous with prior delete according to runtime errors with this uncommented?
			files.add(file);
			filesProcessed++;
			if (progressListener!=null){
				progressListener.progress(((double)filesProcessed)/numFiles);
			}
		}
		return files.toArray(new File[0]);

	}


}

package org.vcell.vis.mapping.vcell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.vis.io.CartesianMeshFileReader;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vis.io.VtuVarInfo.DataType;
import org.vcell.vis.vcell.CartesianMesh;
import org.vcell.vis.vismesh.thrift.FiniteVolumeIndex;
import org.vcell.vis.vismesh.thrift.FiniteVolumeIndexData;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vtk.VisMeshUtils;
import org.vcell.vis.vtk.VtkService;

import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.VCData;
import cbit.vcell.solver.AnnotatedFunction;


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
		AnnotatedFunction[] annotationFunctions = vcData.getFunctions(outputContext);

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
					String expressionString = null;
					if (dataID.isFunction()){
						for (AnnotatedFunction f : annotationFunctions){
							if (f.getName().equals(dataID.getName())){
								expressionString = f.getExpression().infix();
							}
						}
					}
					varInfos.add(new VtuVarInfo(dataID.getName(), "("+domainName+")  "+dataID.getDisplayName(), domainName, varDomain, expressionString, DataType.CellData, bMeshVar));
				}
			}
			varInfos.add(new VtuVarInfo(GLOBAL_INDEX_VAR, "("+domainName+")  "+GLOBAL_INDEX_VAR, domainName, varDomain, null, DataType.CellData, true));
			varInfos.add(new VtuVarInfo(REGION_ID_VAR, "("+domainName+")  "+REGION_ID_VAR, domainName, varDomain, null, DataType.CellData, true));
		}
		return varInfos.toArray(new VtuVarInfo[0]); 
	}
	
	public File[] writeVtuExportFiles(VCellSimFiles vcellFiles, File destinationDirectory, ProgressListener progressListener) throws Exception{
		throw new RuntimeException("not yet implemented");
	}
	

	private File getVtuMeshFileName(VCellSimFiles vcellFiles, String domainName) {
		File directory = vcellFiles.cartesianMeshFile.getParentFile();
		return new File(directory,vcellFiles.getCannonicalFilePrefix(domainName)+".vtu");
	}

	private File getFiniteVolumeIndexDataFileName(VCellSimFiles vcellFiles, String domainName) {
		File directory = vcellFiles.cartesianMeshFile.getParentFile();
		return new File(directory,vcellFiles.getCannonicalFilePrefix(domainName)+".fvindex");
	}

	public double[] getVtuMeshData(VCellSimFiles vcellFiles,  OutputContext outputContext, SimDataBlock simDataBlock, File destinationDirectory, VtuVarInfo var, final double time) throws Exception {
		//
		// read the "empty" vtk mesh from a file (create the files if necessary)
		// read the indicing arrays from this file to know how to reorder the data into the vtk cell data order.
		// return the vtk cell data
		//
		File finiteVolumeIndexDataFile = getFiniteVolumeIndexDataFileName(vcellFiles, var.domainName);
		
		if (!finiteVolumeIndexDataFile.exists()){
			writeEmptyMeshFiles(vcellFiles, destinationDirectory, null);
			if (!finiteVolumeIndexDataFile.exists()){
				throw new RuntimeException("failed to find finite volume index file "+finiteVolumeIndexDataFile.getAbsolutePath());
			}
		}
		
		FiniteVolumeIndexData finiteVolumeIndexData = VisMeshUtils.readFiniteVolumeIndexData(finiteVolumeIndexDataFile);
		int maxGlobalIndex = 0;
		int maxRegionIndex = 0;
		for (FiniteVolumeIndex fvIndex : finiteVolumeIndexData.finiteVolumeIndices){
			maxGlobalIndex = Math.max((int)fvIndex.globalIndex,maxGlobalIndex);
			maxRegionIndex = Math.max((int)fvIndex.regionIndex,maxRegionIndex);
		}
		
		int numCells = finiteVolumeIndexData.finiteVolumeIndices.size();
		
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
				int cartesianMeshGlobalIndex = (int)finiteVolumeIndexData.finiteVolumeIndices.get(vtkCellIndex).globalIndex;
				vtkData[vtkCellIndex] = cartesianMeshData[cartesianMeshGlobalIndex];
			}
		}else{
			// data is from region variable, uses region indices
			for (int vtkCellIndex=0; vtkCellIndex < numCells; vtkCellIndex++){
				int cartesianMeshRegionIndex = (int)finiteVolumeIndexData.finiteVolumeIndices.get(vtkCellIndex).regionIndex;
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
			//
			// find the globalIndexes for each domain element - use this to extract the domain-only data.
			//
			VisMesh visMesh = domainMeshMap.get(domainName);
			File vtuFile = getVtuMeshFileName(vcellFiles, domainName);
			File fvIndexDataFileName = getFiniteVolumeIndexDataFileName(vcellFiles, domainName);
			VtkService.getInstance().writeFiniteVolumeSmoothedVtkGridAndIndexData(visMesh, domainName, vtuFile, fvIndexDataFileName);
			files.add(vtuFile);
			filesProcessed++;
			if (progressListener!=null){
				progressListener.progress(((double)filesProcessed)/numFiles);
			}
		}
		return files.toArray(new File[0]);

	}


}

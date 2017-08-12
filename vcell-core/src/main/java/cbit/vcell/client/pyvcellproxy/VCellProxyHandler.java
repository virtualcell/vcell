package cbit.vcell.client.pyvcellproxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.thrift.TException;
import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vis.vtk.VisMeshUtils;

import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.VtkManager;

public class VCellProxyHandler implements VCellProxy.Iface{
	
	private final VCellClientDataService vcellClientDataService;
//private final VCellClient vcellClient;
private final File localVisDataDir;

public VCellProxyHandler(VCellClientDataService vcellClientDataService) {
	this.vcellClientDataService = vcellClientDataService;
	this.localVisDataDir = ResourceUtil.getLocalVisDataDir();
}

@Override 
public List<SimulationDataSetRef> getSimsFromOpenModels() {
	return vcellClientDataService.getSimsFromOpenModels();
}

@Override
public List<VariableInfo> getVariableList(SimulationDataSetRef simulationDataSetRef) throws cbit.vcell.client.pyvcellproxy.ThriftDataAccessException {
	try {
		VtkManager vtkManager = vcellClientDataService.getVtkManager(simulationDataSetRef);
		VtuVarInfo[] vtuVarInfos = vtkManager.getVtuVarInfos();
		ArrayList<VariableInfo> varInfoList = new ArrayList<VariableInfo>();
		for (VtuVarInfo vtuVarInfo : vtuVarInfos){
			DomainType variableDomainType = null;
			switch (vtuVarInfo.variableDomain){
				case VARIABLEDOMAIN_MEMBRANE:{
					variableDomainType = DomainType.MEMBRANE;
					break;
				}
				case VARIABLEDOMAIN_VOLUME:{
					variableDomainType = DomainType.VOLUME;
					break;
				}
				case VARIABLEDOMAIN_POINT:{
					System.err.println("VCellProxyHandler.getVariableList() skipping Point Variable "+vtuVarInfo.name+" ... implement later.");
					continue; // skip point variables now.
				}
				default:{
					break;
				}
			}
			DataType dataType = null;
			switch (vtuVarInfo.dataType){
			case CellData:{
				dataType = DataType.CELLDATA;
				break;
			}
			case PointData:{
				dataType = DataType.POINTDATA;
				break;
			}
			}
			String unitsLabel = "<unknown unit>";
			if (vtuVarInfo.bMeshVariable){
				continue; // skip "mesh variables" like size, vcRegionArea, etc, globalIndex, etc.
			}
			VariableInfo variableInfo = new VariableInfo(vtuVarInfo.name, vtuVarInfo.displayName, vtuVarInfo.domainName, variableDomainType, unitsLabel, vtuVarInfo.bMeshVariable, dataType);
			variableInfo.setExpressionString(vtuVarInfo.functionExpression);
			varInfoList.add(variableInfo);
		}
		return varInfoList;
	} catch (Exception e) {
		e.printStackTrace();
		throw new ThriftDataAccessException("failed to retrieve variable list for data set.");
	}

}



private File getEmptyMeshFileLocation(SimulationDataSetRef simulationDataSetRef, String domainName, int timeIndex) throws FileNotFoundException{
		
	//
	// create the dataset directory if necessary
	//
	File vtuSimDataFolder = new File(localVisDataDir,simulationDataSetRef.getSimId());
	if (!(vtuSimDataFolder.exists() && vtuSimDataFolder.isDirectory())){
		vtuSimDataFolder.mkdirs();
	}
	
	//
	// compose the specific mesh filename
	//
	File vtuDataFile = new File(vtuSimDataFolder, simulationDataSetRef.getSimId()+"_"+domainName+"_"+String.format("%06d" , timeIndex)+".vtu");
	return vtuDataFile;
}

private File getPopulatedMeshFileLocation(SimulationDataSetRef simulationDataSetRef, VariableInfo varInfo, int timeIndex) throws FileNotFoundException{
	
	//
	// create the dataset directory if necessary
	//
	File vtuSimDataFolder = new File(localVisDataDir,simulationDataSetRef.getSimId());
	if (!(vtuSimDataFolder.exists() && vtuSimDataFolder.isDirectory())){
		vtuSimDataFolder.mkdirs();
	}
	
	//
	// compose the specific mesh filename
	//
	String timeIndexStr = String.format("%06d" , timeIndex);
	String domainName = varInfo.domainName;
	String varName = varInfo.variableVtuName.replace(":", "_");
	File vtuDataFile = new File(vtuSimDataFolder, simulationDataSetRef.getSimId()+"_"+domainName+"_"+varName+"_"+timeIndexStr+".vtu");
	return vtuDataFile;
}

@Override
public String getDataSetFileOfVariableAtTimeIndex(SimulationDataSetRef simulationDataSetRef, VariableInfo var, int timeIndex) throws ThriftDataAccessException {
	try {
		if (var.isMeshVar){
			return getEmptyMeshFile(simulationDataSetRef, var.domainName, timeIndex).getAbsolutePath();
		}
		File meshFileForVariableAndTime = getPopulatedMeshFileLocation(simulationDataSetRef, var, timeIndex);
		if (meshFileForVariableAndTime.exists()){
			return meshFileForVariableAndTime.getAbsolutePath();
		}
		
		//
		// get data from server for this variable, domain, time
		//
		VtkManager vtkManager = vcellClientDataService.getVtkManager(simulationDataSetRef);
		VariableDomain variableDomainType = VariableDomain.VARIABLEDOMAIN_UNKNOWN;
		if (var.variableDomainType == DomainType.MEMBRANE){
			variableDomainType = VariableDomain.VARIABLEDOMAIN_MEMBRANE;
		}else if (var.variableDomainType == DomainType.VOLUME){
			variableDomainType = VariableDomain.VARIABLEDOMAIN_VOLUME;
		}
		org.vcell.vis.io.VtuVarInfo.DataType dataType = org.vcell.vis.io.VtuVarInfo.DataType.CellData;
		if (var.isSetDataType() && var.dataType == DataType.CELLDATA){
			dataType = org.vcell.vis.io.VtuVarInfo.DataType.CellData;
		}else if (var.isSetDataType() && var.dataType == DataType.POINTDATA){
			dataType = org.vcell.vis.io.VtuVarInfo.DataType.PointData;
		}
		VtuVarInfo vtuVarInfo = new VtuVarInfo(var.getVariableVtuName(),var.getVariableDisplayName(),var.getDomainName(),variableDomainType,var.getExpressionString(),dataType,var.isMeshVar);
		List<Double> times = getTimePoints(simulationDataSetRef);
		double time = (double)times.get(timeIndex);
		double[] data = vtkManager.getVtuMeshData(vtuVarInfo, time);
		
		//
		// get empty mesh file for this domain (getEmptyMeshFile() will ensure that the file exists or create it).
		//
		File emptyMeshFile = getEmptyMeshFile(simulationDataSetRef, var.getDomainName(), timeIndex);
		if (var.getDataType()==DataType.CELLDATA){
			VisMeshUtils.writeCellDataToVtu(emptyMeshFile, var.getVariableVtuName(), data, meshFileForVariableAndTime);
		}else if (var.getDataType()==DataType.POINTDATA){
			VisMeshUtils.writePointDataToVtu(emptyMeshFile, var.getVariableVtuName(), data, meshFileForVariableAndTime);
		}
		return meshFileForVariableAndTime.getAbsolutePath();
	} catch (Exception e) {
		e.printStackTrace();
		throw new ThriftDataAccessException("failed to retrieve data file for variable "+var.getVariableVtuName()+" at time index "+timeIndex);
	}
}


private File getEmptyMeshFile(SimulationDataSetRef simulationDataSetRef, String domainName, int timeIndex) throws ThriftDataAccessException {
	
	if (!simulationDataSetRef.isTimeVaryingMesh){
		timeIndex = 0;
	}

	File vtuEmptyMeshFile;
	try {
		vtuEmptyMeshFile = getEmptyMeshFileLocation(simulationDataSetRef, domainName, timeIndex);
	} catch (FileNotFoundException e1) {
		e1.printStackTrace();
		throw new ThriftDataAccessException("failed to find data location: "+e1.getMessage());
	}

	System.out.println("looking for file: "+vtuEmptyMeshFile);


	//
	// if requested mesh file is not found, get files for this timepoint
	//
	if (!vtuEmptyMeshFile.exists()){
		try {
			VtuFileContainer vtuFileContainer = downloadEmptyVtuFileContainer(simulationDataSetRef, timeIndex);

			for (VtuFileContainer.VtuMesh mesh : vtuFileContainer.getVtuMeshes()){
				FileUtils.writeByteArrayToFile(mesh.vtuMeshContents, getEmptyMeshFileLocation(simulationDataSetRef, mesh.domainName, timeIndex));
			}
			
			if (!vtuEmptyMeshFile.exists()){
				System.out.println("after export, couldn't find requested empty mesh file "+vtuEmptyMeshFile);
				throw new ThriftDataAccessException("after export, couldn't find empty requested mesh file "+vtuEmptyMeshFile);
			}
			System.out.println("vtuData file exists, " + vtuEmptyMeshFile);

		}catch (IOException e){
			e.printStackTrace();
			System.out.println("failed to export entire dataset: "+e.getMessage());
			throw new ThriftDataAccessException("failed to export entire dataset: "+e.getMessage());
		}
	}
	
	return vtuEmptyMeshFile;
}

private VtuFileContainer downloadEmptyVtuFileContainer(SimulationDataSetRef simulationDataSetRef, int timeIndex) throws ThriftDataAccessException {
	try {
		VtkManager vtkManager = vcellClientDataService.getVtkManager(simulationDataSetRef);
		VtuFileContainer vtuFileContainer = vtkManager.getEmptyVtuMeshFiles(timeIndex);
		return vtuFileContainer;
	}catch (Exception e){
		e.printStackTrace();
		throw new ThriftDataAccessException("failed to get data for simulation "+simulationDataSetRef+": "+e.getMessage());
	}
}


@Override
public List<Double> getTimePoints(SimulationDataSetRef simulationDataSetRef) throws ThriftDataAccessException {
	try {
		VtkManager vtkManager = vcellClientDataService.getVtkManager(simulationDataSetRef);
		
		ArrayList<Double> timesList = new ArrayList<Double>();
		if (vtkManager != null) {				
			double[] timesArray = vtkManager.getDataSetTimes();
			for (int i=0; i<timesArray.length; i++){
			    timesList.add(new Double(timesArray[i]));
			}
			return timesList;			 
		}
		return null;
	} catch (FileNotFoundException | DataAccessException e) {
		e.printStackTrace();
		throw new ThriftDataAccessException(e.getMessage());
	}
}

@Override
public PostProcessingData getPostProcessingData(
		SimulationDataSetRef simulationDataSetRef)
		throws ThriftDataAccessException, TException {
	return null;
}

@Override
public void displayPostProcessingDataInVCell(SimulationDataSetRef simulationDataSetRef)throws ThriftDataAccessException, TException {
	 try {
		 vcellClientDataService.displayPostProcessingDataInVCell(simulationDataSetRef);
	} catch (Throwable exc2) {
		 exc2.printStackTrace();
		 throw new ThriftDataAccessException(exc2.getMessage());
	}
}

double[] ListOfDoublesToPrimitiveArrayOfdoubles(List<Double> arrayListOfDoubles){
	ListIterator<Double> listIter = arrayListOfDoubles.listIterator();
	double[] doubles = new double[arrayListOfDoubles.size()];
	int i = 0;
	while (listIter.hasNext()){
		doubles[i] = listIter.next().doubleValue();
	}
	return doubles;
}

}
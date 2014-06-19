package cbit.vcell.simdata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
//import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.zip.ZipFile;

import javax.swing.tree.DefaultMutableTreeNode;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5CompoundDS;

import org.vcell.chombo.ChomboBox;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.Origin;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.mongodb.VCMongoMessage;

public class SimulationDataSpatialHdf5
{
	public static class SimLogFileEntry
	{
		int iteration;
		String simHdf5FileName;
		String hdf5ZipFileName;
		double time;
		public SimLogFileEntry(int iteration, String simFileName,
				String zipFileName, double time) {
			super();
			this.iteration = iteration;
			this.simHdf5FileName = simFileName;
			this.hdf5ZipFileName = zipFileName;
			this.time = time;
		}
	}
	public static class ChomboMesh
	{
		int dimension;
		int[] nx = new int[3];
		double[] dx = new double[3];
		double[] extent = new double[3];
		double[] origin = new double[3];
		String[] metricsColumnNames;
		
		private List<ChomboBox> boxList = new ArrayList<ChomboBox>();
		private Map<Integer, ChomboTriangle> surfaceMap = new HashMap<Integer, ChomboTriangle>();
		private List<ChomboMeshMetricsEntry> metrics = new ArrayList<ChomboMeshMetricsEntry>();
		
		public List<Number[]> getMetricsNumbers()
		{
			List<Number[]> numberList = new ArrayList<Number[]>();
			for (ChomboMeshMetricsEntry entry : metrics)
			{
				numberList.add(entry.getNumbers(dimension));
			}
			return numberList;
		}
		
		public String[] getMetricsColumnNames()
		{
			return metricsColumnNames;
		}
		public Origin getOrigin(){
			return new Origin(origin[0],origin[1],origin[2]);
		}
		public Extent getExtent(){
			return new Extent(extent[0],(extent[1]==0?.5:extent[1]),(extent[2]==0?.5:extent[2]));
		}
		public int getSizeX(){
			return nx[0];
		}
		public int getSizeY(){
			return (nx[1]==0?1:nx[1]);
		}
		public int getSizeZ(){
			return (nx[2]==0?1:nx[2]);
		}
		public int getDimension(){
			return dimension;
		}
	}
		
	public static class ChomboMeshMetricsEntry
	{
		int index,i,j,k;
		double x,y,z;
		double normalx, normaly, normalz;
		double volFrac;
		double areaFrac;
		public ChomboMeshMetricsEntry(int index, int i, int j, int k, double x,
				double y, double z, double normalx, double normaly,
				double normalz, double volFrac, double areaFrac) 
		{
			super();
			this.index = index;
			this.i = i;
			this.j = j;
			this.k = k;
			this.x = x;
			this.y = y;
			this.z = z;
			this.normalx = normalx;
			this.normaly = normaly;
			this.normalz = normalz;
			this.volFrac = volFrac;
			this.areaFrac = areaFrac;
		}
		Number[] getNumbers(int dimension)
		{
			if (dimension == 2)
			{
				return new Number[]{index, i, j, x, y, normalx, normaly, volFrac, areaFrac};
			}
			return new Number[]{index, i, j, k, x, y, z, normalx, normaly, normalz, volFrac, areaFrac};
		}
	}
	
	public static class ChomboTriangle
	{
		double[] p0;
		double[] p1;
		double[] p2;
	}
	
	public static class SimDataSet
	{
		public double[] solValues;
		public Double mean;
		public Double sumVolFrac;
		public Double l2Error;
		public Double maxError;
	}
	
	public static final String LOG_EXT = ".log";
	public static final String HDF5_EXT = ".hdf5";
	public static final String MESH_HDF5_EXT = ".mesh.hdf5";
//	public static final String SIM_ZIP_HDF5_EXT = ".hdf5.zip";
//	public static final String SIM_ZIP_EXT = ".zip";
	
	private VCDataIdentifier vcDataId = null;
	private List<DataSetIdentifier> dataSetIdentifierList = Collections.synchronizedList(new ArrayList<DataSetIdentifier>());
	private File userDirectory = null;
	private List<SimLogFileEntry> logfileEntryList = Collections.synchronizedList(new ArrayList<SimLogFileEntry>());
	private File logFile = null;
	private long logFileLastModified = 0;
	private long logFileLength = 0;	
	private ChomboMesh chomboMesh;
	
	public SimulationDataSpatialHdf5(VCDataIdentifier argVCDataID, File primaryUserDir, File secondaryUserDir) 
			throws IOException, DataAccessException 
	{
		this.vcDataId = argVCDataID;
		this.userDirectory = primaryUserDir;
		findLogFile();
		if (logFile == null)
		{
			userDirectory = secondaryUserDir;
			findLogFile();
			if (logFile == null)
			{
				throw new FileNotFoundException("simulation data for [" + vcDataId + "] " +
						"does not exist in primary [" + primaryUserDir + "] or secondary [" + secondaryUserDir + "] user directory .");
			}
		}
		VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.SimulationDataSpatialHdf5() <<EXIT>>");
	}
	
	public synchronized void readVarAndFunctionDataIdentifiers() throws Exception {
		VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.readVarAndFunctionDataIdentifiers Entry");
		readLogFile();
		if(chomboMesh == null){
			chomboMesh = readMeshFile(new File(userDirectory, getMeshFileName()));
		}
		
		if (logfileEntryList.size() > 0) 
		{
			retriveVariableNames();		
			// always read functions file since functions might change
//			getFunctionDataIdentifiers(outputContext);
		}
	}

	private static final String ROOT_GROUP = "/";
	private static final String MESH_GROUP = "/mesh";
	private static final String BOXES_DATASET = "boxes";
	private static final String METRICS_DATASET = "metrics";
	private static final String SURFACE_DATASET = "surface";
	private static final String SLICE_VIEW_DATASET = "slice view";
	private static final String MESH_ATTR_DIMENSION = "dimension";
	private static final String MESH_ATTR_ORIGIN = "origin";
	private static final String MESH_ATTR_EXTENT = "extent";
	private static final String MESH_ATTR_NX = "Nx";
	private static final String MESH_ATTR_DX = "Dx";
	
	private static final String SOLUTION_GROUP = "/solution";
	private static final String SOLUTION_ATTR_TIME = "time";
	private static final String SOLUTION_ATTR_VARIABLES = "variables";
	private static final String SOLUTION_ATTR_VARIABLE_TYPES = "variable types";
	private static final String SOLUTION_DATASET_ATTR_VARIABLE_TYPE = "variable type";
	private static final String SOLUTION_DATASET_ATTR_MEAN = "mean";
	private static final String SOLUTION_DATASET_ATTR_SUM_VOLFRAC = "sum of volume fraction";
	private static final String SOLUTION_DATASET_ATTR_MAX_ERROR = "max error";
	private static final String SOLUTION_DATASET_ATTR_RELATIVE_L2ERROR = "relative L2 error";

	public static ChomboMesh readMeshFile(File chomboMeshFile) throws Exception {
//		if (chomboMesh != null)
//		{
//			return;
//		}
		ChomboMesh chomboMesh = new ChomboMesh();
//		File mfile = new File(userDirectory, getMeshFileName());
		if(H5.H5open() < 0){
			throw new Exception("H5.H5open() failed");
		}
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		if(fileFormat == null){
			throw new Exception("FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5) failed, returned null.");
		}
		FileFormat meshFile = fileFormat.createInstance(chomboMeshFile.getAbsolutePath(), FileFormat.READ);
		meshFile.open();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)meshFile.getRootNode();
		Group rootGroup = (Group)rootNode.getUserObject();
		Group meshGroup = (Group)rootGroup.getMemberList().get(0);
		List<Attribute> meshAttrList = meshGroup.getMetadata();
		
		for (Attribute attr : meshAttrList)
		{
			String attrName = attr.getName();
			Object value = attr.getValue();
			if (attrName.equals(MESH_ATTR_DIMENSION))
			{
				chomboMesh.dimension = ((int[])value)[0];
			}
			else
			{
				String[] valueStrArray = (String[])value;
				String value0 = valueStrArray[0];
				StringTokenizer st = new StringTokenizer(value0, "{,} ");
				List<Double> valueList = new ArrayList<Double>();
				while (st.hasMoreTokens())
				{
					String token = st.nextToken();
					valueList.add(Double.parseDouble(token));
				}
				if (attrName.equals(MESH_ATTR_DX))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						chomboMesh.dx[i] = valueList.get(i);
					}
				}
				else if (attrName.equals(MESH_ATTR_EXTENT))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						chomboMesh.extent[i] = valueList.get(i);
					}
				}
				else if (attrName.equals(MESH_ATTR_NX))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						chomboMesh.nx[i] = valueList.get(i).intValue();
					}
				}
				else if (attrName.equals(MESH_ATTR_ORIGIN))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						chomboMesh.origin[i] = valueList.get(i);
					}
				}
			}
		}
		
		List<HObject> memberList = meshGroup.getMemberList();
		for (HObject member : memberList)
		{
			if (member instanceof Dataset)
			{
				Dataset dataset = (Dataset)member;
				Vector vectValues = (Vector)dataset.read();
				String name = dataset.getName();
				if (name.equals(BOXES_DATASET))
				{
					// not needed right now
				}
				else if (name.equals(METRICS_DATASET))
				{
					H5CompoundDS compoundDataSet = (H5CompoundDS) dataset;
					chomboMesh.metricsColumnNames = compoundDataSet.getMemberNames();
					int c = -1;
					int[] index = (int[]) vectValues.get(++ c);
					int[] i = (int[]) vectValues.get(++ c);
					int[] j = (int[]) vectValues.get(++ c);
					int[] k = null;
					if (chomboMesh.dimension == 3)
					{
						k = (int[]) vectValues.get(++ c);
					}
					double[] x = (double[])vectValues.get(++ c);
					double[] y = (double[])vectValues.get(++ c);
					double[] z = null;
					if (chomboMesh.dimension == 3)
					{
						z = (double[])vectValues.get(++ c);
					}
					double[] normalx = (double[])vectValues.get(++ c);
					double[] normaly = (double[])vectValues.get(++ c);
					double[] normalz = null;
					if (chomboMesh.dimension == 3)
					{
						normalz = (double[])vectValues.get(++ c);
					}
					double[] volFrac = (double[])vectValues.get(++ c);
					double[] areaFrac = (double[])vectValues.get(++ c);
					for (int n = 0; n < index.length; ++ n)
					{
						ChomboMeshMetricsEntry entry = new ChomboMeshMetricsEntry(index[n], i[n], j[n], k == null ? 0 : k[n]
								, x[n], y[n], z == null ? 0 : z[n]
								, normalx[n], normaly[n], normalz == null ? 0 : normalz[n],
										volFrac[n], areaFrac[n]);
						chomboMesh.metrics.add(entry);
					}
				}
				else if (name.equals(SURFACE_DATASET))
				{
					// not needed right now
				}
				else if (name.equals(SLICE_VIEW_DATASET))
				{
					// not needed right now
				}
			}
		}
		return chomboMesh;
	}

	private String getLogFileName()
	{
		return vcDataId.getID() + LOG_EXT;
	}
	private String getMeshFileName()
	{
		return vcDataId.getID() + MESH_HDF5_EXT;
	}
	
	private File findLogFile() {
		if (logFile == null)
		{
			logFile = new File(userDirectory, getLogFileName());
			VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.getLogFile() <<ENTER>> calling logile.exists()");
			if (logFile.exists())
			{
				VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.getLogFile() <<EXIT>> file found");
			}
			else
			{
				logFile = null;
				VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.getLogFile() <<EXIT>> file found");
			}
			
		}
		return logFile;
	}

	/**
	 * This method was created in VisualAge.
	 * @throws IOException 
	 */
	private synchronized void readLogFile() throws DataAccessException, IOException {
		VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.readLog() <<ENTER>>");
		if (logFile == null){
			VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.readLog() log file not found <<EXIT-Exception>>");
			throw new DataAccessException("log file not found for " + vcDataId);
		}
		VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.readLog() logFile exists");
		long length = logFile.length();
		long lastModified = logFile.lastModified();
		if (lastModified == logFileLastModified && logFileLength == length) {
			VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.readLog() hasn't been modified ... <<EXIT>>");
			return;
		}
		
		
		logFileLastModified = lastModified;
		logFileLength = length;
		logfileEntryList.clear();

		//
		// read log file and check whether ODE or PDE data
		//
		String logfileContent = FileUtils.readFileToString(logFile);
		if (logfileContent.length() != logFileLength){
			System.out.println("SimulationDataSpatialHdf5.readLog(), read "+logfileContent.length()+" of "+logFileLength+" bytes of log file");
		}
		
		StringTokenizer st = new StringTokenizer(logfileContent);
		// so parse into 'dataFilenames' and 'dataTimes' arrays
		if (st.countTokens() % 4 != 0) {
			throw new DataAccessException("SimulationDataSpatialHdf5.readLog(), tokens in each line should be factor of 4");
		}

		while (st.hasMoreTokens()){
			int iteration = Integer.parseInt(st.nextToken());
			String simFileName = st.nextToken();
			String zipFileName = st.nextToken();
			double time = Double.parseDouble(st.nextToken());
			logfileEntryList.add(new SimLogFileEntry(iteration, simFileName, zipFileName, time));
		}
		VCMongoMessage.sendTrace("SimulationDataSpatialHdf5.readLog() <<EXIT>>");
	}

	public double[] getDataTimes() {
		double[] times = new double[logfileEntryList.size()];
		int cnt = -1;
		for (SimLogFileEntry entry : logfileEntryList)
		{
			times[++ cnt] = entry.time;
		}
		return times;
	}
	
	public List<DataSetIdentifier> getDataSetIdentifiers()
	{
		return dataSetIdentifierList;
	}
	
	public ChomboMesh getChomboMesh()
	{
		return chomboMesh;
	}
	
	public SimDataSet retrieveSimDataSet(double time, String varName) throws Exception 
	{
		File tempFile = getTempSimHdf5File(time);
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		FileFormat solFile = fileFormat.createInstance(tempFile.getAbsolutePath(), FileFormat.READ);
		solFile.open();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)solFile.getRootNode();
		Group rootGroup = (Group)rootNode.getUserObject();
		Group solGroup = (Group)rootGroup.getMemberList().get(0);
		
		SimDataSet simDataSet = new SimDataSet();
		List<HObject> memberList = solGroup.getMemberList();
		for (HObject member : memberList)
		{
			if (!(member instanceof Dataset))
			{
				continue;
			}
			Dataset dataset = (Dataset)member;
			String dsname = dataset.getName();
			if (!dsname.equals(varName))
			{
				continue;
			}
			simDataSet.solValues = (double[]) dataset.read();
			List<Attribute> attrList = dataset.getMetadata();
			for (Attribute attr : attrList)
			{
				String attrName = attr.getName();
				Object val = attr.getValue();
				double dval = 0;
				if (val  instanceof double[])
				{
					dval = ((double[]) val)[0];
				}
				if (attrName.equals(SOLUTION_DATASET_ATTR_MAX_ERROR))
				{
					simDataSet.maxError = dval;
				}
				else if (attrName.equals(SOLUTION_DATASET_ATTR_MEAN))
				{
					simDataSet.mean = dval;
				}
				else if (attrName.equals(SOLUTION_DATASET_ATTR_RELATIVE_L2ERROR))
				{
					simDataSet.l2Error = dval;
				}
				else if (attrName.equals(SOLUTION_DATASET_ATTR_SUM_VOLFRAC))
				{
					simDataSet.sumVolFrac = dval;
				}
			}
			break;
		}
		return simDataSet;
	}
	
	private void retriveVariableNames() throws Exception {
		// read variables only when I have never read the file since variables don't change
		if (dataSetIdentifierList.size() > 0)
		{
			return;
		}
			
		File tempFile = getTempSimHdf5File(0.0);
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		FileFormat meshFile = fileFormat.createInstance(tempFile.getAbsolutePath(), FileFormat.READ);
		meshFile.open();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)meshFile.getRootNode();
		Group rootGroup = (Group)rootNode.getUserObject();
		Group solGroup = (Group)rootGroup.getMemberList().get(0);
		List<Attribute> solAttrList = solGroup.getMetadata();
		String[] varNames = null;
		int[] varTypes = null;
		
		for (Attribute attr : solAttrList)
		{
			String attrName = attr.getName();
			Object value = attr.getValue();
			if (attrName.equals(SOLUTION_ATTR_VARIABLES))
			{
				varNames = (String[])value;
			}
			else if (attrName.equals(SOLUTION_ATTR_VARIABLE_TYPES))
			{
				varTypes = (int[])value;
			}
		}
		for (int i = 0; i < varNames.length; i++)
		{
			VariableType varType = VariableType.getVariableTypeFromInteger(varTypes[i]);
			Domain domain = Variable.getDomainFromCombinedIdentifier(varNames[i]);
			String varName = Variable.getNameFromCombinedIdentifier(varNames[i]);
			dataSetIdentifierList.add(new DataSetIdentifier(varName,varType,domain));
		}
	}
	
	public File getTempSimHdf5File(double time) throws IOException
	{
		SimLogFileEntry logEntry = null;
		for (SimLogFileEntry entry : logfileEntryList)
		{
			if (entry.time == time)
			{
				logEntry = entry;
				break;
			}
		}
		if (logEntry == null)
		{
			throw new IOException("no data found for time " + time);
		}
		File hdf5ZipFile = new File(logEntry.hdf5ZipFileName);
		hdf5ZipFile = new File(userDirectory, hdf5ZipFile.getName());
		
		ZipFile	hdf5ZipZipFile = null;
		File tempFile = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		try
		{
			hdf5ZipZipFile = DataSet.openZipFile(hdf5ZipFile);
			ZipEntry simEntry = hdf5ZipZipFile.getEntry(logEntry.simHdf5FileName);
			
			// read data from zip file
			bis = new BufferedInputStream(hdf5ZipZipFile.getInputStream((ZipArchiveEntry) simEntry));
			tempFile = File.createTempFile("tmpHdf5", HDF5_EXT);
			tempFile.deleteOnExit();
			fos = new FileOutputStream(tempFile);
			
			byte byteArray[] = new byte[8192];
			while (true) 
			{
				int numRead = bis.read(byteArray);
				if (numRead == -1) {
					break;
				}
				if (numRead > 0) {
					fos.write(byteArray, 0, numRead);
				}
			}
		}
		finally
		{
			if (bis != null)
			{
				bis.close();
			}
			if (fos != null)
			{
				fos.close();
			}			
		}
		return tempFile;
	}
}

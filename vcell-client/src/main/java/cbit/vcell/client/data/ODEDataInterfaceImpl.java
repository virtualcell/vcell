package cbit.vcell.client.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.VCDataIdentifier;

import com.google.common.io.Files;

import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DataSymbolMetadata;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.solver.SimulationModelInfo.ModelCategoryType;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5ScalarDS;

class ODEDataInterfaceImpl implements ODEDataInterface {
	
	private transient java.beans.PropertyChangeSupport propertyChange = new PropertyChangeSupport(this);
	private ModelCategoryType[] selectedFilters = null;
	private final SimulationModelInfo simulationModelInfo;
	private ODESolverResultSet odeSolverResultSet;
	
	ODEDataInterfaceImpl(VCDataIdentifier vcdi, ODESolverResultSet odeSolverResultSet, SimulationModelInfo simulationModelInfo){
		if(odeSolverResultSet instanceof ODESimData) {
			this.odeSolverResultSet = new ODESimData(vcdi, odeSolverResultSet);
		} else {
			this.odeSolverResultSet = new ODESolverResultSet(odeSolverResultSet);
		}
		this.simulationModelInfo = simulationModelInfo;
	}
	
	@Override
	public ModelCategoryType[] getSupportedFilterCategories() {
		DataSymbolMetadataResolver resolver = (simulationModelInfo!=null)?simulationModelInfo.getDataSymbolMetadataResolver():null;
		if(resolver != null){
			return resolver.getUniqueFilterCategories();
		}
		return new ModelCategoryType[0];
	}

	@Override
	public void selectCategory(ModelCategoryType[] filterCategories) {
		this.selectedFilters = filterCategories;
		propertyChange.firePropertyChange("columnDescriptions", null, getFilteredColumnDescriptions());
	}

	@Override
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(listener);
	}

	@Override
	public int getColumnDescriptionsCount() {
		return getFilteredColumnDescriptions().length;
	}

	@Override
	public ColumnDescription getColumnDescription(String columnName) throws ObjectNotFoundException{
		ColumnDescription[] columnDescriptions = getOdeSolverResultSet().getColumnDescriptions();
		for (int j = 0; j < columnDescriptions.length; j++) {
			if(columnDescriptions[j].getName().equals(columnName)){
				return columnDescriptions[j];
			}
		}
		throw new ObjectNotFoundException("Couldn't find DolumnDescription="+columnName);
	}

	@Override
	public FunctionColumnDescription[] getFunctionColumnDescriptions() {
		ArrayList<FunctionColumnDescription> functionColumnDescriptions = new ArrayList<FunctionColumnDescription>();
		ColumnDescription[] filteredColumnDescriptions = getFilteredColumnDescriptions();
		for (int i = 0; i < filteredColumnDescriptions.length; i++) {
			if(filteredColumnDescriptions[i] instanceof FunctionColumnDescription){
				functionColumnDescriptions.add((FunctionColumnDescription)filteredColumnDescriptions[i]);
			}
		}
		return functionColumnDescriptions.toArray(new FunctionColumnDescription[0]);
	}

	@Override
	public double[] extractColumn(String columnName) throws ExpressionException,ObjectNotFoundException{
		for (int i = 0; i < getOdeSolverResultSet().getColumnDescriptionsCount(); i++) {
			if(getOdeSolverResultSet().getColumnDescriptions(i).getName().equals(columnName)){
				return getOdeSolverResultSet().extractColumn(i);
			}
		}
		throw new ObjectNotFoundException("Couldn't find column "+columnName);
	}
	
	@Override
	public LinkedHashMap<String, Integer> parseHDF5File() {
		FileFormat hdf5FileFormat = null;
		File to = null;
		LinkedHashMap<String, Integer> valueToIndexMap = new LinkedHashMap<>();
		try {
			ODESolverResultSet osrs = getOdeSolverResultSet();
			if(osrs instanceof ODESimData) {
				byte[] hdf5FileBytes = ((ODESimData)getOdeSolverResultSet()).getHdf5FileBytes();
				if(hdf5FileBytes != null) {
					to = File.createTempFile("odeStats_"+simulationModelInfo.getSimulationName(), ".hdf5");
					Files.write(hdf5FileBytes, to);
					FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
					if (fileFormat == null){
						throw new Exception("Cannot find HDF5 FileFormat.");
					}
					// open the file with read-only access	
					hdf5FileFormat = fileFormat.createInstance(to.getAbsolutePath(), FileFormat.READ);
					// open the file and retrieve the file structure
					hdf5FileFormat.open();
					Group root = (Group)((javax.swing.tree.DefaultMutableTreeNode)hdf5FileFormat.getRootNode()).getUserObject();
					List<HObject> postProcessMembers = ((Group)root).getMemberList();

					HObject varNames = null;
					for(HObject nextHObject : postProcessMembers) {
						if(nextHObject.getName().equals("VarNames")) {
							varNames = nextHObject;
							break;
						// SimTimes
						// StatMax
						// StatMean
						// StatMin
						// StatStdDev
						// VarNames
						}
					}
					H5ScalarDS h5ScalarDS = (H5ScalarDS)varNames;
					h5ScalarDS.init();
					try {
						long[] dims = h5ScalarDS.getDims();
						System.out.println("---"+varNames.getName()+" "+varNames.getClass().getName()+" Dimensions="+Arrays.toString(dims));
						Object obj = h5ScalarDS.read();
						String[] values = (String[])obj;
						for(int i=0; i<values.length; i++) {
							String value = values[i];
							valueToIndexMap.put(value, i);
						}
						
					} catch(Exception e) {
					}
				}
			}
		} catch (Exception e) {
		} finally {
			if(hdf5FileFormat != null) {try{hdf5FileFormat.close();}catch(Exception e2) {e2.printStackTrace();}}
			if(to != null){try{to.delete();}catch(Exception e2) {e2.printStackTrace();}}
		}
		return valueToIndexMap;
	}
	
	@Override
	public double[] extractColumn(String columnName, PlotType plotType) throws ExpressionException,ObjectNotFoundException {
		FileFormat hdf5FileFormat = null;
		File to = null;
		try {
			ODESolverResultSet osrs = getOdeSolverResultSet();
			if(osrs instanceof ODESimData) {
				byte[] hdf5FileBytes = ((ODESimData)getOdeSolverResultSet()).getHdf5FileBytes();
				if(hdf5FileBytes != null) {
					to = File.createTempFile("odeStats_"+simulationModelInfo.getSimulationName(), ".hdf5");
					Files.write(hdf5FileBytes, to);
					FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
					if (fileFormat == null){
						throw new Exception("Cannot find HDF5 FileFormat.");
					}
					// open the file with read-only access	
					hdf5FileFormat = fileFormat.createInstance(to.getAbsolutePath(), FileFormat.READ);
					// open the file and retrieve the file structure
					hdf5FileFormat.open();
					Group root = (Group)((javax.swing.tree.DefaultMutableTreeNode)hdf5FileFormat.getRootNode()).getUserObject();
					List<HObject> postProcessMembers = ((Group)root).getMemberList();
					for(HObject nextHObject : postProcessMembers) {
						System.out.println(nextHObject.getName()+"   "+nextHObject.getClass().getName());
						H5ScalarDS h5ScalarDS = (H5ScalarDS)nextHObject;
						h5ScalarDS.init();
						try {
							long[] dims = h5ScalarDS.getDims();
							System.out.println("---"+nextHObject.getName()+" "+nextHObject.getClass().getName()+" Dimensions="+Arrays.toString(dims));
							Object obj = h5ScalarDS.read();
							if(dims.length == 2) {
								double[] columns = new double[(int)dims[1]];
								for(int row=0;row<dims[0];row++) {
									System.arraycopy(obj, row*columns.length, columns, 0, columns.length);
									System.out.println(Arrays.toString(columns));
								}
								return null;
//								return columns;
							} else {
								return null;
							}
						} catch(Exception e) {
							return null;
						}
					}
				}
			}
		} catch (Exception e) {
		} finally {
			if(hdf5FileFormat != null) {try{hdf5FileFormat.close();}catch(Exception e2) {e2.printStackTrace();}}
			if(to != null){try{to.delete();}catch(Exception e2) {e2.printStackTrace();}}
		}
		return null;
	}

	@Override
	public boolean isMultiTrialData() {
		return getOdeSolverResultSet().isMultiTrialData();
	}

	@Override
	public int getRowCount() {
		return getOdeSolverResultSet().getRowCount();
	}

	@Override
	public ColumnDescription[] getAllColumnDescriptions() {
		return getOdeSolverResultSet().getColumnDescriptions();
	}
	
	@Override
	public ColumnDescription[] getFilteredColumnDescriptions() {
		if (selectedFilters == null || simulationModelInfo == null){
			return getOdeSolverResultSet().getColumnDescriptions();
		}else{
			ArrayList<ColumnDescription> selectedColumnDescriptions = new ArrayList<ColumnDescription>();
			for (int i = 0; i < getOdeSolverResultSet().getColumnDescriptions().length; i++) {
				String name = getOdeSolverResultSet().getColumnDescriptions()[i].getName();
				DataSymbolMetadataResolver dataSymbolMetadataResolver = simulationModelInfo.getDataSymbolMetadataResolver();
				DataSymbolMetadata dataSymbolMetadata = dataSymbolMetadataResolver.getDataSymbolMetadata(name);
				ModelCategoryType selectedFilterCategory = null;
				if (dataSymbolMetadata!=null){
					selectedFilterCategory = dataSymbolMetadata.filterCategory;
				}
				for (int j = 0; j < selectedFilters.length; j++) {
					if(selectedFilters[j].equals(selectedFilterCategory)){
						selectedColumnDescriptions.add(getOdeSolverResultSet().getColumnDescriptions()[i]);
						break;
					}
				}
			}
			return selectedColumnDescriptions.toArray(new ColumnDescription[0]);
		}
	}

	private ODESolverResultSet getOdeSolverResultSet() {
		return odeSolverResultSet;
	}

	@Override
	public DataSymbolMetadataResolver getDataSymbolMetadataResolver() {
		return (simulationModelInfo!=null?simulationModelInfo.getDataSymbolMetadataResolver():null);
	}

}

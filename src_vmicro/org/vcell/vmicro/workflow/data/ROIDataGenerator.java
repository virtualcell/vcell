package org.vcell.vmicro.workflow.data;

import java.io.File;
import java.util.StringTokenizer;

import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;
import org.vcell.util.NullSessionLog;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.math.DataGenerator;
import cbit.vcell.math.FieldFunctionDefinition;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.DataSet;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solvers.CartesianMesh;

@SuppressWarnings("serial")
public class ROIDataGenerator extends DataGenerator {
	public static final String ROI_GENERATOR_BEGIN = "ROI_DATA_GENERATOR_BEGIN";
	public static final String ROI_GENERATOR_END = "ROI_DATA_GENERATOR_END";
	private String name;
	private int[] volumePoints;
	private int[] membranePoints;
	private int numImgRegions;
	private int zSlice;
	private KeyValue fieldDataKey;
	private FieldFunctionArguments fieldFuncArguments; 
	private boolean bStoreEnabled;
	
	public ROIDataGenerator(String name, int[] volumePoints, int[] membranePoints, int numRegions, int zSlice, KeyValue fieldDataKey, FieldFunctionArguments fd, boolean bStoreEnabled) {
		super(name, null);
		this.name = name;
		this.volumePoints = volumePoints;
		this.membranePoints = membranePoints;
		this.numImgRegions = numRegions;
		this.zSlice = zSlice;
		this.fieldDataKey = fieldDataKey;
		this.fieldFuncArguments = fd; 
		this.bStoreEnabled = bStoreEnabled;
	}

	
	public String getROIDataGeneratorDescription(File userDirectory, SimulationJob simulationJob) throws Exception { //DataAccessException, FileNotFoundException, MathException, IOException, DivideByZeroException, ExpressionException 
		Simulation simulation = simulationJob.getSimulation();
		
		StringBuffer sb = new StringBuffer();
		sb.append(ROI_GENERATOR_BEGIN + " " + name + "\n");
		sb.append("VolumePoints " + volumePoints.length + "\n");
		for (int i = 0; i < volumePoints.length; i++) {
			sb.append(volumePoints[i] + " ");
			if ((i+1) % 20 == 0) {
				sb.append("\n");
			}
		}
		sb.append("\n");
		if (membranePoints != null && membranePoints.length > 0) {	
			sb.append("MembranePoints " + membranePoints.length + "\n");
			for (int i = 0; i < membranePoints.length; i++) {
				sb.append(membranePoints[i] + " ");
				if ((i+1) % 20 == 0) {
					sb.append("\n");
				}
			}
			sb.append("\n");
		}
		sb.append("SampleImage " + numImgRegions + " " + zSlice + " " + fieldDataKey + " " + fieldFuncArguments.infix()+"\n");
		sb.append("StoreEnabled " + bStoreEnabled + "\n");
		//sample image field data file
		FieldDataIdentifierSpec fdis = getSampleImageFieldData(simulation.getVersion().getOwner());
		
		if (fdis == null) {
			throw new DataAccessException("Can't find sample image in ROI data generator.");
		}
		String secondarySimDataDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirProperty, null);	
		DataSetControllerImpl dsci = new DataSetControllerImpl(new NullSessionLog(),null,userDirectory.getParentFile(),secondarySimDataDir == null ? null : new File(secondarySimDataDir));
		CartesianMesh origMesh = dsci.getMesh(fdis.getExternalDataIdentifier());
		SimDataBlock simDataBlock = dsci.getSimDataBlock(null,fdis.getExternalDataIdentifier(), fdis.getFieldFuncArgs().getVariableName(), fdis.getFieldFuncArgs().getTime().evaluateConstant());
		VariableType varType = fdis.getFieldFuncArgs().getVariableType();
		VariableType dataVarType = simDataBlock.getVariableType();
		if (!varType.equals(VariableType.UNKNOWN) && !varType.equals(dataVarType)) {
			throw new IllegalArgumentException("field function variable type (" + varType.getTypeName() + ") doesn't match real variable type (" + dataVarType.getTypeName() + ")");
		}
		double[] origData = simDataBlock.getData();
		String filename = SimulationJob.createSimulationJobID(Simulation.createSimulationID(simulation.getKey()), simulationJob.getJobIndex()) + SimulationData.getDefaultFieldDataFileNameForSimulation(fdis.getFieldFuncArgs());
		File fdatFile = new File(userDirectory, filename);
		DataSet.writeNew(fdatFile,
				new String[] {fdis.getFieldFuncArgs().getVariableName()},
				new VariableType[]{simDataBlock.getVariableType()},
				new ISize(origMesh.getSizeX(),origMesh.getSizeY(),origMesh.getSizeZ()),
				new double[][]{origData});
		
		sb.append("SampleImageFile " + fdis.getFieldFuncArgs().getVariableName() + " " + fdis.getFieldFuncArgs().getTime().infix() + " " + fdatFile + "\n");
		sb.append(ROI_GENERATOR_END);
		return sb.toString();
	}
	
	public FieldDataIdentifierSpec getSampleImageFieldData(User user) {
			String key = fieldDataKey.toString(); // key
			String fieldInput = fieldFuncArguments.infix();
			StringTokenizer st = null;
			int index = fieldInput.indexOf(FieldFunctionDefinition.FUNCTION_name);
			if (index >= 0) {
				st = new StringTokenizer(fieldInput.substring(index), "\n");
				if (st.hasMoreTokens()) {
					String fieldFunction = st.nextToken();
					try {
						Expression exp = new Expression(fieldFunction);					
						FieldFunctionArguments[] ffa = FieldUtilities.getFieldFunctionArguments(exp);
						return new FieldDataIdentifierSpec(ffa[0], new ExternalDataIdentifier(KeyValue.fromString(key), user, ffa[0].getFieldName()));
					} catch (ExpressionException e) {
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());
					} catch (Exception e){
						e.printStackTrace();
						throw new RuntimeException("Failed to load data processing script.");
					}
				}
			}
		return null;
	}


	@Override
	public boolean compareEqual(Matchable object, boolean bIgnoreMissingDomains) {
		if (!(object instanceof ROIDataGenerator)){
			return false;
		}
		
		ROIDataGenerator rdg = (ROIDataGenerator)object;
		if (!name.equals(rdg.name)) {
			return false;
		}
		if (!Compare.isEqualOrNull(volumePoints, rdg.volumePoints)) {
			return false;
		}
		if (!Compare.isEqualOrNull(membranePoints, rdg.membranePoints)) {
			return false;
		}
		if (numImgRegions != rdg.numImgRegions){
			return false;
		}
		if(zSlice != rdg.zSlice){
			return false;
		}
		if (!Compare.isEqualOrNull(fieldDataKey, rdg.fieldDataKey)) {
			return false;
		}
		if (!fieldFuncArguments.equals(rdg.fieldFuncArguments)) {
			return false;
		}
		if(bStoreEnabled != rdg.bStoreEnabled){
			return false;
		}
		return true;
	}

	@Override
	public String getVCML() throws MathException {
		// TODO Auto-generated method stub
		return null;
	}
	
}

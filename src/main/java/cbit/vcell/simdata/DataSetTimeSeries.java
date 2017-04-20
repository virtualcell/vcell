package cbit.vcell.simdata;

import java.io.Serializable;
import java.util.ArrayList;

import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;

public class DataSetTimeSeries implements Serializable {

	public interface DataSetPostProcessData{
		public double[] getTimes();
		public String[] getVariableStatNames();
		public double[] getVariableStatValues(String varName);
	}
	
	public final VCDataIdentifier vcDataIdentifier;
	public final VarData[] varDatas;
	
	public class VarData implements Serializable {
		public final String name;
		public final double[] values;

		public VarData(String name, double[] values) {
			super();
			this.name = name;
			this.values = values;
		}
	}
	
	public DataSetTimeSeries(VCDataIdentifier vcdataID, ODEDataBlock odeDataBlock) {
		this.vcDataIdentifier = vcdataID;
		this.varDatas = getVarDatas(odeDataBlock);
	}

	public DataSetTimeSeries(VCDataIdentifier vcdataID, DataSetPostProcessData dataProcessingOutput) {
		this.vcDataIdentifier = vcdataID;
		this.varDatas = getVarDatas(dataProcessingOutput);
	}
	
	private VarData[] getVarDatas(ODEDataBlock odeDataBlock){
		ArrayList<VarData> valValuesArray = new ArrayList<VarData>();
		ODESimData odeSimData = odeDataBlock.getODESimData();
		int rowCount = odeSimData.getRowCount();
		for (ColumnDescription column : odeSimData.getColumnDescriptions()){
			VarData values = new VarData(column.getName(),new double[rowCount]);
			valValuesArray.add(values);
		}
		for (int i=0; i<rowCount; i++){
			double[] rowData = odeSimData.getRow(i);
			for (int j=0; j<rowData.length; j++){
				valValuesArray.get(j).values[i] = rowData[j];
			}
		}
		return valValuesArray.toArray(new VarData[0]);
	}

	private VarData[] getVarDatas(DataSetPostProcessData dataProcessingOutput){
		ArrayList<VarData> varValuesArray = new ArrayList<VarData>();
		//
		// add time as a variable
		//
		VarData timeValues = new VarData("t",dataProcessingOutput.getTimes());
		varValuesArray.add(timeValues);
		//
		// add the variable data
		//
		String[] varNames = dataProcessingOutput.getVariableStatNames();
		for (String varName : varNames) {
			VarData values = new VarData(varName,dataProcessingOutput.getVariableStatValues(varName));
			varValuesArray.add(values);
		}
		return varValuesArray.toArray(new VarData[0]);
	}

	public String[] getVarNames() {
		String[] names = new String[varDatas.length];
		for (int i=0;i<varDatas.length;i++){
			names[i] = varDatas[i].name;
		}
		return names;
	}

}

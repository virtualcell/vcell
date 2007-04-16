package cbit.vcell.field;

import cbit.vcell.math.ParameterVariable;

public class FieldDataParameterVariable extends ParameterVariable {

	private double[] resampledFileData;
	
	public FieldDataParameterVariable(String name,double[] argResampledFieldData) {
		super(name);
		resampledFileData = argResampledFieldData;
	}

	@Override
	public String getVCML() {
		return "FieldDataParameter  "+getName();
	}

	@Override
	public String toString() {
		return "FieldDataParameter("+hashCode()+") <"+getName()+">";
	}

	public double[] getResampledFieldData(){
		return resampledFileData;
	}
}

package cbit.vcell.opt;


public class CopasiOptimizationParameter implements java.io.Serializable{

	public enum CopasiOptimizationParameterType {
		Number_of_Generations("Number of Generations", CopasiOptSettings.DataType_int),
		Number_of_Iterations("Number of Iterations", CopasiOptSettings.DataType_int),
		Population_Size("Population Size", CopasiOptSettings.DataType_int),
		Random_Number_Generator("Random Number Generator", CopasiOptSettings.DataType_int),
		Seed("Seed", CopasiOptSettings.DataType_int),
		IterationLimit("Iteration Limit", CopasiOptSettings.DataType_int),
		Tolerance("Tolerance", CopasiOptSettings.DataType_float),
		Rho("Rho", CopasiOptSettings.DataType_float),
		Scale("Scale", CopasiOptSettings.DataType_float),
		Swarm_Size("Swarm Size", CopasiOptSettings.DataType_int),
		Std_Deviation("Std Deviation", CopasiOptSettings.DataType_float),
		Start_Temperature("Start Temperature", CopasiOptSettings.DataType_float),
		Cooling_Factor("Cooling Factor", CopasiOptSettings.DataType_float),
		Pf("Pf", CopasiOptSettings.DataType_float);
				
		private String displayName;
		private String dataType;
		CopasiOptimizationParameterType(String displayName, String dataType) {
			this.displayName = displayName;
			this.dataType = dataType;
		}
		public final String getDisplayName() {
			return displayName;
		}
		public final String getDataType(){
			return dataType;
		}
	}
	
	private CopasiOptimizationParameterType type;
	private double value;
	
	public CopasiOptimizationParameter(CopasiOptimizationParameterType type, double dv) {
		this.type = type;
		value = dv;
	}

	public CopasiOptimizationParameter(CopasiOptimizationParameter anotherParameter) {
		this.type = anotherParameter.type;
		value = anotherParameter.value;
	}
	
	public final double getValue() {
		return value;
	}

	public final void setValue(double value) {
		this.value = value;
	}

	public final CopasiOptimizationParameterType getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CopasiOptimizationParameter) {
			CopasiOptimizationParameter cop = (CopasiOptimizationParameter)obj;
			if (cop.type != type) {
				return false;
			}
			if (cop.value != value) {
				return false;
			}
			return true;
		}
		return false;
	}
}
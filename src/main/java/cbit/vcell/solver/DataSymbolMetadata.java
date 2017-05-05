package cbit.vcell.solver;

import cbit.vcell.solver.SimulationModelInfo.ModelCategoryType;
import cbit.vcell.units.VCUnitDefinition;

public class DataSymbolMetadata {
	public final ModelCategoryType filterCategory;
	public final VCUnitDefinition unit;
	public final String tooltipString;
	public DataSymbolMetadata(ModelCategoryType filterCategory, VCUnitDefinition unit, String tooltipString){
		this.filterCategory = filterCategory;
		this.unit = unit;
		this.tooltipString = tooltipString;
	}
	@Override
	public String toString(){
		return "DataSymbolMetadata("+filterCategory+","+unit+")";
	}
}
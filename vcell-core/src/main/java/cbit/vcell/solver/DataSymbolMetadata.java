package cbit.vcell.solver;

import cbit.vcell.solver.SimulationModelInfo.ModelCategoryType;
import cbit.vcell.units.VCUnitDefinition;

public class DataSymbolMetadata {
	public final ModelCategoryType filterCategory;
	public final VCUnitDefinition unit;
	public final String tooltipString;
	public final String displayName;
	
	public DataSymbolMetadata(ModelCategoryType filterCategory, VCUnitDefinition unit, String tooltipString){
		this(filterCategory, unit, tooltipString, null);
	}

	public DataSymbolMetadata(ModelCategoryType filterCategory, VCUnitDefinition unit, String tooltipString, String displayName){
		this.filterCategory = filterCategory;
		this.unit = unit;
		this.tooltipString = tooltipString;
		this.displayName = displayName;
	}
	@Override
	public String toString(){
		return "DataSymbolMetadata("+filterCategory+","+unit+")";
	}
}
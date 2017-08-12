package cbit.vcell.mapping;

import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.parser.SymbolTableEntry;


public interface SimContextTransformer {
	
	public class ModelEntityMapping {
		public final SymbolTableEntry origModelObj;
		public final SymbolTableEntry newModelObj;
		
		public ModelEntityMapping(SymbolTableEntry origModelObj, SymbolTableEntry newModelObject){
			this.origModelObj = origModelObj;
			this.newModelObj = newModelObject;
		}
	}
	
	public class SimContextTransformation {
		public final SimulationContext originalSimContext;
		public final SimulationContext transformedSimContext;
		public final ModelEntityMapping[] modelEntityMappings;
		
		public SimContextTransformation(SimulationContext originalSimContext, SimulationContext transformedSimContext, ModelEntityMapping[] modelEntityMappings){
			this.originalSimContext = originalSimContext;
			this.transformedSimContext = transformedSimContext;
			this.modelEntityMappings = modelEntityMappings;
		}
	}
	
	public SimContextTransformation transform(SimulationContext originalModel, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements);

}

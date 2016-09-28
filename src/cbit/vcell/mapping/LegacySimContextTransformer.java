package cbit.vcell.mapping;

import java.io.IOException;
import java.util.ArrayList;

import org.vcell.sbml.vcell.StructureSizeSolver;
import org.vcell.util.BeanUtils;
import org.vcell.util.ProgrammingException;

import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.model.SpeciesContext;

public class LegacySimContextTransformer implements SimContextTransformer {

	@Override
	public SimContextTransformation transform(SimulationContext originalSimContext, MathMappingCallback mathMappingCallback,	NetworkGenerationRequirements networkGenerationRequirements) {
		SimulationContext transformedSimContext;
		try {
			transformedSimContext = (SimulationContext)BeanUtils.cloneSerializable(originalSimContext);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("unexpected exception: "+e.getMessage());
		}
		try {
			StructureMapping structureMapping = transformedSimContext.getGeometryContext().getStructureMappings()[0];
			StructureSizeSolver.updateAbsoluteStructureSizes(transformedSimContext, structureMapping.getStructure(), 1.0, structureMapping.getSizeParameter().getUnitDefinition());
		} catch (Exception e) {
			throw new ProgrammingException("exception updating sizes",e);
		}

		transformedSimContext.getModel().refreshDependencies();
		transformedSimContext.refreshDependencies1(false);

		ArrayList<ModelEntityMapping> entityMappings = new ArrayList<ModelEntityMapping>();
		for(SpeciesContext sc : transformedSimContext.getModel().getSpeciesContexts()) {
			SpeciesContext origSpeciesContext = originalSimContext.getModel().getSpeciesContext(sc.getName());
			ModelEntityMapping em = new ModelEntityMapping(origSpeciesContext,sc);
			entityMappings.add(em);
		}
		
		ModelEntityMapping[] modelEntityMappings = entityMappings.toArray(new ModelEntityMapping[0]);
		return new SimContextTransformation(originalSimContext, transformedSimContext, modelEntityMappings);
	}

}

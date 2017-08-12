package cbit.vcell.mapping;

import cbit.vcell.model.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
public class SimulationContextTest {
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SimulationContext
 */
public static SimulationContext getExample(int dimension) throws Exception {
	//
	// use example model
	//
	cbit.vcell.model.Model model = getModelFromExample();

	cbit.vcell.geometry.Geometry geo = cbit.vcell.geometry.GeometryTest.getExample(dimension);

	SimulationContext simContext = new SimulationContext(model,geo);
	GeometryContext geoContext = simContext.getGeometryContext();
	ReactionContext reactContext = simContext.getReactionContext();

	if (dimension>0){
		Double charSize = simContext.getCharacteristicSize();
		simContext.setCharacteristicSize(new Double(charSize.doubleValue()/2.0));
	}
	//
	// manually map all features to subVolumes
	//
	if (geo.getDimension()>0){
		cbit.vcell.model.Structure structures[] = geoContext.getModel().getStructures();
		for (int i=0;i<structures.length;i++){
			cbit.vcell.model.Structure structure = structures[i];
			if (structure instanceof cbit.vcell.model.Feature){
				cbit.vcell.model.Feature feature = (cbit.vcell.model.Feature)structure;
				if (feature.getName().equals("extracellular") && geo.getDimension()>0){
					geoContext.assignStructure(feature,geo.getGeometrySpec().getSubVolume("extracellular"));
				}else{
					geoContext.assignStructure(feature,geo.getGeometrySpec().getSubVolume("cytosol"));
				}
			}
		}
	}
	//
	// alter some speciesContextSpecs
	//
	// add initial conditions
	//
	SpeciesContextSpec speciesContextSpecs[] = reactContext.getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec scs = speciesContextSpecs[i];
		scs.getInitialConditionParameter().setExpression(Expression.add(scs.getInitialConditionParameter().getExpression(),new Expression(1.0)));
		scs.getBoundaryXmParameter().setExpression(new Expression(5.5));
	}
	return simContext;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SimulationContext
 */
public static SimulationContext getExampleElectrical(int dimension) throws Exception {
	//
	// use example model
	//
	cbit.vcell.model.Model model = cbit.vcell.model.ModelTest.getExampleWithCurrent();


	cbit.vcell.geometry.Geometry geo = cbit.vcell.geometry.GeometryTest.getExample(dimension);

	SimulationContext simContext = new SimulationContext(model,geo);
	GeometryContext geoContext = simContext.getGeometryContext();
	ReactionContext reactContext = simContext.getReactionContext();

	if (dimension>0){
		Double charSize = simContext.getCharacteristicSize();
		simContext.setCharacteristicSize(new Double(charSize.doubleValue()/2.0));
	}
	//
	// manually map all features to subVolumes
	//
	if (geo.getDimension()>0){
		cbit.vcell.model.Structure structures[] = geoContext.getModel().getStructures();
		for (int i=0;i<structures.length;i++){
			cbit.vcell.model.Structure structure = structures[i];
			if (structure instanceof cbit.vcell.model.Feature){
				cbit.vcell.model.Feature feature = (cbit.vcell.model.Feature)structure;
				if (feature.getName().equals("extracellular") && geo.getDimension()>0){
					geoContext.assignStructure(feature,geo.getGeometrySpec().getSubVolume("extracellular"));
				}else{
					geoContext.assignStructure(feature,geo.getGeometrySpec().getSubVolume("cytosol"));
				}
			}
		}
	}
	MembraneMapping membraneMapping = (MembraneMapping)geoContext.getStructureMapping(model.getStructure("PM"));
	membraneMapping.getSpecificCapacitanceParameter().setExpression(new Expression(1.0));
	membraneMapping.getInitialVoltageParameter().setExpression(new Expression(-70.0));
	
	// Add Electrical Stimulus and Gnd Electrode

	cbit.vcell.mapping.Electrode gndelectrode = new cbit.vcell.mapping.Electrode((Feature)model.getStructure("extracellular"), new org.vcell.util.Coordinate(10.0, 10.0,10.0));
	simContext.setGroundElectrode(gndelectrode);
	
	cbit.vcell.mapping.Electrode newelectrode = new cbit.vcell.mapping.Electrode((Feature)model.getStructure("cytosol"), new org.vcell.util.Coordinate(0.0, 0.0,0.0));
	Expression exp = new Expression("0.1*(t>0.01 && t<0.05)");
	String stimulusName = "Electrode";
	cbit.vcell.mapping.VoltageClampStimulus voltstimulus = new cbit.vcell.mapping.VoltageClampStimulus(newelectrode, stimulusName, exp, simContext);
	cbit.vcell.mapping.CurrentDensityClampStimulus currentstimulus = new cbit.vcell.mapping.CurrentDensityClampStimulus(newelectrode, stimulusName, exp, simContext);

	// simContext.setElectricalStimuli(new cbit.vcell.mapping.ElectricalStimulus[] {currentstimulus});
	simContext.setElectricalStimuli(new cbit.vcell.mapping.ElectricalStimulus[] {voltstimulus});

	
	//
	// alter some speciesContextSpecs
	//
	// add initial conditions
	//
	SpeciesContextSpec speciesContextSpecs[] = reactContext.getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec scs = speciesContextSpecs[i];
		scs.getInitialConditionParameter().setExpression(Expression.add(scs.getInitialConditionParameter().getExpression(),new Expression(1.0)));
		scs.getBoundaryXmParameter().setExpression(new Expression(5.5));
	}
	return simContext;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 1:33:36 PM)
 * @return cbit.vcell.model.Model
 */
public static cbit.vcell.model.Model getModelFromExample() throws Exception {
	return cbit.vcell.model.ModelTest.getExample_Wagner();
}
}

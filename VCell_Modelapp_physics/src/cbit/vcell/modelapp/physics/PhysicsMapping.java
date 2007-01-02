package cbit.vcell.modelapp.physics;

import java.beans.PropertyVetoException;
import java.util.ArrayList;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;
import jscl.plugin.Variable;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionUtilities;
import org.vcell.expression.IExpression;
import org.vcell.physics.component.Capacitor;
import org.vcell.physics.component.Connection;
import org.vcell.physics.component.Connector;
import org.vcell.physics.component.CurrentSource;
import org.vcell.physics.component.Location;
import org.vcell.physics.component.LumpedLocation;
import org.vcell.physics.component.ModelComponent;
import org.vcell.physics.component.OOModel;
import org.vcell.physics.component.OnePortElectricalDevice;
import org.vcell.physics.component.TwoPortElectricalComponent;
import org.vcell.physics.component.VoltageSource;

import cbit.util.TokenMangler;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.modelapp.FeatureMapping;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.StructureMapping;
import cbit.vcell.units.VCUnitDefinition;

public class PhysicsMapping {
	/**
	 * Insert the method's description here.
	 * Creation date: (1/12/2004 1:35:34 AM)
	 * @return ncbc_old.physics.component.PhysicalModel
	 */
	public static OOModel createFromSimulationContext(cbit.vcell.modelapp.SimulationContext simContext) throws ExpressionException, PropertyVetoException {
		OOModel physicalModel = new OOModel();

		//
		// add locations to physicalModel
		// (names of locations always start with the structure name followed by an underscore ... e.g. "cytosol_0_1")
		//
		addLocations(simContext,physicalModel);

		addElectricalDevices(simContext,physicalModel);

		PhysicsMapping.addChemicalDevices(simContext,physicalModel);

		//System.out.println("# locations is "+physicalModel.getLocations().length);
		System.out.println("# components is "+physicalModel.getModelComponents().length);
		System.out.println("# connections is "+physicalModel.getConnections().length);

		return physicalModel;
	}

	public static void addLocations(SimulationContext simContext, OOModel oOModel){
		//
		// add Devices for locations to the physical model
		//
		StructureMapping[] structureMappings = simContext.getGeometryContext().getStructureMappings();
		for (int i = 0; i < structureMappings.length; i++){
			int dim = 0;
			if (structureMappings[i].getStructure() instanceof Feature){
				dim = 3;
			}else if (structureMappings[i].getStructure() instanceof Membrane){
				dim = 2;
			}else{
				throw new RuntimeException("unexpected compartment type "+structureMappings[i].getStructure().getClass().getName());
			}
			LumpedLocation lumpedLocation = new LumpedLocation(structureMappings[i].getStructure().getName(),dim);
			oOModel.addModelComponent(lumpedLocation);
		}
		//
		// Connect Devices (adjacency)
		//
		for (int i = 0; i < structureMappings.length; i++){
			if (structureMappings[i].getStructure() instanceof Membrane){
				Membrane membrane = ((MembraneMapping)structureMappings[i]).getMembrane();
				Feature insideFeature = membrane.getInsideFeature();
				Feature outsideFeature = membrane.getOutsideFeature();
				LumpedLocation membraneLocation = (LumpedLocation)oOModel.getModelComponent(membrane.getName());
				LumpedLocation insideLocation = (LumpedLocation)oOModel.getModelComponent(insideFeature.getName());
				LumpedLocation outsideLocation = (LumpedLocation)oOModel.getModelComponent(outsideFeature.getName());
				membraneLocation.addAdjacentLocation(insideLocation);
				insideLocation.addAdjacentLocation(membraneLocation);
				membraneLocation.addAdjacentLocation(outsideLocation);
				outsideLocation.addAdjacentLocation(membraneLocation);
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/12/2004 1:35:34 AM)
	 * @return ncbc_old.physics.component.PhysicalModel
	 */
	public static void addChemicalDevices(cbit.vcell.modelapp.SimulationContext simContext, org.vcell.physics.component.OOModel oOModel) throws ExpressionException, java.beans.PropertyVetoException {
	
	
		if (simContext.getGeometryContext().getGeometry().getDimension()>0){
			throw new RuntimeException("doesn't currently support spatial models");
		}
	
		//
		// add Devices for molecular species to the physical model
		//
		cbit.vcell.modelapp.SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
		for (int i = 0; i < speciesContextSpecs.length; i++){
			cbit.vcell.modelapp.SpeciesContextSpec scs = speciesContextSpecs[i];
			org.vcell.physics.component.Species species;
			try {
				species = new org.vcell.physics.component.Species(scs.getSpeciesContext().getName(), Expression.valueOf(scs.getInitialConditionParameter().getExpression().infix_JSCL()));
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			oOModel.addModelComponent(species);
		}
		//
		// add Devices for reactions to the physical model
		//
		cbit.vcell.modelapp.ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
		for (int i = 0; i < reactionSpecs.length; i++){
			cbit.vcell.modelapp.ReactionSpec rs = reactionSpecs[i];
			if (rs.getReactionMapping() == cbit.vcell.modelapp.ReactionSpec.INCLUDED ||
				rs.getReactionMapping() == cbit.vcell.modelapp.ReactionSpec.MOLECULAR_ONLY||
				rs.getReactionMapping() == cbit.vcell.modelapp.ReactionSpec.FAST){
				//
				// collect the reactionParticipant names and stoichiometries (need to pass to Reaction Devices).
				//
				cbit.vcell.model.ReactionParticipant reactionParticipants[] = rs.getReactionStep().getReactionParticipants();
				String allSpeciesNames[] = new String[reactionParticipants.length];
				int allStoichs[] = new int[reactionParticipants.length];
				for (int k = 0; k < reactionParticipants.length; k++){
					allSpeciesNames[k] = reactionParticipants[k].getName();
					if (reactionParticipants[k] instanceof cbit.vcell.model.Reactant){
						allStoichs[k] = -1*reactionParticipants[k].getStoichiometry();
					}else if (reactionParticipants[k] instanceof cbit.vcell.model.Product){
						allStoichs[k] =reactionParticipants[k].getStoichiometry();;
					}else if (reactionParticipants[k] instanceof cbit.vcell.model.Catalyst){
						allStoichs[k] = 0;
					}
				}
				cbit.vcell.model.Kinetics.KineticsParameter[] parameters = reactionSpecs[i].getReactionStep().getKinetics().getKineticsParameters();
				Expression[] equations = new Expression[parameters.length];
				//
				// PASS 1: create all parameter expressions ignoring which should be "implicitFunctions".
				//
				for (int p = 0; p < parameters.length; p++){
					try {
						equations[p] = Expression.valueOf(parameters[p].getName()+" - ("+parameters[p].getExpression().infix_JSCL()+")");
					} catch (ParseException e) {
						e.printStackTrace();
						throw new RuntimeException("Exception while parsing expression: "+e.getMessage());
					}
				}
				//
				// PASS 2: a) for all parameter expressions that are not constant, make them "implicitFunctions"
				//         b) for all speciesContexts, make them "implicitFunctions"
				// (FUTURE: could do proper dependency analysis to determine which should be "implicitFunctions").
				//
				for (int p = 0; p < parameters.length; p++){
					if (!parameters[p].getExpression().isNumeric()){
						for (int j = 0; j < equations.length; j++) {
							try {
								equations[j] = equations[j].substitute(Variable.valueOf(parameters[p].getName()), Expression.valueOf(parameters[p].getName()+"(t)"));
							} catch (ParseException e) {
								e.printStackTrace();
								throw new RuntimeException("Exception while paring expression: "+e.getMessage());
							}
						}
					}
				}
				for (int s = 0; s < allSpeciesNames.length; s++){
					for (int j = 0; j < equations.length; j++) {
						try {
							equations[j] = equations[j].substitute(Variable.valueOf(allSpeciesNames[s]), Expression.valueOf(allSpeciesNames[s]+"(t)"));
						} catch (ParseException e) {
							e.printStackTrace();
							throw new RuntimeException("Exception while paring expression: "+e.getMessage());
						}
					}
				}

				org.vcell.physics.component.Reaction reaction;
				try {
					reaction = new org.vcell.physics.component.Reaction(
							TokenMangler.fixTokenStrict(rs.getReactionStep().getName()),
							allSpeciesNames,
							allStoichs,
							rs.getReactionStep().getKinetics().getRateParameter().getName(),
							equations);
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
				oOModel.addModelComponent(reaction);
				org.vcell.physics.component.ModelComponent modelComponents[] = oOModel.getModelComponents();
				for (int l = 0; l < modelComponents.length; l++){
					if (modelComponents[l] instanceof org.vcell.physics.component.Species){
						org.vcell.physics.component.Species species = (org.vcell.physics.component.Species)modelComponents[l];
						for (int k = 0; k < allSpeciesNames.length; k++){
							if (species.getName().equals(allSpeciesNames[k])){
								oOModel.joinConnection(reaction.getConnectors(k),species.getConnectors(0));
							}
						}
					}
				}
			}
//			//
//			// add Current sources (membrane reactions that carry current)
//			//
//			ReactionStep reactionSteps[] = model.getReactionSteps();
//			for (int i = 0; i < reactionSteps.length; i++){
//				if (reactionSteps[i].getStructure() instanceof Membrane){
//					Membrane membrane = (Membrane)reactionSteps[i].getStructure();
//					int physicsOptions = reactionSteps[i].getPhysicsOptions();
//					int valence = (int)reactionSteps[i].getChargeCarrierValence().getExpression().evaluateConstant();
//					if (valence!=0 && (physicsOptions==ReactionStep.PHYSICS_ELECTRICAL_ONLY || physicsOptions==ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL)){
//						for (int j = 0; j < locations.length; j++){
//							if (locations[j] instanceof LumpedLocation && locations[j].getName().startsWith(membrane.getName())){
//								LumpedLocation surfaceLocation = (LumpedLocation)locations[j];
//								String name = reactionSteps[i].getName(); // +"_"+surfaceLocation.getName();
//								IExpression expression = reactionSteps[i].getKinetics().getCurrentParameter().getExpression();
//								CurrentSource newCurrentSource = new CurrentSource("current_"+name,1.0); // expression.evaluateConstant());
//								physicalModel.addModelComponent(newCurrentSource);
//								//
//								// connect the "positive" electrical connector on this device 
//								//
//								Location adjacentLocations[] = surfaceLocation.getAdjacentLocations();
//								LumpedLocation neighbor1 = (LumpedLocation)adjacentLocations[0];
//								OnePortElectricalDevice material1 = (OnePortElectricalDevice)physicalModel.getModelComponent("conductor_"+neighbor1.getName());
//								LumpedLocation neighbor2 = (LumpedLocation)adjacentLocations[1];
//								OnePortElectricalDevice material2 = (OnePortElectricalDevice)physicalModel.getModelComponent("conductor_"+neighbor2.getName());
//								if (neighbor1.getName().startsWith(membrane.getOutsideFeature().getName())){
//									physicalModel.addConnection(new Connection(new Connector[] { newCurrentSource.getConnectors()[0] , material1.getConnectors()[0] } ));
//								}else if (neighbor2.getName().startsWith(membrane.getOutsideFeature().getName())){
//									physicalModel.addConnection(new Connection(new Connector[] { newCurrentSource.getConnectors()[0] , material2.getConnectors()[0] } ));
//								}
//								if (neighbor1.getName().startsWith(membrane.getInsideFeature().getName())){
//									physicalModel.addConnection(new Connection(new Connector[] { newCurrentSource.getConnectors(1), material1.getConnectors(0) } ));
//								}else if (neighbor2.getName().startsWith(membrane.getInsideFeature().getName())){
//									physicalModel.addConnection(new Connection(new Connector[] { newCurrentSource.getConnectors()[1],material2.getConnectors(0) } ));
//								}
//							}
//						}
//					}
//				}
//			}
		}
	}
	public static Location[] getLocations(OOModel ooModel){
		ModelComponent[] components = ooModel.getModelComponents();
		ArrayList<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof Location){
				locations.add((Location)components[i]);
			}
		}
		return locations.toArray(new Location[locations.size()]);
	}
	
	public static void addElectricalDevices(cbit.vcell.modelapp.SimulationContext simContext, OOModel physicalModel) throws org.vcell.expression.ExpressionException, java.beans.PropertyVetoException {

		Model model = simContext.getModel();
		Structure structures[] = model.getStructures();
		Location[] locations = getLocations(physicalModel);
		
		//
		// add Devices for material properties of lumped 3D locations (unresolved volumes)
		//
		for (int i = 0; i < locations.length; i++){
			if (locations[i] instanceof LumpedLocation){
				LumpedLocation lumpedLocation = (LumpedLocation)locations[i];
				if (lumpedLocation.getDimension()==3){
					OnePortElectricalDevice infiniteConductor = new OnePortElectricalDevice("conductor_"+lumpedLocation.getName());
					physicalModel.addModelComponent(infiniteConductor);
				}
			}else{
				throw new RuntimeException("unexpected location type '"+locations[i].getClass().getName()+"'");
			}
		}
		//
		// add Devices for material properties of lumped 2D locations (unresolved membranes)
		// connect devices for material properties (start with SurfaceElectricalMaterial)
		//
		for (int i = 0; i < locations.length; i++){
			if (locations[i] instanceof LumpedLocation){
				LumpedLocation lumpedLocation = (LumpedLocation)locations[i];
				if (lumpedLocation.getDimension()==2){
					Capacitor membraneCapacitance = new Capacitor("cap_"+lumpedLocation.getName(),1);
					physicalModel.addModelComponent(membraneCapacitance);
					Location[] adjacentLocations = lumpedLocation.getAdjacentLocations();
					if (adjacentLocations.length!=2 || adjacentLocations[0].getDimension()!=3 || adjacentLocations[1].getDimension()!=3){
						throw new RuntimeException("expecting two 3D locations adjacent to surface location: "+lumpedLocation.getName());
					}
					OnePortElectricalDevice conductor0 = (OnePortElectricalDevice)physicalModel.getModelComponent("conductor_"+adjacentLocations[0].getName());
					OnePortElectricalDevice conductor1 = (OnePortElectricalDevice)physicalModel.getModelComponent("conductor_"+adjacentLocations[1].getName());
					Connector conductor0_connector = conductor0.getConnectors(0);
					physicalModel.addConnection(new Connection(new Connector[] { conductor0_connector, membraneCapacitance.getConnectors(0)} ));
					Connector conductor1_connector = conductor1.getConnectors(0);
					physicalModel.addConnection(new Connection(new Connector[] { conductor1_connector, membraneCapacitance.getConnectors(1)} ));
				}
			}else{
				throw new RuntimeException("unexpected location type '"+locations[i].getClass().getName()+"'");
			}
		}
		
		//
		// add Device for voltage/current clamp electrode(s)
		//
		cbit.vcell.modelapp.ElectricalStimulus stimuli[] = simContext.getElectricalStimuli();
		cbit.vcell.modelapp.Electrode groundElectrode = simContext.getGroundElectrode();
		for (int i = 0; i < stimuli.length; i++){
			cbit.vcell.modelapp.ElectricalStimulus stimulus = stimuli[i];
			//
			// get electrodes
			//
			cbit.vcell.modelapp.Electrode probeElectrode = stimulus.getElectrode();
			if (probeElectrode == null){
				throw new RuntimeException("null electrode for electrical stimulus");
			}
			if (groundElectrode == null){
				throw new RuntimeException("null ground electrode for electrical stimulus");
			}

			Feature negativeFeature = groundElectrode.getFeature();
			Feature positiveFeature = probeElectrode.getFeature();

			FeatureMapping negativeFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(negativeFeature);
			FeatureMapping positiveFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(positiveFeature);
			//
			// right now, only support clamping a single membrane (almost ALWAYS the case).
			// find the membrane
			//
			Membrane clampedMembrane = null;
			for (int j = 0; j < structures.length; j++){
				if (structures[j] instanceof Membrane){
					Membrane membrane = (Membrane)structures[j];
					if ((membrane.getInsideFeature() == negativeFeature && membrane.getOutsideFeature() == positiveFeature) ||
						(membrane.getInsideFeature() == positiveFeature && membrane.getOutsideFeature() == negativeFeature)){
						clampedMembrane = membrane;
					}
				}
			}
			
			//
			// really should check the position of the electrodes to determine which regions are affected
			//
			System.out.println("<<< WARNING >>> MappingUtilities.addElectricalDevices(): should check which volume regions are affected by voltage/current clamp electrodes");
			LumpedLocation surfaceLocation = null;
			for (int j = 0; j < locations.length; j++){
				if (locations[j] instanceof LumpedLocation && ((LumpedLocation)locations[j]).getDimension()==2 && locations[j].getName().startsWith(clampedMembrane.getName()+"_")){
					surfaceLocation = (LumpedLocation)locations[j];
					LumpedLocation adjacentVolumeLocation1 = (LumpedLocation)surfaceLocation.getAdjacentLocations()[0];
					LumpedLocation adjacentVolumeLocation2 = (LumpedLocation)surfaceLocation.getAdjacentLocations()[1];
					//
					// find location for positive electrode
					//
					LumpedLocation positiveVolumeLocation = null;
					if (adjacentVolumeLocation1.getName().startsWith(positiveFeature.getName())){
						positiveVolumeLocation = adjacentVolumeLocation1;
					}else if (adjacentVolumeLocation2.getName().startsWith(positiveFeature.getName())){
						positiveVolumeLocation = adjacentVolumeLocation2;
					}else{
						throw new RuntimeException("could not resolve positive electrode at surface location : "+surfaceLocation.getName());
					}
					//
					// find location for negative electrode
					//
					LumpedLocation negativeVolumeLocation = null;
					if (adjacentVolumeLocation1.getName().startsWith(negativeFeature.getName())){
						negativeVolumeLocation = adjacentVolumeLocation1;
					}else if (adjacentVolumeLocation2.getName().startsWith(negativeFeature.getName())){
						negativeVolumeLocation = adjacentVolumeLocation2;
					}else{
						throw new RuntimeException("could not resolve negative electrode at surface location : "+surfaceLocation.getName());
					}

					//
					// get VolumeElectricalMaterial to connect positive electrode
					//
					OnePortElectricalDevice positiveElectricalMaterial = (OnePortElectricalDevice)physicalModel.getModelComponent("conductor_"+positiveVolumeLocation.getName());
					if (positiveElectricalMaterial == null){
						throw new RuntimeException("found no conductor modelComponent in VolumeLocation "+positiveVolumeLocation.getName());
					}
					//
					// get VolumeElectricalMaterial to connect negative electrode
					//
					OnePortElectricalDevice negativeElectricalMaterial = (OnePortElectricalDevice)physicalModel.getModelComponent("conductor_"+negativeVolumeLocation.getName());
					if (negativeElectricalMaterial == null){
						throw new RuntimeException("found no conductor in VolumeLocation "+negativeVolumeLocation.getName());
					}

					//
					// create surface component for voltage/current clamp device
					//
					TwoPortElectricalComponent clampDevice = null;
					if (stimulus instanceof cbit.vcell.modelapp.CurrentClampStimulus){
						VCUnitDefinition unit = VCUnitDefinition.UNIT_pA;  // current clamp electrodes are always "total current"
						cbit.vcell.modelapp.CurrentClampStimulus ccStimulus = (cbit.vcell.modelapp.CurrentClampStimulus)stimulus;
						clampDevice = new CurrentSource("currentClamp",ccStimulus.getCurrentParameter().getExpression().evaluateConstant());
						
					}else if (stimulus instanceof cbit.vcell.modelapp.VoltageClampStimulus){
						VCUnitDefinition unit = VCUnitDefinition.UNIT_mV;
						cbit.vcell.modelapp.VoltageClampStimulus vcStimulus = (cbit.vcell.modelapp.VoltageClampStimulus)stimulus;
						clampDevice = new VoltageSource("voltageClamp",vcStimulus.getVoltageParameter().getExpression().evaluateConstant());
					}

					//
					// add device to model and add "Connection" from clamp device "Connectors" to appropriate "VolumeElectricalMaterials"
					//
					physicalModel.addModelComponent(clampDevice);
					Connection positiveConnection = new Connection(new Connector[] { clampDevice.getConnectors(0),positiveElectricalMaterial.getConnectors(0) } );
					physicalModel.addConnection(positiveConnection);
					Connection negativeConnection = new Connection(new Connector[] { clampDevice.getConnectors(1),negativeElectricalMaterial.getConnectors(0) } );
					physicalModel.addConnection(negativeConnection);
					break; // use first region only
				}
			}
		}
	}

}

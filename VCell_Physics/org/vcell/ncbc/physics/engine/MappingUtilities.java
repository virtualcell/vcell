package org.vcell.ncbc.physics.engine;

import java.beans.PropertyVetoException;

import org.vcell.expression.ExpressionException;
import org.vcell.ncbc.physics.component.Connection;
import org.vcell.ncbc.physics.component.CurrentSource;
import org.vcell.ncbc.physics.component.ElectricalDevice;
import org.vcell.ncbc.physics.component.Location;
import org.vcell.ncbc.physics.component.MembraneSpecies;
import org.vcell.ncbc.physics.component.PhysicalModel;
import org.vcell.ncbc.physics.component.ResolvedSurfaceLocation;
import org.vcell.ncbc.physics.component.ResolvedVolumeLocation;
import org.vcell.ncbc.physics.component.SurfaceElectricalMaterial;
import org.vcell.ncbc.physics.component.SurfaceLocation;
import org.vcell.ncbc.physics.component.UnresolvedSurfaceLocation;
import org.vcell.ncbc.physics.component.UnresolvedVolumeLocation;
import org.vcell.ncbc.physics.component.VolumeElectricalMaterial;
import org.vcell.ncbc.physics.component.VolumeLocation;
import org.vcell.ncbc.physics.component.VolumeReaction;
import org.vcell.ncbc.physics.component.VolumeSpecies;
import org.vcell.units.VCUnitDefinition;

import cbit.image.ImageException;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.modelapp.FeatureMapping;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.StructureMapping;
/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 1:35:08 AM)
 * @author: Jim Schaff
 */
public class MappingUtilities {
/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 1:35:34 AM)
 * @return ncbc_old.physics.component.PhysicalModel
 */
public static void addChemicalDevices(cbit.vcell.modelapp.SimulationContext simContext, PhysicalModel physicalModel) throws org.vcell.expression.ExpressionException, java.beans.PropertyVetoException {

	Model model = simContext.getModel();
	Structure structures[] = model.getStructures();
	Location locations[] = physicalModel.getLocations();

	//
	// add Devices for molecular species to the physical model
	//
	cbit.vcell.modelapp.SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < speciesContextSpecs.length; i++){
		cbit.vcell.modelapp.SpeciesContextSpec scs = speciesContextSpecs[i];
		for (int j = 0; j < locations.length; j++){
			String structName = scs.getSpeciesContext().getStructure().getName();
			if (locations[j].getName().startsWith(structName+"_") || locations[j].getName().equals(structName)){
				//
				// insert this species context into this location
				//
				if (speciesContextSpecs[i].isDiffusing()){
					if (locations[j] instanceof VolumeLocation){
						physicalModel.addDevice(new org.vcell.ncbc.physics.component.DiffusingVolumeSpecies(scs.getSpeciesContext().getName(),(VolumeLocation)locations[j]));
					}else if (locations[j] instanceof SurfaceLocation){
						physicalModel.addDevice(new org.vcell.ncbc.physics.component.DiffusingMembraneSpecies(scs.getSpeciesContext().getName(),(SurfaceLocation)locations[j]));
					}
				}else{
					if (locations[j] instanceof VolumeLocation){
						physicalModel.addDevice(new org.vcell.ncbc.physics.component.ImmobileVolumeSpecies(scs.getSpeciesContext().getName(),(VolumeLocation)locations[j]));
					}else if (locations[j] instanceof SurfaceLocation){
						physicalModel.addDevice(new org.vcell.ncbc.physics.component.ImmobileMembraneSpecies(scs.getSpeciesContext().getName(),(SurfaceLocation)locations[j]));
					}
				}
			}
		}
	}
	//
	// add Devices for reactions to the physical model
	//
	cbit.vcell.modelapp.ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
	for (int i = 0; i < reactionSpecs.length; i++){
		cbit.vcell.modelapp.ReactionSpec rs = reactionSpecs[i];
		if (rs.getReactionMapping() == cbit.vcell.modelapp.ReactionSpec.INCLUDED ||
			rs.getReactionMapping() == cbit.vcell.modelapp.ReactionSpec.FAST){
			//
			// collect the reactionParticipant names and stoichiometries (need to pass to Reaction Devices).
			//
			cbit.vcell.model.ReactionParticipant reactionParticipants[] = rs.getReactionStep().getReactionParticipants();
			//
			// find all locations where this reactionStep is needed.
			//
			for (int j = 0; j < locations.length; j++){
				String structName = rs.getReactionStep().getStructure().getName();
				if (locations[j].getName().startsWith(structName+"_") || locations[j].getName().equals(structName)){
					//
					// insert this reactionStep into this location
					//
					if (locations[j] instanceof VolumeLocation){
						String allNames[] = new String[reactionParticipants.length];
						int allStoichs[] = new int[reactionParticipants.length];
						for (int k = 0; k < reactionParticipants.length; k++){
							allNames[k] = reactionParticipants[k].getName();
							if (reactionParticipants[k] instanceof cbit.vcell.model.Reactant){
								allStoichs[k] = -1;
							}else if (reactionParticipants[k] instanceof cbit.vcell.model.Product){
								allStoichs[k] = 1;
							}else if (reactionParticipants[k] instanceof cbit.vcell.model.Catalyst){
								allStoichs[k] = 0;
							}else if (reactionParticipants[k] instanceof cbit.vcell.model.Flux){
								cbit.vcell.model.Flux flux = (cbit.vcell.model.Flux)reactionParticipants[k];
								if (flux.getSpeciesContext().getStructure() == ((Membrane)rs.getReactionStep().getStructure()).getInsideFeature()){
									allStoichs[k] = 1;
								}else if(flux.getSpeciesContext().getStructure() == ((Membrane)rs.getReactionStep().getStructure()).getOutsideFeature()){
									allStoichs[k] = -1;
								}
							}
						}
						VolumeReaction volumeReaction = new org.vcell.ncbc.physics.component.VolumeReaction(rs.getReactionStep().getName(),locations[j],"rate",allNames,allStoichs);
						physicalModel.addDevice(volumeReaction);
						VolumeSpecies volSpecies[] = (VolumeSpecies[])physicalModel.getDevices(locations[j],VolumeSpecies.class);
						for (int k = 0; k < allNames.length; k++){
							for (int l = 0; l < volSpecies.length; l++){
								if (volSpecies[l].getName().startsWith(allNames[k])){
									physicalModel.addConnection(new org.vcell.ncbc.physics.component.Connection(volumeReaction.getConnectorByName(allNames[k]),volSpecies[l].getConcentrationConnector()));
								}
							}
						}
					}else if (locations[j] instanceof SurfaceLocation){
						int membraneSpeciesCount = 0;
						for (int k = 0; k < reactionParticipants.length; k++){
							if (reactionParticipants[k].getSpeciesContext().getStructure() instanceof Membrane){
								membraneSpeciesCount++;
							}
						}
						int memStoichs[] = new int[membraneSpeciesCount];
						int volStoichs[] = new int[reactionParticipants.length - membraneSpeciesCount];
						String memNames[] = new String[membraneSpeciesCount];
						String volNames[] = new String[reactionParticipants.length - membraneSpeciesCount];
						int memIndex = 0;
						int volIndex = 0;
						for (int k = 0; k < reactionParticipants.length; k++){
							int stoich = 0;
							if (reactionParticipants[k] instanceof cbit.vcell.model.Reactant){
								stoich = -1;
							}else if (reactionParticipants[k] instanceof cbit.vcell.model.Product){
								stoich = 1;
							}else if (reactionParticipants[k] instanceof cbit.vcell.model.Catalyst){
								stoich = 0;
							}else if (reactionParticipants[k] instanceof cbit.vcell.model.Flux){
								cbit.vcell.model.Flux flux = (cbit.vcell.model.Flux)reactionParticipants[k];
								if (flux.getSpeciesContext().getStructure() == ((Membrane)rs.getReactionStep().getStructure()).getInsideFeature()){
									stoich = 1;
								}else if(flux.getSpeciesContext().getStructure() == ((Membrane)rs.getReactionStep().getStructure()).getOutsideFeature()){
									stoich = -1;
								}
							}
							if (reactionParticipants[k].getSpeciesContext().getStructure() instanceof Membrane){
								memNames[memIndex] = reactionParticipants[k].getName();
								memStoichs[memIndex] = stoich;
								memIndex++;
							}
							if (reactionParticipants[k].getSpeciesContext().getStructure() instanceof Feature){
								volNames[volIndex] = reactionParticipants[k].getName();
								volStoichs[volIndex] = stoich;
								volIndex++;
							}
						}
						org.vcell.ncbc.physics.component.MembraneReaction membraneReaction = new org.vcell.ncbc.physics.component.MembraneReaction(rs.getReactionStep().getName(),locations[j],"rate",memNames,memStoichs,volNames,volStoichs);
						physicalModel.addDevice(membraneReaction);
						MembraneSpecies memSpecies[] = (MembraneSpecies[])physicalModel.getDevices(locations[j],MembraneSpecies.class);
						for (int k = 0; k < memNames.length; k++){
							for (int l = 0; l < memSpecies.length; l++){
								if (memSpecies[l].getName().startsWith(memNames[k])){
									physicalModel.addConnection(new org.vcell.ncbc.physics.component.Connection(membraneReaction.getConnectorByName(memNames[k]),memSpecies[l].getSpeciesSurfaceDensityConnector()));
								}
							}
						}
						VolumeSpecies volSpecies[] = (VolumeSpecies[])physicalModel.getDevices(locations[j].getAdjacentLocations()[0],VolumeSpecies.class);
						for (int k = 0; k < volNames.length; k++){
							for (int l = 0; l < volSpecies.length; l++){
								if (volSpecies[l].getName().startsWith(volNames[k])){
									physicalModel.addConnection(new org.vcell.ncbc.physics.component.Connection(membraneReaction.getConnectorByName(volNames[k]),volSpecies[l].getConcentrationConnector()));
								}
							}
						}
						volSpecies = (VolumeSpecies[])physicalModel.getDevices(locations[j].getAdjacentLocations()[1],VolumeSpecies.class);
						for (int k = 0; k < volNames.length; k++){
							for (int l = 0; l < volSpecies.length; l++){
								if (volSpecies[l].getName().startsWith(volNames[k])){
									physicalModel.addConnection(new org.vcell.ncbc.physics.component.Connection(membraneReaction.getConnectorByName(volNames[k]),volSpecies[l].getConcentrationConnector()));
								}
							}
						}
					}
				}
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 1:35:34 AM)
 * @return ncbc_old.physics.component.PhysicalModel
 */
public static void addElectricalDevices(cbit.vcell.modelapp.SimulationContext simContext, PhysicalModel physicalModel) throws org.vcell.expression.ExpressionException, java.beans.PropertyVetoException {

	Model model = simContext.getModel();
	Structure structures[] = model.getStructures();
	Location locations[] = physicalModel.getLocations();

	//
	// add Devices for material properties of SurfaceLocations and VolumeLocations
	//
	for (int i = 0; i < locations.length; i++){
		if (locations[i] instanceof VolumeLocation){
			if (((VolumeLocation)locations[i]).getFeatureName()!=null){
				org.vcell.ncbc.physics.component.VolumeLocation volumeLocation = (org.vcell.ncbc.physics.component.VolumeLocation)locations[i];
				physicalModel.addDevice(new org.vcell.ncbc.physics.component.InfiniteVolumeConductor("infCond",volumeLocation));
			}
		}else if (locations[i] instanceof SurfaceLocation){
			if (((SurfaceLocation)locations[i]).getMembraneName()!=null){
				org.vcell.ncbc.physics.component.SurfaceLocation surfaceLocation = (org.vcell.ncbc.physics.component.SurfaceLocation)locations[i];
				physicalModel.addDevice(new org.vcell.ncbc.physics.component.CapacitiveMembrane("cap",surfaceLocation));
			}
		}else{
			throw new RuntimeException("unexpected location type '"+locations[i].getClass().getName()+"'");
		}
	}
	//
	// connect devices for material properties (start with SurfaceElectricalMaterial)
	//
	for (int i = 0; i < locations.length; i++){
		if (locations[i] instanceof SurfaceLocation && ((SurfaceLocation)locations[i]).getMembraneName()!=null){
			SurfaceLocation surfaceLocation = (SurfaceLocation)locations[i];
			org.vcell.ncbc.physics.component.SurfaceElectricalMaterial surfaceMaterial = (SurfaceElectricalMaterial)physicalModel.getDevices(surfaceLocation,SurfaceElectricalMaterial.class)[0];
			//
			// get volumeElectricalMaterials from neighboring volumes and add appropriate connectors (between surface & volumes)
			//
			if (surfaceLocation.getAdjacentLocations().length!=2){
				throw new RuntimeException("unexpected number of neighbors for surface");
			}
			VolumeElectricalMaterial volumeMaterial0 = (VolumeElectricalMaterial)physicalModel.getDevices(surfaceLocation.getAdjacentLocations()[0],VolumeElectricalMaterial.class)[0];
			VolumeElectricalMaterial volumeMaterial1 = (VolumeElectricalMaterial)physicalModel.getDevices(surfaceLocation.getAdjacentLocations()[1],VolumeElectricalMaterial.class)[0];
			
			physicalModel.addConnection(new Connection(surfaceMaterial.getNegativeConnector(),volumeMaterial0.getElectricalConnector()));
			physicalModel.addConnection(new Connection(surfaceMaterial.getPositiveConnector(),volumeMaterial1.getElectricalConnector()));
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
		org.vcell.ncbc.physics.component.SurfaceLocation surfaceLocation = null;
		for (int j = 0; j < locations.length; j++){
			if (locations[j] instanceof org.vcell.ncbc.physics.component.SurfaceLocation && locations[j].getName().startsWith(clampedMembrane.getName()+"_")){
				surfaceLocation = (SurfaceLocation)locations[j];
				VolumeLocation adjacentVolumeLocation1 = (VolumeLocation)surfaceLocation.getAdjacentLocations()[0];
				VolumeLocation adjacentVolumeLocation2 = (VolumeLocation)surfaceLocation.getAdjacentLocations()[1];
				//
				// find location for positive electrode
				//
				org.vcell.ncbc.physics.component.VolumeLocation positiveVolumeLocation = null;
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
				org.vcell.ncbc.physics.component.VolumeLocation negativeVolumeLocation = null;
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
				VolumeElectricalMaterial positiveElectricalMaterials[] = (VolumeElectricalMaterial[])physicalModel.getDevices(positiveVolumeLocation,VolumeElectricalMaterial.class);
				if (positiveElectricalMaterials.length!=1){
					throw new RuntimeException("found "+positiveElectricalMaterials.length+" VolumeElectricalMaterials in VolumeLocation "+positiveVolumeLocation.getName()+", expected 1");
				}
				org.vcell.ncbc.physics.component.VolumeElectricalMaterial positiveElectricalMaterial = positiveElectricalMaterials[0];
				//
				// get VolumeElectricalMaterial to connect negative electrode
				//
				VolumeElectricalMaterial negativeElectricalMaterials[] = (VolumeElectricalMaterial[])physicalModel.getDevices(negativeVolumeLocation,VolumeElectricalMaterial.class);
				if (negativeElectricalMaterials.length!=1){
					throw new RuntimeException("found "+negativeElectricalMaterials.length+" VolumeElectricalMaterials in VolumeLocation "+negativeVolumeLocation.getName()+", expected 1");
				}
				org.vcell.ncbc.physics.component.VolumeElectricalMaterial negativeElectricalMaterial = negativeElectricalMaterials[0];

				//
				// create surface component for voltage/current clamp device
				//
				ElectricalDevice clampDevice = null;
				if (stimulus instanceof cbit.vcell.modelapp.CurrentClampStimulus){
					VCUnitDefinition unit = VCUnitDefinition.UNIT_pA;  // current clamp electrodes are always "total current"
					cbit.vcell.modelapp.CurrentClampStimulus ccStimulus = (cbit.vcell.modelapp.CurrentClampStimulus)stimulus;
					clampDevice = new org.vcell.ncbc.physics.component.CurrentSource("currentClamp",surfaceLocation,unit,ccStimulus.getCurrentParameter().getExpression().infix());
					
				}else if (stimulus instanceof cbit.vcell.modelapp.VoltageClampStimulus){
					VCUnitDefinition unit = VCUnitDefinition.UNIT_mV;
					cbit.vcell.modelapp.VoltageClampStimulus vcStimulus = (cbit.vcell.modelapp.VoltageClampStimulus)stimulus;
					clampDevice = new org.vcell.ncbc.physics.component.VoltageSource("voltageClamp",surfaceLocation,unit,vcStimulus.getVoltageParameter().getExpression().infix());
				}

				//
				// add device to model and add "Connection" from clamp device "Connectors" to appropriate "VolumeElectricalMaterials"
				//
				physicalModel.addDevice(clampDevice);
				Connection positiveConnection = new Connection(clampDevice.getPositiveConnector(),positiveElectricalMaterial.getElectricalConnector());
				physicalModel.addConnection(positiveConnection);
				Connection negativeConnection = new Connection(clampDevice.getNegativeConnector(),negativeElectricalMaterial.getElectricalConnector());
				physicalModel.addConnection(negativeConnection);
				break; // use first region only
			}
		}
	}
	//
	// add Current sources (membrane reactions that carry current)
	//
	ReactionStep reactionSteps[] = model.getReactionSteps();
	for (int i = 0; i < reactionSteps.length; i++){
		if (reactionSteps[i].getStructure() instanceof Membrane){
			Membrane membrane = (Membrane)reactionSteps[i].getStructure();
			int physicsOptions = reactionSteps[i].getPhysicsOptions();
			int valence = (int)reactionSteps[i].getChargeCarrierValence().getExpression().evaluateConstant();
			if (valence!=0 && (physicsOptions==ReactionStep.PHYSICS_ELECTRICAL_ONLY || physicsOptions==ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL)){
				for (int j = 0; j < locations.length; j++){
					if (locations[j] instanceof SurfaceLocation && locations[j].getName().startsWith(membrane.getName())){
						SurfaceLocation surfaceLocation = (SurfaceLocation)locations[j];
						String name = reactionSteps[i].getName(); // +"_"+surfaceLocation.getName();
						VCUnitDefinition unit = null;
						if (surfaceLocation instanceof ResolvedSurfaceLocation){
							unit = VCUnitDefinition.UNIT_pA_per_um2;
						}else if (surfaceLocation instanceof UnresolvedSurfaceLocation){
							unit = VCUnitDefinition.UNIT_pA;
						}
						String expression = reactionSteps[i].getKinetics().getCurrentParameter().getName();
						CurrentSource newCurrentSource = new CurrentSource(name,surfaceLocation,unit,expression);
						physicalModel.addDevice(newCurrentSource);
						//
						// connect the "positive" electrical connector on this device 
						//
						Location adjacentLocations[] = surfaceLocation.getAdjacentLocations();
						VolumeLocation neighbor1 = (VolumeLocation)adjacentLocations[0];
						VolumeElectricalMaterial material1 = (VolumeElectricalMaterial)physicalModel.getDevices(neighbor1,VolumeElectricalMaterial.class)[0];
						VolumeLocation neighbor2 = (VolumeLocation)adjacentLocations[1];
						VolumeElectricalMaterial material2 = (VolumeElectricalMaterial)physicalModel.getDevices(neighbor2,VolumeElectricalMaterial.class)[0];
						if (neighbor1.getName().startsWith(membrane.getOutsideFeature().getName())){
							physicalModel.addConnection(new Connection(newCurrentSource.getPositiveConnector(),material1.getElectricalConnector()));
						}else if (neighbor2.getName().startsWith(membrane.getOutsideFeature().getName())){
							physicalModel.addConnection(new Connection(newCurrentSource.getPositiveConnector(),material2.getElectricalConnector()));
						}
						if (neighbor1.getName().startsWith(membrane.getInsideFeature().getName())){
							physicalModel.addConnection(new Connection(newCurrentSource.getNegativeConnector(),material1.getElectricalConnector()));
						}else if (neighbor2.getName().startsWith(membrane.getInsideFeature().getName())){
							physicalModel.addConnection(new Connection(newCurrentSource.getNegativeConnector(),material2.getElectricalConnector()));
						}
					}
				}
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 1:35:34 AM)
 * @return ncbc_old.physics.component.PhysicalModel
 * @throws GeometryException 
 */
public static void addLocations(SimulationContext simContext, PhysicalModel physicalModel) throws ImageException, ExpressionException, PropertyVetoException, GeometryException {

	System.out.println("\n\ngenerating surfaces from geometry ... shouldn't have to do this here\n\n");
	cbit.vcell.geometry.GeometrySpec geometrySpec = simContext.getGeometry().getGeometrySpec();
	if (geometrySpec.getDimension()>0){
		cbit.vcell.geometry.surface.GeometrySurfaceDescription geoSurfDesc = simContext.getGeometry().getGeometrySurfaceDescription();
		if (geoSurfDesc.getGeometricRegions()==null){
			geoSurfDesc.updateAll();
		}
		
		//
		// parse volumeGeometricRegions into ResolvedVolumeLocations, UnresolvedVolumeLocations, and UnresolvedMembraneLocations
		//
		cbit.vcell.geometry.surface.GeometricRegion geometricRegions[] = geoSurfDesc.getGeometricRegions();
		for (int i = 0; geometricRegions!=null && i < geometricRegions.length; i++){
			if (geometricRegions[i] instanceof cbit.vcell.geometry.surface.VolumeGeometricRegion){
				cbit.vcell.geometry.surface.VolumeGeometricRegion volGeometricRegion = (cbit.vcell.geometry.surface.VolumeGeometricRegion)geometricRegions[i];
				cbit.vcell.geometry.SubVolume subVolume = volGeometricRegion.getSubVolume();
				Feature resolvedFeature = simContext.getGeometryContext().getResolvedFeature(subVolume);
				
				String name = null;
				if (resolvedFeature==null){
					name = "unmapped_"+subVolume.getName()+"_"+volGeometricRegion.getRegionID();
				}else{
					name = resolvedFeature.getName()+"_"+volGeometricRegion.getRegionID();
				}
				org.vcell.ncbc.physics.component.ResolvedVolumeLocation resolvedVolumeLocation = new org.vcell.ncbc.physics.component.ResolvedVolumeLocation(name);
				resolvedVolumeLocation.setSubVolumeName(subVolume.getName());
				if (resolvedFeature!=null){
					resolvedVolumeLocation.setFeatureName(resolvedFeature.getName());
				}
				resolvedVolumeLocation.setVolumeGeometricRegion(volGeometricRegion);
				physicalModel.addLocation(resolvedVolumeLocation);
				System.out.println("added resolvedVolumeLocation("+resolvedVolumeLocation.getName()+")");
				//
				// add any unresolved structures to this resolved region
				//
				// first add unnresolved features (volumes) that are mapped to the current ResolvedVolumeLocation
				StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
				for (int j = 0; j < structureMappings.length; j++){
					if (structureMappings[j] instanceof FeatureMapping){
						FeatureMapping featureMapping = (FeatureMapping)structureMappings[j];
						if (!featureMapping.getResolved() && featureMapping.getSubVolume()==subVolume){
							UnresolvedVolumeLocation unresolvedVolumeLocation = new UnresolvedVolumeLocation(featureMapping.getFeature().getName()+"_"+volGeometricRegion.getRegionID());
							unresolvedVolumeLocation.setFeatureName(featureMapping.getFeature().getName());
							physicalModel.addLocation(unresolvedVolumeLocation);
						}
					}
				}
				// now add unnresolved membranes (surfaces), and connect them to the volumes
				for (int j = 0; j < structureMappings.length; j++){
					if (structureMappings[j] instanceof MembraneMapping && !((MembraneMapping)structureMappings[j]).getResolved(simContext)){
						MembraneMapping membraneMapping = (MembraneMapping)structureMappings[j];
						FeatureMapping insideFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(membraneMapping.getMembrane().getInsideFeature());
						FeatureMapping outsideFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(membraneMapping.getMembrane().getOutsideFeature());
						if (insideFeatureMapping.getSubVolume() != outsideFeatureMapping.getSubVolume()){
							throw new RuntimeException("adjacent feature mappings for unresolved membrane '"+membraneMapping.getMembrane().getName()+" mapped to different subVolumes");
						}
						if (insideFeatureMapping.getSubVolume()==subVolume){
							UnresolvedSurfaceLocation unresolvedSurfaceLocation = new UnresolvedSurfaceLocation(membraneMapping.getMembrane().getName()+"_"+volGeometricRegion.getRegionID());
							unresolvedSurfaceLocation.setMembraneName(membraneMapping.getMembrane().getName());
							physicalModel.addLocation(unresolvedSurfaceLocation);
							//
							// get Inner neighbor's volumeLocation and connect them
							//
							VolumeLocation insideVolumeLocation = (VolumeLocation)physicalModel.getLocation(membraneMapping.getMembrane().getInsideFeature().getName()+"_"+volGeometricRegion.getRegionID());
							insideVolumeLocation.addAdjacentLocation(unresolvedSurfaceLocation);
							unresolvedSurfaceLocation.addAdjacentLocation(insideVolumeLocation);
							//
							// get Outer neighbor's volumeLocation and connect them
							//
							VolumeLocation outsideVolumeLocation = (VolumeLocation)physicalModel.getLocation(membraneMapping.getMembrane().getOutsideFeature().getName()+"_"+volGeometricRegion.getRegionID());
							outsideVolumeLocation.addAdjacentLocation(unresolvedSurfaceLocation);
							unresolvedSurfaceLocation.addAdjacentLocation(outsideVolumeLocation);
						}
					}
				}
			}
		}

		
		//
		// parse MembraneGeometricRegions into ResolvedSurfaceLocations
		//
		for (int i = 0; geometricRegions!=null && i < geometricRegions.length; i++){
			if (geometricRegions[i] instanceof cbit.vcell.geometry.surface.SurfaceGeometricRegion){
				cbit.vcell.geometry.surface.SurfaceGeometricRegion surfaceGeometricRegion = (cbit.vcell.geometry.surface.SurfaceGeometricRegion)geometricRegions[i];

				cbit.vcell.geometry.surface.GeometricRegion[] adjacentRegions = (cbit.vcell.geometry.surface.GeometricRegion[])surfaceGeometricRegion.getAdjacentGeometricRegions();
				cbit.vcell.geometry.surface.VolumeGeometricRegion adjacentVolumeRegion1 = (cbit.vcell.geometry.surface.VolumeGeometricRegion)adjacentRegions[0];
				cbit.vcell.geometry.surface.VolumeGeometricRegion adjacentVolumeRegion2 = (cbit.vcell.geometry.surface.VolumeGeometricRegion)adjacentRegions[1];
				SubVolume adjacentSubvolume1 = adjacentVolumeRegion1.getSubVolume();
				SubVolume adjacentSubvolume2 = adjacentVolumeRegion2.getSubVolume();
				Feature adjacentFeature1 = simContext.getGeometryContext().getResolvedFeature(adjacentSubvolume1);
				Feature adjacentFeature2 = simContext.getGeometryContext().getResolvedFeature(adjacentSubvolume2);
				Structure structures[] = simContext.getModel().getStructures();
				Membrane modelMembrane = null;
				for (int j = 0; j < structures.length; j++){
					if (structures[j] instanceof Membrane){
						Membrane membrane = (Membrane)structures[j];
						if ((membrane.getInsideFeature() == adjacentFeature1 && membrane.getOutsideFeature() == adjacentFeature2) ||
							(membrane.getInsideFeature() == adjacentFeature2 && membrane.getOutsideFeature() == adjacentFeature1)){
							modelMembrane = membrane;
						}
					}
				}
				String membraneName = null;
				if (modelMembrane==null){
					membraneName = "unmapped_"+((cbit.vcell.geometry.surface.VolumeGeometricRegion)adjacentRegions[0]).getRegionID()+"_"+((cbit.vcell.geometry.surface.VolumeGeometricRegion)adjacentRegions[1]).getRegionID();
				}else{
					membraneName = modelMembrane.getName()+"_"+((cbit.vcell.geometry.surface.VolumeGeometricRegion)adjacentRegions[0]).getRegionID()+"_"+((cbit.vcell.geometry.surface.VolumeGeometricRegion)adjacentRegions[1]).getRegionID();
				}
				org.vcell.ncbc.physics.component.ResolvedSurfaceLocation resolvedSurfaceLocation = new org.vcell.ncbc.physics.component.ResolvedSurfaceLocation(membraneName);
				if (modelMembrane!=null){
					resolvedSurfaceLocation.setMembraneName(modelMembrane.getName());
				}
				resolvedSurfaceLocation.setSurfaceGeometricRegion(surfaceGeometricRegion);
				physicalModel.addLocation(resolvedSurfaceLocation);
				//
				// connect this surfaceLocation to its exterior and interior volumeLocations
				//
				Location[] locations = physicalModel.getLocations();
				for (int j = 0; j < locations.length; j++){
					if (locations[j] instanceof ResolvedVolumeLocation){
						VolumeGeometricRegion volumeGeometricRegion = ((ResolvedVolumeLocation)locations[j]).getVolumeGeometricRegion();
						if (volumeGeometricRegion == adjacentVolumeRegion1 || volumeGeometricRegion == adjacentVolumeRegion2){
							resolvedSurfaceLocation.addAdjacentLocation(locations[j]);
							((VolumeLocation)locations[j]).addAdjacentLocation(resolvedSurfaceLocation);
						}
					}
				}
				//VolumeLocation exteriorVolumeLocation = (VolumeLocation)physicalModel.getLocations();
				//surfaceExteriorName);
				//resolvedSurfaceLocation.addAdjacentLocation(exteriorVolumeLocation);
				//exteriorVolumeLocation.addAdjacentLocation(resolvedSurfaceLocation);

				//VolumeLocation interiorVolumeLocation = (VolumeLocation)physicalModel.getLocation(surfaceInteriorName);
				//resolvedSurfaceLocation.addAdjacentLocation(interiorVolumeLocation);
				//interiorVolumeLocation.addAdjacentLocation(resolvedSurfaceLocation);
				System.out.println("added resolvedMembraneLocation("+resolvedSurfaceLocation.getName()+")");
			}
		}
	}else{ // zero dimensional, just add structures directly
		Structure structures[] = simContext.getModel().getStructures();
		//
		// adding surface and volumes
		//
		for (int i = 0; i < structures.length; i++){
			if (structures[i] instanceof Membrane){
				UnresolvedSurfaceLocation unresolvedSurfaceLocation = new UnresolvedSurfaceLocation(structures[i].getName());
				unresolvedSurfaceLocation.setMembraneName(structures[i].getName());
				physicalModel.addLocation(unresolvedSurfaceLocation);
			}else{
				UnresolvedVolumeLocation unresolvedVolumeLocation = new UnresolvedVolumeLocation(structures[i].getName());
				unresolvedVolumeLocation.setFeatureName(structures[i].getName());
				physicalModel.addLocation(unresolvedVolumeLocation);
			}
		}
		//
		// connecting locations (working from membranes)
		//
		for (int i = 0; i < structures.length; i++){
			if (structures[i] instanceof Membrane){
				Membrane membrane = (Membrane)structures[i];
				UnresolvedSurfaceLocation membraneLocation = (UnresolvedSurfaceLocation)physicalModel.getLocation(membrane.getName());
				UnresolvedVolumeLocation insideFeatureLocation = (UnresolvedVolumeLocation)physicalModel.getLocation(membrane.getInsideFeature().getName());
				UnresolvedVolumeLocation outsideFeatureLocation = (UnresolvedVolumeLocation)physicalModel.getLocation(membrane.getOutsideFeature().getName());
				membraneLocation.addAdjacentLocation(insideFeatureLocation);
				membraneLocation.addAdjacentLocation(outsideFeatureLocation);
				insideFeatureLocation.addAdjacentLocation(membraneLocation);
				outsideFeatureLocation.addAdjacentLocation(membraneLocation);
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 1:35:34 AM)
 * @return ncbc_old.physics.component.PhysicalModel
 */
public static PhysicalModel createFromSimulationContext(cbit.vcell.modelapp.SimulationContext simContext) throws ExpressionException, ImageException, GeometryException, PropertyVetoException {
	PhysicalModel physicalModel = new PhysicalModel();

	//
	// add locations to physicalModel
	// (names of locations always start with the structure name followed by an underscore ... e.g. "cytosol_0_1")
	//
	addLocations(simContext,physicalModel);

	addElectricalDevices(simContext,physicalModel);

	addChemicalDevices(simContext,physicalModel);

	System.out.println("# locations is "+physicalModel.getLocations().length);
	System.out.println("# devices is "+physicalModel.getDevices().length);
	System.out.println("# connections is "+physicalModel.getConnections().length);

	return physicalModel;
}
}

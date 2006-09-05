package org.vcell.physics.math;
import java.beans.PropertyVetoException;

import cbit.image.ImageException;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

import java.util.Vector;

import org.vcell.physics.component.Connection;
import org.vcell.physics.component.CurrentSource;
import org.vcell.physics.component.Model;

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
public static void addChemicalDevices(cbit.vcell.mapping.SimulationContext simContext, org.vcell.physics.component.Model model) throws cbit.vcell.parser.ExpressionException, java.beans.PropertyVetoException {

	cbit.vcell.model.Structure structures[] = simContext.getModel().getStructures();

	if (structures.length>1){
		throw new RuntimeException("doesn't currently support multiple compartments");
	}

	//
	// add Devices for molecular species to the physical model
	//
	cbit.vcell.mapping.SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < speciesContextSpecs.length; i++){
		cbit.vcell.mapping.SpeciesContextSpec scs = speciesContextSpecs[i];
		org.vcell.physics.component.Species species = new org.vcell.physics.component.Species(scs.getSpeciesContext().getName());
		model.addModelComponent(species);
	}
	//
	// add Devices for reactions to the physical model
	//
	cbit.vcell.mapping.ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
	for (int i = 0; i < reactionSpecs.length; i++){
		cbit.vcell.mapping.ReactionSpec rs = reactionSpecs[i];
		if (rs.getReactionMapping() == cbit.vcell.mapping.ReactionSpec.INCLUDED ||
			rs.getReactionMapping() == cbit.vcell.mapping.ReactionSpec.MOLECULAR_ONLY||
			rs.getReactionMapping() == cbit.vcell.mapping.ReactionSpec.FAST){
			//
			// collect the reactionParticipant names and stoichiometries (need to pass to Reaction Devices).
			//
			cbit.vcell.model.ReactionParticipant reactionParticipants[] = rs.getReactionStep().getReactionParticipants();
			String allNames[] = new String[reactionParticipants.length];
			int allStoichs[] = new int[reactionParticipants.length];
			for (int k = 0; k < reactionParticipants.length; k++){
				allNames[k] = reactionParticipants[k].getName();
				if (reactionParticipants[k] instanceof cbit.vcell.model.Reactant){
					allStoichs[k] = -1*reactionParticipants[i].getStoichiometry();
				}else if (reactionParticipants[k] instanceof cbit.vcell.model.Product){
					allStoichs[k] =reactionParticipants[i].getStoichiometry();;
				}else if (reactionParticipants[k] instanceof cbit.vcell.model.Catalyst){
					allStoichs[k] = 0;
				}
			}
			cbit.vcell.model.Kinetics.KineticsParameter[] parameters = reactionSpecs[i].getReactionStep().getKinetics().getKineticsParameters();
			Expression[] equations = new Expression[parameters.length];
			for (int p = 0; p < parameters.length; p++){
				equations[p] = new Expression(parameters[p].getName()+" - ("+parameters[i].getExpression().infix()+")");
			}
			org.vcell.physics.component.Reaction reaction = new org.vcell.physics.component.Reaction(rs.getReactionStep().getName(),allNames,allStoichs,equations,rs.isFast());
			model.addModelComponent(reaction);
			org.vcell.physics.component.ModelComponent modelComponents[] = model.getModelComponents();
			for (int l = 0; l < modelComponents.length; l++){
				if (modelComponents[l] instanceof org.vcell.physics.component.Species){
					org.vcell.physics.component.Species species = (org.vcell.physics.component.Species)modelComponents[l];
					for (int k = 0; k < allNames.length; k++){
						if (species.getName().equals(allNames[k])){
							model.joinConnection(reaction.getConnectors(k),species.getConnectors(0));
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
public static Model createFromSimulationContext(cbit.vcell.mapping.SimulationContext simContext) throws ExpressionException, ImageException, GeometryException, PropertyVetoException {
	Model physicalModel = new Model();

	//
	// add locations to physicalModel
	// (names of locations always start with the structure name followed by an underscore ... e.g. "cytosol_0_1")
	//
	//addLocations(simContext,physicalModel);

	//addElectricalDevices(simContext,physicalModel);

	addChemicalDevices(simContext,physicalModel);

	//System.out.println("# locations is "+physicalModel.getLocations().length);
	System.out.println("# components is "+physicalModel.getModelComponents().length);
	System.out.println("# connections is "+physicalModel.getConnections().length);

	return physicalModel;
}
}
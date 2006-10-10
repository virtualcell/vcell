package cbit.vcell.modelapp.physics;

import java.beans.PropertyVetoException;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.physics.component.ModelComponent;
import org.vcell.physics.component.OOModel;
import org.vcell.physics.component.Reaction;
import org.vcell.physics.component.Species;

import cbit.util.TokenMangler;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.modelapp.ReactionSpec;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SpeciesContextSpec;

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
		//addLocations(simContext,physicalModel);

		//addElectricalDevices(simContext,physicalModel);

		PhysicsMapping.addChemicalDevices(simContext,physicalModel);

		//System.out.println("# locations is "+physicalModel.getLocations().length);
		System.out.println("# components is "+physicalModel.getModelComponents().length);
		System.out.println("# connections is "+physicalModel.getConnections().length);

		return physicalModel;
	}



	/**
	 * Insert the method's description here.
	 * Creation date: (1/12/2004 1:35:34 AM)
	 * @return ncbc_old.physics.component.PhysicalModel
	 */
	public static void addChemicalDevices(cbit.vcell.modelapp.SimulationContext simContext, org.vcell.physics.component.OOModel oOModel) throws org.vcell.expression.ExpressionException, java.beans.PropertyVetoException {
	
		cbit.vcell.model.Structure structures[] = simContext.getModel().getStructures();
	
		if (structures.length>1){
			throw new RuntimeException("doesn't currently support multiple compartments");
		}
	
		//
		// add Devices for molecular species to the physical model
		//
		cbit.vcell.modelapp.SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
		for (int i = 0; i < speciesContextSpecs.length; i++){
			cbit.vcell.modelapp.SpeciesContextSpec scs = speciesContextSpecs[i];
			org.vcell.physics.component.Species species = new org.vcell.physics.component.Species(scs.getSpeciesContext().getName());
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
				IExpression[] equations = new IExpression[parameters.length];
				for (int p = 0; p < parameters.length; p++){
					equations[p] = ExpressionFactory.createExpression(parameters[p].getName()+" - ("+parameters[p].getExpression().infix()+")");
				}
				org.vcell.physics.component.Reaction reaction = new org.vcell.physics.component.Reaction(
						TokenMangler.fixTokenStrict(rs.getReactionStep().getName()),
						allSpeciesNames,
						allStoichs,
						rs.getReactionStep().getKinetics().getRateParameter().getName(),
						equations);
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
		}
	}

}

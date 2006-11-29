package cbit.vcell.modelapp.physics;

import java.beans.PropertyVetoException;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;
import jscl.plugin.Variable;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionUtilities;
import org.vcell.physics.component.LumpedLocation;
import org.vcell.physics.component.OOModel;

import cbit.util.TokenMangler;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.modelapp.StructureMapping;

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
	public static void addChemicalDevices(cbit.vcell.modelapp.SimulationContext simContext, org.vcell.physics.component.OOModel oOModel) throws ExpressionException, java.beans.PropertyVetoException {
	
		cbit.vcell.model.Structure structures[] = simContext.getModel().getStructures();
	
		if (simContext.getGeometryContext().getGeometry().getDimension()>0){
			throw new RuntimeException("doesn't currently support spatial models");
		}
	
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
		}
	}

}

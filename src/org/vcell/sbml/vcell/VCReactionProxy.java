package org.vcell.sbml.vcell;

import java.beans.PropertyVetoException;
import java.util.Map;
import java.util.Map.Entry;

import org.vcell.util.BeanUtils;
import org.vcell.util.ProgrammingException;
import org.vcell.util.VCAssert;

import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import edu.uchc.connjur.wb.ExecutionTrace;


public abstract class VCReactionProxy {
	

	private VCReactionProxy() { }
	
	abstract void addReactants(Map<String,Integer> reactants) throws ModelException, PropertyVetoException;
	abstract void addProducts(Map<String,Integer> reactants) throws ModelException, PropertyVetoException;
	
	abstract Model getModel( );
	
	/**
	 * get proxy for Reaction type
	 * @param reaction not null
	 * @return proxy
	 * @throws ProgrammingException unknown type
	 */
	public static VCReactionProxy factory(ReactionStep reaction) {
		VCAssert.assertValid(reaction);
		SimpleReaction sr = BeanUtils.downcast(SimpleReaction.class,reaction);
		if (sr != null) {
			return new Simple(sr);
		}
		FluxReaction fr = BeanUtils.downcast(FluxReaction.class,reaction);
		if (fr != null) {
			return new Flux(fr);
		}
		throw new ProgrammingException("Unknown reaction type " + ExecutionTrace.justClassName(reaction) );
	}
	
	private static class Simple extends VCReactionProxy{
		final SimpleReaction simpleReaction;

		Simple(SimpleReaction simpleReaction) {
			super();
			this.simpleReaction = simpleReaction;
		}
		

		@Override
		Model getModel() {
			return simpleReaction.getModel();
		}

		@Override
		void addReactants(Map<String, Integer> reactants) throws ModelException, PropertyVetoException {
			final Model model = getModel();
			for (Entry<String, Integer> es : reactants.entrySet()) {
				SpeciesContext speciesContext = model.getSpeciesContext(es.getKey());
				int stoich = es.getValue(); 
				simpleReaction.addReactant(speciesContext, stoich);	
			}
		}


		@Override
		void addProducts(Map<String, Integer> reactants) throws ModelException, PropertyVetoException {
			final Model model = getModel();
			for (Entry<String, Integer> es : reactants.entrySet()) {
				SpeciesContext speciesContext = model.getSpeciesContext(es.getKey());
				int stoich = es.getValue(); 
				simpleReaction.addProduct(speciesContext, stoich);	
			}
		}
	}
	
	private static class Flux extends VCReactionProxy {
		final FluxReaction fluxReaction;

		Flux(FluxReaction fluxReaction) {
			super();
			this.fluxReaction = fluxReaction;
		}

		@Override
		Model getModel() {
			return fluxReaction.getModel(); 
		}

		@Override
		void addReactants(Map<String, Integer> reactants)
				throws ModelException, PropertyVetoException {
			final Model model = getModel();
			for (Entry<String, Integer> es : reactants.entrySet()) {
				SpeciesContext speciesContext = model.getSpeciesContext(es.getKey());
				System.out.println(speciesContext.getName());
			}
		}

		@Override
		void addProducts(Map<String, Integer> reactants) throws ModelException,
				PropertyVetoException {
			// TODO Auto-generated method stub
			
		}
	}
}

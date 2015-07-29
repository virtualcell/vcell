package cbit.vcell.mapping;

import java.beans.PropertyVetoException;

import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

public class DisableModelReductionReactionStep extends DummyReactionStep {

	public DisableModelReductionReactionStep(String name, Model model, Structure structure, SpeciesContext speciesContext) throws PropertyVetoException {
		super(name, model, structure, speciesContext);
	}

}

package cbit.vcell.mapping;

import cbit.vcell.model.SpeciesContext;

public class LangevinSpeciesContextSpec extends SpeciesContextSpec {

    public LangevinSpeciesContextSpec(SpeciesContextSpec speciesContextSpec, SimulationContext argSimulationContext) {
        super(speciesContextSpec, argSimulationContext);
    }

    public LangevinSpeciesContextSpec(SpeciesContext speciesContext, SimulationContext argSimulationContext) {
        super(speciesContext, argSimulationContext);
    }
}

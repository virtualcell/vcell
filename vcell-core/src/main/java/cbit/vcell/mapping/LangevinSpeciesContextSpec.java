package cbit.vcell.mapping;

import cbit.vcell.model.SpeciesContext;


@Deprecated
public class LangevinSpeciesContextSpec extends SpeciesContextSpec {

    private final SpeciesContextSpec theSpeciesContextSpec;

    public LangevinSpeciesContextSpec(SpeciesContextSpec speciesContextSpec, SimulationContext argSimulationContext) {
        super(speciesContextSpec, argSimulationContext);
        this.theSpeciesContextSpec = speciesContextSpec;
    }

    public SpeciesContextSpec getTheSpeciesContextSpec() {
        return theSpeciesContextSpec;
    }

}

package cbit.vcell.biomodel;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Equation;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.SubDomain;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

/**
 * Old flux steps written with Stoichiometry="0" on their reactant and product
 * (BioModelTransforms.fixLegacyFluxStoichiometry): restored to 1 when the stored math transports the
 * species through the flux, left at 0 when it does not.
 */
@Tag("Fast")
public class FluxStoichiometryRepairTest {

    private static BioModel load(String resource) throws Exception {
        try (InputStream in = FluxStoichiometryRepairTest.class.getResourceAsStream(resource)) {
            Assertions.assertNotNull(in, resource);
            return XmlHelper.XMLToBioModel(new XMLSource(new String(in.readAllBytes(), StandardCharsets.UTF_8)));
        }
    }

    private static int[] stoichiometries(BioModel bioModel, String flux) {
        FluxReaction reaction = (FluxReaction) bioModel.getModel().getReactionStep(flux);
        return Arrays.stream(reaction.getReactionParticipants()).mapToInt(ReactionParticipant::getStoichiometry).toArray();
    }

    @Test
    public void test_transported_flux_is_restored_and_moves_its_species() throws Exception {
        // Solver Suite 5.2: the stored math of 'non-spatial ODE' has RanC_cyt' = -J_r0 - KFlux_nm_cyt * J_flux0
        BioModel bioModel = load("biomodel_90938824_flux_zero_stoichiometry.vcml");
        Assertions.assertArrayEquals(new int[]{1, 1}, stoichiometries(bioModel, "flux0"));
        SimulationContext simulationContext = bioModel.getSimulationContext("non-spatial ODE");
        simulationContext.updateAll(false);
        MathDescription math = simulationContext.getMathDescription();
        boolean moves = false;
        for (SubDomain subDomain : Collections.list(math.getSubDomains())) {
            for (Equation equation : Collections.list(subDomain.getEquations())) {
                if (equation.getRateExpression() != null
                        && Arrays.asList(equation.getRateExpression().getSymbols()).contains("J_flux0")) {
                    moves = true;
                }
            }
        }
        Assertions.assertTrue(moves, "the regenerated math should transport RanC through flux0");
    }

    @Test
    public void test_untransported_flux_keeps_zero_stoichiometry() throws Exception {
        BioModel bioModel = load("biomodel_90948035_flux_zero_stoichiometry_unused.vcml");
        Assertions.assertArrayEquals(new int[]{0, 0}, stoichiometries(bioModel, "flux0"));
    }
}

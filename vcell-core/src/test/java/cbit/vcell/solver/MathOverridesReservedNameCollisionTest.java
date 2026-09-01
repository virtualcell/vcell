package cbit.vcell.solver;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A user's reaction parameter must not disappear from the math overrides table just because
 * its NAME contains the name of a reserved symbol.
 *
 * Moving_Boundary_Test_6_2 has five user-defined local reaction parameters, all plain numbers:
 * k, k1, kon_R_R_in, kon_U_u_m, kon_u_m_u_in. Math generation emits all five as Constants, so
 * all five belong in the table. Before the fix only four appeared: the filter matched reserved
 * names with String.contains(), and "kon_R_R_in" contains "_R_", the gas constant.
 *
 * Nothing about the model was unusual - any parameter named with an _R_, _T_, _F_ or _PI_ in
 * the middle of it hit this, silently and with no error.
 */
@Tag("Fast")
public class MathOverridesReservedNameCollisionTest {

    private static final List<String> USER_PARAMETERS =
            Arrays.asList("k", "k1", "kon_R_R_in", "kon_U_u_m", "kon_u_m_u_in");

    private static final List<String> RESERVED_THAT_MUST_STAY_HIDDEN =
            Arrays.asList("KMOLE", "_T_", "_F_", "_F_nmol_", "_N_pmol_", "_PI_", "_R_",
                    "_K_GHK_", "K_millivolts_per_volt");

    private static Simulation loadSimulation() throws Exception {
        try (InputStream is = MathOverridesReservedNameCollisionTest.class
                .getResourceAsStream("Moving_Boundary_Test_6_2.vcml")) {
            String vcml = IOUtils.toString(is, StandardCharsets.UTF_8);
            BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcml));
            SimulationContext simContext = bioModel.getSimulationContext(0);
            return simContext.getSimulations()[0];
        }
    }

    @Test
    public void userParametersAreOverridable() throws Exception {
        Simulation simulation = loadSimulation();
        List<String> all = Arrays.asList(simulation.getMathOverrides().getAllConstantNames());
        List<String> shown = Arrays.asList(simulation.getMathOverrides().getFilteredConstantNames());

        for (String p : USER_PARAMETERS) {
            assertTrue(all.contains(p),
                    "expected '" + p + "' to be emitted as a math Constant, but the math has: " + all);
            assertTrue(shown.contains(p),
                    "user parameter '" + p + "' is a math Constant but was filtered out of the "
                            + "math overrides table, so it cannot be overridden. Shown: " + shown);
        }
    }

    @Test
    public void reservedSymbolsAreStillHidden() throws Exception {
        Simulation simulation = loadSimulation();
        List<String> all = Arrays.asList(simulation.getMathOverrides().getAllConstantNames());
        List<String> shown = Arrays.asList(simulation.getMathOverrides().getFilteredConstantNames());

        for (String reserved : RESERVED_THAT_MUST_STAY_HIDDEN) {
            if (!all.contains(reserved)) continue;   // not every model uses every reserved symbol
            assertFalse(shown.contains(reserved),
                    "reserved symbol '" + reserved + "' must not be offered as overridable");
        }
        assertTrue(shown.stream().noneMatch(n -> n.startsWith("UnitFactor")),
                "unit conversion factors must not be offered as overridable: " + shown);
    }
}

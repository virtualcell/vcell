package cbit.vcell.biomodel;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Two membranes of an old document share one voltage name ("Voltage_Membrane0" for both "pm" and "nm"), which
 * current VCell rejects; loading repairs it (BioModelTransforms.fixDuplicateMembraneVoltageNames).
 */
@Tag("Fast")
public class DuplicateMembraneVoltageTest {

    private static BioModel load() throws Exception {
        try (InputStream in = DuplicateMembraneVoltageTest.class.getResourceAsStream("biomodel_94393958_duplicate_membrane_voltage.vcml")) {
            Assertions.assertNotNull(in);
            return XmlHelper.XMLToBioModel(new XMLSource(new String(in.readAllBytes(), StandardCharsets.UTF_8)));
        }
    }

    @Test
    public void test_later_membrane_gets_its_default_voltage_name() throws Exception {
        BioModel bioModel = load();
        Map<String, String> voltages = new LinkedHashMap<>();
        for (Structure structure : bioModel.getModel().getStructures()) {
            if (structure instanceof Membrane membrane) {
                voltages.put(membrane.getName(), membrane.getMembraneVoltage().getName());
            }
        }
        // the first membrane in model order keeps the shared name, the later one gets Voltage_<membrane>
        Assertions.assertEquals(Map.of("pm", "Voltage_Membrane0", "nm", "Voltage_nm"), voltages);
    }

    @Test
    public void test_model_has_no_symbol_clash_and_math_generates() throws Exception {
        BioModel bioModel = load();
        List<Issue> issues = new ArrayList<>();
        bioModel.getModel().gatherIssues(new IssueContext(), issues);
        for (Issue issue : issues) {
            Assertions.assertFalse(issue.getMessage().contains("is used within"), issue.getMessage());
        }
        for (SimulationContext simulationContext : bioModel.getSimulationContexts()) {
            simulationContext.updateAll(false);
            Assertions.assertNotNull(simulationContext.getMathDescription(), simulationContext.getName());
        }
    }
}

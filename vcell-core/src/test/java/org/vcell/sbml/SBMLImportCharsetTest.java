package org.vcell.sbml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sbml.vcell.SBMLImporter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies SBML import reads non-ASCII attribute values byte-for-byte from a
 * UTF-8 source. The two reaction-name patterns chosen here (en-dash U+2013 and
 * Greek mu U+03BC) are common in scientific notation and would mojibake under
 * the previous {@code Charset.defaultCharset()} read on a non-UTF-8 JVM.
 */
@Tag("Fast")
public class SBMLImportCharsetTest {

    private static class CapturingVCLogger extends VCLogger {
        @Override public boolean hasMessages() { return false; }
        @Override public void sendAllMessages() { }
        @Override public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException {
            if (p == Priority.HighPriority) {
                throw new VCLoggerException(p + " " + et + ": " + message);
            }
        }
    }

    private static final String SBML_WITH_NON_ASCII =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<sbml xmlns=\"http://www.sbml.org/sbml/level2/version4\" level=\"2\" version=\"4\">\n" +
            "  <model id=\"charsetTestModel\">\n" +
            "    <listOfCompartments>\n" +
            "      <compartment id=\"c1\" size=\"1.0\"/>\n" +
            "    </listOfCompartments>\n" +
            "    <listOfSpecies>\n" +
            "      <species id=\"s1\" name=\"μ-prot\" compartment=\"c1\" initialConcentration=\"1.0\"/>\n" +
            "    </listOfSpecies>\n" +
            "    <listOfReactions>\n" +
            "      <reaction id=\"r1\" name=\"k_14–3–3\">\n" +
            "        <listOfProducts>\n" +
            "          <speciesReference species=\"s1\"/>\n" +
            "        </listOfProducts>\n" +
            "        <kineticLaw>\n" +
            "          <math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
            "            <cn>1.0</cn>\n" +
            "          </math>\n" +
            "        </kineticLaw>\n" +
            "      </reaction>\n" +
            "    </listOfReactions>\n" +
            "  </model>\n" +
            "</sbml>\n";

    @Test
    public void importsUtf8ReactionAndSpeciesNames() throws Exception {
        Path tmp = Files.createTempFile("vcell-charset-test-", ".xml");
        try {
            Files.write(tmp, SBML_WITH_NON_ASCII.getBytes(StandardCharsets.UTF_8));

            SBMLImporter importer = new SBMLImporter(tmp.toAbsolutePath().toString(), new CapturingVCLogger(), false);
            BioModel bioModel = importer.getBioModel();
            assertNotNull(bioModel);

            ReactionStep r1 = null;
            for (ReactionStep rs : bioModel.getModel().getReactionSteps()) {
                if ("r1".equals(rs.getName())) { r1 = rs; break; }
            }
            assertNotNull(r1, "expected reaction with id 'r1' in imported model");
            assertEquals("k_14–3–3", r1.getSbmlName(),
                    "reaction sbmlName must preserve U+2013 EN DASH characters");

            SpeciesContext s1 = null;
            for (SpeciesContext sc : bioModel.getModel().getSpeciesContexts()) {
                if ("s1".equals(sc.getName())) { s1 = sc; break; }
            }
            assertNotNull(s1, "expected species with id 's1' in imported model");
            assertEquals("μ-prot", s1.getSbmlName(),
                    "species sbmlName must preserve U+03BC GREEK SMALL LETTER MU");
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void inputStreamPathPreservesUtf8() throws Exception {
        try (java.io.ByteArrayInputStream in =
                     new java.io.ByteArrayInputStream(SBML_WITH_NON_ASCII.getBytes(StandardCharsets.UTF_8))) {
            SBMLImporter importer = new SBMLImporter(in, new CapturingVCLogger(), false);
            BioModel bioModel = importer.getBioModel();
            assertNotNull(bioModel);

            ReactionStep r1 = null;
            for (ReactionStep rs : bioModel.getModel().getReactionSteps()) {
                if ("r1".equals(rs.getName())) { r1 = rs; break; }
            }
            assertNotNull(r1);
            assertEquals("k_14–3–3", r1.getSbmlName());
        }
    }
}

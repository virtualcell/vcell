package org.vcell.sbml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sbml.vcell.SBMLImporter;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Imports a COPASI-produced SBML document whose annotation block contains an element JSBML's
 * reader does not expect, and asserts it does not blow up. See issue #1461.
 *
 * <p>JSBML's {@code SBMLReader} assumed the element on its stack at the end of an annotation was
 * always an {@code Annotation} and cast it unconditionally. Some COPASI exports put something else
 * there -- an {@code XMLNode} -- and the import died with
 * {@code ClassCastException: class org.sbml.jsbml.xml.XMLNode cannot be cast to class
 * org.sbml.jsbml.Annotation}. The validator considers these documents well formed, so this is
 * JSBML's assumption being wrong, not the file being broken.
 *
 * <p>Fixed in our JSBML fork by skipping the element and logging instead of casting
 * (virtualcell/vcell-jsbml#5, shipped in 1.6.1-VCELL-4). The fork carries its own test, but that
 * one runs only when somebody runs Maven inside the fork by hand -- JitPack publishes with
 * {@code skipTests} and the fork has no CI. This test is the one that actually guards VCell,
 * because it runs here against whatever artifact {@code jsbml.version} resolves to. Drop
 * {@code jsbml.version} back to 1.6.1-VCELL and it fails.
 *
 * <p>The model is Ota 2015 (GDI-integrated), taken from the fork's test data. It is only a carrier
 * for the malformed annotation -- nothing about this model's biology matters here, so the
 * assertion is deliberately just "the import produced a BioModel".
 */
@Tag("Fast")
public class SBMLImportCopasiAnnotationTest {

    private static class FailOnHighPriority extends VCLogger {
        @Override public boolean hasMessages() { return false; }
        @Override public void sendAllMessages() { }
        @Override public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException {
            if (p == Priority.HighPriority) {
                throw new VCLoggerException(p + " " + et + ": " + message);
            }
        }
    }

    @Test
    public void importsCopasiModelWhoseAnnotationHoldsANonAnnotationElement() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("Ota2015_GDI-integrated.xml")) {
            assertNotNull(in, "test fixture Ota2015_GDI-integrated.xml is missing from the classpath");

            SBMLImporter importer = new SBMLImporter(in, new FailOnHighPriority(), false);
            BioModel bioModel = importer.getBioModel();

            assertNotNull(bioModel, "import returned no BioModel");
            assertNotNull(bioModel.getModel(), "imported BioModel has no Model");
        }
    }
}

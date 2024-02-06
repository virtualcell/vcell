package cbit.vcell.modeldb;

import cbit.image.ImageException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.*;
import cbit.vcell.math.MathDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.SolverException;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.vcell.model.rbm.MolecularComponentPattern;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static cbit.vcell.mapping.ReactionRuleSpec.Subtype.INCOMPATIBLE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vcell.util.document.BioModelChildSummary.MathType.SpringSaLaD;


public class SpeciesContextSpecTableTest {


//    private static String previousInstallDir = null;
//    @BeforeAll
//    public static void setup() {
//        previousInstallDir = PropertyLoader.getProperty(PropertyLoader.installationRoot, null);
//        PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
//    }
//
//    @AfterAll
//    public static void tearDown() {
//        if(previousInstallDir != null) {
//            PropertyLoader.setProperty(PropertyLoader.installationRoot, previousInstallDir);
//        }
//    }

    /* -------------------------------------------------------------------------------------------------
     * This test will exercise creation and parsing of a species context spec entry in the SQL table
     *
     */
	@Test
	public void test_springsalad_application() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException,
			ImageException, IllegalMappingException, MappingException, SolverException {

		BioModel bioModel = getBioModelFromResource("Spring_application.vcml");
		SimulationContext simContext = bioModel.getSimulationContext(0);

		// WARNING!! Debug configuration for this JUnit test required System property "vcell.installDir"
		// ex: -Dvcell.installDir=C:\dan\jprojects\git\vcell
		MathDescription mathDescription = simContext.getMathDescription();
        assertTrue((mathDescription.getMathType() != null && mathDescription.getMathType() == SpringSaLaD) ? true : false, "expecting SpringSaLaD math type");

		SpeciesContextSpec[] speciesContextSpecs = simContext.getReactionContext().getSpeciesContextSpecs();
		SpeciesContextSpec scs = speciesContextSpecs[0];		// we test roundtrip for just one SpeciesContextSpec
		String internalLinkSetSQL = SpeciesContextSpecTable.getInternalLinksSQL(scs);
		String siteAttributesMapSQL = SpeciesContextSpecTable.getSiteAttributesSQL(scs);
		Set<MolecularInternalLinkSpec> internalLinkSet = SpeciesContextSpecTable.readInternalLinksSQL(scs, internalLinkSetSQL);
		Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap = SpeciesContextSpecTable.readSiteAttributesSQL(scs, siteAttributesMapSQL);

		double bondLength = 1;
		Map<ReactionRuleSpec.Subtype, Integer> subTypeMap = new LinkedHashMap<>();
		ReactionRuleSpec[] rrSpecs = simContext.getReactionContext().getReactionRuleSpecs();
		for(ReactionRuleSpec rrs : rrSpecs) {
			Map<String, Object> analysisResults = new LinkedHashMap<>();
			rrs.analizeReaction(analysisResults);
			ReactionRuleSpec.Subtype st = rrs.getSubtype(analysisResults);
			if(subTypeMap.containsKey(st)) {
				int count = subTypeMap.get(st);
				count++;
				subTypeMap.put(st, count);
			} else {
				subTypeMap.put(st, 1);
			}
			if(ReactionRuleSpec.Subtype.BINDING == st) {
				bondLength = rrs.getFieldBondLength();
			}
		}
        assertTrue(subTypeMap.size() == 5 ? true : false, "Number of compatible subtypes must be 5");
        assertTrue(subTypeMap.containsKey(INCOMPATIBLE) ? false : true, "No incompatible subtype may exist");
        assertTrue(bondLength == 3 ? true : false, "BondLength must be 3");

		// verify roundtrip for internalLinkSet (through sampling)
        assertTrue(internalLinkSet.size() == scs.getInternalLinkSet().size() ? true : false, "internalLinkSet size different after roundtrip");
		MolecularInternalLinkSpec milsThis = internalLinkSet.iterator().next();
		boolean found = false;
		for(MolecularInternalLinkSpec milsThat : scs.getInternalLinkSet()) {
			if(milsThis.compareEqual(milsThat)) {
				found = true;
				break;
			}
		}
        assertTrue(found ? true : false, "MolecularInternalLinkSpec element not found after roundtrip");

		// verify roundtrip for siteAttributesMap (through sampling)
        assertTrue(siteAttributesMap.size() == scs.getSiteAttributesMap().size() ? true : false, "siteAttributesMap size different after roundtrip");
		MolecularComponentPattern mcpThis = siteAttributesMap.keySet().iterator().next();
		SiteAttributesSpec sasThis = siteAttributesMap.get(mcpThis);
		SiteAttributesSpec sasThat = scs.getSiteAttributesMap().get(mcpThis);
        assertTrue(sasThis.compareEqual(sasThat) ? true : false, "SiteAttributesSpec element not found in siteAttributesMap after roundtrip");
	}


    // ==========================================================================================================================

    private static BioModel getBioModelFromResource(String fileName) throws IOException, XmlParseException {
        InputStream inputStream = SpeciesContextSpecTableTest.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return XmlHelper.XMLToBioModel(new XMLSource(vcml));
        }
    }
//    private static MathModel getMathModelFromResource(String fileName) throws IOException, XmlParseException {
//        InputStream inputStream = SpeciesContextSpecTableTest.class.getResourceAsStream(fileName);
//        if (inputStream == null) {
//            throw new FileNotFoundException("file not found! " + fileName);
//        } else {
//            String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//            return XmlHelper.XMLToMathModel(new XMLSource(vcml));
//        }
//    }
//
//    public static int checkIssuesBySeverity(Vector<Issue> issueList, Issue.Severity severity) {
//        int counter = 0;
//        for (Issue issue : issueList) {
//            if (severity == issue.getSeverity()) {
//                counter++;
//            }
//        }
//        return counter;
//    }

}

package org.vcell.sbml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Spatial stochastic applications (PDE/particle hybrids) in SBML Spatial: each species carries its representation,
 * {@code <vcell:SpeciesContextSpecSettings vcell:representation="continuous|particle"/>}, and continuous is the
 * default when the annotation is absent.
 */
@Tag("Fast")
public class SBMLSpatialHybridTest {

	@BeforeAll
	public static void before() {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
	}

	private static String readResource(String path) throws Exception {
		try (InputStream is = SBMLSpatialHybridTest.class.getResourceAsStream(path)) {
			Assertions.assertNotNull(is, "missing test resource " + path);
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private static SpeciesContextSpec spec(SimulationContext simContext, String speciesName) {
		return simContext.getReactionContext().getSpeciesContextSpec(simContext.getModel().getSpeciesContext(speciesName));
	}

	/**
	 * A hybrid application (A particles, B a continuous field; A_p -> B and B -> A_p) exports with an explicit
	 * representation per species and imports back as the same hybrid, with equivalent math.
	 */
	@Test
	public void testHybridRoundTrip() throws Exception {
		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(readResource("/sbmlspatial/hybrid_two_way_exchange.vcml")));
		bioModel.updateAll(false);
		SimulationContext original = bioModel.getSimulationContext(0);
		Assertions.assertTrue(original.isStoch() && original.getGeometry().getDimension() > 0);
		Assertions.assertFalse(spec(original, "A").isForceContinuous());
		Assertions.assertTrue(spec(original, "B").isForceContinuous());

		BioModel bioModelSBML = ModelUnitConverter.createBioModelWithSBMLUnitSystem(bioModel);
		bioModelSBML.updateAll(false);
		SBMLExporter exporter = new SBMLExporter(bioModelSBML.getSimulationContext(0), 3, 1, false);
		String sbml = exporter.getSBMLString();
		Assertions.assertTrue(sbml.contains("representation=\"particle\""), "particle species A not annotated");
		Assertions.assertTrue(sbml.contains("representation=\"continuous\""), "continuous species B not annotated");

		SBMLImporter importer = new SBMLImporter(new ByteArrayInputStream(sbml.getBytes(StandardCharsets.UTF_8)),
				new SBMLExporter.MemoryVCLogger(), true);
		BioModel roundTrip = importer.getBioModel();
		roundTrip.updateAll(false);
		SimulationContext imported = roundTrip.getSimulationContext(0);
		Assertions.assertEquals(SimulationContext.Application.NETWORK_STOCHASTIC, imported.getApplicationType());
		Assertions.assertEquals(3, imported.getGeometry().getDimension());
		Assertions.assertFalse(spec(imported, "A").isForceContinuous(), "A should import as particles");
		Assertions.assertTrue(spec(imported, "B").isForceContinuous(), "B should import as continuous");

		MathDescription math = imported.getMathDescription();
		Assertions.assertTrue(math.getVariable("A") instanceof ParticleVariable, "A is not a particle variable");
		Assertions.assertTrue(math.getVariable("B") instanceof VolVariable, "B is not a continuous variable");
		MathCompareResults equivalent = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),
				bioModelSBML.getSimulationContext(0).getMathDescription(), math);
		Assertions.assertTrue(equivalent.isEquivalent(), "math descriptions didn't match: " + equivalent.toDatabaseStatus());
	}

	/**
	 * Without representation annotations, a spatial model imports as a deterministic application, as before.
	 */
	@Test
	public void testDefaultIsContinuous() throws Exception {
		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(readResource("/sbmlspatial/hybrid_two_way_exchange.vcml")));
		bioModel.updateAll(false);
		BioModel bioModelSBML = ModelUnitConverter.createBioModelWithSBMLUnitSystem(bioModel);
		bioModelSBML.updateAll(false);
		String sbml = new SBMLExporter(bioModelSBML.getSimulationContext(0), 3, 1, false).getSBMLString();
		String plain = sbml.replaceAll("\\s*<vcell:SpeciesContextSpecSettings[^>]*/>", "");
		Assertions.assertFalse(plain.contains("representation="));

		BioModel imported = new SBMLImporter(new ByteArrayInputStream(plain.getBytes(StandardCharsets.UTF_8)),
				new SBMLExporter.MemoryVCLogger(), true).getBioModel();
		imported.updateAll(false);
		SimulationContext simContext = imported.getSimulationContext(0);
		Assertions.assertEquals(SimulationContext.Application.NETWORK_DETERMINISTIC, simContext.getApplicationType());
		Assertions.assertEquals(3, simContext.getGeometry().getDimension());
	}
}

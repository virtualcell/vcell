package cbit.vcell.math;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The {@code Dilution} construct of a moving membrane (vcell-fenics ADR 014): its species are densities carried
 * by the membrane's velocity, diluting as it stretches. The fixture is the public BioModel 294599465
 * ("moving_bleb"), whose moving membrane carries species; its stored math predates the construct.
 */
@Tag("Fast")
public class MembraneDilutionTest {

	private static final String RESOURCE = "moving_bleb_biomodel_294599465.vcml";

	private static SimulationContext movingApplication() throws Exception {
		String vcml;
		try (InputStream in = MembraneDilutionTest.class.getResourceAsStream(RESOURCE)) {
			assertNotNull(in, RESOURCE);
			vcml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcml));
		return bioModel.getSimulationContext("moving_bleb");
	}

	private static List<MembraneSubDomain> membranes(MathDescription math) {
		List<MembraneSubDomain> membranes = new ArrayList<>();
		for (SubDomain subDomain : Collections.list(math.getSubDomains())) {
			if (subDomain instanceof MembraneSubDomain membrane) {
				membranes.add(membrane);
			}
		}
		return membranes;
	}

	private static MembraneSubDomain movingWithSpecies(MathDescription math) {
		return membranes(math).stream()
				.filter(m -> m.isMoving() && !m.getEquationCollection().isEmpty())
				.findFirst().orElseThrow();
	}

	@Test
	public void testTheGeneratedMathStatesDilutionForAMovingMembraneWithSpecies() throws Exception {
		SimulationContext application = movingApplication();
		MathDescription stored = application.getMathDescription();
		assertFalse(movingWithSpecies(stored).isDilution(), "the stored math predates the construct");

		MathDescription generated = application.createNewMathMapping().getMathDescription();
		assertTrue(movingWithSpecies(generated).isDilution());
		for (MembraneSubDomain membrane : membranes(generated)) {
			// stated only where it matters: every other membrane's math is unchanged
			assertEquals(membrane.isMoving() && !membrane.getEquationCollection().isEmpty(), membrane.isDilution(),
					membrane.getName());
		}
	}

	@Test
	public void testDilutionSurvivesTheXmlAndVcmlRoundTripsAndIsCompared() throws Exception {
		SimulationContext application = movingApplication();
		MathDescription generated = application.createNewMathMapping().getMathDescription();
		application.setMathDescription(generated);
		assertTrue(XmlHelper.bioModelToXML(application.getBioModel()).contains("Dilution=\"true\""), "XML writer");
		boolean cloneUsingXml = XmlHelper.cloneUsingXML;
		XmlHelper.cloneUsingXML = true; // clone through Xmlproducer -> XmlReader, not Java serialization
		MathDescription viaXml;
		try {
			viaXml = XmlHelper.cloneMathDescription(generated);
		} finally {
			XmlHelper.cloneUsingXML = cloneUsingXml;
		}
		assertTrue(movingWithSpecies(viaXml).isDilution(), "XML");

		String vcml = generated.getVCML_database();
		assertTrue(vcml.contains(VCML.Dilution), "VCML");
		MathDescription viaVcml = MathDescription.fromEditor(generated, vcml);
		assertTrue(movingWithSpecies(viaVcml).isDilution(), "VCML");

		assertTrue(generated.compareEqual(viaXml));
		movingWithSpecies(viaXml).setDilution(false);
		assertFalse(generated.compareEqual(viaXml), "a difference in Dilution alone is a difference in the math");
	}

	@Test
	public void testTheMovingBoundarySolverRefusesSpeciesOnTheMovingMembrane() throws Exception {
		// it has no membrane state: it would drop the membrane species and their fluxes (vcell-mbsolver#35)
		SimulationContext application = movingApplication();
		application.setMathDescription(application.createNewMathMapping().getMathDescription());
		Simulation simulation = new Simulation(application.getMathDescription(), application);
		simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.MovingBoundary);
		List<Issue> issues = new ArrayList<>();
		simulation.gatherIssues(new IssueContext(), issues);
		assertTrue(issues.stream().anyMatch(i -> i.getCategory() == Issue.IssueCategory.MovingBoundary_MembraneSpecies_NotSupported
				&& i.getSeverity() == Issue.Severity.ERROR), issues.toString());
		// and its image geometry, which the solver's input writer refuses, is an issue before the run too
		assertTrue(issues.stream().anyMatch(i -> i.getCategory() == Issue.IssueCategory.MovingBoundary_Geometry_NotSupported
				&& i.getMessage().contains("image-based") && i.getSeverity() == Issue.Severity.ERROR), issues.toString());
	}
}

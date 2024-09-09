package cbit.vcell.biomodel;

import cbit.image.ImageException;
import cbit.vcell.geometry.*;
import cbit.vcell.mapping.*;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.math.*;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.*;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.solver.langevin.LangevinLngvWriter;
import org.vcell.solver.langevin.LangevinSolver;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.User;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static cbit.vcell.mapping.ReactionRuleSpec.Subtype.INCOMPATIBLE;
import static cbit.vcell.mapping.SimulationContext.Application.SPRINGSALAD;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vcell.util.document.BioModelChildSummary.MathType.SpringSaLaD;

@Tag("Fast")
public class SpringSaLaDGoodReactionsTest {
	
	private static final String reactionTestString = "'r0' ::     'MT0' : 'Site1' : 'state0' --> 'state1'  Rate 50.0  Condition Free";
	private static final String L_x = "L_x: 0.1";
	private static final String molecule = "MOLECULE: \"MT0\" Intracellular Number 10 Site_Types 2 Total_Sites 2 Total_Links 1 is2D false";
	private static final String analyticExpressionIntra = "(z < 0.09)";

	private static String previousInstallDir = null;
	@BeforeAll
	public static void setup() {
		previousInstallDir = PropertyLoader.getProperty(PropertyLoader.installationRoot, null);
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
	}

	@AfterAll
	public static void tearDown() {
		if(previousInstallDir != null) {
			PropertyLoader.setProperty(PropertyLoader.installationRoot, previousInstallDir);
		}
	}

	/* ---------------------------------------------------------------------------------------------------------------------------
	 * This test will check the compatibility with springsalad requirements as follows:
	 * - compartment number and name
	 * - seed species and their molecules
	 */
	@Test
	public void test_springsalad_model_bad() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException {
		
//		System.out.println("start springsalad test for compartments, seed species and molecules");
		BioModel bioModel = getBioModelFromResource("Spring_model_bad.vcml");
        assertTrue(bioModel != null ? true : false, "expecting non-null biomodel");
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
        assertTrue(simContext != null ? true : false, "expecting non-null simulation context");
		
		Application appType = simContext.getApplicationType();
        assertTrue((appType != null && appType == SPRINGSALAD) ? true : false, "expecting SPRINGSALAD application type");

		// we want to delete the link between the sites of this species and test that the corresponding issue is triggered
		// after this change we should have 8 warning issues (7 otherwise)
		SpeciesContext scCandidate = simContext.getModel().getSpeciesContext("MT2");
		SpeciesContextSpec scsCandidate = simContext.getReactionContext().getSpeciesContextSpec(scCandidate);
		Set<MolecularInternalLinkSpec> internalLinkSet = scsCandidate.getInternalLinkSet();
		int size = internalLinkSet.size();
        assertTrue((internalLinkSet.size() == 1) ? true : false, "expecting one link for species 'MT2'");
		internalLinkSet.clear();
		
		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simContext.getModel().gatherIssues(issueContext, issueList);
		simContext.gatherIssues(issueContext, issueList, true);		// bIgnoreMathDescription == true
		int numErrors = checkIssuesBySeverity(issueList, Issue.Severity.ERROR);
		int numWarnings = checkIssuesBySeverity(issueList, Issue.Severity.WARNING);
        assertTrue((numErrors == 2 && numWarnings == 8) ? true : false, "expecting 2 errors and 8 warnings for this model");

		/*		We should detect the following:
		
		c0:		SpringSaLaD requires exactly 3 Structures: one Membrane and two Features (Compartments)
		c0:		'c0' not legal identifier for SpringSaLaD applications. Try using 'Intracellular' or 'Extracellular'.
					(Structure.SpringStructureEnum.Intracellular, Structure.SpringStructureEnum.Extracellular)
		s0:		SpringSaLaD requires the MolecularType to have at least one Site.
		s1:		Each Site must have at least one State.
		s2:		Internal Links are possible only when the Molecule has at least 2 sites.
		s2:		The Species and the Molecular Type must share the same name.
		MT2:	Link chain within the molecule has at least one discontinuity.
		Sink:	SpringSaLaD reserved Molecules 'Source' and 'Sink' must not have any sites defined
		s5:		There must be a biunivocal correspondence between the Species and the associated MolecularType.
		s6:		There must be a biunivocal correspondence between the Species and the associated MolecularType.
		
			TODO: we may consider initializing all the issue string as static named variable and compare identity, but it will probably be overkill
			TODO: add membrane molecules and test for Anchor; also test for Anchor missing in non-membrane molecules
		*/
	}

	/* -------------------------------------------------------------------------------------------------------------------------
	 * This test exercises a complete set of springsalad-incompatible reactions
	 * All the reactions in this example should be marked as incompatible, either Error or Warning issues.
	 */
	@Test
	public void test_springsalad_bad_reactions() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException {
		
//		System.out.println("start springsalad test for incompatible reactions");
		BioModel bioModel = getBioModelFromResource("Spring_reactions_bad.vcml");
        assertTrue(bioModel != null ? true : false, "expecting non-null biomodel");
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
        assertTrue(simContext != null ? true : false, "expecting non-null simulation context");
		
		Application appType = simContext.getApplicationType();
        assertTrue((appType != null && appType == SPRINGSALAD) ? true : false, "expecting SPRINGSALAD application type");
		
		Geometry geometry = simContext.getGeometry();
        assertTrue(geometry.getDimension() == 3 ? true : false, "expecting 3D geometry");

		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simContext.getModel().gatherIssues(issueContext, issueList);
		simContext.gatherIssues(issueContext, issueList, true);		// bIgnoreMathDescription == true
		int numErrors = checkIssuesBySeverity(issueList, Issue.Severity.ERROR);
		int numWarnings = checkIssuesBySeverity(issueList, Issue.Severity.WARNING);
        assertTrue((numErrors == 0 && numWarnings == 14) ? true : false, "expecting 1 errors and 14 warning issues");
	}
	
	/* -------------------------------------------------------------------------------------------------------------------------
	 * This test exercises a complete set of springsalad-compatible reactions and math generation
	 * All the reactions in this example should be marked as valid, there should be no error or warning issues whatsoever.
	 * Math generation should succeed.
	 */
	@Test
	public void test_springsalad_good_reactions() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException {
		
//		System.out.println("start springsalad test for all compatible reactions");
		BioModel bioModel = getBioModelFromResource("Spring_reactions_good.vcml");
        assertTrue(bioModel != null ? true : false, "expecting non-null biomodel");
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
        assertTrue(simContext != null ? true : false, "expecting non-null simulation context");
		
		Application appType = simContext.getApplicationType();
        assertTrue((appType != null && appType == SPRINGSALAD) ? true : false, "expecting SPRINGSALAD application type");
		
		Geometry geometry = simContext.getGeometry();
        assertTrue(geometry.getDimension() == 3 ? true : false, "expecting 3D geometry");

		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simContext.getModel().gatherIssues(issueContext, issueList);
		simContext.gatherIssues(issueContext, issueList, true);		// bIgnoreMathDescription == true
		int numErrors = checkIssuesBySeverity(issueList, Issue.Severity.ERROR);
		int numWarnings = checkIssuesBySeverity(issueList, Issue.Severity.WARNING);
        assertTrue((numErrors == 0 && numWarnings == 0) ? true : false, "expecting no Application error/warning issues");
		
		// WARNING!! Debug configuration for this JUnit test required System property "vcell.installDir"
		// ex: -Dvcell.installDir=C:\dan\jprojects\git\vcell
		bioModel.updateAll(false);
		MathDescription mathDescription = simContext.getMathDescription();
		List<ParticleMolecularType> particleMolecularTypes = mathDescription.getParticleMolecularTypes();
		int count = 0;
		for(ParticleMolecularType pmt : particleMolecularTypes) {
			if(pmt instanceof LangevinParticleMolecularType) {
				count++;
			}
		}
        assertTrue(count == 4 ? true : false, "expecting 4 LangevinParticleMolecularType entities");
		
		CompartmentSubDomain compartmentSubDomain = mathDescription.getCompartmentSubDomain(String.valueOf(Structure.SpringStructureEnum.Intracellular));
		List<ParticleJumpProcess> particleJumpProcesses = compartmentSubDomain.getParticleJumpProcesses();
		List<ReactionRule> reactionRuleList = bioModel.getModel().getRbmModelContainer().getReactionRuleList();
        assertTrue(reactionRuleList.size() == 9 ? true : false, "expecting 9 ReactionRule entities");
        assertTrue(particleJumpProcesses.size() == 10 ? true : false, "expecting 10 ParticleJumpProcess entities");
		
		MathType mathType = mathDescription.getMathType();
        assertTrue((mathType != null && mathType == SpringSaLaD) ? true : false, "expecting SpringSaLaD math type");
	}
	
	/* ------------------------------------------------------------------------------------------------------------------------------
	 * This will construct and initialize a simulation based on the langevin solver and create the input file for running the solver. 
	 * At a later date we'd want to run the solver and compare the results against some expected result set.
	 */
	@Test
	public void test_springsalad_simple_simulation() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException, SolverException {
		
		BioModel bioModel = getBioModelFromResource("Spring_transition_free.vcml");
        assertTrue(bioModel != null ? true : false, "expecting non-null biomodel");
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
        assertTrue(simContext != null ? true : false, "expecting non-null simulation context");
		
		Application appType = simContext.getApplicationType();
        assertTrue((appType != null && appType == SPRINGSALAD) ? true : false, "expecting SPRINGSALAD application type");
		
		Geometry geometry = simContext.getGeometry();
        assertTrue(geometry.getDimension() == 3 ? true : false, "expecting 3D geometry");

		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simContext.getModel().gatherIssues(issueContext, issueList);
		simContext.gatherIssues(issueContext, issueList, true);		// bIgnoreMathDescription == true
		int numErrors = checkIssuesBySeverity(issueList, Issue.Severity.ERROR);
		int numWarnings = checkIssuesBySeverity(issueList, Issue.Severity.WARNING);
        assertTrue((numErrors == 0 && numWarnings == 0) ? true : false, "expecting no Application error/warning issues");
		
		// WARNING!! Debug configuration for this JUnit test required System property "vcell.installDir"
		// ex: -Dvcell.installDir=C:\dan\jprojects\git\vcell
		bioModel.updateAll(false);		// this call generates math
		MathDescription mathDescription = simContext.getMathDescription();
        assertTrue((mathDescription.getMathType() != null && mathDescription.getMathType() == SpringSaLaD) ? true : false, "expecting SpringSaLaD math type");

		
		// -------------------------------------------------------------------------------
		Simulation simulation = new Simulation(mathDescription, simContext);
		simContext.addSimulation(simulation);
		
		File localSimDataDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());	

		SimulationJob simJob = new SimulationJob(simulation, 0, null);
		SimulationTask simTask = new SimulationTask(simJob, 0);
		
		
		SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
        assertTrue((solverDescription != null) ? true : false, "expecting non-null SolverDescription");
        assertTrue(("Langevin".equals(solverDescription.getDatabaseName())) ? true : false, "expecting databese name 'Langevin'");

		// generate the input file for the solver and validate it
		int randomSeed = 0;
		String langevinLngvString = null;
		langevinLngvString = LangevinLngvWriter.writeLangevinLngv(simTask.getSimulation(), randomSeed);
        assertTrue((langevinLngvString != null) ? true : false, "expecting non-null solver input string");
        assertTrue((langevinLngvString.contains(reactionTestString)) ? true : false, "expecting properly formatted transition reaction");
		
		SolverUtilities.prepareSolverExecutable(solverDescription);	
		// create solver from SolverFactory
		Solver solver = SolverFactory.createSolver(localSimDataDir, simTask, false);
        assertTrue((solver instanceof LangevinSolver) ? true : false, "expecting instanceof Langevin solver");
	}


	/* -------------------------------------------------------------------------------------------------
	 * This test will exercise loading a springsalad mathmodel
	 * and producing a langevin input file as string
	 * and validating its content
	 *
	 */
	@Test
	public void test_springsalad_mathmodel() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException,
			ImageException, IllegalMappingException, MappingException, SolverException {

		MathModel mathModel = getMathModelFromResource("Spring_simulation_transition.vcml");
		MathDescription mathDescription = mathModel.getMathDescription();
        assertTrue(mathDescription.isLangevin() ? true : false, "MathDescription must be Langevin");


		Simulation simulation = mathModel.getSimulations()[0];
		Geometry geometry = simulation.getMathDescription().getGeometry();
		GeometrySpec geometrySpec = geometry.getGeometrySpec();
        assertTrue(geometrySpec.getDimension() == 3 ? true : false, "GeometrySpec must be 3D");

		//		LangevinSimulationOptions lso = simulation.getSolverTaskDescription().getLangevinSimulationOptions();
		int randomSeed = 0;
		String lngvString = LangevinLngvWriter.writeLangevinLngv(simulation, randomSeed);
        assertTrue(lngvString.contains(L_x) ? true : false, "Default Lx must be 100 nm");
        assertTrue(lngvString.contains(molecule) ? true : false, "Molecule must match the saved string pattern");

		for(SubVolume subVolume : geometrySpec.getSubVolumes()) {
            assertTrue(subVolume instanceof AnalyticSubVolume ? true : false, "SpringSaLaD requires Analytic geometry");
			AnalyticSubVolume analyticSubvolume = (AnalyticSubVolume)subVolume;
			if (analyticSubvolume.getName().equals(String.valueOf(Structure.SpringStructureEnum.Intracellular))) {
				var expression = analyticSubvolume.getExpression();
				String exp = expression.infix();
                assertTrue(analyticExpressionIntra.equals(exp) ? true : false, "Analytic geometry expression must match the saved string pattern");
			}
		}
	}

		// ==========================================================================================================================

	private static BioModel getBioModelFromResource(String fileName) throws IOException, XmlParseException {
		InputStream inputStream = SpringSaLaDGoodReactionsTest.class.getResourceAsStream(fileName);
		if (inputStream == null) {
			throw new FileNotFoundException("file not found! " + fileName);
		} else {
			String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			return XmlHelper.XMLToBioModel(new XMLSource(vcml));
		}
	}
	private static MathModel getMathModelFromResource(String fileName) throws IOException, XmlParseException {
		InputStream inputStream = SpringSaLaDGoodReactionsTest.class.getResourceAsStream(fileName);
		if (inputStream == null) {
			throw new FileNotFoundException("file not found! " + fileName);
		} else {
			String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			return XmlHelper.XMLToMathModel(new XMLSource(vcml));
		}
	}

	public static int checkIssuesBySeverity(Vector<Issue> issueList, Issue.Severity severity) {
		int counter = 0;
		for (Issue issue : issueList) {
			if (severity == issue.getSeverity()) {
				counter++;
			}
		}
		return counter;
	}
}

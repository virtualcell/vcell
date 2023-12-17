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
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Tag;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.solver.langevin.LangevinLngvWriter;
import org.vcell.solver.langevin.LangevinSolver;
import org.vcell.test.Fast;
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

@Category(Fast.class)
@Tag("Fast")
public class SpringSaLaDGoodReactionsTest {
	
	private static final String reactionTestString = "'r0' ::     'MT0' : 'Site1' : 'state0' --> 'state1'  Rate 50.0  Condition Free";
	private static final String L_x = "L_x: 0.1";
	private static final String molecule = "MOLECULE: \"MT0\" Intracellular Number 10 Site_Types 2 Total_Sites 2 Total_Links 1 is2D true";
	private static final String analyticExpressionIntra = "(z < 0.09)";

	private static String previousInstallDir = null;
	@BeforeClass
	public static void setup() {
		previousInstallDir = PropertyLoader.getProperty(PropertyLoader.installationRoot, null);
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
	}

	@AfterClass
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
		Assert.assertTrue("expecting non-null biomodel", bioModel != null ? true : false);
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
		Assert.assertTrue("expecting non-null simulation context", simContext != null ? true : false);
		
		Application appType = simContext.getApplicationType();
		Assert.assertTrue("expecting SPRINGSALAD application type", (appType != null && appType == Application.SPRINGSALAD) ? true : false);

		// we want to delete the link between the sites of this species and test that the corresponding issue is triggered
		// after this change we should have 8 warning issues (7 otherwise)
		SpeciesContext scCandidate = simContext.getModel().getSpeciesContext("MT2");
		SpeciesContextSpec scsCandidate = simContext.getReactionContext().getSpeciesContextSpec(scCandidate);
		Set<MolecularInternalLinkSpec> internalLinkSet = scsCandidate.getInternalLinkSet();
		int size = internalLinkSet.size();
		Assert.assertTrue("expecting one link for species 'MT2'", (internalLinkSet.size() == 1) ? true : false);
		internalLinkSet.clear();
		
		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simContext.getModel().gatherIssues(issueContext, issueList);
		simContext.gatherIssues(issueContext, issueList, true);		// bIgnoreMathDescription == true
		int numErrors = checkIssuesBySeverity(issueList, Issue.Severity.ERROR);
		int numWarnings = checkIssuesBySeverity(issueList, Issue.Severity.WARNING);
		Assert.assertTrue("expecting 2 errors and 8 warnings for this model", (numErrors == 2 && numWarnings == 8) ? true : false);

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
		Assert.assertTrue("expecting non-null biomodel", bioModel != null ? true : false);
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
		Assert.assertTrue("expecting non-null simulation context", simContext != null ? true : false);
		
		Application appType = simContext.getApplicationType();
		Assert.assertTrue("expecting SPRINGSALAD application type", (appType != null && appType == Application.SPRINGSALAD) ? true : false);
		
		Geometry geometry = simContext.getGeometry();
		Assert.assertTrue("expecting 3D geometry", geometry.getDimension() == 3 ? true : false);

		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simContext.getModel().gatherIssues(issueContext, issueList);
		simContext.gatherIssues(issueContext, issueList, true);		// bIgnoreMathDescription == true
		int numErrors = checkIssuesBySeverity(issueList, Issue.Severity.ERROR);
		int numWarnings = checkIssuesBySeverity(issueList, Issue.Severity.WARNING);
		Assert.assertTrue("expecting 1 errors and 14 warning issues", (numErrors == 1 && numWarnings == 14) ? true : false);
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
		Assert.assertTrue("expecting non-null biomodel", bioModel != null ? true : false);
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
		Assert.assertTrue("expecting non-null simulation context", simContext != null ? true : false);
		
		Application appType = simContext.getApplicationType();
		Assert.assertTrue("expecting SPRINGSALAD application type", (appType != null && appType == Application.SPRINGSALAD) ? true : false);
		
		Geometry geometry = simContext.getGeometry();
		Assert.assertTrue("expecting 3D geometry", geometry.getDimension() == 3 ? true : false);

		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simContext.getModel().gatherIssues(issueContext, issueList);
		simContext.gatherIssues(issueContext, issueList, true);		// bIgnoreMathDescription == true
		int numErrors = checkIssuesBySeverity(issueList, Issue.Severity.ERROR);
		int numWarnings = checkIssuesBySeverity(issueList, Issue.Severity.WARNING);
		Assert.assertTrue("expecting no Application error/warning issues", (numErrors == 0 && numWarnings == 0) ? true : false);
		
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
		Assert.assertTrue("expecting 4 LangevinParticleMolecularType entities", count == 4 ? true : false);
		
		CompartmentSubDomain compartmentSubDomain = mathDescription.getCompartmentSubDomain(String.valueOf(Structure.SpringStructureEnum.Intracellular));
		List<ParticleJumpProcess> particleJumpProcesses = compartmentSubDomain.getParticleJumpProcesses();
		List<ReactionRule> reactionRuleList = bioModel.getModel().getRbmModelContainer().getReactionRuleList();
		Assert.assertTrue("expecting 9 ReactionRule entities", reactionRuleList.size() == 9 ? true : false);
		Assert.assertTrue("expecting 10 ParticleJumpProcess entities", particleJumpProcesses.size() == 10 ? true : false);
		
		MathType mathType = mathDescription.getMathType();
		Assert.assertTrue("expecting SpringSaLaD math type", (mathType != null && mathType == MathType.SpringSaLaD) ? true : false);
	}
	
	/* ------------------------------------------------------------------------------------------------------------------------------
	 * This will construct and initialize a simulation based on the langevin solver and create the input file for running the solver. 
	 * At a later date we'd want to run the solver and compare the results against some expected result set.
	 */
	@Test
	public void test_springsalad_simple_simulation() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException, SolverException {
		
		BioModel bioModel = getBioModelFromResource("Spring_transition_free.vcml");
		Assert.assertTrue("expecting non-null biomodel", bioModel != null ? true : false);
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
		Assert.assertTrue("expecting non-null simulation context", simContext != null ? true : false);
		
		Application appType = simContext.getApplicationType();
		Assert.assertTrue("expecting SPRINGSALAD application type", (appType != null && appType == Application.SPRINGSALAD) ? true : false);
		
		Geometry geometry = simContext.getGeometry();
		Assert.assertTrue("expecting 3D geometry", geometry.getDimension() == 3 ? true : false);

		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simContext.getModel().gatherIssues(issueContext, issueList);
		simContext.gatherIssues(issueContext, issueList, true);		// bIgnoreMathDescription == true
		int numErrors = checkIssuesBySeverity(issueList, Issue.Severity.ERROR);
		int numWarnings = checkIssuesBySeverity(issueList, Issue.Severity.WARNING);
		Assert.assertTrue("expecting no Application error/warning issues", (numErrors == 0 && numWarnings == 0) ? true : false);
		
		// WARNING!! Debug configuration for this JUnit test required System property "vcell.installDir"
		// ex: -Dvcell.installDir=C:\dan\jprojects\git\vcell
		bioModel.updateAll(false);		// this call generates math
		MathDescription mathDescription = simContext.getMathDescription();
		Assert.assertTrue("expecting SpringSaLaD math type", (mathDescription.getMathType() != null && mathDescription.getMathType() == MathType.SpringSaLaD) ? true : false);

		
		// -------------------------------------------------------------------------------
		Simulation simulation = new Simulation(mathDescription, simContext);
		simContext.addSimulation(simulation);
		
		File localSimDataDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());	

		SimulationJob simJob = new SimulationJob(simulation, 0, null);
		SimulationTask simTask = new SimulationTask(simJob, 0);
		
		
		SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
		Assert.assertTrue("expecting non-null SolverDescription", (solverDescription != null) ? true : false);
		Assert.assertTrue("expecting databese name 'Langevin'", ("Langevin".equals(solverDescription.getDatabaseName())) ? true : false);

		// generate the input file for the solver and validate it
		int randomSeed = 0;
		String langevinLngvString = null;
		langevinLngvString = LangevinLngvWriter.writeLangevinLngv(simTask.getSimulation(), randomSeed);
		Assert.assertTrue("expecting non-null solver input string", (langevinLngvString != null) ? true : false);
		Assert.assertTrue("expecting properly formatted transition reaction", (langevinLngvString.contains(reactionTestString)) ? true : false);
		
		SolverUtilities.prepareSolverExecutable(solverDescription);	
		// create solver from SolverFactory
		Solver solver = SolverFactory.createSolver(localSimDataDir, simTask, false);
		Assert.assertTrue("expecting instanceof Langevin solver", (solver instanceof LangevinSolver) ? true : false);
	}

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
		Assert.assertTrue("expecting SpringSaLaD math type", (mathDescription.getMathType() != null && mathDescription.getMathType() == MathType.SpringSaLaD) ? true : false);

		SpeciesContextSpec[] speciesContextSpecs = simContext.getReactionContext().getSpeciesContextSpecs();
		SpeciesContextSpec scs = speciesContextSpecs[0];		// we test roundtrip for just one SpeciesContextSpec
		String internalLinkSetSQL = scs.getInternalLinksSQL();
		String siteAttributesMapSQL = scs.getSiteAttributesSQL();
		Set<MolecularInternalLinkSpec> internalLinkSet = scs.readInternalLinksSQL(internalLinkSetSQL);
		Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap = scs.readSiteAttributesSQL(siteAttributesMapSQL);

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
		Assert.assertTrue("Number of compatible subtypes must be 5", subTypeMap.size() == 5 ? true : false);
		Assert.assertTrue("No incompatible subtype may exist", subTypeMap.containsKey(ReactionRuleSpec.Subtype.INCOMPATIBLE) ? false : true);
		Assert.assertTrue("BondLength must be 3", bondLength == 3 ? true : false);

		// verify roundtrip for internalLinkSet (through sampling)
		Assert.assertTrue("internalLinkSet size different after roundtrip", internalLinkSet.size() == scs.getInternalLinkSet().size() ? true : false);
		MolecularInternalLinkSpec milsThis = internalLinkSet.iterator().next();
		boolean found = false;
		for(MolecularInternalLinkSpec milsThat : scs.getInternalLinkSet()) {
			if(milsThis.compareEqual(milsThat)) {
				found = true;
				break;
			}
		}
		Assert.assertTrue("MolecularInternalLinkSpec element not found after roundtrip", found ? true : false);

		// verify roundtrip for siteAttributesMap (through sampling)
		Assert.assertTrue("siteAttributesMap size different after roundtrip", siteAttributesMap.size() == scs.getSiteAttributesMap().size() ? true : false);
		MolecularComponentPattern mcpThis = siteAttributesMap.keySet().iterator().next();
		SiteAttributesSpec sasThis = siteAttributesMap.get(mcpThis);
		SiteAttributesSpec sasThat = scs.getSiteAttributesMap().get(mcpThis);
		Assert.assertTrue("SiteAttributesSpec element not found in siteAttributesMap after roundtrip", sasThis.compareEqual(sasThat) ? true : false);
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
		Assert.assertTrue("MathDescription must be Langevin", mathDescription.isLangevin() ? true : false);


		Simulation simulation = mathModel.getSimulations()[0];
		Geometry geometry = simulation.getMathDescription().getGeometry();
		GeometrySpec geometrySpec = geometry.getGeometrySpec();
		Assert.assertTrue("GeometrySpec must be 3D", geometrySpec.getDimension() == 3 ? true : false);

		//		LangevinSimulationOptions lso = simulation.getSolverTaskDescription().getLangevinSimulationOptions();
		int randomSeed = 0;
		String lngvString = LangevinLngvWriter.writeLangevinLngv(simulation, randomSeed);
		Assert.assertTrue("Default Lx must be 100 nm", lngvString.contains(L_x) ? true : false);
		Assert.assertTrue("Molecule must match the saved string pattern", lngvString.contains(molecule) ? true : false);

		for(SubVolume subVolume : geometrySpec.getSubVolumes()) {
			Assert.assertTrue("SpringSaLaD requires Analytic geometry", subVolume instanceof AnalyticSubVolume ? true : false);
			AnalyticSubVolume analyticSubvolume = (AnalyticSubVolume)subVolume;
			if (analyticSubvolume.getName().equals(String.valueOf(Structure.SpringStructureEnum.Intracellular))) {
				var expression = analyticSubvolume.getExpression();
				String exp = expression.infix();
				Assert.assertTrue("Analytic geometry expression must match the saved string pattern", analyticExpressionIntra.equals(exp) ? true : false);
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

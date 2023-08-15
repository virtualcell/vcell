package cbit.vcell.biomodel;

import cbit.image.ImageException;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.IllegalMappingException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MolecularInternalLinkSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.LangevinParticleMolecularType;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Issue.Severity;
import org.vcell.util.document.BioModelChildSummary.MathType;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class SpringSaLaDGoodReactionsTest {

	/*
	 * This test will check the compatibility with springsalad requirements as follows:
	 * - compartment number and name
	 * - seed species and their molecules
	 */
	@Test
	public void test_springsalad_model_bad() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException {
		
		System.out.println("start springsalad test for compartments, seed species and molecules");
		BioModel bioModel = getBioModelFromResource("Spring_model_bad.vcml");
		Assert.assertTrue("expecting non-null biomodel", bioModel != null ? true : false);
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
		Assert.assertTrue("expecting non-null simulation context", simContext != null ? true : false);
		
		Application appType = simContext.getApplicationType();
		Assert.assertTrue("expecting SPRINGSALAD application type", (appType != null && appType == Application.SPRINGSALAD) ? true : false);

		// we want to delete its link and test that the right issue is triggered
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
		s0:		SpringSaLaD requires the MolecularType to have at least one Site.
		s1:		Each Site must have at least one State.
		s2:		Internal Links are possible only when the Molecule has at least 2 sites.
		s2:		The Species and the Molecular Type must share the same name.
		MT2:	Link chain within the molecule has at least one discontinuity.
		Sink:	SpringSaLaD reserved Molecules 'Source' and 'Sink' must not have any sites defined
		s5:		There must be a biunivocal correspondence between the Species and the associated MolecularType.
		s6:		There must be a biunivocal correspondence between the Species and the associated MolecularType.
		*/

		System.out.println("end springsalad test");
	}

	@Test
	public void test_springsalad_good_reactions() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException {
		
		System.out.println("start springsalad test for compatible reactions");
		BioModel bioModel = getBioModelFromResource("Spring_reactions_good.vcml");
		Assert.assertTrue("expecting non-null biomodel", bioModel != null ? true : false);
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
		Assert.assertTrue("expecting non-null simulation context", simContext != null ? true : false);
		
		Application appType = simContext.getApplicationType();
		Assert.assertTrue("expecting SPRINGSALAD application type", (appType != null && appType == Application.SPRINGSALAD) ? true : false);
		
		Geometry geometry = simContext.getGeometry();
		geometry.getDimension();
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
		
		CompartmentSubDomain compartmentSubDomain = mathDescription.getCompartmentSubDomain("Intracellular");
		List<ParticleJumpProcess> particleJumpProcesses = compartmentSubDomain.getParticleJumpProcesses();
		List<ReactionRule> reactionRuleList = bioModel.getModel().getRbmModelContainer().getReactionRuleList();
		Assert.assertTrue("expecting 9 ReactionRule entities", reactionRuleList.size() == 9 ? true : false);
		Assert.assertTrue("expecting 10 ParticleJumpProcess entities", particleJumpProcesses.size() == 10 ? true : false);
		
		MathType mathType = mathDescription.getMathType();
		Assert.assertTrue("expecting SpringSaLaD math type", (mathType != null && mathType == MathType.SpringSaLaD) ? true : false);

		System.out.println("end springsalad test");
	}
	
	@Test
	public void test_springsalad_simple_simulation() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException {
		
		System.out.println("start springsalad test");
		BioModel bioModel = getBioModelFromResource("Spring_transition_free.vcml");
		SimulationContext simContext = bioModel.addNewSimulationContext("Application", SimulationContext.Application.SPRINGSALAD);
		// TODO: check for issues, confirm that there are none
		
		
		// WARNING!! Debug configuration for this JUnit test required System property "vcell.installDir"
		// ex: -Dvcell.installDir=C:\dan\jprojects\git\vcell
		bioModel.updateAll(false);
		MathDescription mathDescription = simContext.getMathDescription();
		// TODO: validate LangevinParticleMoleculartype, LangevinParticleJumpProcess

		
		// -------------------------------------------------------------------------------
		Simulation simulation = new Simulation(mathDescription, simContext);
		try {
			simContext.addSimulation(simulation);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		System.out.println("end springsalad test");
	}
    
    private static BioModel getBioModelFromResource(String fileName) throws IOException, XmlParseException {
        InputStream inputStream = SpringSaLaDGoodReactionsTest.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return XmlHelper.XMLToBioModel(new XMLSource(vcml));
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

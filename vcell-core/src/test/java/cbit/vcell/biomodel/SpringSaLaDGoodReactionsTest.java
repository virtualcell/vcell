package cbit.vcell.biomodel;

import cbit.image.ImageException;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.IllegalMappingException;
import cbit.vcell.mapping.MappingException;
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
import org.vcell.util.document.BioModelChildSummary.MathType;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;

public class SpringSaLaDGoodReactionsTest {

	@Test
	public void test_springsalad_good_reactions() throws IOException, XmlParseException, PropertyVetoException, ExpressionException, GeometryException, 
			ImageException, IllegalMappingException, MappingException {
		
		System.out.println("start springsalad test");
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

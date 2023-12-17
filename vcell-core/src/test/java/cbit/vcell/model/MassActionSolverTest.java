package cbit.vcell.model;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sbml.VcmlTestSuiteFiles;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Tag("Fast")
public class MassActionSolverTest {
	
	@Test
	public void testMassActionSolver() throws XmlParseException, MappingException, ClassNotFoundException, PropertyVetoException, IOException {
//		String savedInstalldirProperty = System.getProperty("vcell.installDir");
//		System.setProperty("vcell.installDir", "..");

		String filename = "biomodel_52337206_nonspatial.vcml";
		InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);
		String vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
				.lines().collect(Collectors.joining("\n"));

		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		
		TransformMassActions transformMassActions = new TransformMassActions();
		for (ReactionStep reactionStep : bioModel.getModel().getReactionSteps()) {
			if (reactionStep.getName().equals("BarbedD PointedD to 2FAD")) {	// this takes very long
				continue;
			}
			// check if I should kill myself
			transformMassActions.transformOne(reactionStep);
			break;
		}
	}
}

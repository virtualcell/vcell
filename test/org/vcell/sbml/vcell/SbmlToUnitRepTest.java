package org.vcell.sbml.vcell;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.sbml.libsbml.CVTerm;
import org.sbml.libsbml.Unit;
import org.vcell.sbml.ConversionTest;
import org.vcell.sbml.LibSBMLConstantsAdapter;
import org.vcell.sbml.SbmlException;
import org.vcell.util.logging.Logging;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;
import edu.uchc.connjur.spectrumtranslator.CodeUtil;

public class SbmlToUnitRepTest  extends ConversionTest {
	private static String[] PREFIXES = {"is", "get"};
	private Collection<String> prefixes;
	public SbmlToUnitRepTest() {
		prefixes = Arrays.asList(PREFIXES);
	}

	@Before
	public void setup( )  {
		Logging.init( );
		ResourceUtil.setNativeLibraryDirectory();
		NativeLib.SBML.load();
	}

	@Override
	protected void process(String unitString, UnitRepresentation unitRep) {
		try {
			List<Unit> sunits = SBMLUnitTranslator.convert(unitRep, 3,1);
			for (Unit u: sunits) {
				System.out.println("u: " + u.getId() + " n ");
//				String sm = CodeUtil.callStatusMethods(u, 1, prefixes);
//				System.out.println(sm);
				int k = u.getKind();
				System.out.println("kind " + LibSBMLConstantsAdapter.lookup(k, "KIND_"));
			}
		} catch (SbmlException e) {
			e.printStackTrace();
		}
		
	}
}

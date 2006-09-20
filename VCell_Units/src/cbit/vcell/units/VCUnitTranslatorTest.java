package cbit.vcell.units;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import cbit.vcell.units.VCUnitDefinition;

import org.jdom.Element;
import org.jdom.Namespace;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
/**
 * Insert the type's description here.
 * Creation date: (9/8/2004 3:59:09 PM)
 * @author: Jim Schaff
 */
public class VCUnitTranslatorTest extends junit.framework.TestCase {
/**
 * VCUnitTranslatorTest constructor comment.
 */
public VCUnitTranslatorTest() {
	super();
}


	//utility method.
	private ArrayList loadUnits() {

		ArrayList VCStandardUnits = new ArrayList();	
		java.lang.reflect.Field [] fields = VCUnitDefinition.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				if (fields[i].get(null) instanceof VCUnitDefinition) {
					VCStandardUnits.add((VCUnitDefinition)fields[i].get(null));
				}
			} catch (IllegalAccessException e) {
				System.err.println("Unable to access VC standard units.");
				e.printStackTrace();
				continue;
			}
		}

		return VCStandardUnits;
	}


	public void testVCUnitTranslator_VCCell() {

		ArrayList VCStandardUnits = loadUnits();
		Namespace tNamespace = Namespace.getNamespace(CELLMLTags.CELLML_NS);
		Namespace tAttNamespace = Namespace.getNamespace("");
		for (int i = 0; i < VCStandardUnits.size(); i++) {	
			VCUnitDefinition temp = (VCUnitDefinition)VCStandardUnits.get(i);
			System.out.println((i+1) + ")Testing VCCell round trip for unit: " + temp.getSymbol());
			Element Cell_UnitDef = VCUnitTranslator.VCUnitToCellML(temp.getSymbol(), tNamespace, tAttNamespace);
			if (temp.getSymbol().equals(VCUnitDefinition.TBD_SYMBOL)) {
				continue;
			}
			System.out.println(XmlUtil.xmlToString(Cell_UnitDef));
			//add a dummy parent for the cellml unit, for the test to work
			Element dummyParent = new Element(CELLMLTags.MODEL, tNamespace);
			dummyParent.setAttribute(CELLMLTags.name, "dummyParent", tAttNamespace);
			dummyParent.addContent(Cell_UnitDef);
			//define a new base unit, 'item'
			Element units = new Element(CELLMLTags.UNITS, tNamespace);
			units.setAttribute(CELLMLTags.name, CELLMLTags.ITEM, tAttNamespace);
			units.setAttribute(CELLMLTags.baseUnits, "yes", tAttNamespace);
        	dummyParent.addContent(units);
			//define 'molecules', for the units that need. Useless for others.
			units = new Element(CELLMLTags.UNITS, tNamespace);
			units.setAttribute(CELLMLTags.name, VCUnitDefinition.UNIT_molecules.getSymbol(), tAttNamespace);
        	Element unit1 = new Element(CELLMLTags.UNIT, tNamespace);
        	unit1.setAttribute(CELLMLTags.units, CELLMLTags.ITEM, tAttNamespace);
        	units.addContent(unit1);

        	
			VCUnitDefinition VC_UnitDef = VCUnitTranslator.CellMLToVCUnit(Cell_UnitDef, 
										  Namespace.getNamespace(CELLMLTags.CELLML_NS), Namespace.getNamespace(""));
			assertTrue("Round trip VCCell unit failed for unit: " + temp.getSymbol(), VC_UnitDef.equals(temp));
		}
	
	}
}
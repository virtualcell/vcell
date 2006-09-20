package cbit.vcell.vcml.test;
import cbit.vcell.cellml.CELLMLTags;
import cbit.vcell.cellml.VCQualCellTranslator;
import cbit.vcell.cellml.VCQuanCellTranslator;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.MathMLTags;
import cbit.util.TokenMangler;
import cbit.vcell.vcml.Translator;
import cbit.vcell.xml.XMLTags;
import junit.framework.TestCase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
   Partial round trip testing for MathModel --> Quantitative CellML model --> MathModel. 
 * Creation date: (2/10/2004 10:35:42 AM)
 * @author: Rashad Badrawi
 */
 
public class VCQuanCRoundTripTest extends TestCase {

	private Element sRoot, iRoot, tRoot;
	private Translator t;
	private PrintStream ps;

	private void compareMathSymbols(Element e1, Element e2, String type) {

		Expression exp1, exp2;
		String f1, f2;
		if (!type.equals(XMLTags.OdeEquationTag)) {
			//a hack because the string is coming back unrestored from jdom for VCML
			f1 = TokenMangler.getRestoredString(e1.getTextTrim());
			f2 = TokenMangler.getRestoredString(e2.getTextTrim());
		} else {
			f1 = TokenMangler.getRestoredString(e1.getChild(XMLTags.RateTag).getTextTrim());
			f2 = TokenMangler.getRestoredString(e2.getChild(XMLTags.RateTag).getTextTrim());
		}
		try {
			exp1 = new Expression(f1);
			exp2 = new Expression(f2);
		} catch (ExpressionException e) {
			fail("Unable to compare formulas");
			return;
		} catch (cbit.vcell.parser.TokenMgrError tme) {
			fail("Unable to parse formulas: " + f1 + "\n" + f2);
			return;
		}
		assertTrue(exp1.equals(exp2));
	}


//leaves out the testing of the math expressions to the roundtrip testing methods.
//fails if names were mangled.
	private void matchCellMLVars(Element source, Element var, String type) {

		String exp1;
		if (type.equals(XMLTags.ConstantTag) || type.equals(XMLTags.FunctionTag)) {
			exp1 = source.getTextTrim();
			assertTrue(exp1.length() > 0);
			if (type.equals(XMLTags.ConstantTag))
				assertEquals(exp1, var.getAttributeValue(CELLMLTags.initial_value));
		} else if (type.equals(XMLTags.OdeEquationTag)) {   //initial is optional for odes
			exp1 = source.getChildText(XMLTags.InitialTag);
			if (exp1.length() > 0) {
				assertEquals(exp1, var.getAttributeValue(CELLMLTags.initial_value));
			}
		}
	}


	//fails if names were mangled.
	private void matchVars(ArrayList l1, ArrayList l2, String elementType, String testType) {

		boolean flag = false;
		Element var = null;
		for (int i = 0; i < l1.size(); i++) {
			Element source = (Element)l1.get(i);
			String name = source.getAttributeValue(XMLTags.NameTag);
			for (int j = 0; j < l2.size(); j++) {
				var = (Element)l2.get(j);
				String varName;
				if (testType.equals("VCML"))
					varName = var.getAttributeValue(XMLTags.NameTag);
				else
					varName = var.getAttributeValue(CELLMLTags.name);
				if (name.equals(varName)) {
					flag = true;
					break;
				}
			}
			assertTrue("No match for: " + name, flag);
			flag = false;
			if (testType.equals("VCML")) 
				matchVCMLVars(source, var, elementType);
			else
				matchCellMLVars(source, var, elementType);
		}
	}


//fails if names were mangled.
	private void matchVCMLVars(Element source, Element var, String type) {

		String exp1, exp2;
		if (type.equals(XMLTags.ConstantTag) || type.equals(XMLTags.FunctionTag)) {
			if (type.equals(XMLTags.ConstantTag)) {
				exp1 = source.getTextTrim();
				exp2 = var.getTextTrim();
				assertTrue(exp1.length() > 0);
				assertTrue(exp2.length() > 0);
				assertEquals(exp1, exp2);
			} else {
				compareMathSymbols(source, var, type);
			}
		} else if (type.equals(XMLTags.OdeEquationTag)) {   //rates and initial are optional for odes
			exp1 = source.getChildText(XMLTags.InitialTag);
			exp2 = var.getChildText(XMLTags.InitialTag);
			if (exp1.length() > 0) {                    
 					assertEquals(exp1, exp2);
			} else {
				assertEquals(exp2.length(), 0);
			}
			exp1 = source.getChildText(XMLTags.RateTag);
			exp2 = source.getChildText(XMLTags.RateTag);
			if (exp1.length() > 0) {                      
 					compareMathSymbols(source, var, type);
			} else {
				assertEquals(exp2.length(), 0);
			}
		}
	}


	private void runCellMLTests() {

		Namespace ns = Namespace.getNamespace(CELLMLTags.CELLML_NS);
		String name = sRoot.getAttributeValue(XMLTags.NameTag);
		assertEquals(name, iRoot.getAttributeValue(CELLMLTags.name));
		
		//check that the intermediate cellml model only has one compartment
		ArrayList list1 = new ArrayList(iRoot.getChildren(CELLMLTags.COMPONENT, ns));
		assertEquals(list1.size(), 1);
		
		Element component = (Element)list1.get(0);
		Element md = sRoot.getChild(XMLTags.MathDescriptionTag);
		assertNotNull(md);
		assertEquals(md.getAttributeValue(XMLTags.NameTag), component.getAttributeValue(CELLMLTags.name));
		
		//test constants
		list1 = new ArrayList(md.getChildren(XMLTags.ConstantTag));
		ArrayList list2 = new ArrayList(component.getChildren(CELLMLTags.VARIABLE, ns));
		matchVars(list1, list2, XMLTags.ConstantTag, "CellML");
		//test functions
		list1 = new ArrayList(md.getChildren(XMLTags.FunctionTag));
		matchVars(list1, list2, XMLTags.FunctionTag, "CellML");
		//test odes
		ArrayList csdList = new ArrayList(md.getChildren(XMLTags.CompartmentSubDomainTag));
		for (int i = 0; i < csdList.size(); i++) {
			Element csd = (Element)csdList.get(i);
			list1 = new ArrayList(csd.getChildren(XMLTags.OdeEquationTag));
			matchVars(list1, list2, XMLTags.OdeEquationTag, "CellML");
		}		
	}


	protected void setUp() {

		Document sDoc = null, iDoc = null, tDoc = null;
		ps = TranslationTestSuite.getLogFile();
		ps.println("Testing VCQuanCellRoundTrip...");
		String fileName = TranslationTestSuite.getTestFixtures();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			t = Translator.getTranslator(Translator.VC_QUAN_CELL);
			iDoc = t.translate(br, true);
			sDoc = t.getSource();
			t = Translator.getTranslator(Translator.CELL_QUAN_VC);
			tDoc = t.translate(iDoc, false);                 //t.translate(iDoc, true);
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	    sRoot = sDoc.getRootElement();
	    iRoot = iDoc.getRootElement();
	    tRoot = tDoc.getRootElement();
	}


	//name of CompartmentSubDomain (only one) is not preserved. Just compare with the intermediate. 
	public void testCompSubDomain() {

		Element csd2 = tRoot.getChild(XMLTags.MathDescriptionTag).getChild(XMLTags.CompartmentSubDomainTag);
		assertNotNull(csd2);
		assertEquals(csd2.getAttributeValue(XMLTags.NameTag), iRoot.getAttributeValue(CELLMLTags.name));

		ArrayList csdList = new ArrayList(
							sRoot.getChild(XMLTags.MathDescriptionTag).getChildren(XMLTags.CompartmentSubDomainTag));
		ArrayList list2 = new ArrayList(csd2.getChildren(XMLTags.OdeEquationTag));
		for (int i = 0; i < csdList.size(); i++) {
			Element csd1 = (Element)csdList.get(i);
			ArrayList list1 = new ArrayList(csd1.getChildren(XMLTags.OdeEquationTag));
			matchVars(list1, list2, XMLTags.OdeEquationTag, "VCML");           
		}		
	}


	//tests that the factory works
	public void testGetTranslator() {

		Translator t2;
		t2 = Translator.getTranslator(Translator.VC_QUAN_CELL);
		assertTrue(t2 instanceof Translator);
		assertTrue(t2 instanceof VCQuanCellTranslator);
		t2 = Translator.getTranslator(Translator.VC_QUAL_CELL);
		assertTrue(t2 instanceof VCQualCellTranslator);
		try {
			t = Translator.getTranslator("crap");
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected: IllegalArgumentException");
	}


	//name of the math description is not preserved. Just compare with the intermediate. 
	public void testMathDescription() {

		Element md1 = sRoot.getChild(XMLTags.MathDescriptionTag);
		Element md2 = tRoot.getChild(XMLTags.MathDescriptionTag);
		assertNotNull(md1);
		assertNotNull(md2);
		assertEquals(md2.getAttributeValue(XMLTags.NameTag), iRoot.getAttributeValue(CELLMLTags.name));
		//test constants
		ArrayList list1 = new ArrayList(md1.getChildren(XMLTags.ConstantTag));
		ArrayList list2 = new ArrayList(md2.getChildren(XMLTags.ConstantTag));
		matchVars(list1, list2, XMLTags.ConstantTag, "VCML");
		list1 = new ArrayList(md1.getChildren(XMLTags.FunctionTag));
		list2 = new ArrayList(md2.getChildren(XMLTags.FunctionTag));
		matchVars(list1, list2, XMLTags.FunctionTag, "VCML");
	}


	public void testMathModel() {
		
		assertNotNull(sRoot);
		runCellMLTests();
		assertNotNull(tRoot);
		assertEquals(sRoot.getName(), tRoot.getName());
		assertEquals(sRoot.getAttributeValue(XMLTags.NameTag), tRoot.getAttributeValue(XMLTags.NameTag));
	}


		//name of the Geometry subvolume is not preserved. Just compare with the intermediate. 
	public void testSubVolume() {

		Element sv2 = tRoot.getChild(XMLTags.GeometryTag).getChild(XMLTags.SubVolumeTag);
		assertNotNull(sv2);
		assertEquals(sv2.getAttributeValue(XMLTags.NameTag), iRoot.getAttributeValue(CELLMLTags.name));
	}
}
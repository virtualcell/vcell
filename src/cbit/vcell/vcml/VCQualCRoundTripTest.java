package cbit.vcell.vcml;
import org.jdom.filter.ElementFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.MathMLTags;
import cbit.util.TokenMangler;
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
import cbit.util.xml.CELLMLTags;

/**
 * Partial round trip testing for BioModel --> Qualitative CellML model --> BioModel.

 * Creation date: (2/13/2004 10:40:02 AM)
 * @author: Rashad Badrawi
 */
public class VCQualCRoundTripTest extends TestCase {

	private Element sRoot, iRoot, tRoot;
	private Translator t;
	private PrintStream ps;
	private Namespace ns = Namespace.getNamespace(CELLMLTags.CELLML_NS);

	private void compareMathSymbols(Element e1, Element e2, String type) {

		Expression exp1, exp2;
		String f1, f2;
		if (!XMLTags.OdeEquationTag.equals(type)) {
			//a hack because the string is coming back unrestored from jdom for VCML
			f1 = TokenMangler.getRestoredString(e1.getTextTrim());
			f2 = TokenMangler.getRestoredString(e2.getTextTrim());
		} else {
			f1 = TokenMangler.getRestoredString(e1.getChild(XMLTags.ParameterTag).getTextTrim());
			f2 = TokenMangler.getRestoredString(e2.getChild(XMLTags.ParameterTag).getTextTrim());
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
	
		if (type.equals(XMLTags.SpeciesContextTag)) {
			runCellMLCompTest(source, var);	
		} else if (type.equals(XMLTags.SimpleReactionTag)) {
			runCellMLReactionTest(source, var);			
		}
	}


	private void matchReactionElements(ArrayList l1, ArrayList l2, String type) {

		boolean flag = false;
		Element var = null;
		for (int i = 0; i < l1.size(); i++) {
			Element source = (Element)l1.get(i);
			String name;
			if (type.equals(XMLTags.ParameterTag))
				name = source.getAttributeValue(XMLTags.NameTag);
			else
				name = source.getAttributeValue(XMLTags.SpeciesContextRefAttrTag);
			for (int j = 0; j < l2.size(); j++) {
				var = (Element)l2.get(j);
				String varName;
				if (type.equals(XMLTags.ParameterTag))
					varName = var.getAttributeValue(XMLTags.NameTag);
				else
					varName = var.getAttributeValue(XMLTags.SpeciesContextRefAttrTag);
				if (name.equals(varName)) {
					flag = true;
					break;
				}
			}
			assertTrue("No match for: " + name, flag);
			flag = false;
			if (type.equals(XMLTags.ReactantTag) || type.equals(XMLTags.ProductTag)) {
				assertEquals(source.getAttributeValue(XMLTags.StoichiometryAttrTag), 
							 var.getAttributeValue(XMLTags.StoichiometryAttrTag));
			}
			if (type.equals(XMLTags.ParameterTag)) {
				compareMathSymbols(source, var, null);
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
	private void matchVCMLVars(Element source, Element target, String type) {

		if (type.equals(XMLTags.SimpleReactionTag)) {     //no additional testing for localized compounds.
			ArrayList list1 = new ArrayList(source.getChildren(XMLTags.ReactantTag));
			ArrayList list2 = new ArrayList(target.getChildren(XMLTags.ReactantTag));
			matchReactionElements(list1, list2, XMLTags.ReactantTag);
			
			list1 = new ArrayList(source.getChildren(XMLTags.ProductTag));
			list2 = new ArrayList(target.getChildren(XMLTags.ProductTag));
			matchReactionElements(list1, list2, XMLTags.ProductTag);
			
			list1 = new ArrayList(source.getChildren(XMLTags.CatalystTag));
			list2 = new ArrayList(target.getChildren(XMLTags.CatalystTag));
			matchReactionElements(list1, list2, XMLTags.CatalystTag);

			list1 = new ArrayList(source.getChild(XMLTags.KineticsTag).getChildren(XMLTags.ParameterTag));
			list2 = new ArrayList(target.getChild(XMLTags.KineticsTag).getChildren(XMLTags.ParameterTag));
			matchReactionElements(list1, list2, XMLTags.ParameterTag);

			Element rate1 = null;
			for (int i = 0; i < list1.size(); i++) {
				if (((Element)list1.get(i)).getAttributeValue(XMLTags.ParamRoleAttrTag)
					                       .equals(XMLTags.ParamRoleReactionRateTag)) {

					rate1 = (Element)list1.get(i);	
				}
			}
			Element rate2 = null;
			for (int i = 0; i < list2.size(); i++) {
				if (((Element)list2.get(i)).getAttributeValue(XMLTags.ParamRoleAttrTag)
					                       .equals(XMLTags.ParamRoleReactionRateTag)) {

					rate2 = (Element)list2.get(i);	
				}
			}
			compareMathSymbols(rate1, rate2, null);
		}
	}


	private void runCellMLCompTest(Element source, Element var) {

		boolean flag = false;
		Element comp = null, lcs = null;
		
		ArrayList vars = new ArrayList(var.getChildren(CELLMLTags.VARIABLE, ns));
		String name = source.getAttributeValue(XMLTags.NameTag);
		for (int i = 0; i < vars.size(); i++) {
			comp = (Element)vars.get(i);
			if (comp.getAttributeValue(CELLMLTags.name).equals(name)) {
				flag = true;
				break;
			}
		}
		assertTrue("Cannot find the compound: " + name, flag);
		flag = false;
		JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(XMLTags.SpeciesContextSpecTag));
		while (walker.hasNext()) {
			lcs = (Element)walker.next();
			if (lcs.getAttributeValue(XMLTags.SpeciesContextRefAttrTag).equals(name)) {
				flag = true;
				break;
			}
		}
		if (!flag) {                                                       //simspecs are optional 
			assertEquals("0.0", comp.getAttributeValue(CELLMLTags.initial_value));
			return;
		}
		String initial = lcs.getChildText(XMLTags.InitialTag);
		if (initial.length() == 0)
			assertEquals("0.0", comp.getAttributeValue(CELLMLTags.initial_value));
		else
			assertEquals(initial, comp.getAttributeValue(CELLMLTags.initial_value));	
	}


	private void runCellMLKineticsTest(Element source, Element target) {

		boolean flag = false; 
		Element param, var = null;
		
		Element kinetics = source.getChild(XMLTags.KineticsTag);
		ArrayList list1 = new ArrayList(kinetics.getChildren(XMLTags.ParameterTag));
		for (int i = 0; i < list1.size(); i++) {
			param = (Element)list1.get(i);
			String name = param.getAttributeValue(XMLTags.NameTag);
			ArrayList list2 = new ArrayList(target.getChildren(CELLMLTags.VARIABLE, ns));
			for (int j = 0; j < list2.size(); j++) {
				var = (Element)list2.get(j);
				if (name.equals(var.getAttributeValue(CELLMLTags.name))) {
					flag = true;
					break;
				}
			}
			assertTrue("Reaction parameter not found: " + name, flag);
			flag = false;
			String text = param.getTextTrim();
			if (text.length() > 0)
				assertEquals(text, var.getAttributeValue(CELLMLTags.initial_value));
		}
	}


	private void runCellMLReactionTest(Element source, Element target) {

		boolean flag = false;
		ArrayList list1 = new ArrayList(source.getChildren(XMLTags.ReactantTag));
		list1.addAll(source.getChildren(XMLTags.ProductTag));
		list1.addAll(source.getChildren(XMLTags.CatalystTag));
		for (int i = 0; i < list1.size(); i++) {
			Element re = (Element)list1.get(i);
			String name = re.getAttributeValue(XMLTags.SpeciesContextRefAttrTag);
			ArrayList list2 = new ArrayList(target.getChildren(CELLMLTags.VARIABLE, ns));
			for (int j = 0; j < list2.size(); j++) {
				Element var = (Element)list2.get(j);
				if (name.equals(var.getAttributeValue(CELLMLTags.name))) {
					flag = true;
					break;
				}
			}
			assertTrue("Reaction element not found: " + name, flag);
			flag = false;
			Element reaction = target.getChild(CELLMLTags.REACTION, ns);
			Element varRef = null;
			list2 = new ArrayList(reaction.getChildren(CELLMLTags.VAR_REF, ns));
			for (int j = 0; j < list2.size(); j++) {
				varRef = (Element)list2.get(j);
				if (name.equals(varRef.getAttributeValue(CELLMLTags.variable))) {
					flag = true;
					break;
				}
			}
			assertTrue("Reaction element not found: " + name, flag);
			flag = false;
			Element role = varRef.getChild(CELLMLTags.ROLE, ns);
			String roleName = re.getName();
			if (roleName.equals(XMLTags.CatalystTag)) {
				roleName = "modifier";
			} else {
				roleName = roleName.toLowerCase();
				//no stoich for catalysts
				assertEquals(re.getAttributeValue(XMLTags.StoichiometryAttrTag), role.getAttributeValue(CELLMLTags.stoichiometry));  
			}
			assertEquals(roleName, role.getAttributeValue(CELLMLTags.role));		
		}
		runCellMLKineticsTest(source, target);
	}


	private void runCellMLTests() {

		String name = sRoot.getChild(XMLTags.ModelTag).getAttributeValue(XMLTags.NameTag);
		if (!Character.isDigit(name.charAt(0)))                   
			assertEquals(name, iRoot.getAttributeValue(CELLMLTags.name));
		
		//test localizedCompounds
		ArrayList list1 = new ArrayList(sRoot.getChild(XMLTags.ModelTag).getChildren(XMLTags.SpeciesContextTag));
		ArrayList list2 = new ArrayList(iRoot.getChildren(CELLMLTags.COMPONENT, ns));
		matchVars(list1, list2, XMLTags.SpeciesContextTag, "CellML");
		//test simple reactions
		list1 = new ArrayList(sRoot.getChild(XMLTags.ModelTag).getChildren(XMLTags.SimpleReactionTag));
		matchVars(list1, list2, XMLTags.SimpleReactionTag, "CellML");
	}


	protected void setUp() {

		Document sDoc = null, iDoc = null, tDoc = null;
		ps = TranslationTestSuite.getLogFile();
		ps.println("Testing VCQualCellRoundTrip...");
		String fileName = TranslationTestSuite.getTestFixtures();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			t = Translator.getTranslator(Translator.VC_QUAL_CELL);
			iDoc = t.translate(br, true);
			sDoc = t.getSource();
			t = Translator.getTranslator(Translator.CELL_QUAL_VC);
			tDoc = t.translate(iDoc, false);                 //t.translate(iDoc, true);
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	    sRoot = sDoc.getRootElement();
	    iRoot = iDoc.getRootElement();
	    tRoot = tDoc.getRootElement();
	}


	public void testLocComps() {

		ArrayList list1 = new ArrayList(sRoot.getChild(XMLTags.ModelTag).getChildren(XMLTags.SpeciesContextTag));
		ArrayList list2 = new ArrayList(tRoot.getChild(XMLTags.ModelTag).getChildren(XMLTags.SpeciesContextTag));
		matchVars(list2, list1, XMLTags.SpeciesContextTag, "VCML");   //reveresed the ordering, not all comps are reversible
	}


	public void testLocCompsSpec() {

		Element simSpec1 = sRoot.getChild(XMLTags.SimulationSpecTag);
		Element simSpec2 = tRoot.getChild(XMLTags.SimulationSpecTag);

		if (simSpec1 == null || simSpec2 == null)
			return;
		ArrayList l1 = new ArrayList(simSpec1.getChild(XMLTags.ReactionContextTag).getChildren(XMLTags.SpeciesContextSpecTag));
		ArrayList l2 = new ArrayList(simSpec2.getChild(XMLTags.ReactionContextTag).getChildren(XMLTags.SpeciesContextSpecTag));
		boolean flag = false;
		Element var = null;
		for (int i = 0; i < l2.size(); i++) {                          //reveresed the ordering, not all comps are reversible
			Element source = (Element)l2.get(i);
			String name = source.getAttributeValue(XMLTags.SpeciesContextRefAttrTag);
			for (int j = 0; j < l1.size(); j++) {
				var = (Element)l1.get(j);
				String varName = var.getAttributeValue(XMLTags.SpeciesContextRefAttrTag);
				if (name.equals(varName)) {
					flag = true;
					break;
				}
			}
			assertTrue("No match for: " + name, flag);
			flag = false;
			String init2 = source.getChildText(XMLTags.InitialTag);
			String init1 = var.getChildText(XMLTags.InitialTag);
			if (init1.length() != 0)      //the source may have no 'initial' which will default to '0.0' in target
				assertEquals(init1, init2);
		}
	}


//biomodel name is not reversible.
	public void testModel() {
		
		assertNotNull(sRoot);
		runCellMLTests();
		assertNotNull(tRoot);
		assertEquals(sRoot.getName(), tRoot.getName());
		assertEquals(sRoot.getChild(XMLTags.ModelTag).getName(), tRoot.getChild(XMLTags.ModelTag).getName());
	}


	public void testSimpleReactions() {

		ArrayList list1 = new ArrayList(sRoot.getChild(XMLTags.ModelTag).getChildren(XMLTags.SimpleReactionTag));
		ArrayList list2 = new ArrayList(tRoot.getChild(XMLTags.ModelTag).getChildren(XMLTags.SimpleReactionTag));
		matchVars(list1, list2, XMLTags.SimpleReactionTag, "VCML");
	}
}
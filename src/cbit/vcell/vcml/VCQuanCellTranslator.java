package cbit.vcell.vcml;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLPrinter;
import cbit.vcell.parser.MathMLTags;
import cbit.vcell.server.PropertyLoader;
import cbit.util.TokenMangler;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XMLTags;
import cbit.util.xml.CELLMLTags;
import cbit.util.xml.XmlUtil;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.ElementFilter;

import java.io.IOException;
import java.util.Iterator;

/**
 Translator from VCML math model to Quantitative CellML model. The mapping is as follows:
 
 MathModel.Name = model.name
 MathModel.MathDescription.Name = model.component.name  //all the math model goes into one component
 MathModel.MathDescription.Constant.Name = model.component.variable.name
 MathModel.MathDescription.Constant.Text = model.component.variable.initial_value
 MathModel.MathDescription.Function.Name = model.component.variable.name
 MathModel.MathDescription.Function.Text = model.component.math
 MathModel.MathDescription.CompartmentSubDomain.OdeEquation.Name = model.component.variable.name   //add one extra time variable
 MathModel.MathDescription.CompartmentSubDomain.OdeEquation.Initial.Text = model.component.variable.initial_value
 MathModel.MathDescription.CompartmentSubDomain.OdeEquation.Rate.Text = model.component.math
 defaults (dimentionless) = variable.unit
 
 - Note: VolumeVariables not translated.
 
 Unlike other translator classes, the naming of the methods follow the 'source' and not the 'target' in an attempt to
 differentiate it from the qualitative translation.
 
 * Creation date: (8/28/2003 4:32:13 PM)
 * @author: Rashad Badrawi
 */

public class VCQuanCellTranslator extends Translator {

	protected Namespace sNamespace;
	protected Namespace tNamespace;
	protected Namespace tAttNamespace;                     					 //placeholder in case needed in the future. 
	protected Namespace mathns;

	protected VCQuanCellTranslator() {

		sNamespace = Namespace.getNamespace(VCML_NS);
		//tNamespace = Namespace.getNamespace(CELLML_NS_PREFIX, CELLML_NS);
		tNamespace = Namespace.getNamespace(CELLMLTags.CELLML_NS);
		tAttNamespace = Namespace.getNamespace("");
		mathns = Namespace.getNamespace(Translator.MATHML_NS);
		schemaLocation = VCML_NS + "  " + PropertyLoader.getProperty(PropertyLoader.vcmlSchemaUrlProperty, Translator.DEF_VCML_SL);
		schemaLocationPropName = XmlUtil.SCHEMA_LOC_PROP_NAME;
	}


	protected void addConstants(Element source, Element target) {

		Iterator i = source.getChildren(XMLTags.ConstantTag, sNamespace).iterator();
		Element temp, var;
		while (i.hasNext()) {
			temp = (Element)i.next();
			var = new Element(CELLMLTags.VARIABLE, tNamespace);
			var.setAttribute(CELLMLTags.name, temp.getAttributeValue(XMLTags.NameTag), tAttNamespace);
			var.setAttribute(CELLMLTags.initial_value, temp.getTextTrim(), tAttNamespace);
			var.setAttribute(CELLMLTags.units, CELLMLTags.noDimUnit, tAttNamespace);
			target.addContent(var);
		}
	}


	protected void addFunctions(Element source, Element target) {

		Iterator i = source.getChildren(XMLTags.FunctionTag, sNamespace).iterator();
		Element temp, var;
		String name;
		while (i.hasNext()) {
			temp = (Element)i.next();
			name = temp.getAttributeValue(XMLTags.NameTag);
			var = new Element(CELLMLTags.VARIABLE, tNamespace);
			var.setAttribute(CELLMLTags.name, name, tAttNamespace);
			var.setAttribute(CELLMLTags.units, CELLMLTags.noDimUnit, tAttNamespace);
			target.addContent(var);
			target.addContent(getFunctionMathML(temp.getTextTrim(), name));
		}
	}


	protected void addMathDescription() {

		Element md = sRoot.getChild(XMLTags.MathDescriptionTag, sNamespace);
		Element comp = new Element(CELLMLTags.COMPONENT, tNamespace);
		comp.setAttribute(CELLMLTags.name, md.getAttributeValue(XMLTags.NameTag), tAttNamespace);
		addConstants(md, comp);
		addFunctions(md, comp);
		addODE(md, comp);
		tRoot.addContent(comp);
	}


	private void addMathML(Element math, String formStr) {

		try {
			String expression = TokenMangler.getRestoredString(formStr);
			Expression mathexpr = new Expression(expression);
			String xmlStr = ExpressionMathMLPrinter.getMathML(mathexpr);
			Element root = XmlUtil.stringToXML(xmlStr, null);            //no validation for the mathml.
      		JDOMTreeWalker walker = new JDOMTreeWalker(root, new ContentFilter(ContentFilter.ELEMENT));
      		root.setNamespace(mathns);
      		while (walker.hasNext()) {
				((Element)walker.next()).setNamespace(mathns);
      		}
      		math.addContent((Element)root.detach());
		} catch(cbit.vcell.parser.ParserException pe) {
			System.err.println("Infix problem: " + formStr); 
			pe.printStackTrace();
		} catch (ExpressionException ee) {
			System.err.println("Infix problem: " + formStr);
			ee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch(RuntimeException e) { 
			e.printStackTrace();
		}
	}


	protected void addMathModel() {

		//check if spatial model
		String dim = sRoot.getChild(XMLTags.GeometryTag).getAttributeValue(XMLTags.DimensionAttrTag);
		if (dim.length() > 0 && Integer.parseInt(dim) > 0)
			throw new RuntimeException("Cannot translate model: " + sRoot.getAttributeValue(XMLTags.NameTag) +
										" to CellML. Spatial models are not supported.");
		tRoot = new Element(CELLMLTags.MODEL, tNamespace);
		tRoot.setAttribute(CELLMLTags.name, sRoot.getAttributeValue(XMLTags.NameTag), tAttNamespace);
		addMathDescription();
	}


	protected void addODE(Element source, Element target) {

		Element temp, csd, var;
		String name;
		boolean first = true;
		Iterator j = source.getChildren(XMLTags.CompartmentSubDomainTag, sNamespace).iterator();
		while (j.hasNext()) {
			csd = (Element)j.next();
			Iterator i = csd.getChildren(XMLTags.OdeEquationTag, sNamespace).iterator();
			while (i.hasNext()) {
				temp = (Element)i.next();
				name = temp.getAttributeValue(XMLTags.NameTag);
				var = new Element(CELLMLTags.VARIABLE, tNamespace);
				var.setAttribute(CELLMLTags.name, name, tAttNamespace);
				var.setAttribute(CELLMLTags.initial_value, getInitial(temp), tAttNamespace); 
				var.setAttribute(CELLMLTags.units, CELLMLTags.noDimUnit, tAttNamespace);
				if (first) {
					first = false;
					Element tvar = new Element(CELLMLTags.VARIABLE, tNamespace);
					tvar.setAttribute(CELLMLTags.name, CELLMLTags.timeVar, tAttNamespace);
					tvar.setAttribute(CELLMLTags.units, CELLMLTags.noDimUnit, tAttNamespace);
					target.addContent(tvar);
				}
				target.addContent(var);
				Element rate = temp.getChild(XMLTags.RateTag, sNamespace);
				if (rate != null) {
					target.addContent(getODEMathML(rate.getTextTrim(), name));
				}
			}
		}
	}


	private Element getFunctionMathML(String formStr, String fName) {

		Element math = new Element(CELLMLTags.MATH, mathns);
		Element apply, eq, ci;
		apply = new Element(MathMLTags.APPLY, mathns);
		eq = new Element(MathMLTags.EQUAL, mathns);
		ci = new Element(MathMLTags.IDENTIFIER, mathns);
		ci.setText(fName);
		math.addContent(apply);
		apply.addContent(eq);
		apply.addContent(ci);

		addMathML(apply, formStr);             //add to 'apply' element

		return math;
	}


	private String getInitial(Element ode) {

		String initial = ode.getChildText(XMLTags.InitialTag, sNamespace);
		if (initial == null || initial.length() == 0)
			initial = "0.0";
		if (TransFilter.isFloat(initial)) {
			return initial;
		} else {         //need the fix here
			return initial;
		}
	}


	private Element getODEMathML(String formStr, String eName) {

		Element math = new Element(CELLMLTags.MATH, mathns);
		Element apply = new Element(MathMLTags.APPLY, mathns);
		Element eq = new Element(MathMLTags.EQUAL, mathns);
		math.addContent(apply.addContent(eq));
		Element apply2 = new Element(MathMLTags.APPLY, mathns);
		Element diff = new Element(MathMLTags.DIFFERENTIAL, mathns);
		Element bvar = new Element(MathMLTags.BVAR, mathns);
		apply2.addContent(diff);
		apply2.addContent(bvar);
		Element ci = new Element(MathMLTags.IDENTIFIER, mathns);
		ci.setText(CELLMLTags.timeVar);
		bvar.addContent(ci);
		ci = new Element(MathMLTags.IDENTIFIER, mathns);
		ci.setText(eName);
		apply2.addContent(ci);
		apply.addContent(apply2);

		addMathML(apply, formStr);

		return math;
	}


	protected void translate() {

		trimAndMangleSource();
		addMathModel();	
	}


	private void trimAndMangleSource() {

		TransFilter ts = new TransFilter(null, null, TransFilter.VCQUANCELL_MANGLE);
		ts.filter(sRoot);
	}
}
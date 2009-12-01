package org.vcell.cellml;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLPrinter;
import cbit.vcell.parser.MathMLTags;
import cbit.vcell.xml.XmlParseException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLTags;

import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.ElementFilter;
import org.vcell.util.PropertyLoader;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.VCDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * Translator from VCML to Qualitative CellML.
 BioModel.Name = lost
 Model.Name = model.name
 N/A = component.name (environment)
 N/A = component.variable.name (t) ...etc.
 BioModel.LocalizedCompound.Name = component.name
 same = component.variable.name
 BioModel.SimSpec.ReactionContext.LocalizedCompoundSpec.initial = component.variable.initial_value
 defaults =component.variable....
 N/A = component.variable(time var for each compound)
 N/A = component.mathML (representation for the compound and time, and the delta variables, if its not a constant)
 N/A = component.variable (delta vars).
 N/A = map each component.variable for a compound to the environment through the time variable.

 Reaction.Name = Component.name
 Reaction.ReactionElement.LocalizedCompoundRef = Component.variable.name
 Reaction.Name = Component.variable.name
 Reaction.ReactionElement.LocalizedCompoundRef = Component.variable.name (after prefix). for deltas.
 Reaction.ReactionElement.LocalizedCompoundRef = Component.Reaction.var_ref.variable.name     (var ref. for the vars of the reaction elements)
 Reaction.ReactionElement.Stoichiometry = Component.reaction.var_ref.role.stoichiometry
 Reaction.Kinetics.rate = component.reaction.var_ref.role.math (var ref for the reaction variable)
 Reaction.Kinetics.parameter.name = component.variable.name
 Reaction.Kinetics.parameter.Text = component.variable.initial_value
 - Map the reaction elements to the species variables and the deltas.

 - Some utility type overlap with the SBML counterpart. 

 * Creation date: (8/26/2003 9:05:21 AM)
 * @author: Rashad Badrawi
 */
public class VCQualCellTranslator extends Translator {

	public static final String DELTA_PREFIX = "delta_";
	
	protected Namespace sNamespace;
	protected Namespace tNamespace;
	protected Namespace tAttNamespace;
	protected Namespace mathns;
	protected NameManager nameManager;
	protected String preferedSimSpec;
	protected ArrayList connections;

	protected VCQualCellTranslator() {

		sNamespace = Namespace.getNamespace(XMLTags.VCML_NS);
		//tNamespace = Namespace.getNamespace(CELLML_NS_PREFIX, CELLML_NS);
		tNamespace = Namespace.getNamespace(CELLMLTags.CELLML_NS);
		tAttNamespace = Namespace.getNamespace("");
		mathns = Namespace.getNamespace(MATHML_NS);
		nameManager = new NameManager();
		connections = new ArrayList();
		schemaLocation = XMLTags.VCML_NS + "  " + PropertyLoader.getProperty(PropertyLoader.vcmlSchemaUrlProperty, org.vcell.cellml.Translator.DEF_VCML_SL);
		schemaLocationPropName = XmlUtil.SCHEMA_LOC_PROP_NAME;
	}


	protected void addEnvironment() {

		Element component = new Element(CELLMLTags.COMPONENT, tNamespace);
		component.setAttribute(CELLMLTags.name, CELLMLTags.envComp, tAttNamespace);
		Element variable = new Element(CELLMLTags.VARIABLE, tNamespace);
		variable.setAttribute(CELLMLTags.name, CELLMLTags.timeVar, tAttNamespace);
		variable.setAttribute(CELLMLTags.public_interface, CELLMLTags.outInterface, tAttNamespace);
		variable.setAttribute(CELLMLTags.units, CELLMLTags.defTimeUnit, tAttNamespace);
		component.addContent(variable);
		tRoot.addContent(component);
		nameManager.put(CELLMLTags.timeVar);
	}


	protected void addLocalizedCompoundConn(String name) {

		Element conn = new Element(CELLMLTags.CONNECTION, tNamespace);
		Element mapComp = new Element(CELLMLTags.MAP_COMP, tNamespace);
		mapComp.setAttribute(CELLMLTags.comp1, name, tAttNamespace);
		mapComp.setAttribute(CELLMLTags.comp2, CELLMLTags.envComp, tAttNamespace);
		conn.addContent(mapComp);
		Element mapVar = new Element(CELLMLTags.MAP_VAR, tNamespace);
		mapVar.setAttribute(CELLMLTags.var1, CELLMLTags.timeVar, tAttNamespace);
		mapVar.setAttribute(CELLMLTags.var2, CELLMLTags.timeVar, tAttNamespace);
		conn.addContent(mapVar);
		connections.add(conn);
	}


//unit is set based on the fact that we only have one 'feature' compartment. 
	protected void addLocalizedCompounds(Element model) {

		Iterator i = model.getChildren(XMLTags.SpeciesContextTag, sNamespace).iterator();
		Element compound, comp, var;
		String name;
		 
		while (i.hasNext()) { 
			compound = (Element)i.next();
			name = compound.getAttributeValue(XMLTags.NameTag);
			comp = new Element(CELLMLTags.COMPONENT, tNamespace);
			comp.setAttribute(CELLMLTags.name, name, tAttNamespace);
			var = new Element(CELLMLTags.VARIABLE, tNamespace);
			var.setAttribute(CELLMLTags.name, name, tAttNamespace);
			var.setAttribute(CELLMLTags.public_interface, CELLMLTags.outInterface, tAttNamespace);
			var.setAttribute(CELLMLTags.initial_value, getInitial(compound), tAttNamespace);
			var.setAttribute(CELLMLTags.units, VCUnitDefinition.UNIT_uM.getSymbol(), tAttNamespace);
			comp.addContent(var);
			// Import the "delta" variable, if the compound is a reactant or a product in a reaction
			if (!isConstant(compound)) {
				Iterator j = nameManager.getIterator(name);                    
				Element varLocal;
				while (j.hasNext()) {
					String delta_name = ((NameManager.MyStructure)j.next()).toString();
					var = new Element(CELLMLTags.VARIABLE, tNamespace);
					var.setAttribute(CELLMLTags.name, delta_name, tAttNamespace);
					var.setAttribute(CELLMLTags.public_interface, CELLMLTags.inInterface, tAttNamespace);      
					var.setAttribute(CELLMLTags.units, VCUnitDefinition.UNIT_uM_per_s.getSymbol(), tAttNamespace);
					comp.addContent(var);
				}
			} 
			var = new Element(CELLMLTags.VARIABLE, tNamespace);
			var.setAttribute(CELLMLTags.name, CELLMLTags.timeVar, tAttNamespace);
			var.setAttribute(CELLMLTags.public_interface, CELLMLTags.inInterface, tAttNamespace);
			var.setAttribute(CELLMLTags.units, CELLMLTags.defTimeUnit, tAttNamespace);
			comp.addContent(var);
			if (!isConstant(compound)) {
				Element math = getCompoundMathML(name);
				comp.addContent(math);
			}
			addLocalizedCompoundConn(name);
			tRoot.addContent(comp);
		}
	}


	protected void addModel() {

		Element source = sRoot.getChild(XMLTags.ModelTag, sNamespace);
		tRoot = new Element (CELLMLTags.MODEL, tNamespace);
		tRoot.setAttribute(CELLMLTags.name, source.getAttributeValue(XMLTags.NameTag), tAttNamespace);
		Iterator i = source.getChildren(XMLTags.SpeciesContextTag, sNamespace).iterator();
		Element temp;
		String name;
		while (i.hasNext()) {
			temp = (Element)i.next();
			name = temp.getAttributeValue(XMLTags.SpeciesRefAttrTag);
			nameManager.put(name);          
		}
		addUnits(source, tRoot);
		addEnvironment();
		addSimpleReactions(source);
		addLocalizedCompounds(source);
		i = connections.iterator();
		while (i.hasNext()) {
			Element conn = (Element)i.next();
			tRoot.addContent(conn);
		}
	}


	private void addParamUnits(Element source, Element model, ArrayList unitList) {

		ArrayList tempList = new ArrayList(source.getChildren(XMLTags.SimpleReactionTag, sNamespace));
		tempList.addAll(source.getChildren(XMLTags.FluxStepTag, sNamespace));
		ArrayList paramList = new ArrayList();	
		for (int i = 0; i < tempList.size(); i++) {
			paramList.addAll(((Element)tempList.get(i)).getChild(XMLTags.KineticsTag, sNamespace).getChildren(XMLTags.ParameterTag, sNamespace));
		}
		for (int i = 0; i < paramList.size(); i++) {
			Element param = (Element)paramList.get(i);
			String unitSymbol = param.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag, sNamespace);
			if (unitList.contains(unitSymbol)) {
				continue;
			}
			//no need to go through adding a unit if its a base unit (like 'second').
			if (CELLMLTags.isCellBaseUnit(unitSymbol)) {
				continue;
			}
			Element units = org.vcell.cellml.VCUnitTranslator.VCUnitToCellML(unitSymbol, tNamespace, tAttNamespace);
			if (units != null) {                                          //to accomodate TBDs
				unitList.add(unitSymbol);
				model.addContent(units);
			} else if (unitSymbol.equals(VCUnitDefinition.UNIT_TBD)) {    //define TBD as a base unit if its used.
				unitList.add(unitSymbol);
				units = new Element(CELLMLTags.UNITS, tNamespace);
				units.setAttribute(CELLMLTags.name, VCUnitDefinition.UNIT_TBD.getSymbol(), tAttNamespace);
				units.setAttribute(CELLMLTags.baseUnits, "true", tAttNamespace);
				model.addContent(units);	
			}
		}	
	}


	protected void addReactionElement(Element source, Element target, String elementType, String rName) {

		Iterator i = source.getChildren(elementType, sNamespace).iterator();
		Element re, varRef, role;
		String name;
		
		while (i.hasNext()) {
			re = (Element)i.next();
			name = re.getAttributeValue(XMLTags.SpeciesContextRefAttrTag);
			varRef = new Element(CELLMLTags.VAR_REF, tNamespace);
			varRef.setAttribute(CELLMLTags.variable, name, tAttNamespace);
			role = new Element(CELLMLTags.ROLE, tNamespace);
			role.setAttribute(CELLMLTags.role, elementType.toLowerCase(), tAttNamespace);
			role.setAttribute(CELLMLTags.direction, CELLMLTags.rxnForward, tAttNamespace);
			if (!elementType.equalsIgnoreCase(XMLTags.CatalystTag)) {
				role.setAttribute(CELLMLTags.delta_variable, nameManager.getMangledName(name, rName), tAttNamespace);  
				role.setAttribute(CELLMLTags.stoichiometry, re.getAttributeValue(XMLTags.StoichiometryAttrTag), tAttNamespace);
			}
			varRef.addContent(role);
			target.addContent(varRef);
		}
	}


//Maps the variables defined for the reaction elements, to their counterparts in the compound definition. 
//Also maps the delta variables. No connection made in the nameManager
	protected void addReactionElementConn(Element source, String elementType, String rName) {

		Iterator i = source.getChildren(elementType, sNamespace).iterator();
		Element re, conn, mapComp, mapVar;
		String name, mName;
		while (i.hasNext()) {
			re = (Element)i.next();
			name = re.getAttributeValue(XMLTags.SpeciesContextRefAttrTag);
			conn = new Element(CELLMLTags.CONNECTION, tNamespace);
			mapComp = new Element(CELLMLTags.MAP_COMP, tNamespace);
			mapComp.setAttribute(CELLMLTags.comp1, name, tAttNamespace);
			mapComp.setAttribute(CELLMLTags.comp2, rName, tAttNamespace);
			conn.addContent(mapComp);
			mapVar = new Element(CELLMLTags.MAP_VAR, tNamespace);
			mapVar.setAttribute(CELLMLTags.var1, name, tAttNamespace);
			mapVar.setAttribute(CELLMLTags.var2, name, tAttNamespace);
			conn.addContent(mapVar);
			if (!elementType.equals(XMLTags.CatalystTag)) {
				JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(XMLTags.SpeciesContextTag));
				Element matchingComp = walker.getMatchingElement(XMLTags.NameTag, name);
				if (!isConstant(matchingComp)) {
					mName = nameManager.getMangledName(name, rName);
					mapVar = new Element(CELLMLTags.MAP_VAR, tNamespace);
					mapVar.setAttribute(CELLMLTags.var1, mName, tAttNamespace);
					mapVar.setAttribute(CELLMLTags.var2, mName, tAttNamespace);
					conn.addContent(mapVar);
				}
			}
			connections.add(conn);
		}
	}


	protected void addReactionElementDelta(Element source, Element target, String elementType, String reactionName) {

		Iterator i = source.getChildren(elementType, sNamespace).iterator();
  		Element re, variable;
		String name, dname;
		
		while (i.hasNext()) {
			re = (Element)i.next();
			name = re.getAttributeValue(XMLTags.SpeciesContextRefAttrTag);
			JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(XMLTags.SpeciesContextTag));
			Element matchingComp = walker.getMatchingElement(XMLTags.NameTag, name);
			if (!isConstant(matchingComp)) {
				dname = nameManager.getUniqueName(DELTA_PREFIX + name);                              
				variable = new Element(CELLMLTags.VARIABLE, tNamespace);
				variable.setAttribute(CELLMLTags.name, dname, tAttNamespace);
				variable.setAttribute(CELLMLTags.public_interface, CELLMLTags.outInterface, tAttNamespace);
				variable.setAttribute(CELLMLTags.units, VCUnitDefinition.UNIT_uM_per_s.getSymbol(), tAttNamespace);
				nameManager.put(dname);                            
				nameManager.add(name, reactionName, dname);                                  
				target.addContent(variable);
			}
		}
	}


	protected void addReactionElementVars(Element source, Element target, String elementType) {

		Iterator i = source.getChildren(elementType, sNamespace).iterator();
		Element re, variable;
		while (i.hasNext()) {
			re = (Element)i.next();
			variable = new Element(CELLMLTags.VARIABLE, tNamespace);
			variable.setAttribute(CELLMLTags.name, re.getAttributeValue(XMLTags.SpeciesContextRefAttrTag), tAttNamespace);
			variable.setAttribute(CELLMLTags.public_interface, CELLMLTags.inInterface, tAttNamespace);
			variable.setAttribute(CELLMLTags.units, VCUnitDefinition.UNIT_uM.getSymbol(), tAttNamespace);
			target.addContent(variable);
		}
	}


	protected void addReactionKinetics(Element source, Element target, String rName) {
 
		Element kinetics = source.getChild(XMLTags.KineticsTag, sNamespace);
		ArrayList params = new ArrayList(kinetics.getChildren(XMLTags.ParameterTag, sNamespace));
		Element rate = null;
		for (int i = 0; i < params.size(); i++) {
			rate = (Element)params.get(i);
			if (rate.getAttributeValue(XMLTags.ParamRoleAttrTag).equals(getRateRole(kinetics))) {
				break;
			} else {
				rate = null;
			}
		}
		if (rate == null) {
			System.err.println("No rate added for reaction: " + source.getAttributeValue(XMLTags.NameAttrTag));
			return;
		} 
		Element varRef = new Element(CELLMLTags.VAR_REF, tNamespace);
		varRef.setAttribute(CELLMLTags.variable, rName, tAttNamespace);
		Element role = new Element(CELLMLTags.ROLE, tNamespace);
		role.setAttribute(CELLMLTags.role, CELLMLTags.rateRole, tAttNamespace);
		Element math = getReactionMathML(rate.getTextTrim(), rName);
		role.addContent(math);
		varRef.addContent(role);
		target.addContent(varRef);
	}


	protected void addReactionParamVars(Element source, Element target) {

		Element kinetics = source.getChild(XMLTags.KineticsTag, sNamespace);
		Iterator i = kinetics.getChildren(XMLTags.ParameterTag, sNamespace).iterator();
		Element param, variable;
		String name, value, paramUnit;
		while (i.hasNext()) {
			param = (Element)i.next();
			//rate is added separately.
			if (param.getAttributeValue(XMLTags.ParamRoleAttrTag).equals(XMLTags.ParamRoleReactionRateTag)) {
				continue;
			}
			name = param.getAttributeValue(XMLTags.NameTag);
			paramUnit = param.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
			value = param.getTextTrim();
			variable = new Element(CELLMLTags.VARIABLE, tNamespace);
			variable.setAttribute(CELLMLTags.name, name, tAttNamespace);
			if (paramUnit != null) {
				variable.setAttribute(CELLMLTags.units, paramUnit, tAttNamespace);
			} else {
				variable.setAttribute(CELLMLTags.units, CELLMLTags.noDimUnit, tAttNamespace);
			}
			variable.setAttribute(CELLMLTags.initial_value, value, tAttNamespace);         		//need fix here
			target.addContent(variable);
			nameManager.put(name);                                       
		}
	}


//saving the original reaction names, instead of the numbered ones (more of a CellML tradition)
	protected void addSimpleReactions(Element model) {

		Iterator i = model.getChildren(XMLTags.SimpleReactionTag, sNamespace).iterator();
		Element source, target = null, variable;
		String name;
		while (i.hasNext()) {
			source = (Element)i.next();
			//name = nameManager.getUniqueName("reaction");  
			name = source.getAttributeValue(XMLTags.NameTag); 
			nameManager.put(name);          
			//add reaction component
			target = new Element(CELLMLTags.COMPONENT, tNamespace);
			target.setAttribute(CELLMLTags.name, name, tAttNamespace);
			addReactionElementVars(source, target, XMLTags.ReactantTag);
			addReactionElementVars(source, target, XMLTags.ProductTag);
			addReactionElementVars(source, target, XMLTags.CatalystTag);
			//create the variable that maps to the reaction 
			variable = new Element(CELLMLTags.VARIABLE, tNamespace);
			variable.setAttribute(CELLMLTags.name, name, tAttNamespace);
			variable.setAttribute(CELLMLTags.units, VCUnitDefinition.UNIT_uM_per_s.getSymbol(), tAttNamespace);
			target.addContent(variable);
			
			addReactionElementDelta(source, target, XMLTags.ReactantTag, name);
			addReactionElementDelta(source, target, XMLTags.ProductTag, name);
			addReactionParamVars(source, target);
			//add reaction
			Element target2 = new Element(CELLMLTags.REACTION, tNamespace);
			target2.setAttribute(CELLMLTags.reversible, "yes", tAttNamespace);
			addReactionElement(source, target2, XMLTags.ReactantTag, name);
			addReactionElement(source, target2, XMLTags.ProductTag, name);
			addReactionElement(source, target2, XMLTags.CatalystTag, name);
			
			addReactionKinetics(source, target2, name);
			
			addReactionElementConn(source, XMLTags.ReactantTag, name);
			addReactionElementConn(source, XMLTags.ProductTag, name);
			addReactionElementConn(source, XMLTags.CatalystTag, name);
			target.addContent(target2);
			tRoot.addContent(target);
		}
	}


//all units are defined 'globally' since there is no room for contradictions.
//no need to define 'membrane reaction' units since they won't be used.
	protected void addUnits(Element source, Element model) {

		ArrayList unitList = new ArrayList();
		unitList.add(VCUnitDefinition.UNIT_uM.getSymbol());
		unitList.add(VCUnitDefinition.UNIT_uM_per_s.getSymbol());

		//define uM
		Element units = new Element(CELLMLTags.UNITS, tNamespace);
		units.setAttribute(CELLMLTags.name, VCUnitDefinition.UNIT_uM.getSymbol(), tAttNamespace);
		Element unit1 = new Element(CELLMLTags.UNIT, tNamespace);
		unit1.setAttribute(CELLMLTags.units, "mole", tAttNamespace);
        unit1.setAttribute(CELLMLTags.prefix, "-6", tAttNamespace);
		Element unit2 = new Element(CELLMLTags.UNIT, tNamespace);
		unit2.setAttribute(CELLMLTags.units, "litre", tAttNamespace);
        unit2.setAttribute(CELLMLTags.exponent, "-1", tAttNamespace);
		units.addContent(unit1);
		units.addContent(unit2);
		model.addContent(units);		

		//define uM_s
		units = new Element(CELLMLTags.UNITS, tNamespace);
		units.setAttribute(CELLMLTags.name, VCUnitDefinition.UNIT_uM_per_s.getSymbol(), tAttNamespace);
        Element unit3 = new Element(CELLMLTags.UNIT, tNamespace);
        unit3.setAttribute(CELLMLTags.units, "second", tAttNamespace);
        unit3.setAttribute(CELLMLTags.exponent, "-1", tAttNamespace);
		units.addContent((Element)unit1.clone());
		units.addContent((Element)unit2.clone());
		units.addContent(unit3);
		model.addContent(units);

		//define a new base unit 'item'
		units = new Element(CELLMLTags.UNITS, tNamespace);
		units.setAttribute(CELLMLTags.name, CELLMLTags.ITEM, tAttNamespace);
		units.setAttribute(CELLMLTags.baseUnits, "yes", tAttNamespace);
        model.addContent(units);
        
		//define 'molecules', for the units that need. Useless for others.
		units = new Element(CELLMLTags.UNITS, tNamespace);
		units.setAttribute(CELLMLTags.name, VCUnitDefinition.UNIT_molecules.getSymbol(), tAttNamespace);
        unit1 = new Element(CELLMLTags.UNIT, tNamespace);
        unit1.setAttribute(CELLMLTags.units, CELLMLTags.ITEM, tAttNamespace);
        units.addContent(unit1);
        model.addContent(units);
        
		addParamUnits(source, model, unitList);

	}


	protected String checkLocalizedCompoundSpec(Element temp, String attName) {

		Element simSpec = getSimSpec();
		if (simSpec == null)
			return "";
	    Iterator i = new JDOMTreeWalker(simSpec, new ElementFilter(XMLTags.SpeciesContextSpecTag));
	    String name = temp.getAttributeValue(XMLTags.NameTag);
        Element lcs;
        String param = "";
        while (i.hasNext()) {
            lcs = (Element) i.next();
            if (lcs.getAttributeValue(XMLTags.SpeciesContextRefAttrTag).equals(name)) {
                if (attName.equals(XMLTags.InitialTag))
                    param = lcs.getChild(attName, sNamespace).getTextTrim();
                else
                    param = lcs.getAttributeValue(attName);
                break;
            }
        }

        return param;
    }


	protected Element getCompoundMathML(String compName) {

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
		ci.setText(compName);
		apply2.addContent(ci);
		apply.addContent(apply2);
		Element temp;
		Iterator i = nameManager.getIterator(compName);                
		if (i.hasNext()) {
			temp = new Element(MathMLTags.IDENTIFIER, mathns);
			temp.setText(((NameManager.MyStructure)i.next()).toString());           //mangled name
		} else
			return math;
		if (!i.hasNext()) {
			apply.addContent(temp);
			return math;
		}
		Element apply3 = new Element(MathMLTags.APPLY, mathns);
		Element plus = new Element(MathMLTags.PLUS, mathns);
		apply3.addContent(plus);
		apply3.addContent(temp);
		while (i.hasNext()) {
			temp = 	new Element(MathMLTags.IDENTIFIER, mathns);
			temp.setText(((NameManager.MyStructure)i.next()).toString());
			apply3.addContent(temp);
		}
		apply.addContent(apply3);
		
		return math;	
	}


	private String getInitial(Element temp) {

        String initial = checkLocalizedCompoundSpec(temp, XMLTags.InitialTag);
        /*try {                                              //need fix here.
            Double d = new Double(initial);
        } catch (NumberFormatException e) {
            return "0.0";
        }*/
        if (initial.length() == 0)
        	initial = "0.0";

        return initial;
    }


    public String getPreferedSimSpec() { return preferedSimSpec; }


	//utility method. Can be moved.
	protected String getRateRole(Element kinetics) {

		String type = kinetics.getAttributeValue(XMLTags.KineticsTypeAttrTag);
    
		if (type.equals(XMLTags.KineticsTypeGeneralCurrentKinetics)) {
			return XMLTags.ParamRoleInwardCurrentTag;
		} else {
			return XMLTags.ParamRoleReactionRateTag;
		}
	}


	private Element getReactionMathML(String formStr, String rName) {

		Element math = new Element(CELLMLTags.MATH, mathns);

		Element apply = new Element(MathMLTags.APPLY, mathns);
		Element eq = new Element(MathMLTags.EQUAL, mathns);
		apply.addContent(eq);
		Element ci = new Element(MathMLTags.IDENTIFIER, mathns);
		ci.setText(rName);
		apply.addContent(ci);
		Element apply2 = new Element(MathMLTags.APPLY, mathns);
		Element minus = new Element(MathMLTags.MINUS, mathns);
		apply2.addContent(minus);	
		apply.addContent(apply2);
		math.addContent(apply);

		try {
			String expression = TokenMangler.getRestoredString(formStr);
			Expression mathexpr = new Expression(expression);
			String xmlStr = ExpressionMathMLPrinter.getMathML(mathexpr, true);
			Element root = XmlUtil.stringToXML(xmlStr, null).getRootElement();            //no validation for the mathml.
      		root.setNamespace(mathns);
      		JDOMTreeWalker walker = new JDOMTreeWalker(root, new ContentFilter(ContentFilter.ELEMENT));
      		while (walker.hasNext()) {
				((Element)walker.next()).setNamespace(mathns);
      		}
      		apply2.addContent((Element)root.detach());
		} catch(cbit.vcell.parser.ParserException pe) {
			System.err.println("Infix problem: " + formStr); 
			pe.printStackTrace();
		} catch (ExpressionException ee) {
			System.err.println("Infix problem: " + formStr);
			ee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
      	return math;
	}


	protected Element getSimSpec() {

		JDOMTreeWalker i;
	    Element simSpec = null;

	   	i = new JDOMTreeWalker(sRoot, new ElementFilter(XMLTags.SimulationSpecTag));
	    if (preferedSimSpec != null) {
			simSpec = i.getMatchingElement(XMLTags.NameTag, preferedSimSpec);
			if (simSpec == null) {
				System.err.println("Error: simSpec specified does not exist: " + 
									preferedSimSpec);
			}
	    } else {
		    while (i.hasNext()) {
				simSpec = (Element)i.next();                  //return the first
				break;   
		    }    
	    }
	    
		return simSpec;
	}


	protected boolean isConstant(Element comp) {

		 String fc = checkLocalizedCompoundSpec(comp, XMLTags.ForceConstantAttrTag);

		 if (fc.length() == 0)
		 	return false;
		 	
		 return new Boolean(fc).booleanValue();
	}


  //this method should be called before the call to translate() is made.  
	public void setPreferedSimSpec(String simSpecName) throws IllegalArgumentException {

	    if (simSpecName == null || simSpecName.length() == 0) {
	    	throw new IllegalArgumentException ("Invalid Simulation Spec selected: " + simSpecName);
	    }
	    //a fix for the mangling of the preferred simspec name by TransFilter.
		String mSimSpecName = TokenMangler.mangleToSName(simSpecName);
		if (!mSimSpecName.equals(simSpecName)) {
			System.out.println("Preferred Sim Spec name mangled: " + mSimSpecName);
		}  
		this.preferedSimSpec = mSimSpecName;
    }


	protected VCDocument translate() {
// TODO
		trimAndMangleSource();
		addModel();	
		return null;
	}


	private void trimAndMangleSource() {

		TransFilter ts = new TransFilter(null, null, TransFilter.VCQUALCELL_MANGLE);
		ts.filter(sRoot);
	}
}
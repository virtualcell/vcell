package cbit.vcell.vcml;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.parser.MathMLTags;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.NameList;
import cbit.vcell.xml.NameManager;
import cbit.gui.PropertyLoader;
import cbit.util.xml.XmlParseException;
import cbit.util.xml.XmlUtil;
import cbit.util.TokenMangler;
import cbit.vcell.xml.XMLTags;
import cbit.util.xml.CELLMLTags;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.ElementFilter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
 
/**
Translator implementation from CellML to VCML:
BioModel.Name = model.name
BioModel.Model.Name = model.name
BioModel.Model.Feature.Name = model.name (for the sole feature).
BioModel.Model.Compound.Name = model.component.variable.name
BioModel.Model.LocalizedCompound.Name = model.component.variable.name
BioModel.Model.LocalizedCompound.CompoundRef = model.component.variable.name
BioModel.Model.LocalizedCompound.Structure = model.name
BioModel.Model.LocalizedCompound.OverrideName = defaults ("false")
BioModel.Model.SimpleReaction.Name = model.component.name (separate mangling)
BioModel.Model.SimpleReaction.Structure = model.name
BioModel.Model.SimpleReaction.Kinetics.Type = default ("General Kinetics")
BioModel.Model.SimpleReaction.Kinetics.Parameter.Name = model.component.variable.name
BioModel.Model.SimpleReaction.Kinetics.Parameter.text = model.component.variable.name  OR
BioModel.Model.SimpleReaction.Kinetics.Parameter.text = model.component.math.* (if expressed as a formula)
BioModel.Model.SimpleReaction.Kinetics.Rate = model.component.math.*
BioModel.SimulationSpec.Name = Default ("Default Application")
BioModel.SimulationSpec.Geometry.* = defaults (see code)
BioModel.SimulationSpec.GeometryContext.* = defaults (see code)
BioModel.SimulationSpec.ReactionContext.LocalizedCompoundSpec.LocalizedCompoundRef = model.component.variable.name
BioModel.SimulationSpec.ReactionContext.LocalizedCompoundSpec.ForceConstant = defaults ("false")
BioModel.SimulationSpec.ReactionContext.LocalizedCompoundSpec.EnableDiffusion = defaults ("true")
BioModel.SimulationSpec.ReactionContext.LocalizedCompoundSpec.Initial.text = model.component.variable.initial_value
BioModel.SimulationSpec.ReactionContext.LocalizedCompoundSpec.Diffusion.text = defaults ("0.0")

- Variable (and component?) names are mangled, by appending a number to the end of the variable name 
  if there is more than one instance of it within a cellML model.
- No support for membranes, or compartments. Only one default compartment is generated.
- For connected variables, the 'connected-to' variable is the one that is translated (after possible mangling).

 * Creation date: (9/9/2003 5:34:54 PM)
 * @author: Rashad Badrawi
 */

 public class CellQualVCTranslator extends Translator {

	public static final String GEOM_NAME = "Default";
	public static final String SUBVOL_NAME = "Compartmental";
	protected NameManager nm;
    protected NameList nlcomp, nlparam;
    protected Namespace sNamespace, sAttNamespace, tNamespace, mathns;
	protected Hashtable locCompHash;
	protected TreeMap spUnitParam = new TreeMap();

    protected CellQualVCTranslator() {

		//sNamespace = Namespace.getNamespace(CELLML_NS_PREFIX, CELLML_NS);
		sNamespace = Namespace.getNamespace(CELLMLTags.CELLML_NS);
		sAttNamespace = Namespace.getNamespace("");                          //dummy NS
		tNamespace = Namespace.getNamespace(VCML_NS);
		mathns = Namespace.getNamespace(MATHML_NS);
		nm = new NameManager();
		nlcomp = new NameList();
		nlparam = new NameList();
		locCompHash = new Hashtable();
		schemaLocation = CELLMLTags.CELLML_NS + " " + PropertyLoader.getProperty(PropertyLoader.cellmlSchemaUrlProperty, Translator.DEF_CELLML_SL);
		schemaLocationPropName = XmlUtil.NS_SCHEMA_LOC_PROP_NAME;
    }


    protected void addBioModel() {

	    trimAndMangleSource();
		tRoot = new Element(XMLTags.BioModelTag, tNamespace); 
		String sName = sRoot.getAttributeValue(CELLMLTags.name, sAttNamespace); 
		tRoot.setAttribute(XMLTags.NameTag, sName);
		
		Comment comment = new Comment("Transforming CELLML into a VCML BioModel.\nAssuming the bound variable " + 
					      "is always time. VCML created from a CELLML document. "); 
		tRoot.addContent(comment);
		
		Element model = new Element(XMLTags.ModelTag, tNamespace);
		model.setAttribute(XMLTags.NameTag, sName);
		tRoot.addContent(model);         
		//Define a single compartment
		Element comp = new Element(XMLTags.FeatureTag, tNamespace);
		comp.setAttribute(XMLTags.NameTag, sName); 
		model.addContent(comp);
 
		addVarsAndConns();
		//System.out.println(nm.dumpNames());
		addUnits();
		addCompounds(model); 
		addReactions(model);
		fixStoich(model);
		addSimSpec();
		cbit.vcell.units.VCUnitTranslator.clearCellMLUnits();
    }


	protected void addBoundariesTypes(Element fm) {

		Element bt = new Element(XMLTags.BoundariesTypesTag, tNamespace);
		String temp = "Value";
		bt.setAttribute(XMLTags.BoundaryAttrValueXm, temp);
		bt.setAttribute(XMLTags.BoundaryAttrValueXp, temp);
		bt.setAttribute(XMLTags.BoundaryAttrValueYm, temp);
		bt.setAttribute(XMLTags.BoundaryAttrValueYp, temp);
		bt.setAttribute(XMLTags.BoundaryAttrValueZm, temp);
		bt.setAttribute(XMLTags.BoundaryAttrValueZp, temp);
		
		fm.addContent(bt);
	}


	protected void addCompound(Element model, String compoundName, String variableName, String componentName) {

		Element compound = new Element(XMLTags.SpeciesTag, tNamespace);
		compound.setAttribute(XMLTags.NameTag, compoundName);
		model.addContent(compound);
		Element locComp = new Element(XMLTags.SpeciesContextTag, tNamespace);
		locComp.setAttribute(XMLTags.NameTag, compoundName);
		locComp.setAttribute(XMLTags.SpeciesRefAttrTag, compoundName);
		//the single compartment is named with the model identifier
		locComp.setAttribute(XMLTags.StructureAttrTag, sRoot.getAttributeValue(CELLMLTags.name, sAttNamespace));
		locComp.setAttribute(XMLTags.HasOverrideAttrTag, "false");
					
		Element initial = new Element(XMLTags.InitialTag, tNamespace);
		initial.addContent(getInitial(componentName, variableName));
		Element diffusion = new Element(XMLTags.DiffusionTag, tNamespace);
		diffusion.addContent("0.0");
		Element lcs = new Element(XMLTags.SpeciesContextSpecTag, tNamespace);                   
		lcs.setAttribute(XMLTags.SpeciesContextRefAttrTag, compoundName);
		lcs.addContent(initial);
		lcs.addContent(diffusion);
		locCompHash.put(locComp, lcs);
		nlcomp.setMangledName(variableName,componentName, compoundName);
	}


//assumes that if its not a 'rate' variable, then its a 'compound' variable. 
	protected void addCompounds(Element model) {
		
		JDOMTreeWalker reactionWalker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.REACTION));
		Element reaction;
		while (reactionWalker.hasNext()) {
			reaction = (Element)reactionWalker.next();
			//Loop through all the variables which might be compounds
			JDOMTreeWalker compWalker = new JDOMTreeWalker(reaction, new ElementFilter(CELLMLTags.VAR_REF));
			Element varRef;
			while (compWalker.hasNext()) {
	  	  		varRef = (Element)compWalker.next();
	  	  		Iterator j = varRef.getChildren(CELLMLTags.ROLE, sNamespace).iterator();
	  	  		Element role;
	  	  		while (j.hasNext()) {
					role = (Element)j.next();
		  	  		if (role.getAttributeValue(CELLMLTags.role, sAttNamespace).equals(CELLMLTags.rateRole)) {      
						addRateVar(varRef);
						continue;
					}
					//we have a compound - declare it if it is not already defined
					String componentName = reaction.getParent().getAttributeValue(CELLMLTags.name, sAttNamespace);
	    			String variableName = varRef.getAttributeValue(CELLMLTags.variable, sAttNamespace);
	    			String compName = nm.getMangledName(componentName, variableName);       //compName is 'compound Name'
	    			if (compName.length() == 0) {
	    				System.err.println("Unable to resolve the variable '" + variableName + 
		    				   "' in the component '" + componentName + "'");
	    				continue;
	    			}
					//we need the resolved component and variable name 
					String rComponentName = nm.getFirstSecondKey(compName);          //same as componentName?
					String rVariableName = nm.getMangledName(compName, rComponentName);
					String declaredComp = nlcomp.getMangledName(rVariableName, rComponentName);   
					//The coumpound has not been declared, so declare, add it to the list of declared compounds
					if (declaredComp.length() == 0) {
						addCompound(model, compName, rVariableName, rComponentName);
					}
	  	  		}	
			}
		}
		//separate the listing of 'compounds' from 'localized compounds'
		Iterator i = locCompHash.keySet().iterator();
		while (i.hasNext()) {
			model.addContent((Element)i.next());
		}
	}


//no scaling for parameter units, whether the ones explicitly defined or the ones buried in mathml.
	protected void addFormulaParam (Element kinetics, Element comp, Element paramVar, Element rhs, NameList nlParamLocal, NameList mangledParam, 
									String compName, String paramName) {

		boolean flag = false;
		if (rhs != null) {
			//we first need to declare any new parameters that may be in this parameters equation
			JDOMTreeWalker walker = new JDOMTreeWalker(rhs, new ElementFilter(MathMLTags.IDENTIFIER));
			String varN;
			Element ci;
			
			while (walker.hasNext()) {
				ci = (Element)walker.next();
				flag = true;
				//check for already declared parameters and compounds
				varN = nm.getMangledName(compName, ci.getTextTrim());
				if (varN.length() == 0)
					System.err.println("Unable to resolve the variable '" + ci.getTextTrim() +"' in the component '" + compName + "'");
				String varComp = nm.getFirstSecondKey(varN);
				String varVar = nm.getMangledName(varN, varComp);
				String varDeclaredComp = nlcomp.getMangledName(varVar, varComp);
				String varDeclaredParam = nlParamLocal.getMangledName(varVar, varComp);
				//the variable is neither a compound or a declared parameter, so we need to define it, recursively
				if (varDeclaredComp.length() == 0 && varDeclaredParam.length() == 0) {
					addReactionParam(kinetics, comp, nlParamLocal, mangledParam, varN, varVar, varComp);
				}
			}
		}
		//get a unique parameter name for the parameter, add it to the hashtable, then, post-process the equation
		Element param = new Element(XMLTags.ParameterTag, tNamespace);
		String uniqueID = getUniqueParamName(paramName);
		param.setAttribute(XMLTags.NameTag, uniqueID);
		param.setAttribute(XMLTags.ParamRoleAttrTag, XMLTags.ParamRoleUserDefinedTag);
		mangledParam.setMangledName(paramName, "dummy", uniqueID);
		if (flag) {
			Element math = new Element(CELLMLTags.MATH, MATHML_NS).addContent((Element)rhs.detach());
			String exp = mathMLToString(paramVar.getParent(), math, kinetics);
			exp = processMathExp(comp, exp);
			//Now we need to check for any parameters in the equation who need to have their name changed
			exp = mangleMathExp(exp, mangledParam);
			param.setText(exp);
		} else {
			String initialVal = paramVar.getAttributeValue(CELLMLTags.initial_value, sAttNamespace);
			//this is a temporary fix for the lack of handling VCML exp. initial values. Needed for roundtrip.
			Expression exp;
			if (initialVal != null) {
				initialVal = flattenExp(initialVal);
				param.setText(initialVal);
			} else {
				param.setText("0.0");
			}
		}
		VCUnitDefinition cellUnit;
		String unitSymbol = paramVar.getAttributeValue(CELLMLTags.units, sAttNamespace);
		if (unitSymbol != null) {                   //should never be the case.
			cellUnit = cbit.vcell.units.VCUnitTranslator.getMatchingCellMLUnitDef(paramVar.getParent(), sAttNamespace, unitSymbol);
			if (cellUnit != null) {
				param.setAttribute(XMLTags.VCUnitDefinitionAttrTag, cellUnit.getSymbol());
			}
		}	
		kinetics.addContent(param);
	}


	protected void addGeomContext(Element simSpec) {

		Element geomContext = new Element(XMLTags.GeometryContextTag, tNamespace);
		Element target;
		
		target = new Element(XMLTags.FeatureMappingTag, tNamespace);
		target.setAttribute(XMLTags.FeatureTag, sRoot.getAttributeValue(CELLMLTags.name, sAttNamespace));       //only one compartment
		target.setAttribute(XMLTags.SubVolumeTag, SUBVOL_NAME);
		target.setAttribute(XMLTags.ResolvedAttrTag, "false");
		addBoundariesTypes(target);
			
		geomContext.addContent(target);	
		simSpec.addContent(geomContext);	
	}


	protected void addGeometry(Element simSpec) {

		Element geom = new Element(XMLTags.GeometryTag, tNamespace);
		geom.setAttribute(XMLTags.NameTag, GEOM_NAME);
		geom.setAttribute(XMLTags.DimensionAttrTag, "0");
		
		Element extent = new Element(XMLTags.ExtentTag, tNamespace);
		String temp = "10.0";
		extent.setAttribute(XMLTags.XAttrTag, temp);
		extent.setAttribute(XMLTags.YAttrTag, temp);
		extent.setAttribute(XMLTags.ZAttrTag, temp);
		geom.addContent(extent);
		
		Element origin = new Element(XMLTags.OriginTag, tNamespace);
		temp = "0.0";
		origin.setAttribute(XMLTags.XAttrTag, temp);
		origin.setAttribute(XMLTags.YAttrTag, temp);
		origin.setAttribute(XMLTags.ZAttrTag, temp);
		geom.addContent(origin);

		Element subVol = new Element(XMLTags.SubVolumeAttrTag, tNamespace);
		subVol.setAttribute(XMLTags.NameTag, SUBVOL_NAME);
		subVol.setAttribute(XMLTags.HandleAttrTag, "0");
		subVol.setAttribute(XMLTags.TypeAttrTag, XMLTags.CompartmentBasedTypeTag);
		geom.addContent(subVol);
		
		simSpec.addContent(geom);
	}


//for now, just removes the extras
//mathParent can be either a component or a model element.
//no scaling for the math constants units.
	private Element addMathMLUnits(Element mathParent, Element math, Element kinetics) {

		ArrayList l = new ArrayList();
		Iterator walker = new JDOMTreeWalker(math, new ContentFilter(ContentFilter.ELEMENT));
		while (walker.hasNext())
			l.add(walker.next());
      	Element temp;
      	for (int i = 0; i < l.size(); i++) {     
	      	temp = (Element)l.get(i);
	      	if (temp.getName().equals("cn")) {
		    	ArrayList atts = new ArrayList(temp.getAttributes());
				org.jdom.Attribute att;
				for (int j = 0; j < atts.size(); j++) {
					att = (org.jdom.Attribute)atts.get(j);
					temp.removeAttribute(att.getName(), sNamespace);
				}
				temp.removeNamespaceDeclaration(sNamespace);
	      	} 
      	}

      	return math;
		/*//int cnt = 0;
		ArrayList l = new ArrayList();
		//Element temp = new Element(CELLMLTags.MATH, math.getNamespace());
		Iterator walker = new JDOMTreeWalker(math, new ContentFilter(ContentFilter.ELEMENT));
		while (walker.hasNext()) {
			l.add((Element)(((Element)walker.next()).clone()));
		}
      	Element constant;
      	for (int i = 0; i < l.size(); i++) {
	      	constant = (Element)l.get(i);
	      	if (constant.getName().equals(MathMLTags.CONSTANT)) {
		    	ArrayList atts = new ArrayList(constant.getAttributes());
				org.jdom.Attribute att;
				//if (atts.size() == 0) {
					//temp.addContent(constant.detach());
					//continue;
				//}
				for (int j = 0; j < atts.size(); j++) {
					att = (org.jdom.Attribute)atts.get(j);
					constant.removeAttribute(att.getName(), sNamespace);
					if (att.getName().equals(CELLMLTags.units)) {
						VCUnitDefinition cellUnit = null;
						String unitSymbol = att.getValue();
						if (unitSymbol != null && unitSymbol.length() > 0) {                   
							cellUnit = VCUnitTranslator.getMatchingCellUnitDef(mathParent, sAttNamespace, unitSymbol);
						}
						if (cellUnit == null) {
							System.err.println("No unit for the math constant under element: " + 
								               mathParent.getAttributeValue(CELLMLTags.name));
							constant.removeAttribute(att.getName(), sNamespace);          //let it go through
							temp.addContent((Element)constant.detach());
						} else {
				 			//if (unitSymbol.equals(CELLMLTags.noDimUnit)) {                //don't include dimensionless's
								//continue;
							//}
							//add param to kinetics.
							String newParamName = "exp_param_" + cnt++;
							Element param = new Element(XMLTags.ParameterTag, Namespace.getNamespace(Translator.VCML_NS));
							param.setAttribute(XMLTags.NameAttrTag, newParamName);
							param.setAttribute(XMLTags.VCUnitDefinitionAttrTag, cellUnit.getSymbol());
							param.setAttribute(XMLTags.ParamRoleAttrTag, XMLTags.ParamRoleUserDefinedTag);
							param.setText(constant.getTextTrim());
							kinetics.addContent(param);
							//replace cn with ci in math tree ??
							Element identifier = new Element(MathMLTags.IDENTIFIER);
							identifier.setText(newParamName);
							//l.remove(i);                      
							//l.add(i, identifier);
							temp.addContent(identifier);
						}
					} 
				}
	      		constant.removeNamespaceDeclaration(sNamespace);
	      	//} else {
				//temp.addContent(constant.detach());
	      	}
      	}

      	return math;*/
      	//return temp;
	}


	//Convert the rate equation in the math element into a VCell infix expression
	protected String addRateFormula(Element kinetics, Element component, Element role, NameList mangledParam, String varName) {
 
	    String exp = "";
	   
	    Element math = role.getChild(CELLMLTags.MATH, mathns);
	    if (math == null) {
			System.err.println("No equation for rate: " + varName + " in component: " + 
										component.getAttributeValue(CELLMLTags.name, sAttNamespace));
			return exp;
    	}
		Iterator j = math.getChild(MathMLTags.APPLY, mathns).getChildren().iterator();
		Element rhs = null, ci;
		while (j.hasNext()) {
			ci = (Element)j.next();
			if (!ci.getName().equals(MathMLTags.IDENTIFIER))
				continue;
			if (ci.getTextTrim().equals(varName)) {
				rhs = (Element)j.next();
				break;
			}
		}
		if (rhs == null) {
			System.err.println("No equation for rate: " + varName + " in component: " + 
										component.getAttributeValue(CELLMLTags.name, sAttNamespace));
			return exp;
		}
		Element trimmedMath = new Element(CELLMLTags.MATH, mathns).addContent((Element)rhs.detach());                                    
		exp = mathMLToString(component, trimmedMath, kinetics);
		exp = processMathExp(component, exp);
		//Now we need to check for any parameters in the equation who need to have their name changed
		exp = mangleMathExp(exp, mangledParam);
		//and add a minus sign to the equation - will result in a double minus ?
		exp = "(-(" + exp + "))";
		exp = flattenExp(exp);

		return exp;
    }


    protected void addRateVar(Element varRef) {

	    String componentName = varRef.getParent().getParent().getAttributeValue(CELLMLTags.name, sAttNamespace);
	    String variableName = varRef.getAttributeValue(CELLMLTags.variable, sAttNamespace);
	    String rateName = nm.getMangledName(componentName, variableName);
	    if (rateName.length() == 0)
	    	System.err.println("Unable to resolve the variable '" + variableName + 
		    				   "' in the component '" + componentName + "'");
		//we need the resolved component and variable name
		String component = nm.getFirstSecondKey(rateName);
		String variable	= nm.getMangledName(rateName, component);
		nlcomp.setMangledName(variable, component, "dummy-for-a-rate");  
    }


	protected void addReactionContext(Element simSpec) {


		Element rc = new Element(XMLTags.ReactionContextTag, tNamespace);
		Iterator i = locCompHash.values().iterator();
		Element lcs;
		while (i.hasNext()) {
			lcs = (Element)i.next();
			lcs.setAttribute(XMLTags.ForceConstantAttrTag, "false");
			lcs.setAttribute(XMLTags.EnableDiffusionAttrTag, "true");
			rc.addContent(lcs);
		}

		simSpec.addContent(rc);
	}


    protected Element addReactionElement(Element role, String varName, String compName, String type) {

		Element re = new Element(type, tNamespace);
		String temp = nm.getMangledName(compName, varName);
		if (temp.length() == 0) {
			System.err.println("Unable to resolve the variable '" + varName + "' in the component '" +
								compName + "'");
			System.err.println("Generated BioModel will not load in VC");
			return re; 
		}
		re.setAttribute(XMLTags.SpeciesContextRefAttrTag, temp);
		//For now, assume a stoichiometry of 1 if it is not 
        //specified in the CellML - if its not specified, then there
        //should be some maths which will override this anyway ??
        if (!type.equals(XMLTags.CatalystTag)) {
        	String stoich = role.getAttributeValue(CELLMLTags.stoichiometry, sAttNamespace);
         	if (stoich == null)
         		stoich = "1";
        	re.setAttribute(XMLTags.StoichiometryAttrTag, stoich);
        }
        
		return re;
    }


//units are set based on the assumption that all reactions are taking place in a single 'feature' compartment
	protected Element addReactionKinetics(Element role, String varName) {

    	Element component = role.getParent().getParent().getParent();
    	//For now, assume that there is only going to be one math element
    	//and that it contains the reaction kinetics?? Or, 
    	//iterate through all the math looking for the one with the reaction name?

    	Element kinetics = new Element(XMLTags.KineticsTag, tNamespace);
    	kinetics.setAttribute(XMLTags.KineticsTypeAttrTag, XMLTags.KineticsTypeGeneralKinetics);

		//we need to keep track of the parameters as they are declared, and their mangled versions. 
		NameList nlparamLocal = new NameList();
		NameList mangledParam = new NameList();

		//add all the variables declared in the component, but not already
      	//defined as compounds, as parameters in the kinetics
      	Element var;
      	String paramName, compName = component.getAttributeValue(CELLMLTags.name, sAttNamespace);
      	String rComponentName = null, rVariableName = null;
		Iterator i = component.getChildren(CELLMLTags.VARIABLE, sNamespace).iterator();
		while (i.hasNext()) {
			var = (Element)i.next();
			paramName = nm.getMangledName(compName, var.getAttributeValue(CELLMLTags.name, sAttNamespace));
			if (paramName.length() == 0) {
				System.err.println("Unable to resolve the variable '" + 
								    var.getAttributeValue(CELLMLTags.name, sAttNamespace) +"' in the component '" +
								    compName + "'");
			}
			//need to get the resolved component and variable names
			rComponentName = nm.getFirstSecondKey(paramName);
			rVariableName = nm.getMangledName(paramName, rComponentName);
			String declaredComp = nlcomp.getMangledName(rVariableName, rComponentName);  
			if (declaredComp.length() == 0) {
				//if it is a delta variable ignore it, the VCELL does not use those
				JDOMTreeWalker walker = new JDOMTreeWalker(component, new ElementFilter(CELLMLTags.ROLE));
				Element delta = walker.getMatchingElement(CELLMLTags.delta_variable, sAttNamespace, rVariableName);
				if (delta == null) {
					//the variable is not a compound, so make it a parameter
					//walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.COMPONENT));
					//Element comp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, rComponentName);
					//addReactionParam(kinetics, comp, nlparamLocal, mangledParam, paramName, rVariableName, rComponentName);
					addReactionParam(kinetics, component, nlparamLocal, mangledParam, paramName, rVariableName, rComponentName);
				}
			}
		}
		Element rate = new Element(XMLTags.ParameterTag, tNamespace);
		rate.setAttribute(XMLTags.NameAttrTag, varName);          //not using default rate name 'J'
		rate.setAttribute(XMLTags.ParamRoleAttrTag, XMLTags.ParamRoleReactionRateTag);
		String rateStr = addRateFormula(kinetics, component, role, mangledParam, varName);
		rateStr = scaleRateUnit(kinetics, rComponentName, role.getParent().getAttributeValue(CELLMLTags.variable), rateStr);
		rate.setAttribute(XMLTags.VCUnitDefinitionAttrTag, VCUnitDefinition.UNIT_uM_per_s.getSymbol());
		rate.setText(rateStr);
		kinetics.addContent(rate);

		return kinetics;
	}


    protected void addReactionParam(Element kinetics, Element comp, NameList nlParamLocal, 
	    							NameList mangledParam, String paramName, String varName, String compName) {
		    							
		//check if the parameter is already defined
		String declaredParam = nlParamLocal.getMangledName(varName, compName);
		if (declaredParam.length() != 0) 
			return;                                      //?
		//and add it to the list of parameters for this reaction 
		nlParamLocal.setMangledName(varName, compName, paramName);
		//need to check for a value/expression for the parameter
		JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.COMPONENT));         
		Element paramComp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, compName);         //diff from the passed component?
		walker = new JDOMTreeWalker(paramComp, new ElementFilter(CELLMLTags.VARIABLE));
		Element paramVar = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, varName);
		Element rhs = null, math = null, eq, ci;
		//assumes the possibility of more than one math expression?
		//may include mathml from elements other than rate?
		walker = new JDOMTreeWalker(paramComp, new ElementFilter(CELLMLTags.MATH));
		boolean found = false;
		while (walker.hasNext()) {
			math = (Element)walker.next();
			Element apply = math.getChild(MathMLTags.APPLY, mathns);
			if (apply == null) {
				System.err.println("Unable to find reaction rate param: " + paramName + " " + varName + " " + compName);
				break;
			}
			Iterator i = apply.getChildren().iterator();
			eq = (Element)i.next();
			if (eq.getName().equals(MathMLTags.EQUAL)) {
				ci = (Element)i.next();                            //element ordering?
				if (ci.getName().equals(MathMLTags.IDENTIFIER) && ci.getTextTrim().equals(CELLMLTags.rateRole)) {
					Element apply2 = (Element)i.next();
					JDOMTreeWalker j = new JDOMTreeWalker(apply2, new ElementFilter(MathMLTags.IDENTIFIER));
					while (j.hasNext()) {
						ci = (Element)j.next();
						//System.out.println(ci.getTextTrim() + " " + varName);
						if (ci.getTextTrim().equals(varName)) {
							rhs = ci;
							found = true;
						}
					}
				}
			}
			if (found)
				break;
		}
		addFormulaParam(kinetics, comp, paramVar, rhs, nlParamLocal, mangledParam, compName, paramName);
	}


    protected void addReactions(Element model) {

	    JDOMTreeWalker reactionWalker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.REACTION));
	    Element source, varRef, role, reaction = null;
	    String compName, varName, reactName, roleType;
	    while (reactionWalker.hasNext()) {
			source = (Element)reactionWalker.next();
			compName = source.getParent().getAttributeValue(CELLMLTags.name, sAttNamespace);    //component name
			reaction = new Element(XMLTags.SimpleReactionTag, tNamespace);
			//get a unique name and store it for future tests 
			reactName = nm.generateUnique(compName); 
			reaction.setAttribute(XMLTags.NameTag, reactName);
			//the single compartment is named with the model name identifier
			reaction.setAttribute(XMLTags.StructureAttrTag, sRoot.getAttributeValue(CELLMLTags.name, sAttNamespace));
			//Now set the participating species
			Iterator i = source.getChildren(CELLMLTags.VAR_REF, sNamespace).iterator();
			while (i.hasNext()) {
				varRef = (Element)i.next();
				varName = varRef.getAttributeValue(CELLMLTags.variable, sAttNamespace);
				Iterator j = varRef.getChildren(CELLMLTags.ROLE, sNamespace).iterator();
				while (j.hasNext()) {
					role = (Element)j.next();
					roleType = role.getAttributeValue(CELLMLTags.role, sAttNamespace);
					if (roleType.equals(CELLMLTags.reactantRole))
						reaction.addContent(addReactionElement(role, varName, compName, XMLTags.ReactantTag));
					else if (roleType.equals(CELLMLTags.productRole))
						reaction.addContent(addReactionElement(role, varName, compName, XMLTags.ProductTag));
					else if (roleType.equals(CELLMLTags.rateRole))
						reaction.addContent(addReactionKinetics(role, varName)); 
					else if (roleType.equals(CELLMLTags.modifierRole) || roleType.equals(CELLMLTags.activatorRole) || 
						    roleType.equals(CELLMLTags.inhibitorRole) || roleType.equals(CELLMLTags.catalystRole)) 
						reaction.addContent(addReactionElement(role, varName, compName, XMLTags.CatalystTag));
					
				}
			}
			model.addContent(reaction);
	    }
    }


	protected void addSimSpec() {

		Element simSpec = new Element(XMLTags.SimulationSpecTag, tNamespace);
		simSpec.setAttribute(XMLTags.NameTag, "DefaultApplication");
		addGeometry(simSpec);
		addGeomContext(simSpec);
		addReactionContext(simSpec);
		if ( (simSpec.getChild(XMLTags.GeometryTag, tNamespace) == null) ||
			 (simSpec.getChild(XMLTags.GeometryContextTag, tNamespace) == null) ||
			 (simSpec.getChild(XMLTags.ReactionContextTag, tNamespace) == null) )	
			return;
		tRoot.addContent(simSpec);
	}


	protected void addSpeciesUnitFactor(Element source, double factor) {

		Element param = new Element(XMLTags.ParameterTag, Namespace.getNamespace(Translator.VCML_NS));
		param.setAttribute(XMLTags.NameAttrTag, source.getAttributeValue(CELLMLTags.name) + "_unitFactor");
		param.setAttribute(XMLTags.VCUnitDefinitionAttrTag, VCUnitDefinition.UNIT_DIMENSIONLESS.getSymbol());
		param.setAttribute(XMLTags.ParamRoleAttrTag, XMLTags.ParamRoleUserDefinedTag);
		param.setText(String.valueOf(factor));
		
		spUnitParam.put(source.getAttributeValue(CELLMLTags.name), param);
	}


	protected void addUnits() {

		addUnits(sRoot);
		ArrayList components = new ArrayList(sRoot.getChildren(CELLMLTags.COMPONENT, sNamespace));
		for (int i = 0; i < components.size(); i++) {
			Element component = (Element)components.get(i);
			addUnits(component);
		}
	}


//for both model and component level units.
	protected void addUnits(Element owner) {

		ArrayList unitList = new ArrayList(owner.getChildren(CELLMLTags.UNITS, sNamespace));
		for (int i = 0; i < unitList.size(); i++) {
			Element lou = (Element)unitList.get(i);
		    VCUnitDefinition unit;
		    String unitName = lou.getAttributeValue(CELLMLTags.name, sAttNamespace);
		    String isBaseUnit = lou.getAttributeValue(CELLMLTags.baseUnits, sAttNamespace);
		    if ("yes".equals(isBaseUnit)) {
				unit = cbit.vcell.units.VCUnitTranslator.getBaseUnit(unitName);
		    } else {
				unit = cbit.vcell.units.VCUnitTranslator.CellMLToVCUnit(lou, sNamespace, sAttNamespace);
			}
			if (unit == null) {
				System.err.println("Unit: " + unitName + " not well defined.");
			}
	    }		
	}


//connection order?
	protected void addVarsAndConns() {

	    JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.VARIABLE));
		Element temp;
		String firstKey, secondKey;
		while (walker.hasNext()) {
	    	temp = (Element)walker.next();
	    	firstKey = temp.getParent().getAttributeValue(CELLMLTags.name, sAttNamespace);
	    	secondKey = temp.getAttributeValue(CELLMLTags.name, sAttNamespace);
	    	nm.add(firstKey, secondKey);
		}
		
		walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.CONNECTION));
		Element mapComp, mapVar;
		String comp1, comp2, var1, var2;
		boolean flag;

		while (walker.hasNext()) {
	    	temp = (Element)walker.next();
	    	mapComp = temp.getChild(CELLMLTags.MAP_COMP, Namespace.getNamespace("http://www.cellml.org/cellml/1.0#"));
	    	comp1 = mapComp.getAttributeValue(CELLMLTags.comp1, sAttNamespace);
	    	comp2 = mapComp.getAttributeValue(CELLMLTags.comp2, sAttNamespace);
	    	Iterator i = temp.getChildren(CELLMLTags.MAP_VAR, sNamespace).iterator();
	    	while (i.hasNext()) {
				mapVar = (Element)i.next();
				var1 = mapVar.getAttributeValue(CELLMLTags.var1, sAttNamespace);
				var2 = mapVar.getAttributeValue(CELLMLTags.var2, sAttNamespace);
				flag = isVisible(comp1, var1);
				boolean connected;
				if (flag) {  	//component1/variable1 is the "output" variable so connect component2/variable2 to it
		    		connected = nm.connect(comp2, var2, comp1, var1);
				} else {                 //connect to component2/variable2 if its the output
					flag = isVisible(comp2, var2);
					if (flag) {
		  		    	connected = nm.connect(comp1, var1, comp2, var2);
					} else {               //or allow connections to remain chained (in case the model defines groups)  
						flag = isMiddleComp(comp1, var1);
						if (flag) {
	  		    			connected = nm.connect(comp2, var2, comp1, var1);
						} else {
		  		    		connected = nm.connect(comp1, var1, comp2, var2);	
						}
					}
				}
				if (!connected)
					System.err.println("Error: Unable to connect variables: " + comp1 + " " + var1 + ", " + comp2 + " " + var2);
	    	}
		}
		nm.generateMangledNames();
    }


	//temporary method. 
	private void fixMathMLBug(Element math) {
		
		ArrayList l = new ArrayList();
		Iterator walker = new JDOMTreeWalker(math, new ContentFilter(ContentFilter.ELEMENT));
		while (walker.hasNext())
			l.add(walker.next());
      	Element temp;
      	for (int i = 0; i < l.size(); i++) {     
	      	temp = (Element)l.get(i);
	      	if (temp.getName().equals("cn")) {
		    	ArrayList atts = new ArrayList(temp.getAttributes());
				org.jdom.Attribute att;
				for (int j = 0; j < atts.size(); j++) {
					att = (org.jdom.Attribute)atts.get(j);
					temp.removeAttribute(att.getName(), sNamespace);
				}
				temp.removeNamespaceDeclaration(sNamespace);
	      	} 
      	}
	}


	/**
	This method iterates through the different reactions of the Biomodel, and modifies any 
	translated 'fraction' stoichiometry representation to an integer. It assumes that fractions
	are usually '0.5' or something like that, so it resolves the problem by multiplying the
	stoichiometry for all reaction elements by two. May not be scientifically valid. 
	*/
	private void fixStoich(Element model) {

		Iterator j = model.getChildren(XMLTags.SimpleReactionTag, tNamespace).iterator();
		while (j.hasNext()) {
			Element reaction = (Element)j.next();
			boolean flag = false;
			ArrayList re = new ArrayList(reaction.getChildren(XMLTags.ReactantTag, tNamespace));
			re.addAll(reaction.getChildren(XMLTags.ProductTag, tNamespace));
			for (int i = 0; i < re.size(); i++) {
				String stoich = ((Element)re.get(i)).getAttributeValue(XMLTags.StoichiometryAttrTag, tNamespace);
				try {                              //not a good practice in general
					Integer.parseInt(stoich);
				} catch (NumberFormatException e) {
					flag = true;
				}
			}
			try {
				if (flag) {
					for (int i = 0; i < re.size(); i++) {
						Element temp = (Element)re.get(i);
						String stoich = temp.getAttributeValue(XMLTags.StoichiometryAttrTag, tNamespace);
						stoich = stoich + "*2";
						temp.setAttribute(XMLTags.StoichiometryAttrTag, 
							              String.valueOf((int)new Expression(stoich).evaluateConstant()), tNamespace);
					}
				}
			} catch (ExpressionException e) {
				System.err.println("Unable to change stoich");
				e.printStackTrace();
			}
		}
	}


	private String flattenExp(String expStr) {

		String str;
		
		try {
			expStr = TokenMangler.getRestoredString(expStr.trim());
			Expression exp = new Expression(expStr);
			str = exp.flatten().infix();
			if (!expStr.equals(str)) {
				//System.out.println("Flattened expression: " + expStr + " --> " + str);
				expStr = str;
			}
		} catch (ExpressionException e) {
			System.err.println("Unable to flatten expression.");
			e.printStackTrace();
			return expStr;
		}

		return str;
	}


//spatial dimensions cannot be estimated. All species are assumed to be in one 'feature' compartment.
//basically, if the two units are not 'compatible to begin with, you are out of luck. 
	private String getInitial(String componentName, String variableName) {

		String initial = "0.0";
		JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.COMPONENT));
		Element temp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, componentName);
		if (temp != null) {
			walker = new JDOMTreeWalker(temp, new ElementFilter(CELLMLTags.VARIABLE));
			temp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, variableName);
			if (temp != null) {
				initial = temp.getAttributeValue(CELLMLTags.initial_value, sAttNamespace);
				if (initial == null) {
					return "0.0";
				}
			}
		}
		VCUnitDefinition cellUnit = null, VCunit = VCUnitDefinition.UNIT_uM;
		String unitSymbol = temp.getAttributeValue(CELLMLTags.units, sAttNamespace);
		if (unitSymbol != null) {
			cellUnit = cbit.vcell.units.VCUnitTranslator.getMatchingCellMLUnitDef(temp.getParent(), sAttNamespace, unitSymbol);
		}
		if (cellUnit == null) {
			System.err.println("Unable to find a unit definition with symbol: " + unitSymbol +
								" for species: " + variableName);
			return initial;
		}
		try {
			if (VCunit.isCompatible(cellUnit)) {
				double factor = VCunit.convertTo(1.0, cellUnit);
				if (factor != 1.0) {
					addSpeciesUnitFactor(temp, factor);
					return Expression.mult(new Expression(initial), new Expression(factor)).infix();
				} else {
					return new Expression(initial).infix();
				}
			} else {
				System.err.println("Inconsistent units: " + cellUnit.getSymbol() + " vs. " + VCunit.getSymbol());
				return initial;
			}
		} catch (ExpressionException e) {
			System.err.println("Unable to set the units for species: " + variableName);
			e.printStackTrace();
			return initial;
		}
    }


    private String getUniqueParamName(String name) {
 
		//Used to ensure that when reaction parameters are used more than once they have unique identifers
		String temp = nlparam.getMangledName(name, "dummy");
		while (temp.length() > 0) {
			//the name has already been used, so append an '_' and try again
			name = name + "_";
			temp = nlparam.getMangledName(name, "dummy");
		}
		//we have a name which has not been used, so add it to the hashtable and write it out/return it
		nlparam.setMangledName(name, "dummy", name);                //?   

		return name;
    }


	private boolean isMiddleComp(String comp, String var) {
 
		boolean flag = false;
		String privateInterface; 

		JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.COMPONENT));
		Element matchingComp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, comp);
		if (matchingComp != null) {
			walker = new JDOMTreeWalker(matchingComp, new ElementFilter(CELLMLTags.VARIABLE));
			Element matchingVar = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, var);
			if (matchingVar != null) {
				privateInterface = matchingVar.getAttributeValue(CELLMLTags.private_interface, sAttNamespace);
		    	if (CELLMLTags.outInterface.equals(privateInterface)) 
					flag = true;
			}
		}

		return flag;
	}


//the absence of the 'optional' interface atts will result in a 'true' return.
	private boolean isVisible(String comp, String var) {
 
		boolean flag = false;
		String publicInterface, privateInterface;

		JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.COMPONENT));
		Element matchingComp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, comp);
		if (matchingComp != null) {
			walker = new JDOMTreeWalker(matchingComp, new ElementFilter(CELLMLTags.VARIABLE));
			Element matchingVar = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, var);
			if (matchingVar != null) {
				publicInterface = matchingVar.getAttributeValue(CELLMLTags.public_interface, sAttNamespace);
				if (publicInterface == null)
					publicInterface = "";
				privateInterface = matchingVar.getAttributeValue(CELLMLTags.private_interface, sAttNamespace);
				if (privateInterface == null)
					privateInterface = "";
		    	if (!publicInterface.equals(CELLMLTags.inInterface) && !privateInterface.equals(CELLMLTags.inInterface)) 
					flag = true;
			}
		}

		return flag;
	}


// Takes an equation from a reaction and looks for parameter names
// that need to be mangled into their unique version for this specific reaction
	private String mangleMathExp(String exp, NameList mangledParam) {
		
		return mangledParam.mangleString(exp);

	}


	private String mathMLToString(Element mathParent, Element math, Element kinetics) {

		math = addMathMLUnits(mathParent, math, kinetics);
		String expressionStr = null;
		try {
			expressionStr = (new ExpressionMathMLParser(null)).fromMathML(math).infix();
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error getting expression string from mathML : "+e.getMessage());
		}
		return expressionStr;
	}


	//post-process a mathematical expression string to handle the variable mapping
	private String processMathExp(Element comp, String expStr) {

		try {
			Expression exp = new Expression(expStr);
			String [] symbols = exp.getSymbols();
			String mName;
			JDOMTreeWalker walker;
			Element temp;
			for (int i = 0; i < symbols.length; i++) {
				walker = new JDOMTreeWalker(comp, new ElementFilter(CELLMLTags.VARIABLE));  //needed?
				temp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, symbols[i]);
				if (temp != null) {
					mName = nm.getMangledName(comp.getAttributeValue(CELLMLTags.name, sAttNamespace), symbols[i]);
					if (!mName.equals(symbols[i]))
						expStr = TransFilter.replaceString(expStr, symbols[i], mName);
				}
			}
		} catch (ExpressionException e) {
			e.printStackTrace();
			return expStr;
		}	

		return expStr;
	}

	
	private void removeEmbeddedNamespace(Element math, Element kinetics) {
		
		ArrayList l = new ArrayList();
		Iterator walker = new JDOMTreeWalker(math, new ContentFilter(ContentFilter.ELEMENT));
		while (walker.hasNext())
			l.add(walker.next());
      	Element temp;
      	for (int i = 0; i < l.size(); i++) {     
	      	temp = (Element)l.get(i);
	      	if (temp.getName().equals("cn")) {
		    	ArrayList atts = new ArrayList(temp.getAttributes());
				org.jdom.Attribute att;
				for (int j = 0; j < atts.size(); j++) {
					att = (org.jdom.Attribute)atts.get(j);
					if (att.getName().equals(CELLMLTags.units)) {
						String unitSymbol = att.getValue();
						temp.removeAttribute(att.getName(), sNamespace);
					}
				}
				temp.removeNamespaceDeclaration(sNamespace);
	      	} 
      	}
	}


	//if rates are not compatible in the first place, then there is not much you can do.
	protected String scaleRateUnit(Element kinetics, String componentName, String variableName, String rateStr) {

		//get the variable element
		JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.COMPONENT));
		Element component = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, componentName);
		walker = new JDOMTreeWalker(component, new ElementFilter(CELLMLTags.VARIABLE));
		Element variable = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, variableName);
		VCUnitDefinition cellUnit = null;
		String unitSymbol = variable.getAttributeValue(CELLMLTags.units, sAttNamespace);
		if (unitSymbol != null) {                   //should never be the case.
			cellUnit = cbit.vcell.units.VCUnitTranslator.getMatchingCellMLUnitDef(variable.getParent(), sAttNamespace, unitSymbol);
		}
		if (cellUnit == null) {
			System.err.println("No unit for the rate variable: " + variableName);
			return rateStr;
		}
		//add all the species scaling units, previously saved.
		try {
			Expression rateExp = new Expression(rateStr);
			String symbols [] = rateExp.getSymbols();
			if (symbols != null) {
				for (int j = 0; j < symbols.length; j++) {
					if (spUnitParam.containsKey(symbols[j])) {
						Element fParam = (Element)spUnitParam.get(symbols[j]);
						kinetics.addContent(fParam);             //add as a param.
						Expression symbolExp = new Expression(symbols[j]);
						Expression newSymbolExp = Expression.invert(new Expression(fParam.getAttributeValue(XMLTags.NameAttrTag)));      //?
						newSymbolExp = Expression.mult(symbolExp, newSymbolExp); 
						rateExp.substituteInPlace(newSymbolExp, symbolExp);     //add to the expression
					}
				}
			}
			//scale the rate unit if needed.
			VCUnitDefinition VCunit = VCUnitDefinition.UNIT_uM_per_s;
			if (VCunit.isCompatible(cellUnit)) {
				double factor = VCunit.convertTo(1.0, cellUnit);
				if (factor != 1.0) {
					Element param = new Element(XMLTags.ParameterTag, Namespace.getNamespace(Translator.VCML_NS));
					param.setAttribute(XMLTags.NameAttrTag, variableName + "_unitFactor");
					param.setAttribute(XMLTags.VCUnitDefinitionAttrTag, VCUnitDefinition.UNIT_DIMENSIONLESS.getSymbol());
					param.setAttribute(XMLTags.ParamRoleAttrTag, XMLTags.ParamRoleUserDefinedTag);
					param.setText(String.valueOf(factor));
					return Expression.mult(new Expression(rateStr), new Expression(factor)).infix();
				} else {
					return new Expression(rateStr).infix();
				}
			} else {
				System.err.println("Inconsistent units: " + cellUnit.getSymbol() + " vs. " + VCunit.getSymbol());
				return rateStr;
			}
		} catch (ExpressionException e) {
			System.err.println("Unable to set the units for rate: " + variableName);
			e.printStackTrace();
			return rateStr;
		}
	}


	protected void translate() {

		addBioModel();	
	}


	private void trimAndMangleSource() {

		String elements [] = { CELLMLTags.MODEL, CELLMLTags.COMPONENT, CELLMLTags.VARIABLE, CELLMLTags.REACTION, 
						       CELLMLTags.VAR_REF, CELLMLTags.ROLE, CELLMLTags.CONNECTION, CELLMLTags.MAP_COMP, 
						       CELLMLTags.MAP_VAR, CELLMLTags.UNITS, CELLMLTags.UNIT};
		TransFilter ts = new TransFilter(elements, null, TransFilter.QUANCELLVC_MANGLE);
		ts.filter(sRoot);
	}
}
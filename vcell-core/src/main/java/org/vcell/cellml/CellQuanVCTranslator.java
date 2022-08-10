/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.cellml;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.ElementFilter;
import org.vcell.util.document.VCDocument;

import cbit.util.xml.JDOMTreeWalker;
import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VolVariable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.parser.MathMLTags;

/**
 * Implementation of the translation from a CellML Quantitative model to a VCML math model. The actual mapping is (ignoring possible mangling):
 model.name = MathModel.Name
 model.name = MathModel.MathDescription.Name
 model.name = MathModel.Geometry.SubVolume.Name
 model.name = MathModel.MathDescription.CompartmentSubDomain.Name   //only one compartment
 model.component.math.apply.apply.ci.Text = MathModel.MathDescription.CompartmentSubDomain.OdeEquation.Name
 defaults ("Unknown") = MathModel.MathDescription.CompartmentSubDomain.OdeEquation.SolutionType
 model.component.math.apply.[cn | apply] = MathModel.MathDescription.CompartmentSubDomain.OdeEquation.Rate.Text     //differential equation math
 model.component.variable.initial_amount = MathModel.MathDescription.CompartmentSubDomain.OdeEquation.Initial
 [model.component.math.apply.apply.ci.Text | model.component.variable.name] = MathModel.MathDescription.VolumeVariable.Name
 [model.component.math.apply.ci.Text | model.component.variable.name] = MathModel.MathDescription.Function.Name
 model.component.math.apply.[apply | ci] = MathModel.MathDescription.Function.Text
 
 - MathModel.Geometry: default values
 - MathModel.MathDescription.CompartmentSubDomain.BoundaryType: default values
 - Special treatment for rate functions
 * Creation date: (9/23/2003 12:03:24 PM)
 * @author: Rashad Badrawi
 */
public class CellQuanVCTranslator extends Translator {

	public static final String GEOM_NAME = "Default";
	public static final String SUBVOL_NAME = "Compartmental";

	protected NameManager nm;
	protected NameList nl;
	protected Namespace sNamespace = null;
	protected Namespace sAttNamespace = null;
	protected Namespace mathns = null;
	protected MathDescription mathDescription = null;
	private VariableHash varsHash = new VariableHash();
	private Vector<CellmlConnection> connectionsVector = new Vector<CellmlConnection>();
	
	//Internal class to store connection information
	public class CellmlConnection {
		public String component_1;				// component_1 name
		public String component_2;				// component_2 name
		public Vector<String> vars1_vector;		// vector of variable_1s of each connection
		public Vector<String> vars2_vector;		// vector of variable_2s of each connection

		public CellmlConnection(String comp1, String comp2, Vector<String> varV1, Vector<String> varV2){
			component_1 = comp1;
			component_2 = comp2;
			vars1_vector = varV1;
			vars2_vector = varV2;
		}

		public String getComponent_1() {
			return component_1;
		}
		public String getComponent_2() {
			return component_2;
		}
		public Vector<String> getVariables_1() {
			return vars1_vector;
		}
		public Vector<String> getVariables_2() {
			return vars2_vector;
		}
		// Does variable 'varName' in component 'comp' exist in this connection?
		public boolean isComponentVarPresent(String compName, String varName) {
			if (compName.equals(component_1) || compName.equals(component_2)) {
				// component is part of connection, now check if this connection has 'varName' in its list of vars
				// enough to check any one vars vector, since the var name has to be same in both var_1 and var_2 
				for (int i = 0; i < vars1_vector.size(); i++) {
					if (varName.equals(vars1_vector.get(i))) {
						return true;
					}
				}
			}
			return false;
		}
	};

	
	public CellQuanVCTranslator() { 
//		sNamespace = Namespace.getNamespace(CELLML_NS_PREFIX, CELLML_NS);
		sNamespace = Namespace.getNamespace(CELLMLTags.CELLML_NS);
		sAttNamespace = Namespace.getNamespace("");                   //dummy NS  
		mathns = Namespace.getNamespace(MATHML_NS);
		nm = new NameManager();
		nl = new NameList();
		schemaLocation = CELLMLTags.CELLML_NS + " " + Translator.DEF_CELLML_SL;
		schemaLocationPropName = XmlUtil.NS_SCHEMA_LOC_PROP_NAME;
    } 

/**
 * 	addCompartmentSubDomain : we pick the variable name by reading through the mathML. 
 * 	Redundancy in variable name with the volume variable?
 */
    
	protected void addCompartmentSubDomain() throws Exception {
	    String csdName = sRoot.getAttributeValue(CELLMLTags.name, sAttNamespace);
	    CompartmentSubDomain csd = new CompartmentSubDomain(csdName, CompartmentSubDomain.NON_SPATIAL_PRIORITY);

		Iterator<?> compElementIter = sRoot.getChildren(CELLMLTags.COMPONENT, sNamespace).iterator();
		// JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.COMPONENT));
	    Element comp, math;
	    String compName, varName, mangledName;
	    while (compElementIter.hasNext()) {
			comp = (Element)compElementIter.next();
			compName = comp.getAttributeValue(CELLMLTags.name, sAttNamespace);
			@SuppressWarnings("unchecked")
			Iterator<Element> mathIter = comp.getChildren(CELLMLTags.MATH, mathns).iterator();
			while (mathIter.hasNext()) {
				math = mathIter.next();
				Element apply, apply2, apply3, ci;
				//allow multiple 'apply' children.
				@SuppressWarnings("unchecked")
				Iterator<Element> applyIter = math.getChildren(MathMLTags.APPLY, mathns).iterator();
				while (applyIter.hasNext()) { 
					apply = applyIter.next();
					@SuppressWarnings("unchecked")
					ArrayList<Element> list = new ArrayList<Element>(apply.getChildren());
					if (list.size() < 3)
						continue;
					if (!(list.get(0)).getName().equals(MathMLTags.EQUAL))
						continue; 
					apply2 = list.get(1);
					if (!apply2.getName().equals(MathMLTags.APPLY))
						continue;
					@SuppressWarnings("unchecked")
					ArrayList<Element> list2 = new ArrayList<Element>(apply2.getChildren());
					if (list2.size() < 3)
						continue;
					if (!(list2.get(0)).getName().equals(MathMLTags.DIFFERENTIAL))
						continue;
					ci = list2.get(2);    //skip the time variable
					varName = ci.getTextTrim();
					apply3 = list.get(2);       //can be a constant
					
					mangledName = nm.getMangledName(compName, varName);
					Element trimmedMath = new Element(CELLMLTags.MATH, mathns).addContent(apply3.detach());
					fixMathMLBug(trimmedMath);
					Expression rateExp = null;
					try {
						rateExp = (new ExpressionMathMLParser(null)).fromMathML(trimmedMath, "t");
						rateExp = processMathExp(comp, rateExp);
						rateExp = rateExp.flatten();
						nl.mangleString(rateExp.infix());
					} catch (ExpressionException e) {
						e.printStackTrace(System.out);
						throw new RuntimeException(e.getMessage());
					}
					Expression initExp = new Expression(getInitial(comp, varName));
					Domain domain = null;
					OdeEquation ode = new OdeEquation(new VolVariable(mangledName,domain), initExp, rateExp);
					csd.addEquation(ode);
				}
			}
	    }
	    mathDescription.addSubDomain(csd);
    }


    private Function addFunction(Element temp, Element comp, String mangledName) {
 	    String expStr = null;
		Expression exp = null;
		Element parent = (Element) temp.getParent();
		Element sibling = parent.getChild(MathMLTags.APPLY, mathns);   
		if (sibling == null) {                          //check if its value is assigned to another variable (i.e. A = B)
			@SuppressWarnings("unchecked")
			ArrayList<Element> list = new ArrayList<Element>(parent.getChildren(MathMLTags.IDENTIFIER, mathns));
			if (list.size() == 2) {
				expStr = (list.get(1)).getTextTrim();
			}
			if (expStr == null || expStr.length() == 0) {
				expStr = parent.getChildText(MathMLTags.CONSTANT, mathns);
			}
			if (expStr == null || expStr.length() == 0) {                         //check if 'piecewise'
				sibling = parent.getChild(MathMLTags.PIECEWISE, mathns);
			}
			if (expStr != null) {
				try {
					exp = new Expression(expStr);
					exp = processMathExp(comp, exp);
					nl.mangleString(expStr);
				} catch (ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException(e.getMessage());
				} 
			}
		}
		if (sibling != null) { 
			Element trimmedMath = new Element(CELLMLTags.MATH, mathns).addContent(sibling.detach());
			fixMathMLBug(trimmedMath); 
			try {
				exp = (new ExpressionMathMLParser(null)).fromMathML(trimmedMath, "t");
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException(e.getMessage());
			}
			exp = processMathExp(comp, exp);
			expStr = exp.infix();
			nl.mangleString(expStr);
		}
		Domain domain = null;
		return new Function(mangledName, exp, domain);
    }


	//use a dummy compartmental geometry
	protected void addGeometry() {
		try {
			// Create dummy compartmental geometry for now ?
			Geometry geometry = new Geometry("Default", 0);
			geometry.getGeometrySpec().setExtent(new org.vcell.util.Extent(10.0, 10.0, 10.0));
			geometry.getGeometrySpec().setOrigin(new org.vcell.util.Origin(0.0, 0.0, 0.0));
			mathDescription.setGeometry(geometry);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error creating geometry for model : " + e.getMessage());
		}
	}

	protected MathModel addMathModel() {

		trimAndMangleSource();                                         
		// Create the Mathmodel and return
		String modelName = sRoot.getAttributeValue(CELLMLTags.name, sAttNamespace);
		MathModel mathModel = new MathModel(null);
		mathDescription = new MathDescription(modelName);
		// Read in variables and connections into NameManager
		addVarsAndConns();
		// process of adding mathDescription - consts, functions, VolVariables, etc.
	    processVariables();
		// create dummy compartmental geometry for now
		addGeometry();
	    try {
	    	// add subdomains, equations, etc.
	    	addCompartmentSubDomain();
	    	//set mathmodel name - doing it here, since a try-catch block is required.
			mathModel.setName(modelName);
	    } catch (Exception e) {
	    	e.printStackTrace(System.out);
	    	throw new RuntimeException("Error adding compartment subdomain : " + e.getMessage());
	    }
	    mathModel.setMathDescription(mathDescription);
	  	// refresh mathmodel
	  	mathModel.refreshDependencies();
	  	return mathModel;
	}


	private Function addRateFunction (Element varRef, String compName, String mangledName) {

	    Element role;  
		String stoich = null, roleAtt = null;
		//can only imply the delta function if the stoichiometry attribute is present also
		//not sure if there can be more than one, if not, could just pass in the 'role' element itself.
		@SuppressWarnings("unchecked")
		Iterator<Element> i = varRef.getChildren(CELLMLTags.ROLE, sNamespace).iterator();
		while (i.hasNext()) {
			role = i.next();
			stoich = role.getAttributeValue(CELLMLTags.stoichiometry, sAttNamespace);
			roleAtt = role.getAttributeValue(CELLMLTags.role, sAttNamespace);
			if (stoich != null)
				break;
		}
		if (stoich != null) {
			//Can only imply the function if a variable_ref with a role of "rate" exists
			JDOMTreeWalker reactionWalker = new JDOMTreeWalker((Element)varRef.getParent(), new ElementFilter(CELLMLTags.ROLE));
			Element rateRole = reactionWalker.getMatchingElement(CELLMLTags.role, sAttNamespace, CELLMLTags.rateRole);
			if (rateRole != null) {
				String rateVarName = ((Element)rateRole.getParent()).getAttributeValue(CELLMLTags.variable, sAttNamespace);
				StringBuffer formula = new StringBuffer("(");
				if (roleAtt.equals(CELLMLTags.productRole))
					formula.append("-(");
				formula.append(stoich + "*" + nm.getMangledName(compName, rateVarName));
				if (roleAtt.equals(CELLMLTags.productRole))
					formula.append(")");
				formula.append(")");
				try {
					Domain domain = null;
					return new Function(mangledName, new Expression(formula.toString()),domain);
					// fnsVector.add(function);
					// mathDescription.addVariable(function);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Error adding function (" + mangledName + ") to mathDexcription : " + e.getMessage());
				}
			}
		}
		return null;
    }


//add all the components and variables to the name manager, and resolve all connections 
    protected void addVarsAndConns() { 
 
	    JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.VARIABLE));
		Element temp;
		String firstKey, secondKey;
		while (walker.hasNext()) {
	    	temp = (Element)walker.next();
	    	firstKey = ((Element)temp.getParent()).getAttributeValue(CELLMLTags.name, sAttNamespace);
	    	secondKey = temp.getAttributeValue(CELLMLTags.name, sAttNamespace);
	    	nm.add(firstKey, secondKey); 
		}

		walker = new JDOMTreeWalker(sRoot, new ElementFilter(CELLMLTags.CONNECTION));
		Element mapComp, mapVar;
		String comp1, comp2, var1, var2;
		boolean flag;

		while (walker.hasNext()) {
	    	temp = (Element)walker.next();
	    	Vector<String> vars1_vect = new Vector<String>();
	    	Vector<String> vars2_vect = new Vector<String>();
	    	mapComp = temp.getChild(CELLMLTags.MAP_COMP, sNamespace);
	    	comp1 = mapComp.getAttributeValue(CELLMLTags.comp1, sAttNamespace);
	    	comp2 = mapComp.getAttributeValue(CELLMLTags.comp2, sAttNamespace);
	    	@SuppressWarnings("unchecked")
			Iterator<Element> i = temp.getChildren(CELLMLTags.MAP_VAR, sNamespace).iterator();
	    	while (i.hasNext()) {
				mapVar = i.next();
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
				if (!connected) {
					System.err.println("Error: Unable to connect variables: " + comp1 + " " + var1 + ", " + comp2 + " " + var2);
				} 
				else {	// they are connected; add vars to vars_vectors
					vars1_vect.add(var1);
					vars2_vect.add(var2);
				}
	    	}
	    	CellmlConnection newConnection = new CellmlConnection(comp1, comp2, vars1_vect, vars2_vect);
	    	connectionsVector.add(newConnection);
		}
		nm.generateMangledNames();
    }


    //temporary method. 
	private void fixMathMLBug(Element math) {
		
		ArrayList<Element> l = new ArrayList<Element>();
		@SuppressWarnings("unchecked")
		Iterator<Element> walker = new JDOMTreeWalker(math, new ContentFilter(ContentFilter.ELEMENT));
		while (walker.hasNext())
			l.add(walker.next());
      	Element temp;
      	for (int i = 0; i < l.size(); i++) {     
	      	temp = l.get(i);
	      	if (temp.getName().equals(MathMLTags.CONSTANT)) {
		    	@SuppressWarnings("unchecked")
				ArrayList<Attribute> atts = new ArrayList<Attribute>(temp.getAttributes());
				org.jdom.Attribute att;
				for (int j = 0; j < atts.size(); j++) {
					att = atts.get(j);
					temp.removeAttribute(att.getName(), mathns);
				}
				temp.removeNamespaceDeclaration(mathns);
	      	} 
      	}
	}


    private String getInitial(Element comp, String variableName) {

		String initial = "0.0"; 
		Element temp;
		
		JDOMTreeWalker walker = new JDOMTreeWalker(comp, new ElementFilter(CELLMLTags.VARIABLE));
		temp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, variableName);
		if (temp != null) {
			initial = temp.getAttributeValue(CELLMLTags.initial_value, sAttNamespace);
			if (initial == null || initial.length() == 0)
				initial = "0.0";
		}
		
		return initial;
    }


    private Element getMatchingVarRef(Element comp, String varName) { 

		JDOMTreeWalker walker = new JDOMTreeWalker(comp, new ElementFilter(CELLMLTags.ROLE));
		Element temp = walker.getMatchingElement(CELLMLTags.delta_variable, sAttNamespace, varName);
		if (temp != null) {
			return (Element) temp.getParent();
		} else {
			return null;
		}
    } 
 
	private Element getMathElementIdentifier(Element comp, String varName, String elementName) {
 
//		JDOMTreeWalker i = new JDOMTreeWalker(comp, new ElementFilter(CELLMLTags.MATH, Namespace.getNamespace(MATHML_NS)));
		@SuppressWarnings("unchecked")
		List<Element> mathElementList = comp.getChildren(CELLMLTags.MATH, mathns);
		Iterator<Element> mathElementIterator = mathElementList.iterator();
		
		Element math, apply, eq, apply2, diff, target = null;
		while (mathElementIterator.hasNext()) {
			math = mathElementIterator.next();
			//this first 'apply' loop because some components define all the vars under one 'math' element.
			@SuppressWarnings("unchecked")
			Iterator<Element> m = math.getChildren(MathMLTags.APPLY, mathns).iterator();
			while (m.hasNext()) {
				apply = m.next();
				@SuppressWarnings("unchecked")
				Iterator<Element> j = apply.getChildren().iterator();        //this element can also have an ID. 
				eq = j.next();                             //element ordering
				if (eq == null || !eq.getName().equals(MathMLTags.EQUAL))
					continue;
				while (j.hasNext()) {
					if (elementName.equals(MathMLTags.IDENTIFIER)) {             //one extra iteration
						target = j.next();
						//System.out.println("target:" + target.getName() + " " + target.getTextTrim() + " " + varName);
						if (!(target.getName().equals(MathMLTags.IDENTIFIER) && target.getTextTrim().equals(varName)))    //do the first 'ci' only
							target = null;
						break;
					} else if (elementName.equals(MathMLTags.DIFFERENTIAL)) {
						apply2 = j.next();
						if (!apply2.getName().equals(MathMLTags.APPLY))
							continue;
						@SuppressWarnings("unchecked")
						Iterator<Element> k = apply2.getChildren().iterator();
						diff = k.next();
						if (!diff.getName().equals(MathMLTags.DIFFERENTIAL))
							continue;
						while (k.hasNext()) {
							target = k.next();
							if (target.getName().equals(MathMLTags.IDENTIFIER) && target.getTextTrim().equals(varName))
								break;
							else
								target = null;                      //need another break ?
						}
					}
				}
				if (target != null)                       //target found, don't overwrite it!
					break;
			}
			if (target != null)
				break;
		}
		
		return target;
    }

	//Used to ensure that when reaction parameters are used more than once they have unique identifers
	private String getUniqueParamName(String name) {
		String newName = new String(name);
		String temp = nl.getMangledName(name, "dummy");
		while (temp.length() > 0) {
			//the name has already been used, so append an '_' and try again
			newName = newName + "_";
			temp = nl.getMangledName(newName, "dummy");
		}
		//we have a name which has not been used, so add it to the hashtable and write it out/return it
		nl.setMangledName(name, "dummy", newName);                   

		return newName;
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


	private boolean isTimeVar(String varName) { 

		boolean flag = false;
		JDOMTreeWalker walker = new JDOMTreeWalker(sRoot, new ElementFilter(MathMLTags.BVAR));
		while (walker.hasNext()) {
			Element bvar = (Element)walker.next();
			String timeVar = bvar.getChildText(MathMLTags.IDENTIFIER, mathns);
			if (varName.trim().equals(timeVar.trim())) {
				flag = true;
				break;
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


   	//post-process a mathematical expression string to handle the variable mapping
	private Expression processMathExp(Element comp, Expression exp) {
		try {
			String [] symbols = exp.getSymbols();
			String mName;
			JDOMTreeWalker walker;
			Element temp;
			if (symbols == null)
				return exp;
			for (int i = 0; i < symbols.length; i++) {
				walker = new JDOMTreeWalker(comp, new ElementFilter(CELLMLTags.VARIABLE));  
				temp = walker.getMatchingElement(CELLMLTags.name, sAttNamespace, symbols[i]);
				if (temp != null) {
					mName = nm.getMangledName(comp.getAttributeValue(CELLMLTags.name, sAttNamespace), symbols[i]);
					if (!mName.equals(symbols[i])) {
						exp = exp.getSubstitutedExpression(new Expression(symbols[i]), new Expression(mName));
					}
				}
				//added for a temporary fix for time:
				if (symbols[i].equals(CELLMLTags.timeVarCell)) {
					exp = exp.getSubstitutedExpression(new Expression(symbols[i]), new Expression(CELLMLTags.timeVar));
				}
			}
		} catch (ExpressionException e) {
			e.printStackTrace();
			return exp;
		}

		return exp;
	}

    protected void processVariables() { 
		@SuppressWarnings("unchecked")
		List<Element> compElementList = sRoot.getChildren(CELLMLTags.COMPONENT, sNamespace);
		Iterator<Element> compElementIterator = compElementList.iterator();
	    
	    Element comp, var, varRef, temp;
	    String compName, varName, publicIn, privateIn, mangledName; 
	    while (compElementIterator.hasNext()) {
			comp = compElementIterator.next();
			compName = comp.getAttributeValue(CELLMLTags.name, sAttNamespace);
			@SuppressWarnings("unchecked")
			List<Element> varElementList = comp.getChildren(CELLMLTags.VARIABLE, sNamespace);
			Iterator<Element> varElementIterator = varElementList.iterator();
			// clear const and volVars vectors for each component
			while (varElementIterator.hasNext()) {
				var = varElementIterator.next();
				publicIn = var.getAttributeValue(CELLMLTags.public_interface, sAttNamespace);
				privateIn = var.getAttributeValue(CELLMLTags.private_interface, sAttNamespace);
				if ( (publicIn != null && publicIn.equals(CELLMLTags.inInterface)) ||
					 (privateIn != null && privateIn.equals(CELLMLTags.inInterface)) )
					continue;
				varName = var.getAttributeValue(CELLMLTags.name, sAttNamespace);
			    mangledName = nm.getMangledName(compName, varName);
				String uniqueVarName = getUniqueParamName(mangledName);
				System.out.println("Vars: " + compName + "." + varName + " " + mangledName + " " + uniqueVarName);
			    //declare any differential free variables as VolumeVariable's
			    temp = getMathElementIdentifier(comp, varName, MathMLTags.DIFFERENTIAL);
			    try {
				    if (temp != null) {                                   //formula ignored at this point
				    	Domain domain = null;
				    	VolVariable volVar = new VolVariable(mangledName,domain);
				    	varsHash.addVariable(volVar);
						continue;
				    }
				    //any LHS variables of an eq operator are functions
				    temp = getMathElementIdentifier(comp, varName, MathMLTags.IDENTIFIER);
					if (temp != null) {
						Function fn = addFunction(temp, comp, mangledName);
						varsHash.addVariable(fn);
						continue;
					}
					//Handle delta variables as a special case function
					varRef = getMatchingVarRef(comp, varName);
					if (varRef != null) {
						Function rateFn = addRateFunction(varRef, compName, uniqueVarName);    //2 functions with same name?
						varsHash.addVariable(rateFn);
						continue;
					}
					//check if it is the time variable, if not add as a constant. No handling for a specific value for the time var.
					if (!isTimeVar(varName)) {
						Constant constant = new Constant(uniqueVarName, new Expression(getInitial(comp, varName)));
						varsHash.addVariable(constant);
					}
			    } catch (Exception e) {
			    	e.printStackTrace(System.out);
			    	throw new RuntimeException("Error adding variable to variablesHash : " + e.getMessage());
			    }
			}	// end iteration for VARIABLES
	    }	// end iteration for COMPONENTS
	    // add the variables from varsHash to mathDescription
	    try {
		    mathDescription.setAllVariables(varsHash.getAlphabeticallyOrderedVariables());
		} catch (Exception e) {
	    	e.printStackTrace(System.out);
	    	throw new RuntimeException("Error adding variables to mathDescription : " + e.getMessage());
		}
    }
    
	@Override
	protected VCDocument translate() {
		return (addMathModel());	
	}


	private void trimAndMangleSource() {

		String elements [] = { CELLMLTags.MODEL, CELLMLTags.COMPONENT, CELLMLTags.VARIABLE, CELLMLTags.REACTION, 
							   CELLMLTags.VAR_REF, CELLMLTags.MATH, CELLMLTags.ROLE, CELLMLTags.CONNECTION, CELLMLTags.MAP_COMP, 
							   CELLMLTags.MAP_VAR, CELLMLTags.UNITS, CELLMLTags.UNIT, MathMLTags.APPLY, MathMLTags.EQUAL,
							   MathMLTags.NOT_EQUAL, MathMLTags.GREATER, MathMLTags.LESS, MathMLTags.GREATER_OR_EQUAL, 
							   MathMLTags.LESS_OR_EQUAL, MathMLTags.AND, MathMLTags.OR, MathMLTags.NOT, MathMLTags.DIFFERENTIAL, 
							   MathMLTags.IDENTIFIER, MathMLTags.CONSTANT, MathMLTags.PIECEWISE, MathMLTags.PIECE, MathMLTags.OTHERWISE, 
							   MathMLTags.BVAR, MathMLTags.TIMES, MathMLTags.DIVIDE, MathMLTags.MINUS, MathMLTags.PLUS, 
							   MathMLTags.POWER, MathMLTags.EXP, MathMLTags.LN, MathMLTags.LOG_10, MathMLTags.LOGBASE, 
							   MathMLTags.ROOT, MathMLTags.ABS, MathMLTags.FALSE, MathMLTags.TRUE, MathMLTags.FACTORIAL, MathMLTags.CSYMBOL};
		TransFilter ts = new TransFilter(elements, null, TransFilter.QUANCELLVC_MANGLE);
		ts.filter(sRoot);
	}
}

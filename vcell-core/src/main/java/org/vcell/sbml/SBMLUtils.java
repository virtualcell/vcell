/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.vcell.util.ProgrammingException;
import org.vcell.util.TokenMangler;

import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLTags;

public abstract class SBMLUtils {

	public static final String SBML_VCELL_NS = XMLTags.SBML_VCELL_NS;
	public static final String SBML_SPATIAL_NS_PREFIX = XMLTags.SBML_SPATIAL_NS_PREFIX;

	public static class SBMLUnitParameter implements SymbolTableEntry {
		private String paramName = null;
		private cbit.vcell.parser.Expression paramExpression = null;
		private cbit.vcell.parser.NameScope paramNameScope = null;
		private cbit.vcell.units.VCUnitDefinition paramUnitDefinition = null;
		
		public SBMLUnitParameter(String argName, Expression argExpr,  VCUnitDefinition argUnitDefn, NameScope argNameScope) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			this.paramName = argName;
			this.paramExpression = argExpr;
			this.paramUnitDefinition = argUnitDefn;
			this.paramNameScope = argNameScope;
		}
		public SBMLUnitParameter(String argName, Expression argExpr,  VCUnitDefinition argUnitDefn) {
			this(argName, argExpr, argUnitDefn, null);
		}
		
		public cbit.vcell.parser.Expression getExpression() {
			return this.paramExpression;
		}
		public int getIndex() {
			return -1;
		}
		public String getName(){ 
			return this.paramName; 
		}   
		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return paramUnitDefinition;
		}
		public cbit.vcell.parser.NameScope getNameScope() {
			return paramNameScope;
		}
		public double getConstantValue() throws ExpressionException {
			return 0;
		}
		public boolean isConstant() throws ExpressionException {
			return false;
		}
	}
	
	public static void writeStringToFile(String xmlString, String filename, boolean bUseUTF8) throws IOException {
		File outputFile = new File(filename);
		OutputStreamWriter fileOSWriter = null;
		if (bUseUTF8){
			fileOSWriter = new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8");
		}else{
			fileOSWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
		}
		fileOSWriter.write(xmlString);
		fileOSWriter.flush();
		fileOSWriter.close();
	}

	public static Element readXML(Reader reader) throws IOException, SbmlException {
		try {
			SAXBuilder builder = new SAXBuilder(false);
			Document sDoc = builder.build(reader);
			Element root = sDoc.getRootElement();
			return root;
		}catch (JDOMException e){
			e.printStackTrace(System.out);
			throw new SbmlException(e.getMessage());
		}
	}
	
	/**
	 * safely convert double with zero fractional part to integer
	 * @param in
	 * @return in as integer
	 * @throws ProgrammingException if in is not nn.000000
	 */
	public static int ignoreZeroFraction(double in) {
		final int rval = (int) in;
		if (rval == in) {
			return rval;
		}
		throw new ProgrammingException("Value " + in + " not integer");
	}
	

	/**
	 * @return {@link TokenMangler#mangleToSName(String)}
	 * use TokenMangler.mangleToSName 
	 */
	@Deprecated
	public static String mangleToSName(String name) {
		return TokenMangler.mangleToSName(name);
	}

	/**
 * 	getConcUnitFactor : 
 * 		Calculates species concentration unit conversion from 'fromUnit' to 'toUnit'. 
 * 		If they are directly compatible, it computes the non-dimensional conversion factor/
 * 		If the 'fromUnit' is in item and 'toUnit' is in moles, it checks compatibility of fromUnit/KMOLE with toUnit.
 * 		Note : KMOLE is the Virtual VCell-defined reserved work (constant) = 1.0/602.214179.
 * 		If the 'toUnit' is in item and 'fromUnit' is in moles, it checks compatibility of fromUnit*KMOLE with toUnit.
 * 		 
 * @param fromUnit
 * @param toUnit
 * @return	non-dimensional (numerical) conversion factor
 * @throws ExpressionException
 */
public static SBMLUnitParameter getConcUnitFactor(String name, VCUnitDefinition fromUnit, VCUnitDefinition toUnit, ReservedSymbol kMoleSymbol) throws ExpressionException {
	SBMLUnitParameter sbmlUnitsParam = null;
	if (fromUnit.isCompatible(toUnit)) {
		Expression factorExpr = new Expression(fromUnit.convertTo(1.0, toUnit));
		sbmlUnitsParam = new SBMLUnitParameter(name, factorExpr, toUnit.divideBy(fromUnit));
	} else if (fromUnit.divideBy(kMoleSymbol.getUnitDefinition()).isCompatible(toUnit)) {
		// if SBML substance unit is 'item'; VC substance unit is 'moles'
		fromUnit = fromUnit.divideBy(kMoleSymbol.getUnitDefinition());
		Expression factorExpr = new Expression(fromUnit.convertTo(1.0, toUnit));
		factorExpr = Expression.div(factorExpr, kMoleSymbol.getExpression());
		sbmlUnitsParam = new SBMLUnitParameter(name, factorExpr, toUnit.divideBy(fromUnit));
	} else if (fromUnit.multiplyBy(kMoleSymbol.getUnitDefinition()).isCompatible(toUnit)) {
		// if VC substance unit is 'item'; SBML substance unit is 'moles' 
		fromUnit = fromUnit.multiplyBy(kMoleSymbol.getUnitDefinition());
		Expression factorExpr = new Expression(fromUnit.convertTo(1.0, toUnit));
		factorExpr = Expression.mult(factorExpr,kMoleSymbol.getExpression());
		sbmlUnitsParam = new SBMLUnitParameter(name, factorExpr, toUnit.divideBy(fromUnit));
	}  else {
		throw new RuntimeException("Unable to scale the species unit from: " + fromUnit + " -> " + toUnit.getSymbol());
	}
	return sbmlUnitsParam;
}

}

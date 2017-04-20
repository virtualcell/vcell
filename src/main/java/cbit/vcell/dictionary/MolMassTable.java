/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.dictionary;

/**
 * Insert the type's description here.
 * Creation date: (4/18/2005 1:25:16 PM)
 * @author: Frank Morgan
 */
public class MolMassTable {

	public static final String[] atoms = {
		"Ac","Ag","Al","Am","As","At","Au","Ba","Be","Bi","Bk","Br","Ca","Cd","Ce","Cf","Cl",
		"Co","Cr","Cs","Cu","Dy","Er","Es","Eu","Fe","Ga","Gd","Ge","Hf","Hg","Ho","In","Ir",
		"La","Li","Lr","Lu","Md","Mg","Mn","Mo","Na","Nb","Nd","Ni","No","Np","Os","Pa","Pb",
		"Pd","Pm","Po","Pr","Pt","Pu","Ra","Rb","Re","Rh","Ru","Sb","Sc","Se","Si","Sm","Sn",
		"Sr","Ta","Tb","Tc","Te","Th","Ti","Tl","Tm","Yb","Zn","Zr","B","C","F","H","I","K",
		"N","O","P","S","U","V","W","Y"
	};

	public static final cbit.vcell.parser.SimpleSymbolTable PeriodicTableOfElements =
		new cbit.vcell.parser.SimpleSymbolTable(atoms);
	
	public static final double[] mass = {
		227.0278,107.8682,26.98,243.0614,74.9216,209.987,196.966,137.327,9.012,208.98,247.07,79.904,
		40.078,112.411,140.115,251.0796,35.4527,58.933,51.996,132.905,63.546,162.5,167.26,252.083,151.965,
		55.847,69.723,157.25,72.61,178.49,200.59,164.93,114.82,192.22,138.906,6.941,260.1053,174.967,
		258.099,24.305,54.938,95.94,22.9,92.906,144.24,58.69,259.1009,237.048,190.2,231.036,207.2,106.42,
		146.915,208.9824,140.908,195.08,244.064,226.03,85.47,186.207,102.91,101.07,121.75,44.96,78.96,
		28.09,150.36,118.71,87.62,180.95,158.93,98.91,127.6,232.04,47.88,204.38,168.93,173.04,65.39,
		91.224,10.811,12.011,18.998,1.00794,126.905,39.0983,14.007,15.994,30.974,32.066,238.029,50.94,183.85,88.91
	};
/**
 * Insert the method's description here.
 * Creation date: (4/19/2005 12:27:41 PM)
 * @return double
 * @param formula java.lang.String
 */
public static double calcMass(String argFormula) throws cbit.vcell.parser.ExpressionException{

	if(argFormula == null ||argFormula.length() == 0){
		throw new IllegalArgumentException("Formula cannot be empty");
	}
	
	//Remove all spaces	
	String formula = null;
	StringBuffer sb = new StringBuffer(argFormula);
	do{
		formula = sb.toString();
		for(int i=0;i<sb.length();i+= 1){
			if(sb.charAt(i) == ' '){
				sb.deleteCharAt(i);
				break;
			}
		}
	}while(formula.length() != sb.length());

	//check matching parentheses
	int pCount = 0;
	int lpCount = 0;
	for(int i=0;i<formula.length();i+= 1){
		if(formula.charAt(i) == '('){pCount+= 1;lpCount+= 1;}
		if(formula.charAt(i) == ')'){pCount-= 1;}
		
	}
	if(pCount != 0){
		throw new RuntimeException("Parentheses mismatch in formula");
	}
		
	StringBuffer symbols = new StringBuffer();
	
	int index = 0;
	while(index != formula.length()){
		if(formula.charAt(index) == '+'){
			index+= 1;
			continue;
		}
		if(formula.charAt(index) == '('){
			symbols.append("+(");
			index+= 1;
			continue;
		}
		if(formula.charAt(index) == ')'){
			index+= 1;
			String numS = parseNumberString(formula.substring(index));
			symbols.append(")"+(numS.length() != 0?"*"+numS:""));
			index+= numS.length();
			continue;
		}
		boolean bFound = false;
		for(int i=0;i<atoms.length;i+= 1){
			if(formula.substring(index).startsWith(atoms[i])){
				index+=atoms[i].length();
				String numS = parseNumberString(formula.substring(index));
				symbols.append(
					(symbols.length() > 0 && 
						symbols.charAt(symbols.length()-1) != '+' &&
						symbols.charAt(symbols.length()-1) != '('?"+":"")+
					atoms[i]+(numS.length() != 0?"*"+numS:""));
				index+= numS.length();
				bFound = true;
				break;
			}
		}
		if(bFound){continue;}
		
		throw new RuntimeException("Error parsing formula "+formula);
	}

	cbit.vcell.parser.Expression exp = new cbit.vcell.parser.Expression(symbols.toString());
	exp.bindExpression(PeriodicTableOfElements);
	return exp.evaluateVector(mass);
}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2005 8:22:42 AM)
 */
private static String parseNumberString(String argS) {

	int index = 0;
	for(int i=0;i<argS.length();i+= 1){
		if(Character.isDigit(argS.charAt(index))){
			index+= 1;
		}else{
			break;
		}
	}
	return (index != 0?argS.substring(0,index):"");
}
}

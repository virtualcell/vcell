/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest;

import java.util.Enumeration;

import cbit.vcell.math.Equation;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
public class ConstructedSolutionTemplate {
	private SolutionTemplate solutionTemplates[] = null;

public ConstructedSolutionTemplate(cbit.vcell.math.MathDescription mathDesc) {
	super();
	initialize(mathDesc);
}

public SolutionTemplate getSolutionTemplate(String varName, String domainName) {
	for (int i = 0; i < solutionTemplates.length; i++){
		if (solutionTemplates[i].getVarName().equals(varName) && solutionTemplates[i].getDomainName().equals(domainName)){
			return solutionTemplates[i];
		}
	}
	return null;
}

public SolutionTemplate[] getSolutionTemplates() {
	return solutionTemplates;
}

private void initialize(cbit.vcell.math.MathDescription mathDesc) {
	//
	// for all valid combinations of variables/domains ... add a solution template with a default solution.
	//
	java.util.Vector solutionTemplateList = new java.util.Vector();
	
	Enumeration enumSubDomains = mathDesc.getSubDomains();
	while (enumSubDomains.hasMoreElements()){
		SubDomain subDomain = (SubDomain)enumSubDomains.nextElement();
		Enumeration enumEquations = subDomain.getEquations();
		while (enumEquations.hasMoreElements()){
			Equation equation = (Equation)enumEquations.nextElement();
			Variable var = equation.getVariable();
			String baseName = " "+var.getName()+"_"+subDomain.getName();
			String amplitudeName = baseName+"_A";
			String tau1Name = baseName+"_tau1";
			String tau2Name = baseName+"_tau2";
			if (equation instanceof OdeEquation){
				try {
					Expression exp = new Expression(amplitudeName+" * (1.0 + exp(-t/"+tau1Name+")*sin(2*"+Math.PI+"/"+tau2Name+"*t))");
					solutionTemplateList.add(new SolutionTemplate(equation.getVariable().getName(),subDomain.getName(),exp));
				}catch (cbit.vcell.parser.ExpressionException e){
					throw new RuntimeException(e.getMessage(), e);
				}
			}else if (equation instanceof PdeEquation){
				try {
					Expression exp = new Expression(amplitudeName+" * (1.0 + exp(-t/"+tau1Name+") + "+tau2Name+"*x)");
					solutionTemplateList.add(new SolutionTemplate(equation.getVariable().getName(),subDomain.getName(),exp));
				}catch (cbit.vcell.parser.ExpressionException e){
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	this.solutionTemplates = (SolutionTemplate[])org.vcell.util.BeanUtils.getArray(solutionTemplateList,SolutionTemplate.class);
}
}

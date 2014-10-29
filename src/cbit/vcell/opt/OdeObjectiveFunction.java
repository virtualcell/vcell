/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;
import org.vcell.util.DataAccessException;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;

import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
/**
 * Insert the type's description here.
 * Creation date: (8/3/2005 12:09:38 PM)
 * @author: Jim Schaff
 */
public class OdeObjectiveFunction extends ObjectiveFunction implements IssueSource {
	private MathDescription mathDescription = null;
	private SimpleReferenceData simpleReferenceData = null;

/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 2:39:38 PM)
 */
private OdeObjectiveFunction() {}


/**
 * OdeObjectiveFunction constructor comment.
 */
public OdeObjectiveFunction(MathDescription argMathDescription, ReferenceData argReferenceData) {
	super();
	if ((argMathDescription == null) || (argMathDescription.isSpatial())) {
		throw new RuntimeException("MathDescription should be non-null and non-spatial.");
	}
	this.mathDescription = argMathDescription;
	this.simpleReferenceData = new SimpleReferenceData(argReferenceData);
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 1:17:52 PM)
 * @param tokens cbit.vcell.math.CommentStringTokenizer
 */
public static OdeObjectiveFunction fromVCML(org.vcell.util.CommentStringTokenizer tokens) throws DataAccessException {
	try {
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase("{")){
			throw new DataAccessException("unexpected token "+token+" expecting "+"{");
		}
		MathDescription mathDescription = new MathDescription("odeSystem");
		mathDescription.setGeometry(new cbit.vcell.geometry.Geometry("geometry",0));
		mathDescription.read_database(tokens);

		SimpleReferenceData simpleReferenceData = SimpleReferenceData.fromVCML(tokens);

		// read "}" for end of file
		token = tokens.nextToken();
		if (!token.equals("}")){
			throw new RuntimeException("unexpected symbol '"+token+"', expecting '}'");
		}
		
		OdeObjectiveFunction odeObjectiveFunction = new OdeObjectiveFunction();
		odeObjectiveFunction.mathDescription = mathDescription;
		odeObjectiveFunction.simpleReferenceData = simpleReferenceData;
		return odeObjectiveFunction;

	}catch (DataAccessException e){
		throw e;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new org.vcell.util.DataAccessException("line #"+(tokens.lineIndex()+1)+" Exception: "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 2:30:24 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(IssueContext issueContext, java.util.List<Issue> issueList) {
	//
	// check for a data column named "t"
	//
	if (simpleReferenceData.findColumn("t")<0){
		issueList.add(new Issue(this,issueContext,IssueCategory.ParameterEstimationRefereceDataNoTime,"missing time data column with name 't'",Issue.SEVERITY_ERROR));
	}
	//
	// for those columns that are not "t", check for a corresponding math description Function or VolumeVariable
	//
	String[] columnNames = simpleReferenceData.getColumnNames();
	for (int i = 0; i < columnNames.length; i++){
		if (columnNames[i].equals("t")){
			continue;
		}
		Variable mathVar = mathDescription.getVariable(columnNames[i]);
		if (mathVar==null){
			issueList.add(new Issue(this,issueContext,IssueCategory.ParameterEstimationRefereceDataNotMapped,"data column '"+columnNames[i]+"' not found in math model",Issue.SEVERITY_ERROR));
		}else if (!(mathVar instanceof VolVariable) && !(mathVar instanceof Function)){
			issueList.add(new Issue(this,issueContext,IssueCategory.ParameterEstimationRefereceDataMappedImproperly,"data column '"+columnNames[i]+"' not a variable or function in math model",Issue.SEVERITY_ERROR));
		}
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:10:43 PM)
 * @return cbit.vcell.math.MathDescription
 */
public cbit.vcell.math.MathDescription getMathDescription() {
	return mathDescription;
}


/**
 * Insert the method's description here.
 * Creation date: (8/5/2005 11:40:58 AM)
 * @return cbit.vcell.opt.ReferenceData
 */
public ReferenceData getReferenceData() {
	return simpleReferenceData;
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:09:38 PM)
 * @return java.lang.String
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("OdeObjectiveFunction {\n");
	try {
		buffer.append(mathDescription.getVCML_database()+"\n");
	}catch (cbit.vcell.math.MathException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("unexpected error serializing mathDescription: "+e.getMessage());
	}

	buffer.append(simpleReferenceData.getVCML()+"\n");
	
	buffer.append("}\n");
	return buffer.toString();
}
}

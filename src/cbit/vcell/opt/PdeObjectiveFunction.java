package cbit.vcell.opt;
import java.io.File;
import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
/**
 * Insert the type's description here.
 * Creation date: (8/3/2005 12:09:38 PM)
 * @author: Jim Schaff
 */
public class PdeObjectiveFunction extends ObjectiveFunction {
	private MathDescription mathDescription = null;
	private SpatialReferenceData referenceData = null;
	private FieldDataIdentifierSpec[] fieldDataIDSs = null;
	private File workingDirectory = null;


/**
 * OdeObjectiveFunction constructor comment.
 */
public PdeObjectiveFunction(MathDescription argMathDescription, SpatialReferenceData argReferenceData, File workDir, FieldDataIdentifierSpec[] argFieldDataIDSs) {
	super();
	if ((argMathDescription == null) || !argMathDescription.isSpatial()) {
		throw new RuntimeException("MathDescription should be non-null and non-spatial.");
	}
	this.mathDescription = argMathDescription;
	this.referenceData = argReferenceData;
	workingDirectory = workDir;
	fieldDataIDSs = argFieldDataIDSs;
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 2:30:24 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(List<Issue> issueList) {
	//
	// check for a data column named "t"
	//
	if (referenceData.findVariable("t")<0){
		issueList.add(new Issue(this,IssueCategory.ParameterEstimationRefereceDataNoTime,"missing time data column with name 't'",Issue.SEVERITY_ERROR));
	}
	//
	// for those columns that are not "t", check for a corresponding math description Function or VolumeVariable
	//
	String[] variableNames = referenceData.getVariableNames();
	for (int i = 0; i < variableNames.length; i++){
		if (variableNames[i].equals("t")){
			continue;
		}
		Variable mathVar = mathDescription.getVariable(variableNames[i]);
		if (mathVar==null){
			issueList.add(new Issue(this,IssueCategory.ParameterEstimationRefereceDataNotMapped,"variable '"+variableNames[i]+"' not found in math model",Issue.SEVERITY_ERROR));
		}else if (!(mathVar instanceof VolVariable) && !(mathVar instanceof Function)){
			issueList.add(new Issue(this,IssueCategory.ParameterEstimationRefereceDataMappedImproperly,"variable '"+variableNames[i]+"' not a variable or function in math model",Issue.SEVERITY_ERROR));
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
public SpatialReferenceData getReferenceData() {
	return referenceData;
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:09:38 PM)
 * @return java.lang.String
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("PdeObjectiveFunction {\n");
	try {
		buffer.append(mathDescription.getVCML_database()+"\n");
	}catch (cbit.vcell.math.MathException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("unexpected error serializing mathDescription: "+e.getMessage());
	}

	buffer.append(referenceData.getVCML()+"\n");
	
	buffer.append("}\n");
	return buffer.toString();
}


public FieldDataIdentifierSpec[] getFieldDataIDSs() {
	return fieldDataIDSs;
}

public File getWorkingDirectory() {
	return workingDirectory;
}
}
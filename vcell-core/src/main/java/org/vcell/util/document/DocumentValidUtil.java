package org.vcell.util.document;

import java.util.Vector;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.OutputFunctionContext.OutputFunctionIssueSource;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;

public class DocumentValidUtil {

	public static void checkIssuesForErrors(SimulationContext simulationContext, boolean bIgnoreMathDescription) {
		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		simulationContext.getModel().gatherIssues(issueContext, issueList);
		simulationContext.gatherIssues(issueContext, issueList, bIgnoreMathDescription);
		checkIssuesForErrors(issueList);
	}

	public static void checkIssuesForErrors(BioModel bioModel) {
		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		bioModel.gatherIssues(issueContext, issueList);
		gatherMathOverrideIssues(bioModel, issueContext, issueList);
		checkIssuesForErrors(issueList);
	}

	private static void gatherMathOverrideIssues(BioModel bioModel, IssueContext issueContext, Vector<Issue> issueList) {
		Simulation[] sims = bioModel.getSimulations();
		for(Simulation sim : sims) {
			MathOverrides mo = sim.getMathOverrides();
			if(mo != null && mo.hasUnusedOverrides()) {
				String msg = "The Simulation has unused Math Overrides.";
				String tip = "Remove the unused Math Overrides.";
				issueList.add(new Issue(sim, issueContext, IssueCategory.Simulation_Override_NotFound, msg, tip, Issue.Severity.ERROR));
				break;
			}
		}
	}

	public static void checkIssuesForErrors(Vector<Issue> issueList) {
		final int MaxCounter = 5;
		String errMsg = "Unable to perform operation. Errors found: \n\n";
		boolean bErrorFound = false;
		int counter = 0;
		for (int i = 0; i < issueList.size(); i++){
			Issue issue = issueList.elementAt(i);
			if (issue.getSeverity() == Issue.Severity.ERROR){
				bErrorFound = true;
				Object issueSource = issue.getSource();
				if (!(issueSource instanceof OutputFunctionIssueSource)) {
					if(counter >= MaxCounter) {		// We display MaxCounter error issues 
						errMsg += "\n...and more.\n";
						break;
					}
					errMsg += issue.getMessage() + "\n";
					counter++;
				}
			}
		}
		for (int i = 0; i < issueList.size(); i++){
			Issue issue = issueList.elementAt(i);
			if (issue.getSeverity() == Issue.Severity.ERROR){
				bErrorFound = true;
				Object issueSource = issue.getSource();
				if (issueSource instanceof OutputFunctionIssueSource) {
					SimulationOwner simulationOwner = ((OutputFunctionIssueSource)issueSource).getOutputFunctionContext().getSimulationOwner();
					String funcName = ((OutputFunctionIssueSource)issueSource).getAnnotatedFunction().getDisplayName();
					if (simulationOwner instanceof SimulationContext) {
						String opErrMsg = "Output Function '" + funcName + "' in application '" + simulationOwner.getName() + "' "; 
						if (issue.getCategory().equals(IssueCategory.OUTPUTFUNCTIONCONTEXT_FUNCTION_EXPBINDING)) {
							opErrMsg += "refers to an unknown variable. Either the model changed or this version of VCell generates variable names differently.\n";
						}
						errMsg += opErrMsg;
					}
					errMsg += issue.getMessage() + "\n";
					break;		// we display no more than 1 issue of this type because it may get very verbose
				}
			}
		}
		if(bErrorFound) {
			errMsg += "\n See the Problems panel for a full list of errors and detailed error information.";
			throw new RuntimeException(errMsg);
		}
	}

}

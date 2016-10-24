/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;

import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.OutputFunctionContext.OutputFunctionIssueSource;
import cbit.vcell.solver.SimulationOwner;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class DocumentValid extends AsynchClientTask {
	public DocumentValid() {
		super("Checking document consistency", TASKTYPE_NONSWING_BLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
	if (documentWindowManager.getVCDocument() instanceof BioModel) {
		// try to successfully generate math and geometry region info
		BioModel bioModel = (BioModel)documentWindowManager.getVCDocument();
		SimulationContext scArray[] = bioModel.getSimulationContexts();
		if (scArray!=null) {
			MathDescription[] mathDescArray = new MathDescription[scArray.length];
			for (int i = 0; i < scArray.length; i++){
				//check if all structure sizes are specified
				scArray[i].checkValidity();
				//
				// compute Geometric Regions if necessary
				//
				cbit.vcell.geometry.surface.GeometrySurfaceDescription geoSurfaceDescription = scArray[i].getGeometry().getGeometrySurfaceDescription();
				if (geoSurfaceDescription!=null && geoSurfaceDescription.getGeometricRegions()==null){
					cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geoSurfaceDescription);
				}
				if (scArray[i].getModel() != bioModel.getModel()){
					throw new Exception("The BioModel's physiology doesn't match that for Application '"+scArray[i].getName()+"'");
				}
				//
				// create new MathDescription
				//
				MathDescription math = scArray[i].createNewMathMapping().getMathDescription();
				//
				// load MathDescription into SimulationContext 
				// (BioModel is responsible for propagating this to all applicable Simulations).
				//
				mathDescArray[i] = math;
			}
			hashTable.put("mathDescArray", mathDescArray);
		}
		// check issues for errors
		DocumentValid.checkIssuesForErrors(bioModel);

//		Vector<Issue> issueList = new Vector<Issue>();
//		IssueContext issueContext = new IssueContext();
//		bioModel.gatherIssues(issueContext,issueList);
//		for (int i = 0; i < issueList.size(); i++){
//			Issue issue = issueList.elementAt(i);
//			if (issue.getSeverity() == Issue.SEVERITY_ERROR){
//				String errMsg = "Error: ";
//				Object issueSource = issue.getSource();
//				if (issueSource instanceof OutputFunctionIssueSource) {
//					SimulationOwner simulationOwner = ((OutputFunctionIssueSource)issueSource).getOutputFunctionContext().getSimulationOwner();
//					String funcName = ((OutputFunctionIssueSource)issueSource).getAnnotatedFunction().getDisplayName();
//					if (simulationOwner instanceof SimulationContext) {
//						String opErrMsg = "Output Function '" + funcName + "' in application '" + simulationOwner.getName() + "' "; 
//						if (issue.getCategory().equals(IssueCategory.OUTPUTFUNCTIONCONTEXT_FUNCTION_EXPBINDING)) { 
//							opErrMsg += "refers to an unknown variable. Either the model changed or this version of VCell generates variable names differently.\n\n";
//						}
//						errMsg += opErrMsg;
//					} 
//				}
//				throw new Exception(errMsg + issue.getMessage());
//			}
//		}
	}
	if (documentWindowManager.getVCDocument() instanceof cbit.vcell.geometry.Geometry) {
		// try to successfully generate GeometricRegions if spatial and not present.
		cbit.vcell.geometry.Geometry geometry = (cbit.vcell.geometry.Geometry)documentWindowManager.getVCDocument();
		if (geometry.getGeometrySurfaceDescription()!=null && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
			try {
				cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geometry.getGeometrySurfaceDescription());
			}catch (Exception e){
				throw new Exception("Error determining regions in spatial geometry '"+geometry.getName()+"': \n"+e.getMessage());
			}
		}
	}
	if (documentWindowManager.getVCDocument() instanceof cbit.vcell.mathmodel.MathModel) {
		// try to successfully generate GeometricRegions if spatial and not present.
		cbit.vcell.mathmodel.MathModel mathModel = (cbit.vcell.mathmodel.MathModel)documentWindowManager.getVCDocument();
		cbit.vcell.geometry.Geometry geometry = mathModel.getMathDescription().getGeometry();
		if (geometry.getGeometrySurfaceDescription()!=null && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
			try {
				cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geometry.getGeometrySurfaceDescription());
			}catch (Exception e){
				throw new Exception("Error determining regions in spatial geometry '"+geometry.getName()+"': \n"+e.getMessage());
			}
		}
	}
}

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
	checkIssuesForErrors(issueList);
}
private static void checkIssuesForErrors(Vector<Issue> issueList) {
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

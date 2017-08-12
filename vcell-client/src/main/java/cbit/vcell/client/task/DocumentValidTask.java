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

import org.vcell.util.document.DocumentValidUtil;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class DocumentValidTask extends AsynchClientTask {
	public DocumentValidTask() {
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
		DocumentValidUtil.checkIssuesForErrors(bioModel);

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

}

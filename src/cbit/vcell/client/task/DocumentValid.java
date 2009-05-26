package cbit.vcell.client.task;
import cbit.vcell.client.*;
import java.util.*;
import cbit.vcell.client.desktop.*;
import cbit.vcell.mapping.*;
import cbit.vcell.math.*;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.document.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class DocumentValid extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Checking document consistency";
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_NONSWING_BLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws java.lang.Exception {
	Simulation[] simulations = (Simulation[])hashTable.get("simulations");
	if (simulations != null) {
		for (int i = 0; i < simulations.length; i++) {
			SolverDescription solverDescription = simulations[i].getSolverTaskDescription().getSolverDescription();
			if (solverDescription.equals(SolverDescription.CombinedSundials) || solverDescription.equals(SolverDescription.SundialsPDE)) {
				throw new RuntimeException(solverDescription.getDisplayLabel() + " is ONLY supported by VCell 4.7 or later. Please change the solver.");
			}			
		}
	}
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
	if (documentWindowManager.getVCDocument() instanceof BioModel) {
		// try to successfully generate math and geometry region info
		BioModel bioModel = (BioModel)documentWindowManager.getVCDocument();
		SimulationContext scArray[] = bioModel.getSimulationContexts();
		for (int i = 0;scArray!=null && i < scArray.length; i++){
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
			MathDescription math = null;
			if(!scArray[i].isStoch())
				math = (new MathMapping(scArray[i])).getMathDescription();
			else
				math = (new StochMathMapping(scArray[i])).getMathDescription();

			//
			// load MathDescription into SimulationContext 
			// (BioModel is responsible for propagating this to all applicable Simulations).
			//
			scArray[i].setMathDescription(math);
		}
		// check issues for errors
		Vector issueList = new Vector();
		bioModel.gatherIssues(issueList);
		for (int i = 0; i < issueList.size(); i++){
			cbit.util.Issue issue = (cbit.util.Issue)issueList.elementAt(i);
			if (issue.getSeverity() == cbit.util.Issue.SEVERITY_ERROR){
				throw new Exception("Error: "+issue.getMessage());
			}
		}
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


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:12 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:39:26 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}
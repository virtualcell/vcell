package cbit.vcell.client.task;

import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.Issue;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StochMathMapping;
import cbit.vcell.math.MathDescription;
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
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
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
				MathDescription math = null;
				if(!scArray[i].isStoch())
					math = (new MathMapping(scArray[i])).getMathDescription();
				else
					math = (new StochMathMapping(scArray[i])).getMathDescription();
	
				//
				// load MathDescription into SimulationContext 
				// (BioModel is responsible for propagating this to all applicable Simulations).
				//
				mathDescArray[i] = math;
			}
			hashTable.put("mathDescArray", mathDescArray);
		}
		// check issues for errors
		Vector<Issue> issueList = new Vector<Issue>();
		bioModel.gatherIssues(issueList);
		for (int i = 0; i < issueList.size(); i++){
			Issue issue = issueList.elementAt(i);
			if (issue.getSeverity() == Issue.SEVERITY_ERROR){
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

}
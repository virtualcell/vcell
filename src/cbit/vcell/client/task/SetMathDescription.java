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
public class SetMathDescription extends AsynchClientTask {
	public SetMathDescription() {
		super("Setting MathDescriptions", TASKTYPE_SWING_BLOCKING);
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
		MathDescription[] mathDescArray = (MathDescription[])hashTable.get("mathDescArray");
		if (scArray!=null && mathDescArray != null) {
			for (int i = 0; i < scArray.length; i++){
				scArray[i].setMathDescription(mathDescArray[i]);
			}
		}		
	}
}

}
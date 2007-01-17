package cbit.vcell.clientdb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.mathmodel.*;
import cbit.sql.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.server.*;
/**
 * Insert the type's description here.
 * Creation date: (11/14/00 5:47:31 PM)
 * @author: Jim Schaff
 */
public class ClientDocumentManagerTest extends cbit.vcell.client.test.ClientTester {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		cbit.vcell.client.server.ClientServerManager managerManager = mainInit(args,"ClientDocumentManagerTest",null);

		ClientDocumentManager docManager = (ClientDocumentManager)managerManager.getDocumentManager();

		testMathModel(docManager);

		testBioModel(docManager);

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of ClientDocumentManagerTest");
		exception.printStackTrace(System.out);
	}
	System.exit(0);
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:54:50 PM)
 * @param infos cbit.vcell.biomodel.BioModelInfo[]
 */
private static void show(BioModelInfo[] infos) {
	for (int i=0;i<infos.length;i++){
		System.out.println("BioInfo["+i+"] = "+infos[i]);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:54:50 PM)
 * @param infos cbit.vcell.biomodel.BioModelInfo[]
 */
private static void show(MathModelInfo[] infos) {
	for (int i=0;i<infos.length;i++){
		System.out.println("MathInfo["+i+"] = "+infos[i]);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 12:45:57 AM)
 * @param docManager cbit.vcell.clientdb.ClientDocumentManager
 */
private static void testBioModel(ClientDocumentManager docManager) throws Exception {
	BioModel bioModel = BioModelTest.getExample();
	docManager.save(bioModel,null);
	BioModelInfo bioModelInfos[] = docManager.getBioModelInfos();
	show(bioModelInfos);
	System.out.println("\n\n\nGETTING A COPY OF THE BIOMODEL\n\n\n");
	BioModel newBioModel = docManager.getBioModel(bioModelInfos[0]);
	System.out.println("\n\n\nGETTING A COPY OF THE BIOMODEL\n\n\n");
	newBioModel = docManager.getBioModel(bioModelInfos[0]);
	System.out.println("\n\n\nGETTING A COPY OF THE BIOMODEL\n\n\n");
	newBioModel = docManager.getBioModel(bioModelInfos[0]);
	System.out.println("\n\n\nGETTING A COPY OF THE BIOMODEL\n\n\n");
	BioModel modifiedBioModel = docManager.getBioModel(bioModelInfos[0]);

	//
	// change BioModel (just the 'characteristicSize' property of the first SimulationContext)
	// should only re-save the SimulationContext, not the physiology
	//
	System.out.println("\n\n\n      trying to save again, shouldn't re-save everything     \n\n\n");
	double elementSize = modifiedBioModel.getSimulationContexts(0).getCharacteristicSize().doubleValue();
	modifiedBioModel.getSimulationContexts(0).setCharacteristicSize(new Double(elementSize+0.01));
	modifiedBioModel = docManager.save(modifiedBioModel,null);

	//
	// the Physiology should be cached from before
	//
	KeyValue modelKey = modifiedBioModel.getModel().getVersion().getVersionKey();
	BioModelInfo modifiedBioInfo = new BioModelInfo(modifiedBioModel.getVersion(),modelKey,null);
	System.out.println("\n\n\nGETTING A COPY OF THE MODIFIED BIOMODEL\n\n\n");
	newBioModel = docManager.getBioModel(modifiedBioInfo);
	System.out.println("\n\n\nGETTING A COPY OF THE MODIFIED BIOMODEL\n\n\n");
	newBioModel = docManager.getBioModel(modifiedBioInfo);
	System.out.println("\n\n\nGETTING A COPY OF THE MODIFIED BIOMODEL\n\n\n");
	newBioModel = docManager.getBioModel(modifiedBioInfo);
}


/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 12:45:57 AM)
 * @param docManager cbit.vcell.clientdb.ClientDocumentManager
 */
private static void testMathModel(ClientDocumentManager docManager) throws Exception {
	MathModel mathModel = MathModelTest.getExample();
	docManager.save(mathModel,null);
	MathModelInfo mathModelInfos[] = docManager.getMathModelInfos();
	show(mathModelInfos);
	System.out.println("\n\n\nGETTING A COPY OF THE MATHMODEL\n\n\n");
	MathModel newMathModel = docManager.getMathModel(mathModelInfos[0]);
	System.out.println("\n\n\nGETTING A COPY OF THE MATHMODEL\n\n\n");
	newMathModel = docManager.getMathModel(mathModelInfos[0]);
	System.out.println("\n\n\nGETTING A COPY OF THE MATHMODEL\n\n\n");
	newMathModel = docManager.getMathModel(mathModelInfos[0]);
	System.out.println("\n\n\nGETTING A COPY OF THE MATHMODEL\n\n\n");
	MathModel modifiedMathModel = docManager.getMathModel(mathModelInfos[0]);

	//
	// change MathModel (just the 'annotation' property of the first Simulation)
	// should only re-save the Simulation, not the mathDescription
	//
	System.out.println("\n\n\n      trying to save again, shouldn't re-save everything     \n\n\n");
	String annot = modifiedMathModel.getSimulations(0).getDescription();
	modifiedMathModel.getSimulations(0).setDescription(annot+"___???");
	modifiedMathModel = docManager.save(modifiedMathModel,null);

	//
	// the Physiology should be cached from before
	//
	KeyValue mathDescriptionKey = modifiedMathModel.getMathDescription().getVersion().getVersionKey();
	MathModelInfo modifiedMathInfo = new MathModelInfo(modifiedMathModel.getVersion(),mathDescriptionKey,null);
	System.out.println("\n\n\nGETTING A COPY OF THE MODIFIED MATHMODEL\n\n\n");
	newMathModel = docManager.getMathModel(modifiedMathInfo);
	System.out.println("\n\n\nGETTING A COPY OF THE MODIFIED MATHMODEL\n\n\n");
	newMathModel = docManager.getMathModel(modifiedMathInfo);
	System.out.println("\n\n\nGETTING A COPY OF THE MODIFIED MATHMODEL\n\n\n");
	newMathModel = docManager.getMathModel(modifiedMathInfo);
}
}
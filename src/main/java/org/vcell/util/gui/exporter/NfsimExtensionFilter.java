package org.vcell.util.gui.exporter;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.vcell.solver.nfsim.NFsimXMLWriter;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;

@SuppressWarnings("serial")
public class NfsimExtensionFilter extends SelectorExtensionFilter {
	public NfsimExtensionFilter() {
		super(".xml", 	"NFSim XML file (*.xml)",
			Selector.SPATIAL,Selector.NONSPATIAL,Selector.STOCHASTIC,Selector.DETERMINISTIC); //specifies requires single application of any type
	}

	@Override
	public void writeBioModel(DocumentManager documentManager,
			BioModel bioModel, File exportFile,
			SimulationContext simulationContext) throws Exception {
		// TODO: get the first thing we find for now, in the future we'll need to modify ChooseFile 
		//       to only offer the applications / simulations with bngl content
		// This should be done by creating one or more additional Selector values and add the filtering logic to ChooseFile
		
		SimulationContext simContexts[] = bioModel.getSimulationContexts();
		Simulation selectedSim = simulationContext.getSimulations(0);
		//Simulation selectedSim = (Simulation)hashTable.get("selectedSimulation");
		SimulationTask simTask = new SimulationTask(new SimulationJob(selectedSim, 0, null),0);
		long randomSeed = 0;	// a fixed seed will allow us to run reproducible simulations
		//long randomSeed = System.currentTimeMillis();
		NFsimSimulationOptions nfsimSimulationOptions = new NFsimSimulationOptions();
		// we get the data we need from the math description
		boolean bUseLocationMarks = true;
		Element root = NFsimXMLWriter.writeNFsimXML(simTask, randomSeed, nfsimSimulationOptions, bUseLocationMarks);
		Document doc = new Document();
		doc.setRootElement(root);
		XMLOutputter xmlOut = new XMLOutputter();
		String resultString = xmlOut.outputString(doc);	
		FileUtils.writeStringToFile(exportFile, resultString);
	}
}

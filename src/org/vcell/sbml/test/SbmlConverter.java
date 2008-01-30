package org.vcell.sbml.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.vcell.sbml.vcell.StructureSizeSolver;

import cbit.util.TokenMangler;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Structure;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.xml.XmlHelper;

public class SbmlConverter {

	/**
	 * @param args
	 */
public static void main(String[] args) {
	if (args.length != 2) {
		System.out.println("Usage : -export|import path_of_input_file");
		System.exit(1);
	}
	try {
		String pathName = args[1];
		// Read xml file (Sbml or Vcml) into stringbuffer, pass the string into VCell's importer/exporter
		String xmlString = getXMLString(pathName);
		if (args[0].equals("-import")) {
			// Create a default VCLogger - SBMLImporter needs it
	        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
	            private StringBuffer buffer = new StringBuffer();
	            public void sendMessage(int messageLevel, int messageType) {
	                String message = cbit.util.xml.VCLogger.getDefaultMessage(messageType);
	                sendMessage(messageLevel, messageType, message);	
	            }
	            public void sendMessage(int messageLevel, int messageType, String message) {
	                System.err.println("LOGGER: msgLevel="+messageLevel+", msgType="+messageType+", "+message);
	                if (messageLevel == VCLogger.HIGH_PRIORITY) {
	                	throw new RuntimeException("Import failed : " + message);
	                }
	            }
	            public void sendAllMessages() {
	            }
	            public boolean hasMessages() {
	                return false;
	            }
	        };

	        // invoke SBMLImporter, which returns a Biomodel, convert that to VCML using XMLHelper
	        try {
				BioModel bioModel = (BioModel)XmlHelper.importSBML(logger, xmlString);
				SimulationContext simContext = (SimulationContext)bioModel.getSimulationContexts(0);  
				simContext.setMathDescription((new MathMapping(simContext)).getMathDescription());
				String vcmlString = XmlHelper.bioModelToXML(bioModel);
				
				int beginIndx = pathName.lastIndexOf("\\")+1;
				int endIndx = pathName.indexOf(".xml");
				String filePath = pathName.substring(0, beginIndx);
				String outputFileName = pathName.substring(beginIndx, endIndx);
				File outputFile = new File(filePath + outputFileName + ".vcml");
				java.io.FileWriter fileWriter = new java.io.FileWriter(outputFile);
				fileWriter.write(vcmlString);
				fileWriter.flush();
				fileWriter.close();
	        } catch (Exception e) {
	        	e.printStackTrace(System.err);
	        }
		} else if (args[0].equals("-export")) {
	        try {
				BioModel bioModel = XmlHelper.XMLToBioModel(xmlString);
				SimulationContext simContext = null;
				for (int i = 0; i < bioModel.getSimulationContexts().length; i++) {
					if (bioModel.getSimulationContexts(i).getGeometry().getDimension() == 0 && !bioModel.getSimulationContexts(i).isStoch()) {
						simContext = bioModel.getSimulationContexts(i);
						// check if structure sizes are set. If not, get a structure from the model, and set its size 
						// (thro' the structureMappings in the geometry of the simContext); invoke the structureSizeEvaluator 
						// to compute and set the sizes of the remaining structures.
						
						if (!simContext.getGeometryContext().isAllSizeSpecifiedPositive()) {
							Structure structure = simContext.getModel().getStructures(0);
							double structureSize = 1.0;
							StructureSizeSolver ssEvaluator = new StructureSizeSolver();
							StructureMapping structMapping = simContext.getGeometryContext().getStructureMapping(structure); 
							ssEvaluator.updateAbsoluteStructureSizes(simContext, structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());
						}
						
//						String sbmlString = XmlHelper.exportSBML(bioModel, 2, 1, simContext.getName());
						
						// Export the application itself, with default overrides
						String sbmlString = XmlHelper.exportSBML(bioModel, 2, 1, simContext, null);
						String filePath = pathName.substring(0, pathName.lastIndexOf("\\")+1);
						String outputFileName = TokenMangler.mangleToSName(bioModel.getName() + "_" + i);
						File outputFile = new File(filePath + outputFileName + ".xml");
						java.io.FileWriter fileWriter = new java.io.FileWriter(outputFile);
						fileWriter.write(sbmlString);
						fileWriter.flush();
						fileWriter.close();
						
						// Now export each simulation in the application that has overrides
						Simulation[] simulations = bioModel.getSimulations(simContext);
						for (int j = 0; j < simulations.length; j++) {
							if (simulations[j].getMathOverrides().hasOverrides()) {
								// Check for parameter scan and create simJob to pass into exporter
								for (int k = 0; k < simulations[j].getScanCount(); k++) {
									SimulationJob simJob = new SimulationJob(simulations[j], null, k);
									sbmlString = null;
									sbmlString = XmlHelper.exportSBML(bioModel, 2, 1, simContext, simJob);
									String fileName = TokenMangler.mangleToSName(outputFileName + "_" + j + "_" + k); 
									outputFile = new File(filePath + fileName + ".xml");
									fileWriter = new FileWriter(outputFile);
									fileWriter.write(sbmlString);
									fileWriter.flush();
									fileWriter.close();
								}
							}
						}
					}
				}
				System.out.println("Successfully translated model : " + pathName);
	        } catch (Exception e) {
	        	e.printStackTrace(System.err);
	        }				
		}
		System.exit(0);
	}catch (IOException e) {
		e.printStackTrace(System.err);
		System.exit(1);
	}
}

public static String getXMLString(String fileName) throws IOException {

	BufferedReader br = new BufferedReader(new FileReader(fileName));
	String temp;
	StringBuffer buf = new StringBuffer();
	while ((temp = br.readLine()) != null) {
		buf.append(temp);
		buf.append("\n");
	}
	
	return buf.toString();
}

}


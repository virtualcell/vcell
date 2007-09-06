package cbit.vcell.vcml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Structure;
import cbit.vcell.xml.XmlDialect;
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
		String xmlString = XmlUtil.getXMLString(pathName);
		if (args[0].equals("-import")) {
			// Create a default VCLogger - SBMLImporter needs it
	        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
	            private StringBuffer buffer = new StringBuffer();
	            public void sendMessage(int messageLevel, int messageType) {
	                String message = cbit.vcell.vcml.TranslationMessage.getDefaultMessage(messageType);
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
				BioModel bioModel = (BioModel)XmlHelper.importXMLVerbose(logger, xmlString, XmlDialect.SBML_L2V1);
				SimulationContext simContext = (SimulationContext)bioModel.getSimulationContexts(0);  
				simContext.setMathDescription((new MathMapping(simContext)).getMathDescription());
				String vcmlString = XmlHelper.bioModelToXML(bioModel);
				
				String outputFileName = pathName + ".vcml"; 
				File outputFile = new File(outputFileName);
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
					if (bioModel.getSimulationContexts(i).getGeometry().getDimension() == 0) {
						simContext = bioModel.getSimulationContexts(i);
						break;
					}
				}
				// check if structure sizes are set. If not, get a structure from the model, and set its size 
				// (thro' the structureMappings in the geometry of the simContext); invoke the structureSizeEvaluator 
				// to compute and set the sizes of the remaining structures.
				
//				StructureMapping[] sms = simContext.getGeometryContext().getStructureMappings();
//				boolean bStructSizeSet = true;
//				for (int i = 0; i < sms.length; i++) {
//					if ((sms[i].getSizeParameter() == null) || (sms[i].getSizeParameter().getConstantValue() <= 0.0)) {
//						bStructSizeSet = false;
//					}
//				}
//				if (!bStructSizeSet) {
//					Structure structure = simContext.getModel().getStructures(0);
//					double structureSize = 1.0;
//					cbit.vcell.vcml.StructureSizeSolver ssEvaluator = new cbit.vcell.vcml.StructureSizeSolver();
//					StructureMapping structMapping = simContext.getGeometryContext().getStructureMapping(structure); 
//					ssEvaluator.updateAbsoluteStructureSizes(simContext, structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());
//				}
				
				String sbmlString = XmlHelper.exportXML(bioModel, XmlDialect.SBML_L2V1, simContext.getName());
				String outputFileName = pathName + ".sbml"; 
				File outputFile = new File(outputFileName);
				java.io.FileWriter fileWriter = new java.io.FileWriter(outputFile);
				fileWriter.write(sbmlString);
				fileWriter.flush();
				fileWriter.close();
	        } catch (Exception e) {
	        	e.printStackTrace(System.err);
	        }				
		}
		System.out.println("Successfully translated model : " + pathName);
		System.exit(0);
	}catch (IOException e) {
		e.printStackTrace(System.err);
		System.exit(1);
	}
}
}


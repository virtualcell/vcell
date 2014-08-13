package org.vcell.multicellular;

import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.Species;
import org.vcell.sbml.vcell.SBMLImporter;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.xml.XmlHelper;

public class multicellular {

	public static void main(String[] args) {
		try {
			System.getProperties().setProperty("vcell.installDir", "C:\\developer\\eclipse\\workspace\\vcell\\");
			NativeLib.SBML.load();
			
			MulticellularModel mcModel = new MulticellularModel("myModel");
			CellModel cellModel = mcModel.createCellModel("cellType1");
			
			//
			// biochemical model for cellType
			//
			Compartment compartment = cellModel.createCompartment("comp1");
			Species s1 = cellModel.createSpecies("comp1","s1");
			Species s2 = cellModel.createSpecies("comp1","s2");
			Species s3 = cellModel.createSpecies("comp1","s3");
			Reaction reaction = cellModel.createReaction(compartment,"r1",new Species[] {s1,s2},new Species[] {s3}, "5*s1*s2-10*s3");
			
			//
			// initial conditions
			//
			s1.setInitialConcentration(1.0);
			s2.setInitialConcentration(2.0);
			s3.setInitialConcentration(3.0);
			
			CellInstance cellInstance = mcModel.world.createCellInstance(cellModel, "cell1", 1.0, 1.0, 1.0,5.0);
			
			mcModel.addBackgroundDomain();
			
			VCLogger logger = new VCLogger() {
				public boolean hasMessages() { return false; }
				public void sendAllMessages() {	}
				public void sendMessage(int messageLevel, int messageType) throws Exception {
					System.out.println(VCLogger.getDefaultMessage(messageType));
				}
				public void sendMessage(int messageLevel, int messageType, String message) throws Exception {
					System.out.println(message);
				}
			};
			System.out.println(mcModel.getSBMLText());

			mcModel.writeSBML("generatedSBML.xml");
			String sbmlText = mcModel.getSBMLText();
			
			SBMLImporter importer = new SBMLImporter("generatedSBML.xml", logger, true);
			BioModel bioModel = importer.getBioModel();
			String vcmlString = XmlHelper.bioModelToXML(bioModel);

			XmlUtil.writeXMLStringToFile(vcmlString,"generatedVCML.xml", true);

			System.out.println(vcmlString);
			
			mcModel.close();
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}

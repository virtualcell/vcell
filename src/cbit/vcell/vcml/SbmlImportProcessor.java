package cbit.vcell.vcml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.TranslationLogger;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Structure;
import cbit.vcell.xml.XmlDialect;
import cbit.vcell.xml.XmlHelper;

public class SbmlImportProcessor {

	/**
	 * @param args
	 */
	private static final String fileNames[] = {
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Ataullahkhanov1996_Adenylate.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Bakker2001_Glycolysis.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\bhartiya2003 tryptophan operon.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Bindschadler2001_coupled_Ca_oscillators.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Blum2000_LHsecretion_1.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Borghans1997_CaOscillation_model1.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Borghans1997_CaOscillation_model2.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Borghans1997_CaOscillation_model3.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Bornheimer2004_GTPaseCycle.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Brands2002_MonosaccharideCasein.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Brown2004_NGF_EGF_signaling.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Chassagnole2001_Threonine_Synthesis.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Chassagnole2002_Carbon_Metabolism.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Chen2004_CellCycle.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Cronwright2002_Glycerol_Synthesis.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Curien2003_MetThr_synthesis.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Curto1998_purineMetabol.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Edelstein1996_EPSP_AChEvent.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Edelstein1996_EPSP_AChSpecies.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Elowitz2000_Repressilator.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Ferreira2003_CML_generation2.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Field1974_Oregonator.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Fridlyand2003_Calcium_flux.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Fung2005_Metabolic_Oscillator.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Fuss2006_MitoticActivation.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Galazzo1990_pathway_kinetics.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Gardner1998_CellCycle_Goldbeter.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Goldbeter1991_MinMitOscil.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Goldbeter1991_MinMitOscil_ExplInact.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Goldbeter1995_CircClock.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Goldbeter2006_weightCycling.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\hodgkin-huxley squid-axon 1952.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Hoefnagel2002_PyruvateBranches.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Holzhutter2004_Erythrocyte_Metabolism.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Hornberg2005_ERKcascade.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Huang1996_MAPK_ultrasens.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Hynne2001 Glycolysis.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Keizer1996_Rynodine_receptor_adaptation.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Kholodenko1999_EGFRsignaling.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Kholodenko2000_MAPK_feedback.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Kofahl2004_pheromone.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Kongas2001_creatine.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Leloup1999_CircClock.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Leloup2003_CircClock_DD.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Leloup2003_CircClock_DD_REV-ERBalpha.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Leloup2003_CircClock_LD.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Leloup2003_CircClock_LD_REV-ERBalpha.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Levchenko2000_MAPK_noScaffold.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Levchenko2000_MAPK_Scaffold.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Locke2005_CircClock.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Marhl_CaOscillations.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Markevich2004_MAPK_orderedElementary.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Markevich2004_MAPK_orderedMM2kinases.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Markevich2004_MAPK_orderedMM.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Markevich2004_MAPK_phosphoRandomElementary.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Markevich2004_MAPK_phosphoRandomMM.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Markevich2005_MAPK_AllRandomElementary.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Martins2003_AmadoriDegradation.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Marwan_Genetics_2003.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Maurya2006_GTPaseCycle_reducedOrder.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Morrison1989_FolateCycle.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Nielsen1998_Glycolysis.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Novak1997_CellCycle.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Olsen2003_peroxidase.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Oxhamre2005_Ca_oscillation.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Poolman2004_CalvinCycle.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Proctor2006_telomere.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Rohwer2000_Phosphotransferase_System.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Rohwer2001_Sucrose.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Sasagawa2005_MAPK.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Scheper1999_CircClock.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Smolen2002_CircClock.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Smolen2004_CircClock.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Sneyd2002_IP3_Receptor.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Suh2004_KCNQ_Regulation.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Teusink2000_Glycolysis.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Thomsen1988_AdenylateCyclase_Inhibition.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Thomsen1989_AdenylateCyclase.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Tyson1991_CellCycle_2var.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Tyson1991_CellCycle_6var.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Tyson1999_CircClock.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Ueda2001_CircClock.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Vilar2002_Oscillator.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Xu2003_Phosphoinositide_turnover.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Yi2003_GproteinCycle.xml",
		"C:\\VCell\\SBML_Testing\\New_SBMLRepModels\\Yildirim2003_Lac_Operon.xml"
	};

	// Create a default VCLogger - SBMLImporter needs it
    static class TestLogger implements VCLogger {
        Vector<String> fatalErrorsVector = new Vector<String>();
        public void sendMessage(int messageLevel, int messageType) {
            String message = cbit.vcell.vcml.TranslationMessage.getDefaultMessage(messageType);
            sendMessage(messageLevel, messageType, message);	
        }
        public void sendMessage(int messageLevel, int messageType, String message) {
            if (messageLevel == TranslationLogger.HIGH_PRIORITY) {
            	fatalErrorsVector.addElement(messageType + ";\t" + message);
            }
            System.out.println(messageType + ";\t" + message);
        }
        public void sendAllMessages() {
        }
        public String showMessages() {
        	StringBuffer buffer = new StringBuffer();
        	for (int i = 0; i < fatalErrorsVector.size(); i++) {
				buffer.append(fatalErrorsVector.elementAt(i) + "\n");
			}
        	return buffer.toString();
        }
        public boolean hasMessages() {
        	return (fatalErrorsVector.size() > 0);
        }
    };

	public static void main(String[] args) {
        StringBuffer passBuffer = new StringBuffer();
        StringBuffer failBuffer = new StringBuffer();
        int passCnt = 0;
        int failCnt = 0;

        try {
    		for (int i = 0; i < fileNames.length; i++) {
    			String pathName = fileNames[i];
    			// Read xml file (Sbml) into stringbuffer, pass the string into VCell's SBML importer
    			String xmlString = XmlUtil.getXMLString(pathName);
    	        // invoke SBMLImporter, which returns a Biomodel, convert that to VCML using XMLHelper
    	        try {
    	        	TestLogger logger = new TestLogger();
    				BioModel bioModel = (BioModel)XmlHelper.importXMLVerbose(logger, xmlString, XmlDialect.SBML_L2V1);
    				SimulationContext simContext = (SimulationContext)bioModel.getSimulationContexts(0);  
    				simContext.setMathDescription((new MathMapping(simContext)).getMathDescription());
    				String vcmlString = XmlHelper.bioModelToXML(bioModel);
    				if (logger.hasMessages()) {
    					throw new Exception(logger.showMessages());
    					// logger.showMessages();
    				}
    				passCnt++;
    				passBuffer.append(pathName + " : PASSED\n");
    				// Write to VCML file
//    				int indx = pathName.lastIndexOf("\\");
//    				String newPathName = pathName.substring(0, indx) + "\\VCML";
//    				String newFileName = pathName.substring(indx+1) + ".vcml";
//    				String outputFileName = newPathName + "\\" + newFileName;
//    				XmlUtil.writeXMLString(vcmlString, outputFileName);
    	        } catch (Exception e) {
    	        	// e.printStackTrace(System.out);
    	        	failCnt++;
    				failBuffer.append("\n" + pathName + " : FAILED\n\t\t" + e.getMessage());
    	        }
    		}
    		
    		System.out.println("\n\n(not-validated) IMPORT PASSED : " + passCnt + "\n\n" + passBuffer.toString());
    		System.out.println("\n\n(not-validated) IMPORT FAILED : " + failCnt + "\n\n" + failBuffer.toString());

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

}


package org.vcell.sbml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import org.junit.Ignore;
import org.junit.Test;
import org.vcell.sbml.vcell.SBMLImporter;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;

@Ignore
public class SBMLImporterTest {

	public static void main(String[] args) {
		try {
			VCLogger vcl = new TLogger();
		SBMLImporter imp = new SBMLImporter("samp.sbml", vcl,false); 
		for (;;) {
			/*
			JFileChooser jfc = new JFileChooser( new File(System.getProperty("user.dir")));
			int returnVal = jfc.showOpenDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File f= jfc.getSelectedFile();
				BioModel bm = sa.importSBML(f);
				System.out.println(bm.getName());
			}
			*/
			BioModel bm = imp.getBioModel(); 
			System.out.println(bm.getName());
		}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public enum FAULT {
		RESERVED_WORD,
		DELAY,
		NONINTEGER_STOICH,
		INCONSISTENT_UNIT_SYSTEM,
		EXPRESSION_BINDING_EXCEPTION,
		XOR_MISSING,
		JSBML_ERROR		// seems like a bug in jsbml  RenderParser.processEndDocument() ... line 403 ... wrong constant for extension name
	};
	
	
	
	@Test
	public void testImport() throws XMLStreamException, IOException{
		HashMap<Integer,FAULT> faults = new HashMap();
		faults.put(6, FAULT.RESERVED_WORD);
		faults.put(15, FAULT.RESERVED_WORD);
		faults.put(92, FAULT.RESERVED_WORD);
		faults.put(114, FAULT.RESERVED_WORD);
		faults.put(115, FAULT.RESERVED_WORD);
		faults.put(117, FAULT.RESERVED_WORD);
		faults.put(148, FAULT.RESERVED_WORD);
		faults.put(154, FAULT.RESERVED_WORD);
		faults.put(155, FAULT.RESERVED_WORD);
		faults.put(154, FAULT.RESERVED_WORD);
		faults.put(155, FAULT.RESERVED_WORD);
		faults.put(156, FAULT.RESERVED_WORD);
		faults.put(157, FAULT.RESERVED_WORD);
		faults.put(158, FAULT.RESERVED_WORD);
		faults.put(159, FAULT.RESERVED_WORD);
		faults.put(274, FAULT.RESERVED_WORD);
		faults.put(279, FAULT.RESERVED_WORD);
		faults.put(282, FAULT.RESERVED_WORD);
		faults.put(288, FAULT.RESERVED_WORD);
		faults.put(346, FAULT.RESERVED_WORD);
		faults.put(24, FAULT.DELAY);
		faults.put(25, FAULT.DELAY);
		faults.put(34, FAULT.DELAY);
		faults.put(196, FAULT.DELAY);
		faults.put(39, FAULT.NONINTEGER_STOICH);
		faults.put(59, FAULT.NONINTEGER_STOICH);
		faults.put(63, FAULT.NONINTEGER_STOICH);
		faults.put(81, FAULT.NONINTEGER_STOICH);
		faults.put(145, FAULT.NONINTEGER_STOICH);
		faults.put(151, FAULT.NONINTEGER_STOICH);
		faults.put(199, FAULT.NONINTEGER_STOICH);
		faults.put(206, FAULT.NONINTEGER_STOICH);
		faults.put(232, FAULT.NONINTEGER_STOICH);
		faults.put(244, FAULT.NONINTEGER_STOICH);
		faults.put(246, FAULT.NONINTEGER_STOICH);
		faults.put(110, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(178, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(228, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(245, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(252, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(262, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(263, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(264, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(267, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(283, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(300, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(316, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(317, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(319, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(322, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(323, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(337, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(327, FAULT.EXPRESSION_BINDING_EXCEPTION);
		faults.put(340, FAULT.XOR_MISSING);
		faults.put(248, FAULT.EXPRESSION_BINDING_EXCEPTION);
		faults.put(305, FAULT.EXPRESSION_BINDING_EXCEPTION);
		faults.put(353, FAULT.NONINTEGER_STOICH);
		faults.put(367, FAULT.RESERVED_WORD);
		faults.put(382, FAULT.RESERVED_WORD);
		faults.put(383, FAULT.NONINTEGER_STOICH);
		faults.put(384, FAULT.NONINTEGER_STOICH);
		faults.put(385, FAULT.NONINTEGER_STOICH);
		faults.put(386, FAULT.NONINTEGER_STOICH);
		faults.put(387, FAULT.NONINTEGER_STOICH);
		faults.put(388, FAULT.NONINTEGER_STOICH);
		faults.put(392, FAULT.NONINTEGER_STOICH);
		faults.put(401, FAULT.NONINTEGER_STOICH);
		faults.put(402, FAULT.RESERVED_WORD);
		faults.put(403, FAULT.RESERVED_WORD);
		faults.put(405, FAULT.INCONSISTENT_UNIT_SYSTEM);
		
		
		
		faults.put(539, FAULT.JSBML_ERROR);
		
		File[] sbmlFiles = SBMLUnitTranslatorTest.getBiomodelsCuratedSBMLFiles();
//		File[] sbmlFiles = new File[] {
//				new File("/Users/schaff/Documents/workspace-maven/BioModels_Database-r30_pub-sbml_files/curated/BIOMD0000000001.xml"),
//				new File("/Users/schaff/Documents/workspace-maven/BioModels_Database-r30_pub-sbml_files/curated/BIOMD0000000101.xml"),
//			new File("/Users/schaff/Documents/workspace-maven/sbml-test-suite/cases/semantic/00001/00001-sbml-l3v1.xml")
//		};
		
		VCLogger vcl = new TLogger();
		int start = 401;
		for (int index=start; index<sbmlFiles.length; index++){
			File sbmlFile = sbmlFiles[index];
			int sbmlNumber = Integer.parseInt(sbmlFile.getName().substring(6).replace(".xml", ""));
			
			if (faults.containsKey(sbmlNumber)){
				System.err.println("skipping this model, "+faults.get(sbmlNumber).name());
				continue;
			}
			System.out.println("testing "+sbmlFile);
			SBMLImporter importer = new SBMLImporter(sbmlFile.getAbsolutePath(), vcl, false);
			BioModel bioModel = importer.getBioModel();
		}
		
	}
}

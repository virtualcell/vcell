package org.vcell.sbml;

import java.io.File;
import java.util.Enumeration;

import javax.swing.JFileChooser;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.sbml.vcell.SBMLStandaloneImporter;
import org.vcell.util.logging.Logging;

import cbit.vcell.biomodel.BioModel;

public class SBMLStandaloneImporterTest {

	public static void main(String[] args) {
		Logging.init( );
		try {
		SBMLStandaloneImporter sa = new SBMLStandaloneImporter();
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
			BioModel bm = sa.importSBML(new File("samp.sbml"));
			System.out.println(bm.getName());
		}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

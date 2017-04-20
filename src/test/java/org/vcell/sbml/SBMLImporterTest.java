package org.vcell.sbml;

import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.logging.Logging;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.ResourceUtil;

public class SBMLImporterTest {

	public static void main(String[] args) {
		Logging.init( );
		ResourceUtil.setNativeLibraryDirectory();
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
	
	static class TLogger extends VCLogger {

		@Override
		public boolean hasMessages() {
			return false;
		}

		@Override
		public void sendAllMessages() {
		}

		@Override
		public void sendMessage(Priority p, ErrorType et, String message)
				throws Exception {
			System.err.println(p + " " + et + ": "  + message);
		}

	}
}

package cbit.vcell.vcml.test;
import cbit.gui.PropertyLoader;
import cbit.util.DataAccessException;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ClientTester;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.vcml.Translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.jdom.Document;
 
/**
 * Loader utility for external 'ML' files (currently CellML and SBML) in the VC.
 * Creation date: (8/27/2003 5:30:17 PM)
 * @author: Rashad Badrawi
 */
public class Loader {

	private static final String SBML_ACCNT = "SBMLRep";
	private static final String CELLML_ACCNT = "CellMLRep";
	
	private boolean flushModel = false;
	private boolean flushAll = false;
	private ClientServerManager csManager;
	private BioModelInfo bmInfos[];
	private MathModelInfo mmInfos[];
	private PrintWriter pw;


	public Loader(String uri, String mode, String flush, PrintStream outputStream) throws Exception {

		long stime = System.currentTimeMillis();
		boolean bool = new Boolean(flush).booleanValue();
		pw = new PrintWriter(outputStream);
		String connArgs [] = new String[3];
		connArgs[0] = "-local";							//connArgs[0] = "-jms"; and run DatabaseServer at the same time. 
		//for SBML
		if (mode.equals(Translator.SBVC_1) || mode.equals(Translator.SBVC_2)) {
			connArgs[1] = SBML_ACCNT;
			connArgs[2] = SBML_ACCNT;
		} else if (mode.equals(Translator.CELL_QUAL_VC) || mode.equals(Translator.CELL_QUAN_VC)) {
			connArgs[1] = CELLML_ACCNT;
			connArgs[2] = CELLML_ACCNT;
		}
		try {
			csManager = (ClientServerManager)ClientTester.mainInit(connArgs,"Loader", new javax.swing.JFrame());
			ClientDocumentManager dm = (ClientDocumentManager)csManager.getDocumentManager();
			if (!mode.equals(Translator.CELL_QUAN_VC))
				bmInfos = dm.getBioModelInfos();
			else
				mmInfos = dm.getMathModelInfos();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		File f = new File(uri);
		File files [] = f.listFiles();
		if (files == null) {
			if (bool)
				flushModel = true;
				load(f, mode, connArgs);            //still need the functionality to set a single model public...
		} else {
			for (int i = 0; i < files.length; i++) {
				if (bool && i == 0)           //first time
					flushAll = true;
				load(files[i], mode, connArgs);          
		 	}
			setModelsPublic(mode, connArgs[1]);
		}
	}


	protected void flushBioModel(String ownerName, BioModel bm) throws DataAccessException {

		BioModel temp;

		try {
			for (int i = 0; i < bmInfos.length; i++) {
				if (bmInfos[i].getVersion().getOwner().getName().equals(ownerName)) {          //exclude public models
					if (bm != null) {
						temp = csManager.getDocumentManager().getBioModel(bmInfos[i]);
						if (temp.getName().equals(bm.getName())) {
							System.out.println("BioModel:" + temp.getName() + " " + bmInfos[i].getModelKey().toString() + " is being deleted");
							csManager.getDocumentManager().delete(bmInfos[i]);
						}
					} else {
						System.out.println("BioModel:" + bmInfos[i].getModelKey().toString() + " is being deleted");
						csManager.getDocumentManager().delete(bmInfos[i]);
					}
				}
			}
		} catch (DataAccessException e) {
			System.err.println("Unable to flush model(s): " + ownerName + " " + bm);
			throw e;
		}	
	}


	protected void flushMathModel(String ownerName, MathModel mm) throws DataAccessException {

		MathModel temp;

		try {
			for (int i = 0; i < mmInfos.length; i++) {
				if (mmInfos[i].getVersion().getOwner().getName().equals(ownerName)) {          //exclude public models
					if (mm != null) {
						temp = csManager.getDocumentManager().getMathModel(mmInfos[i]);
						if (temp.getName().equals(mm.getName())) {
							System.out.println("MathModel:" + temp.getName() + " " + mmInfos[i].getMathKey().toString() + " is being deleted");
							csManager.getDocumentManager().delete(mmInfos[i]);
						}
					} else {
						System.out.println("MathModel:" + mmInfos[i].getMathKey().toString() + " is being deleted");
						csManager.getDocumentManager().delete(mmInfos[i]);
					}
				}
			}
		} catch (DataAccessException e) {
			System.err.println("Unable to flush model(s): " + ownerName + " " + mm);
			throw e;
		}	
	}


	protected void load(File file, String mode, String [] connArgs) throws Exception {

		Translator t = Translator.getTranslator(mode);
		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    Document tDoc;
		    if (mode.equals(Translator.CELL_QUAN_VC) || mode.equals(Translator.CELL_QUAL_VC))
				tDoc = t.translate(br, false);
			else
				tDoc = t.translate(br, true);
			t.printSource(pw);
			pw.println("");
			t.printTarget(pw);
			pw.println("");
			if (!mode.equals(Translator.CELL_QUAN_VC)) {
				loadBioModel(tDoc, connArgs[1]);
			} else {
				loadMathModel(tDoc, connArgs[1]);
			}
	    } catch (IOException e) {
		    System.err.println("Unable to load model: " + file + " " + mode);
			throw e;
	    }
	}


	private void loadBioModel(Document doc, String userID) throws Exception {

		BioModel bm;
		try { 
			XmlReader reader = new XmlReader(false);
			bm = reader.getBioModel(doc.getRootElement());
			SimulationContext sc = bm.getSimulationContexts(0);
			MathMapping mp = new MathMapping(sc);
			MathDescription md = mp.getMathDescription();
			sc.setMathDescription(md);
			SimulationContext [] scArray = new SimulationContext[1];
			scArray[0] = sc;
			bm.setSimulationContexts(scArray);
			
			if (flushModel) {
				flushBioModel(userID, bm);
				flushModel = false;
			} else if (flushAll) {                   
				flushBioModel(userID, null);
				flushAll = false;                           //do it one time only
			}
			//bm = dm.save(bm);
			bm = csManager.getDocumentManager().saveAsNew(bm, bm.getName(),null);
			System.out.println("Model: " + bm.getName() + " has been loaded.");
		} catch (Exception e) {         //DataAccessException, XmlParseException, ExpressionException,
										//ModelException, MathException, PropertyVetoException, MappingException
			System.err.println("Unable to load models: " + userID + " " + doc);
			throw e;
		}
	}


	private void loadMathModel(Document doc, String userID) throws Exception {

		MathModel mm;
		try {
			XmlReader reader = new XmlReader(false);
			mm = reader.getMathModel(doc.getRootElement());
			if (flushModel) {
				flushMathModel(userID, mm);
				flushModel = false;
			} else if (flushAll) {                   
				flushMathModel(userID, null);
				flushAll = false;                           //do it one time only
			}
			mm = csManager.getDocumentManager().save(mm,null);
			System.out.println("Model: " + mm.getName() + " has been loaded.");
		} catch (Exception e) {         //DataAccessException, XmlParseException, ExpressionException,
										//ModelException, MathException, PropertyVetoException, MappingException
			System.err.println("Unable to load models: " + userID + " " + doc);
			throw e;
		}
	}


	public static void main (String [] args) throws Exception {

		StringBuffer buf = new StringBuffer();
		buf.append("Usage: java Loader URI TranslationMode FlushExisting.\n");
		buf.append("URI: Name of the file or directory of files to translate and load in the DB.\n");
		buf.append("TranslationMode: One of the following: SBVC_1, SBVC_2, CellQualVC, CellQuanVC\n");
		buf.append("FlushExisting: 'true' or 'false'");
		buf.append("\t Indicates whether to delete the existing model with the same name. In the\n");
		buf.append("case of a directory, it will delete all the models from the existing account.");
		
		if (args.length < 3) {
			System.out.println(buf);
			System.exit(0);
    	}
		PrintStream ps;
		if (args.length == 4) {
			ps = new PrintStream(new FileOutputStream(args[3]));
		} else {
			ps = System.out;
		}
		try {
			PropertyLoader.loadProperties();
		} catch (IOException ioe) {
			System.err.println("Unable to process loading request");
			throw ioe;
		}
    	new Loader(args[0], args[1], args[2], ps);            
    	System.exit(0);
	}


	private void setModelsPublic(String mode, String ownerName) {

		try {
			csManager.reconnect();
			if (!mode.equals(Translator.CELL_QUAN_VC)) {
				bmInfos = csManager.getDocumentManager().getBioModelInfos();
				for (int i = 0; i < bmInfos.length; i++) {
					if (bmInfos[i].getVersion().getOwner().getName().equals(ownerName)) {
						csManager.getDocumentManager().setGroupPublic(bmInfos[i]);
						System.out.println(bmInfos[i] + " is made public.");
					}
				}
			} else {
				mmInfos = csManager.getDocumentManager().getMathModelInfos();
				for (int i = 0; i < mmInfos.length; i++) {
					if (mmInfos[i].getVersion().getOwner().getName().equals(ownerName)) {
						csManager.getDocumentManager().setGroupPublic(mmInfos[i]);
						System.out.println(mmInfos[i] + " is made public.");
					}

				}
			}
		} catch (Exception e) {
			System.err.println("Unable to set models public.");
			e.printStackTrace();
		}
	}
}
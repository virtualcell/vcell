package cbit.vcell.publish;
import java.awt.print.PageFormat;
import java.io.*;
import cbit.util.BigString;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.sql.DBCacheTable;
import cbit.vcell.server.SessionLog;
import cbit.sql.KeyFactory;
import cbit.sql.ConnectionFactory;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.xml.XmlHelper;
import java.awt.PrintJob;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
/**
 * Insert the type's description here.
 * Creation date: (12/8/2004 2:28:38 PM)
 * @author: Anuradha Lakshminarayana
 */
public class PDFGenerator {
	private ConnectionFactory conFactory = null;
	private DatabaseServerImpl dbServerImpl = null;
	private KeyFactory keyFactory = null;
	private DBCacheTable cacheTable = null;
	private SessionLog log = null;

/**
 * PDFGenerator constructor comment.
 */
public PDFGenerator(ConnectionFactory argConFactory, KeyFactory argKeyFactory, SessionLog argSessionLog, DBCacheTable dbCacheTable) throws DataAccessException {
	this.conFactory = argConFactory;
	this.keyFactory = argKeyFactory;
	this.log = argSessionLog;
	this.cacheTable = dbCacheTable;
	this.dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory,dbCacheTable,argSessionLog);
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
public void scan(User users[], String bioModelName) throws DataAccessException {

	for (int i = 0; i < users.length; i++){
		User user = users[i];
		BioModelInfo bioModelInfos[] = dbServerImpl.getBioModelInfos(user,false);
		SessionLog userLog = new cbit.vcell.server.StdoutSessionLog(user.toString());
		userLog.print("Testing user '"+user+"'");

		for (int j = 0; j < bioModelInfos.length; j++){
			//
			// if a single bioModel is requested, then filter all else out
			//
			if (bioModelName!=null && !bioModelInfos[j].getVersion().getName().equals(bioModelName)){
				continue;
			}

			try {
				//
				// read in the BioModel from the database
				//
				BigString bioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfos[j].getVersion().getVersionKey());
				BioModel bioModelFromDB = XmlHelper.XMLToBioModel(bioModelXML.toString());
				bioModelFromDB.refreshDependencies();

				// If biomodel doesn't have any applications, pdf is not generated. Display an error message and continue

				//cbit.vcell.mapping.SimulationContext[] simContexts = bioModelFromDB.getSimulationContexts();
				//if (simContexts.length == 0) {
					//System.out.println("*** No applications in biomodel; Cannot generate PDF for Biomodel : "+bioModelFromDB.getName());
					//continue;
				//}

				//
				// create the pdfWriter and necessary arguments and generate the PDF for the biomodel.
				//

				System.out.println("Generating PDF report for User : "+ user.getName() + "; BioModel : " + bioModelFromDB.getName());
				File pdfFile = new File("c:\\Vcell Temp\\"+bioModelFromDB.getName() + ".pdf");
				// If pdf file exists, do not generate again, go to next bioModel.
				// This was added since the script had to be run multiple times and it was unnecessary to repeat the pdf generation
				// for the biomodels that had already generated the PDF.
				if (pdfFile.exists()) {
					System.out.println("\n\t\t*** PDF File exisits for Biomodel : "+bioModelFromDB.getName()+"\n");
					continue;
				}
				FileOutputStream fos = new FileOutputStream(pdfFile);
				PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
				pageFormat.setOrientation(PageFormat.PORTRAIT);
				ITextWriter pdfWriter = ITextWriter.getInstance(ITextWriter.PDF_WRITER);
				pdfWriter.writeBioModel(bioModelFromDB, fos, pageFormat);
				
			}catch (Throwable e){
				log.exception(e); // exception in whole BioModel
				e.printStackTrace(System.out);
			}
		}	
	}
}
}
package org.vcell.visit;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.vcell.visit.jvisit.VisitManagerJVisit;

public class VisitClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			VisitManager visitManager = new VisitManagerJVisit();
			final VisitSession visitSession = visitManager.createLocalSession();
			visitSession.init();
//			File logFile = "\\\\cfs02.vcell.uchc.edu\\raid\\vcell\\users\\schaff\\";
			File logFile = new File("C:\\Users\\schaff\\.vcell\\simdata\\user\\SimID_536601443_0_.log");
			if (!logFile.exists()){
				logFile = new File("C:\\Users\\schaff.CAM\\.vcell\\simdata\\user\\SimID_33957567_0_.log");
			}
			if (!logFile.exists()){
				throw new RuntimeException("file "+logFile+" not found");
			}
			VisitDatabaseSpec visitDatabaseSpec = new LocalVisitDatabaseSpec(logFile);
			VisitDatabaseInfo visitDatabaseInfo = visitSession.openDatabase(visitDatabaseSpec);
			String varName = "r_PM";
			visitSession.addAndDrawPseudocolorPlot(varName);
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e){
			}
			System.out.println("about to start gui");
			visitSession.openGUI();
			System.out.println("started gui");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}

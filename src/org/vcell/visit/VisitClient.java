package org.vcell.visit;

import java.io.File;

import org.vcell.visit.jvisit.VisitManagerJVisit;

public class VisitClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			VisitManager visitManager = new VisitManagerJVisit();
			VisitSession visitSession = visitManager.createLocalSession();
			visitSession.init();
			File logFile = new File("C:\\Users\\schaff\\.vcell\\simdata\\user\\SimID_536601443_0_.log");
			if (!logFile.exists()){
				throw new RuntimeException("file "+logFile+" not found");
			}
//			File logFile = "\\\\cfs02.vcell.uchc.edu\\raid\\vcell\\users\\schaff\\";
			VisitDatabaseSpec visitDatabaseSpec = new LocalVisitDatabaseSpec(logFile);
			VisitDatabaseInfo visitDatabaseInfo = visitSession.openDatabase(visitDatabaseSpec);
			String varName = "r_PM";
			visitSession.addAndDrawPseudocolorPlot(varName);
			
			System.out.println("hello");
			
			for (int i=0;i<100;i++){
				System.out.println("synchronizing for the "+(i+1)+" time");
				visitSession.synchronize();
				try {
					Thread.sleep(1000);
				}catch (Exception e){
					
				}
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}

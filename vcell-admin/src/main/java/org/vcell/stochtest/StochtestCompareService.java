package org.vcell.stochtest;

import cbit.image.ImageException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XmlParseException;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.stochtest.StochtestCompare.StochtestCompareStatus;
import org.vcell.stochtest.StochtestRun.StochtestRunStatus;
import org.vcell.stochtest.TimeSeriesMultitrialData.SummaryStatistics;
import org.vcell.util.DataAccessException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;


public class StochtestCompareService {
	
	private ConnectionFactory conFactory = null;
	private DatabaseServerImpl dbServerImpl = null;
	private KeyFactory keyFactory = null;
	private File baseDir = null;


	public StochtestCompareService(File baseDir, ConnectionFactory argConFactory, KeyFactory argKeyFactory) 
			throws DataAccessException, SQLException {
		this.conFactory = argConFactory;
		this.keyFactory = argKeyFactory;
		this.dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory);
		this.baseDir = baseDir;
	}

	public static void main(String[] args) {
		
	try {
		
		if (args.length!=1){
			System.out.println("Usage:  StochtestService baseDirectory");
			System.exit(-1);
		}
		File baseDir = new File(args[0]);
		if (!baseDir.exists()){
			throw new RuntimeException("base directory "+baseDir.getPath()+" not found");
		}

		DatabasePolicySQL.bAllowAdministrativeAccess = true;
	    //
	    // get appropriate database factory objects
	    //
	    ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory();
	    KeyFactory keyFactory = conFactory.getKeyFactory();
	    StochtestCompareService stochtestService = new StochtestCompareService(baseDir, conFactory, keyFactory);
	    
	    while (true){
	    	stochtestService.compareOne();
	    }
	    
	} catch (Throwable e) {
	    e.printStackTrace(System.out);
	}
    System.exit(0);
	}

	public void compareOne() throws IllegalArgumentException, SQLException, DataAccessException, XmlParseException, PropertyVetoException, ExpressionException, MappingException, GeometryException, ImageException, IOException{
		
	    StochtestCompare stochtestCompare = StochtestDbUtils.acceptNextWaitingStochtestCompare(conFactory);
	    String biomodelXML = null;
	    if (stochtestCompare!=null){
	    	try {
	    		StochtestRun stochtestRun1 = StochtestDbUtils.getStochtestRun(conFactory, stochtestCompare.stochtestRun1ref);
	    		StochtestRun stochtestRun2 = StochtestDbUtils.getStochtestRun(conFactory, stochtestCompare.stochtestRun2ref);
	    		if (stochtestRun1.status != StochtestRunStatus.complete){
	    			throw new RuntimeException("incomplete run status found: "+stochtestRun1.status.name());
	    		}
	    		if (stochtestRun2.status != StochtestRunStatus.complete){
	    			throw new RuntimeException("incomplete run status found: "+stochtestRun2.status.name());
	    		}
	    		TimeSeriesMultitrialData data1 = StochtestFileUtils.readData(StochtestFileUtils.getStochtestRunDataFile(baseDir, stochtestRun1));
	    		TimeSeriesMultitrialData data2 = StochtestFileUtils.readData(StochtestFileUtils.getStochtestRunDataFile(baseDir, stochtestRun2));

	    		SummaryStatistics results = TimeSeriesMultitrialData.statisticsSummary(data1,data2);
	    		
				XmlUtil.writeXMLStringToFile(results.results(), new File(baseDir,"stochtestcompare_"+stochtestCompare.key+"_summary.txt").getPath(),false);
				StochtestFileUtils.writeVarDiffData(new File(baseDir,"stochtestcompare_"+stochtestCompare.key+"_vardiff.csv"),data1,data2);
				StochtestFileUtils.writeKolmogorovSmirnovTest(new File(baseDir,"stochtestcompare_"+stochtestCompare.key+"_kolmogorovSmirnov.csv"), data1, data2);
				StochtestFileUtils.writeChiSquareTest(new File(baseDir,"stochtestcompare_"+stochtestCompare.key+"_chiSquared.csv"), data1, data2);

				StochtestCompareStatus status = (results.pass())?StochtestCompareStatus.not_verydifferent:StochtestCompareStatus.verydifferent;
				StochtestDbUtils.finalizeAcceptedStochtestCompare(conFactory, stochtestCompare, status, null, results);
	    	}catch (Exception e){
				StochtestDbUtils.finalizeAcceptedStochtestCompare(conFactory, stochtestCompare, StochtestCompare.StochtestCompareStatus.failed,e.getMessage(),null);
				//
				// write exception trace to .txt file
				//
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				e.printStackTrace(printWriter);
				printWriter.flush();
				System.out.println(stringWriter.getBuffer().toString());
				XmlUtil.writeXMLStringToFile(stringWriter.getBuffer().toString(), new File(baseDir,"stochtestrun_"+stochtestCompare.key+"_error.txt").getPath(), false);
	    	}
	    }else{
	    	System.out.println("no compare jobs waiting");
	    	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	}
}

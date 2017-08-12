package cbit.vcell.math;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.help.UnsupportedOperationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.vcell.util.NullSessionLog;
import org.vcell.util.SessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.logging.Logging;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.BadMathVisitor;
import cbit.vcell.modeldb.BatchTester;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;

public class MathVisitor implements BadMathVisitor {
	final PrintWriter loadErrorWriter;
	final PrintWriter parseErrorWriter;
	/**
	 * name of status table to use
	 */
	private final static String STATUS_TABLE = "gerard.math_models";
	

	public static void main(String[] args) {
		Request req =  Request.method(MathVisitor.class, "batchScan");
		JUnitCore core = new JUnitCore();
		Result res = core.run(req);
		for (Failure f : res.getFailures()) {
			System.err.println("Failure:  " + f.toString());
		}
		System.exit(0);
	}
	/**
	 * @param args
	 */
	public void oldMain( )  {
		
		try{
		MathVisitor visitor = new MathVisitor();
		boolean bAbortOnDataAccessException = false;
			PropertyLoader.loadProperties();
			SessionLog sl = new NullSessionLog(); 
			VCDatabaseScanner scanner = VCDatabaseScanner.createDatabaseScanner(sl);
			User[] users = scanner.getAllUsers();
			scanner.scanMathModels(visitor,System.err,users,null,null,null, bAbortOnDataAccessException);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			System.err.flush();
		}
	}
	
	@Before
	public void init( ) {
		Logging.init();
		
	}
	
	@Test
	public void populateDatabase( ) throws Exception {
		SessionLog lg = new StdoutSessionLog("me", System.out);
		BatchTester bt = new BatchTester(lg);
		FileWriter writer = new FileWriter("populateLog.txt");
		bt.keyScanMathModels(this, writer, false,STATUS_TABLE); 
	}
	
	@Test
	public void batchScan( ) throws Exception {
		SessionLog lg = new StdoutSessionLog("me", System.out);
		BatchTester bt = new BatchTester(lg);
		bt.batchScanMathModels(this, STATUS_TABLE,5);
	}

	public MathVisitor( ) throws IOException {
		super();
		this.loadErrorWriter = new PrintWriter(new FileWriter("loaderror.txt"),true);
		this.parseErrorWriter = new PrintWriter(new FileWriter("parseerror.txt"),true);
	}

	@Override
	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean filterGeometry(GeometryInfo geometryInfo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		VCellSoftwareVersion vers = mathModelInfo.getSoftwareVersion();
		boolean current = vers.getMajorVersion() >= 5;
		return current;
	}

	@Override
	public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream) {
			MathDescription md = mathModel.getMathDescription();
			
			String vcml = null; 
			try {
				vcml = md.getVCML_database();
			} catch (MathException me) {
				throw new MathDescriptionException(md.getDescription(), me);
			}
			
			MathDescription  check = null;
			try {
				check = MathDescription.fromEditor(md, vcml);
			} catch (Exception e) {
				throw new VCMLException(vcml, e);
			}
			if (!md.compareEqual(check)) {
				throw new MathDescriptionEqualException(md, check);
			}
	}

	@Override
	public void unableToLoad(KeyValue vk, Exception e) {
		loadErrorWriter.println("load: " + vk + ":  " + e.getMessage());
	}
	
	@SuppressWarnings("serial")
	private static class VCMLException extends RuntimeException {

		public VCMLException(String vcml, Throwable t) {
			super(vcml, t);
		}
	}
	
	@SuppressWarnings("serial")
	private static class MathDescriptionException extends RuntimeException {

		public MathDescriptionException(String md, Throwable t) {
			super(md, t);
		}
	}
	
	@SuppressWarnings("serial")
	private static class MathDescriptionEqualException extends RuntimeException {

		public MathDescriptionEqualException(MathDescription one, MathDescription other) {
			super(one.getDescription() + " : " + other.getDescription());
		}
	}
	
	private static class MethodFilter extends Filter {

		@Override
		public String describe() {
			return "just one method";
		}

		@Override
		public boolean shouldRun(Description description) {
			return false;
		}
		
	}

}
package org.vcell.sbml.vcell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.help.UnsupportedOperationException;

import org.apache.log4j.Logger;
import org.vcell.sbml.vcell.SBMLImportException.Category;
import org.vcell.util.ExecutableException;
import org.vcell.util.LiveProcess;
import org.vcell.util.LiveProcess.LiveProcessStatus;
import org.vcell.util.PropertyLoader;
import org.vcell.util.logging.Logging;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

/**
 * provides process firewall to prevent native dll crashes from 
 * terminating application
 * @author gweatherby
 *
 */
public class SBMLStandaloneImporter {
	/**
	 * log4j logger
	 */
	private static final Logger lg = Logger.getLogger(SBMLStandaloneImporter.class);

	/**
	 * how long to go without input before exiting standalone process
	 */
	//public static final long WATCHDOG_SECONDS = TimeUnit.MINUTES.toSeconds(15);
	public static final long WATCHDOG_SECONDS = 30; 
	
	/**
	 * for threads, logs. et. al.
	 */
	public static final String OUR_LABEL = "SBMLStandaloneImporter";
	
	private LiveProcess subProcess;
	
	public SBMLStandaloneImporter() {
		subProcess = null;
	}
	
	public BioModel importSBML(File sbmlFile) throws IOException, ExecutableException, XmlParseException {
		if (!sbmlFile.canRead()) {
			throw new IOException("Can't read " + sbmlFile);
		}
		
		if (subProcess == null || subProcess.getStatus() != LiveProcessStatus.RUNNING) { 
			subProcess = createProcess( );
		}
		String fullPath = sbmlFile.getAbsolutePath();
		subProcess.send(fullPath + "\n");
		String xml = new String( );
		String frag;
		do {
			frag = subProcess.getStdoutString(10, TimeUnit.SECONDS); 
			if (lg.isDebugEnabled()) {
				lg.debug("read frag length " + frag.length());
			}
			xml += frag;
		} while (!frag.isEmpty() && !frag.contains("</vcml>"));
		if (xml.isEmpty()) {
			String err;
			do {
				err = subProcess.getStderrString(10, TimeUnit.SECONDS); 
				System.err.println(err);
			} while (!err.isEmpty());
			throw new SBMLImportException(err, Category.UNSPECIFIED); 
		}
		
		if (lg.isDebugEnabled()) {
			try (FileWriter fw  = new FileWriter(sbmlFile.getAbsolutePath( ) + ".dump") ) {
				fw.append(xml);
			}
		}
		XMLSource source = new XMLSource(xml);
		return XmlHelper.XMLToBioModel(source);
	}
	
	private LiveProcess createProcess( ) throws ExecutableException {
		String home = System.getProperty("java.home");
		String exe = home + "/bin/java";
		String classpath = System.getProperty("java.class.path");
		String us = getClass( ).getName();
		
		ArrayList<String> cmdArgs = new ArrayList<String>();
		cmdArgs.add(exe);
		cmdArgs.addAll(jvmArgs( ));
		String invokeArgs[] = {"-classpath" , classpath , us}; 
		cmdArgs.addAll(Arrays.asList(invokeArgs));
		
		LiveProcess lp = new LiveProcess(cmdArgs.toArray(new String[cmdArgs.size()]));
		lp.begin(OUR_LABEL);
		while (lp.getStatus() != LiveProcessStatus.RUNNING) {
			
		}
		System.err.println("startup err " + lp.getStderrString(900, TimeUnit.MILLISECONDS) ); 
		System.err.println("startup out " + lp.getStdoutString(900, TimeUnit.MILLISECONDS) ); 
		return lp; 
	}
	
	/**
	 * create the jvm args
	 */
	private Collection<String> jvmArgs( ) {
		String inst = PropertyLoader.getRequiredProperty(PropertyLoader.installationRoot);
		ArrayList<String> jvmArgs = new ArrayList<String>();
		jvmArgs.add("-D" + PropertyLoader.installationRoot + '=' + inst);
		
		//jvmArgs.add("-Dlog4j.debug");
		jvmArgs.add("-Dlog4j.configuration=file:standalonelog4j.properties");
		return jvmArgs;
	}
	
	
	
	public static void main(String[] args) {
		Logging.init();
		ResourceUtil.setNativeLibraryDirectory();
		new Standalone(args);
	}
	
	/**
	 *  implementation class which runs in the separate process / JVM
	 */
	private static class Standalone extends VCLogger {
		
		private BufferedReader inputReader;
		private PrintWriter outputWriter; 
		private PrintWriter errorWriter; 
		private	StringWriter   messageWriter;
		private WatchDog watchDog;

		/**
		 * receive SBML file name on standard in
		 * return BioModel XML on standard out
		 * report errors on standard err
		 * @param args
		 */
		public Standalone(String[] args) {
			try {
				inputReader = new BufferedReader(new InputStreamReader(System.in));
				outputWriter = new PrintWriter(System.out,true);
				errorWriter = new PrintWriter(System.err,true);
				messageWriter = new StringWriter( );
				//we don't want miscellaneous print statements interfering with the XML output
				System.setOut(System.err);
				
				watchDog = new WatchDog();
				Thread t = new Thread(watchDog,OUR_LABEL + " watchdog");
				t.setDaemon(true);
				t.start( );

				for (;;) {
					watchDog.beat();
					BioModel model = readSBML( );
					if (model == null) {
						lg.info("exiting on null read");
						System.exit(2);
					}
					if (lg.isInfoEnabled()) {
						lg.info("read model " + model.getName());
					}
					if (model != null) {
						String xml = translateModel(model);
						if (xml != null) {
							if (lg.isInfoEnabled()) {
								lg.info("sending xml " + xml);
							}
							outputWriter.println(xml);
						}
					}
					StringBuffer sb = messageWriter.getBuffer();
					if (sb.length() > 0) {
						String errString = sb.toString();
						errorWriter.println(errString);
						if (lg.isInfoEnabled()) {
							lg.info("send (err) " + errString); 
						}
						sb.setLength(0);
					}
				}
			}
			catch (Exception e) {
				lg.fatal("exception in main",e);
				try (FileWriter fw = new FileWriter("SBMLStandaloneExceptionLog.txt") ) {
					e.printStackTrace(new PrintWriter(fw) );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		private BioModel readSBML( )  {
			try {
				String filename = inputReader.readLine();
				if (lg.isInfoEnabled()) {
					lg.info("read name " + filename);
				}
				if (filename != null) {
					SBMLImporter importer = new SBMLImporter(filename, this,false); 
					return importer.getBioModel();
				}
				return null;
			}
			catch (Exception e) {
				lg.debug("reading error",e);
				e.printStackTrace(new PrintWriter(messageWriter));
				return null;
			}
		}

		private String translateModel(BioModel model) {
			try {
				return XmlHelper.bioModelToXML(model);
			}
			catch (Exception e) {
				lg.debug("translation error",e);
				e.printStackTrace(new PrintWriter(messageWriter));
				return null;
			}
		}

		@Override
		public boolean hasMessages() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void sendAllMessages() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void sendMessage(int messageLevel, int messageType) throws Exception {
			Priority p = Priority.fromOrdinal(messageLevel);
			ErrorType et = ErrorType.fromOrdinal(messageType);
			messageWriter.append(p + ":  " + et);
		}

		@Override
		public void sendMessage(int messageLevel, int messageType, String message)
				throws Exception {
			Priority p = Priority.fromOrdinal(messageLevel);
			ErrorType et = ErrorType.fromOrdinal(messageType);
			messageWriter.append(p + ":  " + et + " :  " + message);
		}
	}
	
	private static class WatchDog implements Runnable {
		
		private AtomicLong lastTime;
		WatchDog( ) {
			lastTime = new AtomicLong( );
			beat( ); //initial setting
		}
		
		/**
		 * tell watchdog we're alive
		 */
		void beat( ) {
			lastTime.set(System.currentTimeMillis());
		}

		@Override
		public void run() {
			try {
				for (;;) {
					long now = System.currentTimeMillis();
					long elapsed = now - lastTime.get( );
					final long waitMilliseconds = WATCHDOG_SECONDS * 1000 - elapsed;
					if (lg.isDebugEnabled()) {
						lg.debug("waiting " + waitMilliseconds);
					}
					Thread.sleep(waitMilliseconds);
					
					now = System.currentTimeMillis();
					elapsed = (now - lastTime.get( ))/1000; 
					final boolean isTimeToExit = elapsed >= WATCHDOG_SECONDS;
					if (lg.isDebugEnabled()) {
						lg.debug("seconds since beat( ): " + elapsed  + " seconds, threshold is "
								+ WATCHDOG_SECONDS + " time to exit is " + isTimeToExit);
					}
					
					if (isTimeToExit) {
						String msg = "Watchdog timeout " + elapsed + " seconds";
						lg.fatal(msg);
						System.err.println(msg);
						System.exit(1);
					}
				}
			}
				
			catch (Exception e) {
				lg.error("WatchDog exception shutdown",e);
				System.err.println("WatchDog exception shutdown");
				e.printStackTrace(System.err);
			}
		}
	}
}

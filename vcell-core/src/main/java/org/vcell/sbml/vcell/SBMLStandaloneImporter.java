package org.vcell.sbml.vcell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.exe.ExecutableException;
import org.vcell.util.executable.ClosedInput;
import org.vcell.util.executable.IdleProcessSelfTerminator;
import org.vcell.util.executable.InheritOutput;
import org.vcell.util.executable.LiveProcess;
import org.vcell.util.executable.LiveProcess.LiveProcessStatus;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.PropertyLoader;
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
	static final Logger lg = LogManager.getLogger(SBMLStandaloneImporter.class);

	/**
	 * how long to go without input before exiting standalone process
	 */
	public static final long WATCHDOG_SECONDS = TimeUnit.MINUTES.toSeconds(15);
	
	/**
	 * for threads, logs. et. al.
	 */
	public static final String OUR_LABEL = "SBMLStandaloneImporter";
	
	private final static String PORT_KEY = "standalonePortNumber";
	private final static String HOST_KEY = "standaloneHostname";
	
	private final int START_TIMEOUT_SECONDS = 3; 
	
	private LiveProcess<ClosedInput,InheritOutput,InheritOutput> subProcess;
	
	private Socket channel;
	private ObjectOutputStream toChildProcess;
	private ObjectInputStream  fromChildProcess;
	
	public SBMLStandaloneImporter() {
		subProcess = null;
		channel = null;
	}
	
	private BioModel attemptImport(File sbmlFile) throws ExecutableException, IOException, ClassNotFoundException, XmlParseException {
		if (subProcess == null || subProcess.getStatus() != LiveProcessStatus.RUNNING) { 
			subProcess = createProcess( );
		}
		toChildProcess.writeObject(sbmlFile);
		Object back = fromChildProcess.readObject();
		if (back instanceof String) {
			String xml = (String) back; 
			if (lg.isDebugEnabled()) {
				try (FileWriter fw  = new FileWriter(sbmlFile.getAbsolutePath( ) + ".dump") ) {
					fw.append(xml);
				}
			}
			XMLSource source = new XMLSource(xml);
			return XmlHelper.XMLToBioModel(source);
		}
		
		if (back instanceof Exception) {
			try {
				throw (Exception)back;
				
			} catch (SBMLImportException sie) {
				throw sie;
			}
			catch (Exception e) {
				throw new ExecutableException("child process exception",e);
			}
		}
		
		throw new ExecutableException("unexpected object from child process:  " + back.getClass().getName() + " " + back.toString());
		
	}
	
	public BioModel importSBML(File sbmlFile) throws IOException, ExecutableException, XmlParseException, ClassNotFoundException {
		if (!sbmlFile.canRead()) {
			throw new IOException("Can't read " + sbmlFile);
		}
		
		//if process dies on socket exception we're going to try killing and restarting it before giving up
		int tryCount = 0;
		BioModel bm  = null;
		while (bm == null) {
			try {
				bm = attemptImport(sbmlFile);
			}
			catch (SocketException se) {
				if (++tryCount >= 2) {
					throw se;
				}
				subProcess.stop(2, TimeUnit.SECONDS); 
			}
		}
		return bm;
	}
	
	private LiveProcess<ClosedInput,InheritOutput,InheritOutput> createProcess( ) throws ExecutableException, IOException {
		
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(0);
		InetSocketAddress isa = (InetSocketAddress) ss.getLocalSocketAddress(); 
		
		String home = System.getProperty("java.home");
		String exe = home + "/bin/java";
		String classpath = System.getProperty("java.class.path");
		String us = getClass( ).getName();
		
		ArrayList<String> cmdArgs = new ArrayList<String>();
		cmdArgs.add(exe);
		cmdArgs.addAll(jvmArgs( ));
		String invokeArgs[] = {"-classpath" , classpath , us}; 
		cmdArgs.addAll(Arrays.asList(invokeArgs));
		InheritOutput i = new InheritOutput();
		
		LiveProcess<ClosedInput,InheritOutput,InheritOutput> lp  = new LiveProcess<ClosedInput,InheritOutput,InheritOutput>(
				new ClosedInput(),i,i, cmdArgs.toArray(new String[cmdArgs.size()]));
		lp.setEnvironment(PORT_KEY, Integer.toString( isa.getPort() ) );
		lp.setEnvironment(HOST_KEY,isa.getHostName());
		lp.begin(OUR_LABEL);
		
		ss.setSoTimeout(START_TIMEOUT_SECONDS * 1000);
		try {
			channel = ss.accept( );
		} catch (SocketTimeoutException sto) {
			throw new ExecutableException("Standalone client failed to communicate in " + START_TIMEOUT_SECONDS + " seconds");
		}
		toChildProcess = new ObjectOutputStream(channel.getOutputStream());
		fromChildProcess = new ObjectInputStream(channel.getInputStream());
		
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
		ResourceUtil.setNativeLibraryDirectory();
		new Standalone(args);
	}
	
	/**
	 *  implementation class which runs in the separate process / JVM
	 */
	private static class Standalone extends VCLogger {
		
		private IdleProcessSelfTerminator watchDog;
		private ObjectInputStream inputObjects;
		private ObjectOutputStream resultObjects;

		/**
		 * receive SBML file name on socket
		 * return BioModel XML on same socket 
		 * report errors on on same socket 
		 * @param args
		 */
		public Standalone(String[] args) {
			Socket s = null;
			try {
				String host = System.getenv(HOST_KEY);
				int port = Integer.parseInt( System.getenv(PORT_KEY) );
				s = new Socket(host,port);
				inputObjects = new ObjectInputStream(s.getInputStream());
				resultObjects = new ObjectOutputStream(s.getOutputStream());
				
				watchDog = new IdleProcessSelfTerminator(OUR_LABEL, WATCHDOG_SECONDS);
				watchDog.start( );

				for (;;) {
					watchDog.beat();
					BioModel model = readSBML( );
					if (model == null) {
						continue;
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
							resultObjects.writeObject(xml);
						}
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
			finally {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private BioModel readSBML( ) throws IOException  {
			try {
				File file = (File) inputObjects.readObject();
				if (lg.isInfoEnabled()) {
					lg.info("read name " + file.getCanonicalPath()); 
				}
				if (file != null) {
					boolean bIsSpatial = false;
					boolean bValidate = true;
					SBMLImporter importer = new SBMLImporter(file.getAbsolutePath(), this, bIsSpatial, bValidate);
					return importer.getBioModel();
				}
				return null;
			}
			catch (Exception e) {
				lg.debug("reading error",e);
				resultObjects.writeObject(e);
				return null;
			}
		}

		private String translateModel(BioModel model) throws IOException {
			try {
				return XmlHelper.bioModelToXML(model);
			}
			catch (Exception e) {
				lg.debug("translation error",e);
				resultObjects.writeObject(e);
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
		public void sendMessage(Priority p, ErrorType et, String message)
				throws Exception {
			
		}

	}
}

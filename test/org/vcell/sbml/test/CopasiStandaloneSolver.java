package org.vcell.sbml.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.vcell.sbml.SBMLSolver;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;
import org.vcell.util.ExecutableException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.executable.ClosedInput;
import org.vcell.util.executable.IdleProcessSelfTerminator;
import org.vcell.util.executable.InheritOutput;
import org.vcell.util.executable.LiveProcess;
import org.vcell.util.executable.LiveProcess.LiveProcessStatus;
import org.vcell.util.logging.Logging;

import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.SolverException;


/**
 * provides process firewall to prevent native dll crashes from 
 * terminating application
 * @author gweatherby
 *
 */
public class CopasiStandaloneSolver implements SBMLSolver {
	/**
	 * log4j logger
	 */
	static final Logger lg = Logger.getLogger(CopasiStandaloneSolver.class);

	/**
	 * how long to go without input before exiting standalone process
	 */
	public static final long WATCHDOG_SECONDS = TimeUnit.MINUTES.toSeconds(15);
	
	/**
	 * for threads, logs. et. al.
	 */
	public static final String OUR_LABEL = "CopasiStandaloneSolver";
	
	private final static String PORT_KEY = "standalonePortNumber";
	private final static String HOST_KEY = "standaloneHostname";
	
	private final int START_TIMEOUT_SECONDS = 3; 
	
	private LiveProcess<ClosedInput,InheritOutput,InheritOutput> subProcess;
	
	private Socket channel;
	private ObjectOutputStream toChildProcess;
	private ObjectInputStream  fromChildProcess;
	
	public CopasiStandaloneSolver() {
		subProcess = null;
		channel = null;
	}
	
	@Override
	public String getResultsFileColumnDelimiter() {
		return CopasiSBMLSolver.COLUMN_DELIMITER; 
	}
	
	@Override
	public File solve(String filePrefix, File outDir, String sbmlText, SimSpec simSpec) throws IOException, SolverException, SbmlException {
		Object back;
		try {
			if (subProcess == null || subProcess.getStatus() != LiveProcessStatus.RUNNING) { 
				subProcess = createProcess( );
			}
			ArgumentPackage ap = new ArgumentPackage(filePrefix, outDir, sbmlText, simSpec);
			toChildProcess.writeObject(ap);
			back = fromChildProcess.readObject();
			if (back instanceof File) {
				return (File) back;
			}
			
			if (back instanceof Exception) {
				throw (Exception)back;
			}
			
		
		} catch (Exception e ) {
			throw new SolverException(getClass( ).getName() + "exception", e);
		}
		throw new SolverException("unexpected object from child process:  " + back.getClass().getName() + " " + back.toString());
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
		//jvmArgs.add("-Dlog4j.configuration=file:standalonelog4j.properties");
		return jvmArgs;
	}
	
	public static void main(String[] args) {
		Logging.init();
		ResourceUtil.setNativeLibraryDirectory();
		new Standalone(args);
	}
	
	/**
	 * package of input arguments to {@link CopasiSBMLSolver} for
	 * convenient passing over a socket
	 */
	@SuppressWarnings("serial")
	private static class ArgumentPackage implements Serializable {
		final String filePrefix; 
		final File outDir;
		final String sbmlText; 
		final SimSpec simSpec;
		
		public ArgumentPackage(String filePrefix, File outDir, String sbmlText,
				SimSpec simSpec) {
			super();
			this.filePrefix = filePrefix;
			this.outDir = outDir;
			this.sbmlText = sbmlText;
			this.simSpec = simSpec;
			if (filePrefix == null | outDir == null | sbmlText == null || simSpec == null ) {
				throw new NullPointerException("null field in " + toString( ));
			}
		}

		@Override
		public String toString() {
			return "ArgumentPackage [filePrefix=" + filePrefix + ", outDir="
					+ outDir + ", sbmlText=" + sbmlText + ", simSpec="
					+ simSpec + "]";
		}
		
	}
	
	/**
	 *  implementation class which runs in the separate process / JVM
	 */
	private static class Standalone {
		
		private IdleProcessSelfTerminator watchDog;
		private ObjectInputStream inputObjects;
		private ObjectOutputStream resultObjects;
		private CopasiSBMLSolver copasiSolver;

		/**
		 * receive SBML file name on socket
		 * return BioModel XML on same socket 
		 * report errors on on same socket 
		 * @param args
		 */
		public Standalone(String[] args) {
			Socket s = null;
			try {
				copasiSolver = new CopasiSBMLSolver();
				String host = System.getenv(HOST_KEY);
				int port = Integer.parseInt( System.getenv(PORT_KEY) );
				s = new Socket(host,port);
				inputObjects = new ObjectInputStream(s.getInputStream());
				resultObjects = new ObjectOutputStream(s.getOutputStream());
				
				watchDog = new IdleProcessSelfTerminator(OUR_LABEL, WATCHDOG_SECONDS);
				watchDog.start( );

				for (;;) {
					watchDog.beat();
					ArgumentPackage ap;
					Object obj = inputObjects.readObject();
					try {
						ap = (ArgumentPackage) obj; 
					} catch (ClassCastException cce) {
						lg.warn("bad input argument",cce);
						resultObjects.writeObject(cce);
						continue;
					}
					
					try {
						File result = copasiSolver.solve(ap.filePrefix,ap.outDir,ap.sbmlText,ap.simSpec);
						resultObjects.writeObject(result);
					} catch (Exception e) {
						resultObjects.writeObject(e);
					}
				}
			}
			catch (Exception e) {
				lg.fatal("exception in main",e);
				try (FileWriter fw = new FileWriter("CopasiStandaloneExceptionLog.txt") ) {
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
	}
}

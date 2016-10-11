package org.vcell.util.logging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.vcell.util.PropertyLoader;

/**
 * location for startup of logging files
 * @author gerard
 * based on edu.uchc.connjur.core.Logging
 *
 */

public class Logging {
	public static final String COMMAND_LINE_PROPERTY="log4jConfigurationFile";
	/**
	 * {@link org.apache.log4j Log4J } configuration file
	 */
	public static final String PROP_FILE = "log4j.properties";
	static Logger lg = Logger.getLogger(Logging.class);
	/**
	 * @return file specified on command line, if any, if it exists
	 */
	private static File commandLineFile( ) {
		String commandLineProperty = System.getProperty(COMMAND_LINE_PROPERTY);
		if (commandLineProperty != null) {
			File f = new File(commandLineProperty);
			if (f.exists()) {
				return f;
			}
		}
		return null;
	}
	/*
	 * first, look for configuration file specified on command line (#COMMAND_LINE_PROPERTY).
	 * if that's not present, look for default file (#PROP_FILE) in current working directory.
	 * if that's not present, just use hardcoded default settings
	 */
	static {
		lg.setLevel(Level.INFO);
		File initFile = commandLineFile( );
		if (initFile == null) {
			File pFile = new File(PROP_FILE);
			if (pFile.exists()) {
				initFile = pFile;
			}
		}

		boolean initialized = false;
		if (initFile != null) {
			try {
				PropertyConfigurator.configure(initFile.toURI().toURL());
				initialized = true;
				if (lg.isInfoEnabled()) {
					lg.info("logger initialized from file  " + initFile.getAbsolutePath());
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		if (!initialized) {
			Properties def = new Properties();
			def.put("log4j.rootLogger","FATAL, A1");
			def.put("log4j.appender.A1","org.apache.log4j.ConsoleAppender");
			def.put("log4j.appender.A1.layout","org.apache.log4j.PatternLayout");
			def.put("log4j.appender.A1.layout.ConversionPattern","%-4r [%t] %-5p %c %x %d--> %m %n");
			def.put("log4j.appender.A1.Target","System.out");
			PropertyConfigurator.configure(def);
		}
	}

	/**
	 * 
	 * force loading of class to invoke static initialization code
	 */
	public static void init() {
		configureLog4jFromSystemProperties();
	}
	
	public static void configureLog4jFromSystemProperties()
	{
	  final String LOGGER_PREFIX = "log4j.logger.";

	  for(String propertyName : System.getProperties().stringPropertyNames())
	  {
	    if (propertyName.startsWith(LOGGER_PREFIX)) {
	      String loggerName = propertyName.substring(LOGGER_PREFIX.length());
	      String levelName = System.getProperty(propertyName, "");
	      Level level = Level.toLevel(levelName); // defaults to DEBUG
	      if (!"".equals(levelName) && !levelName.toUpperCase().equals(level.toString())) {
	        lg.error("Skipping unrecognized log4j log level " + levelName + ": -D" + propertyName + "=" + levelName);
	        continue;
	      }
	      lg.info("Setting " + loggerName + " => " + level.toString());
	      Logger.getLogger(loggerName).setLevel(level);
	    }
	  }
	}
	
	public static enum ConsoleDestination {
		STD_OUT("System.out"),
		STD_ERR("System.err");
		public final String log4jName;

		private ConsoleDestination(String log4jName) {
			this.log4jName = log4jName;
		}
		
	}
	/**
	 * change console logging from stderr to stdout, or vice-versa
	 * @param from
	 * @param to
	 */
	public static void changeConsoleLogging(ConsoleDestination from, ConsoleDestination to) {
		Logger rootLogger = Logger.getRootLogger();
		Enumeration<?> e = rootLogger.getAllAppenders();
		while (e.hasMoreElements()) {
			Object a = e.nextElement();
			if (a instanceof ConsoleAppender) {
				ConsoleAppender ca = (ConsoleAppender) a;
				if (ca.getTarget().equals(from.log4jName)) {
					ca.setTarget(to.log4jName);
					ca.activateOptions();
				}
			}
		}
	}
	
	/**
	 * redirect standard err and out to filename; if autoflush property not set add shutdown hook
	 * @param logFile 
	 * @throws IllegalArgumentException if logFile null
	 */
	public static void captureStandardOutAndError(File logFile) throws IllegalArgumentException {
		if (logFile != null) {
			boolean autoflush =  Boolean.parseBoolean(PropertyLoader.getProperty(PropertyLoader.autoflushStandardOutAndErr, Boolean.FALSE.toString()));
			try {
				FileOutputStream fos = new FileOutputStream(logFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				PrintStream output =  new  PrintStream(bos, autoflush);
				System.setOut(output);
				System.setErr(output);
				if (!autoflush) {
					Runtime.getRuntime().addShutdownHook(new FlushOnExit(output));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		throw new IllegalArgumentException("null filename passed");
	}

	private static class FlushOnExit extends Thread {
		final PrintStream printStream;
		@Override
		public void run() {
			printStream.flush();
			printStream.close();
		}

		public FlushOnExit(PrintStream printStream) {
			this.printStream = printStream;
		}
	}
}
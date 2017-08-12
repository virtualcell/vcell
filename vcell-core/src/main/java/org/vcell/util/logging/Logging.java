package org.vcell.util.logging;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
	 * then see if logging already initialized e.g. -Dlog4j.configuration jvm arg passed 
	 * if not, look for default file (#PROP_FILE) in current working directory.
	 * if that's not present, just use hardcoded default settings
	 */
	static {
		initFromFile(commandLineFile( ));
		if (!isLoggingInitialized()) {
			initFromFile(new File(PROP_FILE));
		}

		if (!isLoggingInitialized()) {
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
	 * see if logging initialized (kludgy implementation) 
	 * @return true something has been set
	 */
	private static boolean isLoggingInitialized( ) {
		return Logger.getRootLogger().getAllAppenders().hasMoreElements();
	}
	
	/**
	 * attempt to initialize from file 
	 * @param initFile may be null or invalid, just doesn't do anything
	 */
	private static void initFromFile(File initFile) {
		if (initFile != null && initFile.exists()) {
			final boolean watchTheLog = WatchLogging.WATCH_LOGGING_DELAY_MILLIS > 0;
			if (!watchTheLog) {
				PropertyConfigurator.configure(initFile.getAbsolutePath());
			}
			else {
				PropertyConfigurator.configureAndWatch(initFile.getAbsolutePath(),WatchLogging.WATCH_LOGGING_DELAY_MILLIS);
			}
			if (lg.isInfoEnabled()) {
				if (watchTheLog) {
					lg.info("logger initialized from file  " + initFile.getAbsolutePath() + " and watching every " 
						+ WatchLogging.WATCH_LOGGING_DELAY_MILLIS / 1000.0 + " seconds");
				}
				else {
					lg.info("logger initialized from file  " + initFile.getAbsolutePath() ); 
					
				}
			}
		}
	}

	/**
	 * 
	 * force loading of class to invoke static initialization code
	 */
	public static void init() {
	}
	
	public static enum ConsoleDestination {
		STD_OUT("System.out"),
		STD_ERR("System.err");
		public final String log4jName;

		private ConsoleDestination(String log4jName) {
			this.log4jName = log4jName;
		}
		
	}
//	/**
//	 * change console logging from stderr to stdout, or vice-versa
//	 * @param from
//	 * @param to
//	 */
//	public static void changeConsoleLogging(ConsoleDestination from, ConsoleDestination to) {
//		Logger rootLogger = Logger.getRootLogger();
//		Enumeration<?> e = rootLogger.getAllAppenders();
//		while (e.hasMoreElements()) {
//			Object a = e.nextElement();
//			if (a instanceof ConsoleAppender) {
//				ConsoleAppender ca = (ConsoleAppender) a;
//				if (ca.getTarget().equals(from.log4jName)) {
//					ca.setTarget(to.log4jName);
//					ca.activateOptions();
//				}
//			}
//		}
//	}
}
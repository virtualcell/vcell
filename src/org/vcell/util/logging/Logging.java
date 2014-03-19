package org.vcell.util.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

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
	/**
	 * {@link org.apache.log4j Log4J } configuration file
	 */
	public static final String PROP_FILE = "log4j.properties";
	static Logger lg = Logger.getLogger(Logging.class);
	static {
		File initFile = new File(PROP_FILE);
		if (initFile.exists()) {
			PropertyConfigurator.configure(PROP_FILE);
			if (lg.isInfoEnabled())
				lg.info("logger initialized from file  "
						+ initFile.getAbsolutePath());
		}
		else {
			Properties def = new Properties();
			def.put("log4j.rootLogger","FATAL, A1");
			def.put("log4j.appender.A1","org.apache.log4j.ConsoleAppender");
			def.put("log4j.appender.A1.layout","org.apache.log4j.PatternLayout");
			def.put("log4j.appender.A1.layout.ConversionPattern","%-4r [%t] %-5p %c %x --> %m %n");
			def.put("log4j.appender.target","System.err");
			PropertyConfigurator.configure(def);
		}
	}

	/**
	 * 
	 * force loading of class to invoke static initialization code
	 */
	public static void init() {
	}
	
	public static void captureStandardOutAndError(String filename) {
		boolean autoflush =  Boolean.parseBoolean(PropertyLoader.getProperty(PropertyLoader.autoflushStandardOutAndErr, Boolean.FALSE.toString()));
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			PrintStream output =  new  PrintStream(fos, autoflush);
			System.setOut(output);
			System.setErr(output);
			Runtime.getRuntime().addShutdownHook(new FlushOnExit(output));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class FlushOnExit extends Thread {
		final PrintStream printStream;
		@Override
		public void run() {
			printStream.flush();
		}

		public FlushOnExit(PrintStream printStream) {
			super();
			this.printStream = printStream;
		}

	}

}
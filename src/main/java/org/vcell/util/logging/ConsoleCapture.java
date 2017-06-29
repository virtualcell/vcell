package org.vcell.util.logging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.vcell.util.collections.CircularList;

import cbit.vcell.resource.PropertyLoader;

/**
 * Singleton to manage redirecting standard error and out to
 * file, and reading it when desired
 */
public class ConsoleCapture {
	/**
	 * where it goes
	 */
	private File redirectedStandardOutErr;

	/**
	 * the stream used
	 */
	private PrintStream redirectedOutput;
	
	/**
	 * encoding to use
	 */
	private static final String LOG_ENCODING = "UTF-8";
	
	/**
	 * single instance
	 */
	private static ConsoleCapture instance = null;

	/**
	 * lazy create singleton
	 * @return new or existing single object 
	 */
	public static ConsoleCapture getInstance() {
		if (instance == null) {
			instance = new ConsoleCapture();
		}
		return instance;
	}
	
	/**
	 * {@link ConsoleCapture#getLogContent() return 
	 */
	public static class CurrentContent {
		public final boolean isAvailable;
		public final String content;
		/**
		 * valid content
		 * @param content
		 */
		private CurrentContent(String content) {
			super();
			isAvailable = true; 
			this.content = content;
		}
		/**
		 * invalid content
		 */
		private CurrentContent( ) {
			isAvailable = false; 
			this.content = null; 
		}
	}
	
	/**
	 * redirect standard err and out to filename; if autoflush property not set add shutdown hook
	 * @param logFile 
	 * @throws IllegalArgumentException if logFile null
	 */
	public void captureStandardOutAndError(File logFile) throws IllegalArgumentException {
		if (logFile != null) {
			redirectedStandardOutErr = logFile;
			boolean autoflush =  Boolean.parseBoolean(PropertyLoader.getProperty(PropertyLoader.autoflushStandardOutAndErr, Boolean.FALSE.toString()));
			try {
				FileOutputStream fos = new FileOutputStream(logFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				redirectedOutput =  new  PrintStream(bos, autoflush,ConsoleCapture.LOG_ENCODING);
				System.setOut(redirectedOutput);
				System.setErr(redirectedOutput);
				if (!autoflush) {
					Runtime.getRuntime().addShutdownHook(new ConsoleCapture.FlushOnExit(redirectedOutput));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		throw new IllegalArgumentException("null filename passed");
	}

	public boolean isRedirectedStandardOutErr() {
		return redirectedStandardOutErr != null;
	}
	
	/**
	 * @return current log content, if available
	 */
	public CurrentContent getLogContent( ) {
		if (redirectedStandardOutErr != null) {
			redirectedOutput.flush();
			Charset charset = Charset.forName(LOG_ENCODING);
			try {
				String content =  FileUtils.readFileToString(redirectedStandardOutErr, charset) ;
				return new CurrentContent(content);
			} catch (IOException e) {
			}
		}
		return new CurrentContent();
	}
	
	/**
	 * @param maxLines maximum lines to return; 
	 * @return current log content, if available. 
	 */
	public CurrentContent getLastLines(int maxLines) {
		CircularList<String> lines = new CircularList<>(maxLines);
		if (redirectedStandardOutErr != null) {
			redirectedOutput.flush();
			LineIterator iter;
			try {
				iter = FileUtils.lineIterator(redirectedStandardOutErr, LOG_ENCODING);
				while (iter.hasNext()) {
					lines.add(iter.next());
				}
			return new CurrentContent(String.join("\n", lines) );
			} catch (IOException e) {}
		}		
		return new CurrentContent();
	}
	
	/**
	 * use {@link ConsoleCapture#getInstance() to access the instance
	 */
	private ConsoleCapture(  ) {
		redirectedStandardOutErr = null;
		redirectedOutput = null;
	}

	/**
	 * flush stream when JVM exits
	 */
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

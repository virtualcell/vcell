package org.vcell.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * emulate {@link LineNumberReader} but skip comment lines
 * @author gweatherby
 *
 */
public class SkipCommentLineNumberReader implements AutoCloseable {
	
	/**
	 * comment char -- hard code for now, extend if necessary (YAGNI)
	 */
	static private final char COMMENT_CHAR = '#';
	
	private String bufferedLine = null;
	/**
	 * internal reader -- private to simplify number of methods which we have to reimplement
	 */
	private LineNumberReader internalReader;

	public SkipCommentLineNumberReader(Reader in) {
		internalReader = new LineNumberReader(in);
	}

	public SkipCommentLineNumberReader(Reader in, int sz) {
		internalReader = new LineNumberReader(in,sz);
	}

	public String readLine() throws IOException {
		if (bufferedLine != null) {
			String r = bufferedLine;
			bufferedLine = null;
			return r;
		}
		
		String s = internalReader.readLine();
		while (s.indexOf(COMMENT_CHAR) == 0) {
			s = internalReader.readLine();
		}
		return s;
	}
	
	public boolean ready( ) throws IOException {
		if (bufferedLine != null) {
			return true;
		}
		while (internalReader.ready()) {
			bufferedLine = internalReader.readLine();
			if (bufferedLine.indexOf(COMMENT_CHAR) != 0) {
				return true;
			}
		}
		return false;
	}
	
	public int getLineNumber( ) {
		return internalReader.getLineNumber();
	}

	@Override
	public void close() throws IOException {
		internalReader.close();
	}

}

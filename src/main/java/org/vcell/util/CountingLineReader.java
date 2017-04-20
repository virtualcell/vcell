package org.vcell.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * "Reader" which counts characters, including newlines and carriage returns
 * @author gweatherby
 */
public class CountingLineReader implements AutoCloseable { 
	private static final char CR = '\r';
	private static final char NL = '\n';
	private static final int NOT_FOUND = -1;
	
	private Reader source;
	
	char buffer[];
	/**
	 * count of character, relative to beginning of source,
	 * at beginning of buffer 
	 */
	int indexOfBuffer;
	/**
	 * index of last char in buffer with content
	 */
	int buffEnd;
	/**
	 * scan cursor inside buffer
	 */
	int cursor;
	/**
	 * beginning of end of line character(s) inside buffer
	 */
	int eol;
	
	/**
	 * how many end of line characters are there (1 or 2)
	 */
	int endCount;
	
	/**
	 * position of last returned String, relative to beginning of {@link #source}
	 */
	int stringPosition;
	
	/**
	 * @param source 
	 */
	public CountingLineReader(Reader source) {
		this(source,8192);
	}
	
	public CountingLineReader(Reader source, int buffSize) {
		buffer = new char[buffSize];
		init(source);
	}
	
	/**
	 * set to use a new source. All information regarding prior source is lost.
	 * @param source
	 */
	public void setSource(Reader source)  {
		init(source);
	}
	
	/**
	 * common initialization code
	 * @param source
	 */
	private void init(Reader source) {
		this.source = source;
		indexOfBuffer = 0;
		buffEnd = 0;
		cursor = 0;
		eol = NOT_FOUND; 
		endCount =  NOT_FOUND;
		stringPosition =  NOT_FOUND;
	}

	/**
	 * closes underlying reader
	 */
	@Override
	public void close() throws IOException {
		source.close();

	}
	
	/**
	 * find end of line currently in buffer, set
	 * {@link #eol} and {@link #endCount}
	 * @return {@link #eol}
	 */
	private int endOfLine( ) {
		endCount = 0;
		for (int i = cursor; i <buffEnd; i++) {
			switch (buffer[i]) {
			case CR:
				if (i + 1 < buffEnd && buffer[i+1] == NL) {
					endCount++;
				}
			//fall through
			case NL:
				endCount ++;
				return eol = i;
			}
		}
		return eol = NOT_FOUND;
	}
	
	/**
	 * see {@link BufferedReader#readLine}
	 * @return
	 * @throws IOException
	 */
	public String readLine( ) throws IOException {
		//do we have existing content in buffer?
		if (endOfLine( ) != NOT_FOUND) {
			int length = eol - cursor;
			String line = new String(buffer,cursor,length);
			stringPosition = indexOfBuffer + cursor;
			//position cursor at beginning of next line in buffer
			cursor += (length + endCount);
			return line;
		}
		//copy existing content to beginning of array
		int leftoverLength = buffEnd-cursor;
		System.arraycopy(buffer,cursor,buffer,0,leftoverLength);
		indexOfBuffer += cursor;
		cursor = 0;
		
		int space = buffer.length - leftoverLength;
		if (space == 0) {
			throw new IOException("buffer space " + buffer.length + " too small for input");
		}
		int r = source.read(buffer,leftoverLength,space);
		if (r != NOT_FOUND) {
			buffEnd = leftoverLength + r;
			return readLine( ); //recursively call self
		}
		if (leftoverLength > 0) {
			String line = new String(buffer,0,leftoverLength);
			stringPosition = indexOfBuffer;
			buffEnd = 0;
			return line;
		}
		
		return null;
	}


	/**
	 * @return position of last return from {@link #readLine()}, relative to beginning of reader
	 */
	public int lastStringPosition( ) {
		return stringPosition; 
	}

}

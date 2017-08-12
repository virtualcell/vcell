/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

import java.io.FileNotFoundException;
/**
 * Insert the type's description here.
 * Creation date: (1/11/2003 11:26:05 PM)
 * @author: Jim Schaff
 */
import java.io.RandomAccessFile;

public class FileTiffInputSource implements TiffInputSource {
	private RandomAccessFile randomAccessFile = null;
	public FileTiffInputSource(String filename) throws FileNotFoundException {
		randomAccessFile = new RandomAccessFile(filename, "r");
	}
	public void close() throws java.io.IOException {
		randomAccessFile.close();
	}
	public long getFilePointer() throws java.io.IOException {
		return randomAccessFile.getFilePointer();
	}
	public int read(byte buffer[]) throws java.io.IOException {
		return randomAccessFile.read(buffer);
	}
	public int read(byte[] b, int off, int len) throws java.io.IOException {
		return randomAccessFile.read(b,off,len);
	}
	public void seek(long position) throws java.io.IOException {
		randomAccessFile.seek(position);
	}
}

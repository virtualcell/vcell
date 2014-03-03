/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


/**
 * Insert the type's description here.
 * Creation date: (10/10/2002 11:57:56 AM)
 * @author: Ion Moraru
 */
public final class FileUtils {
	/**
	 * path separator for os
	 */
	public final static String PATHSEP = System.getProperty("path.separator");
	
/**
* Convenience method to copy a file from a source to a destination.
* Overwrite is prevented, and the last modified is kept, 4k buffer used.
*
* @throws IOException
*/
public static void copyFile(File sourceFile, File destFile) throws IOException {
	copyFile(sourceFile, destFile, false, true, 4 * 1024);
}
/**
* Method to copy a file from a source to a
* destination specifying if
* source files may overwrite newer destination files and the
* last modified time of <code>destFile</code> file should be made equal
* to the last modified time of <code>sourceFile</code>.
* Local/remote tuning by specifying number of bytes to use for buffering
* (use 2-4k for network, 8-16k for local copying)
*
* @throws IOException
*/
public static void copyFile(File sourceFile, File destFile, boolean overwrite, boolean preserveLastModified, int bufferSize) throws IOException {

    if (overwrite
        || !destFile.exists()
        || destFile.lastModified() < sourceFile.lastModified()) {

        if (destFile.exists() && destFile.isFile()) {
            destFile.delete();
        }

        // ensure that parent dir of dest file exists!
        File parent = destFile.getParentFile();
        if (parent!=null && !parent.exists()) {
            parent.mkdirs();
        }

        InputStream in = null;
        OutputStream out = null;
        try {
	        in = new BufferedInputStream(new FileInputStream(sourceFile));
	        out = new BufferedOutputStream(new FileOutputStream(destFile));
	
	        byte[] buffer = new byte[bufferSize];
	        while (true) {
	        	int count = in.read(buffer, 0, buffer.length);
	        	if (count == -1) {
	        		break;
	        	}
	            out.write(buffer, 0, count);	            
	        }
        } finally {
        	if (in != null) {
        		try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if (out != null) {
        		try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }

        if (preserveLastModified) {
            destFile.setLastModified(sourceFile.lastModified());
        }
    }
}

/**
 * Insert the method's description here.
 * Creation date: (6/30/2005 5:22:16 PM)
 * @return cbit.util.BigString
 */
public static String readFileToString(File file) throws IOException {
		
	// Read characters from input file into character array and transfer into string buffer.
	BufferedReader br = null;
	StringBuffer stringBuffer = new StringBuffer();
	try {
		br = new BufferedReader(new FileReader(file));
		char charArray[] = new char[10000];
		while (true) {
			int numRead = br.read(charArray, 0, charArray.length);
			if (numRead == -1) {
				break;
			}
			if (numRead > 0) {
				stringBuffer.append(charArray,0,numRead);
			}
		}
	} finally {
		if (br != null) {
			br.close();
		}
	}

	return stringBuffer.toString();
}

/**
 * return all directories, recursively
 * @param rootDir
 * @return all subdirectories
 */
public static Collection<File> getAllDirectoriesCollection(File rootDir) {
	ArrayList<File> allDirectories = new ArrayList<File>();
	for (File file : rootDir.listFiles()){
		if (file.exists() && file.isDirectory()){
			allDirectories.add(file);
			allDirectories.addAll(Arrays.asList(getAllDirectories(file)));
		}
	}
	return allDirectories;
}
/**
 * return all directories, recursively (array version)
 * @param rootDir
 * @return all subdirectories
 */
public static File[] getAllDirectories(File rootDir) {
	Collection<File> allDirectories = getAllDirectoriesCollection(rootDir);
	return allDirectories.toArray(new File[allDirectories.size()]);
}

//Following two methods were adapted from stackoverflow.com, question # 921262. Licensed under Creative Commons Attribution-ShareAlike 3.0 License
//License text: http://creativecommons.org/licenses/by-sa/3.0/legalcode
public static void saveUrlToFile(String filename, String urlString) throws MalformedURLException, IOException {
	     saveUrlToFile(new File(filename), urlString);
}

public static void saveUrlToFile(File file, String urlString) throws MalformedURLException, IOException {
    BufferedInputStream in = null;
    FileOutputStream fout = null;
    try
    {
        in = new BufferedInputStream(new URL(urlString).openStream());
        fout = new FileOutputStream(file);

        byte data[] = new byte[1024];
        int count;
        while ((count = in.read(data, 0, 1024)) != -1)
        {
        	fout.write(data, 0, count);
        }
    }
    finally
    {
        if (in != null)
        	{in.close();}
        if (fout != null)
        	{fout.close();}
    }
}

public static void deleteFile(String filePath) throws IOException {
	File f = new File(filePath);
	if (!f.exists()) {
		throw new IOException("File \""+filePath+"\" does not exist.");
	}
	if (!f.canWrite()) {
		throw new IOException("File \""+filePath+"\" is write protected.");
	}	
	if (f.isDirectory()) {
		throw new IOException("File \""+filePath+"\" is a directory, and I'm currently programmed to balk at deleting whole directories.");
	}
	boolean bSuccess = f.delete();
	if (!bSuccess) {
		throw new IOException("File \""+filePath+"\" deletion attempt failed.");
	}
}

public static void writeByteArrayToFile(byte[] byteArray, File outputFile) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
    try {
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        byteBuffer.put(byteArray);
        byteBuffer.flip();
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.write(byteBuffer);
    } finally {
        fileOutputStream.close();
    }
}


public static byte[] readByteArrayFromFile(File inputFile)throws IOException  {
    byte [] inputFileByteArrayBuffer = new byte[(int) inputFile.length()];
    InputStream inputStream = null;
    try {
        inputStream = new FileInputStream(inputFile);
        if ( inputStream.read(inputFileByteArrayBuffer) == -1 ) {
            throw new IOException("EOF character reached while trying to read the whole file:"+inputFile.getAbsolutePath());
        }        
    } finally { 
         if ( inputStream != null ) {
              inputStream.close();
         }
    }
    return inputFileByteArrayBuffer;
}

/**
 * find filename in collection of directories
 * @param filename
 * @param directories
 * @return matching files
 * @throws FileNotFoundException if entry in directories is not a valid directory
 */
public static Collection<File> findFileByName(final String filename, Collection<File> directories) throws FileNotFoundException{
	//
	// search in order of path directories and return first file which matches the filename
	//
	ArrayList<File> filesFound = new ArrayList<File>();
	for (File dir : directories){
		if (!dir.isDirectory()){
			throw new FileNotFoundException("'"+dir.getAbsolutePath()+"' is not found or not a directory");
		}
		File[] files = dir.listFiles(new FileFilter() {	
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().equals(filename)){
					return true;
				}
				return false;
			}
		});
		if (files!=null && files.length>0){
			filesFound.addAll(Arrays.asList(files));
		}
	}
	return filesFound;
}

public static Collection<String> splitPathString(String input) {
	if (input != null) {
		ArrayList<String> rval = new ArrayList<String>();
		String split[] = input.split(FileUtils.PATHSEP);
		for (String s : split) {
			rval.add(s);
		}
		return rval;
	}
	throw new IllegalArgumentException("null input to splitPathString");
}

/**
 * join collection of strings into into operating system path separator String
 * @param input
 * @return String with input elements separated by path
 * @throws IllegalArgumentException if input null 
 */
public static String pathJoinStrings(Collection<String> input) {
	if (input != null) {
		String depsBlob = "";
		Iterator<String> iter = input.iterator();
		if (iter.hasNext()) {
			//no separator for first one
			depsBlob = iter.next( );
			while(iter.hasNext()) {
				depsBlob += FileUtils.PATHSEP + iter.next( ); 
			}
		}
		return depsBlob;
	}
	throw new IllegalArgumentException("null input to splitPathString");
}
/**
 * join collection of File into into operating system path separator String
 * @param input
 * @return String with input elements separated by path
 * @throws IllegalArgumentException if input null 
 */
public static String pathJoinFiles(Collection<File> input) {
	return pathJoinStrings(toStrings(input));
	
}

/**
 * convert collection of Strings to Files. Optionally verify Strings are valid files, and omit from
 * return collection if not
 * @param input 
 * @param validate if true, check Strings to make sure valid files; 
 * @return Collection of Files corresponding to input Strings
 * @throws IllegalArgumentException if input null 
 */
public static Collection<File> toFiles(Collection<String> input, boolean validate) {
	if (input != null) {
		Collection<File> rval = new ArrayList<File>();
		for (String fname :input) {
			File f = new File(fname);
			if (!validate || f.exists()) {
				rval.add(f);
			}
		}
		return rval;
	}
	throw new IllegalArgumentException("null input to splitPathString");
}

/**
 * convert collection of Files to Strings
 * @param input 
 * @return Collection of absolute paths 
 * @throws IllegalArgumentException if input null 
 */
public static Collection<String> toStrings(Collection<File> input) {
	if (input != null) {
		Collection<String> rval = new ArrayList<String>();
		for (File file:input) {
			rval.add(file.getAbsolutePath());
		}
		return rval;
	}
	throw new IllegalArgumentException("null input to splitPathString");
}

}

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
import java.util.Random;


/**
 * Insert the type's description here.
 * Creation date: (10/10/2002 11:57:56 AM)
 * @author: Ion Moraru
 */
public final class FileUtils {
	private static Random rand = new Random(System.currentTimeMillis());
/**
* Convienence method to copy a file from a source to a destination.
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
* Convienence method to copy a file from a source to a destination.
* Overwrite is prevented, and the last modified is kept, 4k buffer used.
*
* @throws IOException
*/
public static void copyFile(String sourceFile, String destFile) throws IOException {
	copyFile(new File(sourceFile), new File(destFile), false, true, 4 * 1024);
}
public static File createTempFile(String prefix, String suffix, File parentDir) {

    File result = null;
    java.text.DecimalFormat fmt = new java.text.DecimalFormat("#####");
    synchronized (rand) {
        do {
            result = new File(parentDir, prefix + fmt.format(rand.nextInt()) + suffix);
        } while (result.exists());
    }
    return result;
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

public static File getRelativePath(File sourceDir, File targetFile, boolean bAllowAbsolutePaths) throws IOException {
//	System.out.println("sourceDir = "+sourceDir.getPath());
//	System.out.println("targetFile = "+targetFile.getPath());
	File originalSourceDir = sourceDir;
	int counter = 0;
	while (sourceDir!=null && !targetFile.getPath().startsWith(sourceDir.getPath())){
		sourceDir = sourceDir.getParentFile();
		counter++;
	}
	if (sourceDir==null){
		if (bAllowAbsolutePaths){
			return targetFile;
		}else{
			throw new IOException("cannot find relative path between '"+originalSourceDir.getPath()+"' and '"+targetFile.getPath()); 
		}
	}
	String sourcePath = sourceDir.getPath();
	String targetPath = targetFile.getPath().replace(sourcePath,"");
	String prefix = "."+File.separator;
	for (int i=0;i<counter;i++){
		prefix = prefix + ".."+File.separator;
//		targetPath = targetPath.substring(targetPath.indexOf(File.separator));
	}
	return new File(prefix + targetPath);
}

public static File[] getAllDirectories(File rootDir) {
	ArrayList<File> allDirectories = new ArrayList<File>();
	for (File file : rootDir.listFiles()){
		if (file.exists() && file.isDirectory()){
			allDirectories.add(file);
			allDirectories.addAll(Arrays.asList(getAllDirectories(file)));
		}
	}
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

public static void main(String[] args) {
	try {
		String inputString = "3.14159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196442881097566593344612847564823378678316527120190914564856692346034861045432664821339360726024914127372458700660631558817488152092096282925409171536436789259036001133053054882046652138414695194151160943305727036575959195309218611738193261179310511854807446237996274956735188575272489122793818301194912\n";
		System.out.println("We're feeding in: "+inputString);
		byte[] data = inputString.getBytes("ISO-8859-1");
		File file = File.createTempFile("TestTempFile", ".tmp");
		System.out.println("Temp file location being created = "+file.getAbsolutePath());
		writeByteArrayToFile(data, file);
		System.out.println("Done writing.  Now will read it back.");
		byte[] bytes = readByteArrayFromFile(file);
		String readBackString = new String(bytes);
		System.out.println("We got back: "+readBackString);
		System.out.println("Comparing the input and output yields: "+inputString.equals(readBackString));
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}

public static File[] findFileByName(final String filename, File[] directories) throws FileNotFoundException{
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
	return filesFound.toArray(new File[filesFound.size()]);
}

}

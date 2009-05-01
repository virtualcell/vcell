package org.vcell.util;

import java.io.*;
import java.util.*;

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
        // not using getParentFile method to stay 1.1 compat
        File parent = new File(destFile.getParent());
        if (!parent.exists()) {
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

}

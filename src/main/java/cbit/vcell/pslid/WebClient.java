/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.pslid;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.VCellThreadChecker;



public class WebClient {
	
	static final int modeReturnText = 0;
	static final int modeReturnBytes = 1;
	static final int modeSaveImage = 2;

	public static StringBuffer doWork(String args, int mode, ClientTaskStatusSupport pp) throws IOException {
		VCellThreadChecker.checkRemoteInvocation();
		
//        System.out.println("http.proxyHost: " + System.getProperty("http.proxyHost"));
//        System.out.println("http.proxyPort: " + System.getProperty("http.proxyPort"));
//        System.out.println("networkaddress.cache.ttl: " + System.getProperty("networkaddress.cache.ttl"));
		StringBuffer results = null;
//		try {
			switch(mode) {
			case modeReturnText:
				results = fetchReturnText(new URL(args), pp);
				break;
			case modeReturnBytes:
			case modeSaveImage:
				results = fetchSaveImage(new URL(args), pp);
				break;
			}
//		} 
//		catch (MalformedURLException e) {
//			System.err.println("Malformed URL: " + args);
//		} 
//		catch (IOException e) {
//			System.err.println("Failed to fetch URL " + args + ": " + e.getMessage());
//			e.printStackTrace();
//		}
//        System.out.println("WebClient: exiting doWork()");
		return results;
	}
	
	private static StringBuffer fetchReturnText(URL url, ClientTaskStatusSupport pp) throws IOException {
		
		if(pp != null) {
			pp.setMessage("Contacting PSLID service...");
			pp.setProgress(pp.getProgress()+3);
		}
	    InputStream in = url.openStream();
		if(pp != null) {
			pp.setMessage("PSLID open, fetching data...");
			pp.setProgress(pp.getProgress()+3);
		}
	    System.out.println("--- stream opened");
	    StringBuffer buffer = new StringBuffer();
	    try {
	    	byte[] data = new byte[1024];
	    	int length = -1;
	    	while ((length = in.read(data)) != -1) {
	    		buffer.append(new String(data, 0, length));
	    	}
//	    	System.out.println(buffer);			// uncomment to flush to console all the results we get
			if(pp != null) {
				pp.setMessage("Data fetched");
				pp.setProgress(pp.getProgress()+20);
			}
	        System.out.println("--- data fetched");
	    } finally {
	    	in.close();
	    }
	    return buffer;
	}

	// saves content as a file
	// used when we download an image
	private static StringBuffer fetchSaveImage(URL url, ClientTaskStatusSupport pp) throws IOException {

		if(pp != null) {
			pp.setMessage("Contacting PSLID service...");
			pp.setProgress(pp.getProgress()+5);
		}
		URLConnection connection = url.openConnection();
		connection.connect();
		InputStream in = connection.getInputStream();
		
		StringBuffer buffer = new StringBuffer();
		String fileName = "c:\\acellimage.tif";
		buffer.append(fileName);
		
		FileOutputStream fileOutput = new FileOutputStream (fileName);
		DataOutputStream os = new DataOutputStream (fileOutput);
		if(pp != null) {
			pp.setMessage("PSLID open, fetching data...");
			pp.setProgress(pp.getProgress()+5);
		}
	    System.out.println("--- stream opened");
		
		try {
			byte[] buf = new byte[1024];
			int nread = 0;
			while ((nread = in.read(buf)) > 0) {
				os.write(buf, 0, nread);
//				System.out.write(buf, 0, nread);
			}
//			System.out.flush();
			if(pp != null) {
				pp.setMessage("Data fetched");
				pp.setProgress(pp.getProgress()+20);
			}
	        System.out.println("--- data fetched");
			os.flush();
		} finally {
			os.close();
			in.close();
		}
	return buffer;
	}
}


//read an image
//Image img = ImageIO.read(new URL("http://java.net/images/header_jnet_new.jpg"));}
//Image img = ImageIO.read(new URL("http://pslid.cbi.cmu.edu/develop/makecompartmentimage.jsp?file=/images/3T3/3D/Mito012-Jarvik/mito012-1/mito012-1--16----1.tif"));
//   http://pslid.cbi.cmu.edu/develop/makecompartmentimage.jsp?file=/images/Hela/3D/LAM/cell49/prot/LAM20020118ss12s06q1i049.z9.tif

/*
private static void fetch(URL url) throws IOException {

URLConnection connection = url.openConnection();
connection.connect();
BufferedReader in = new BufferedReader(
        new InputStreamReader(
        		connection.getInputStream()));
try {
	String inputLine;

    while ((inputLine = in.readLine()) != null) {
        System.out.println(inputLine);
	}
	System.out.flush();
} finally {
	in.close();
}
}
}
*/



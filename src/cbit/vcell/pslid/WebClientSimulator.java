package cbit.vcell.pslid;

import java.io.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

import cbit.vcell.server.PropertyLoader;

public class WebClientSimulator {
		
	public static void main(java.lang.String[] args) {

//		final String myURL = "http://pslid.cbi.cmu.edu/develop/search.jsp?protein=atp5a1";
//		final String myURL = "http://www.yahoo.com";
//		final String myURL = "http://pslid.cbi.cmu.edu/develop/genmodel.jsp?protset1=central_slice_lys_2&selectset2=using&settype=regionset&settitle=2d+region+set&task=genmodel&table=tblregion_Sets&setnum=2&multisel=0&next=Continue";
//		final String myURL = "http://pslid.cbi.cmu.edu/develop/searchreturnxml.jsp?protein=atp5a1";
//		final String myURL = "http://pslid.cbi.cmu.edu/develop/searchreturnxml.jsp?protein=LAMP2";
//		final String myURL = "http://pslid.cbi.cmu.edu/develop/makecompartmentimage.jsp?file=/images/3T3/3D/Mito012-Jarvik/mito012-1/mito012-1--16----1.tif";
//	IMG SRC=images/thumbnails/images/3T3/3D/Mito012-Jarvik/mito012-16/mito012-16--16----1.tif.mask_2.tif.singlenail.jpg		

//		System.setProperty("http.proxyHost", "MS2.vcell.uchc.edu");
//		System.setProperty("http.proxyPort", "80");
//		System.setProperty("networkaddress.cache.ttl", "-1");
	
		/****************************************************************************************
		 * testing of various URLs used for PSLID queries
		 */
	try {
		WebClientInterface wci = new WebClientInterface();
		String result;
		PropertyLoader.loadProperties();
		
		// recovers list of proteins
		wci.requestAllProteinList();
		handshake(wci);
		result = wci.getDoc().toString();
		
		// recovers list of all proteins separated by cell type (Experimental)
		wci = new WebClientInterface();
		wci.requestCellProteinListExperimental();
		handshake(wci);
		result = wci.getDoc().toString();
		
		// recovers list of all proteins separated by cell type (Generated)
		wci = new WebClientInterface();
		wci.requestCellProteinListGenerated();
		handshake(wci);
		result = wci.getDoc().toString();
/*
		// recovers image results for a protein / cell pair
		wci = new WebClientInterface();
		wci.requestProteinCellDetails("LAMP2", "HeLa");
		handshake(wci);
		result = wci.getDoc().toString();
*/		
		// parse the xml file and recover <compartment_image_url>
		wci = new WebClientInterface();
		wci.requestImage("http://pslid.cbi.cmu.edu/tcnp/makecompartmentimage.jsp?regionid=523&stackpos=1");
		handshake(wci);
		result = wci.getDoc().toString();	// here is the image, saved as a file
		System.out.print(result);

		/**********************************************************************************************
		 * Generative model data recovery
		 */
		// recovers a generated model 
		wci = new WebClientInterface();
		wci.requestGenerativeModelImage("central_slice_lys_2", "using");
		handshake(wci);
		result = wci.getDoc().toString();
		System.out.print(result);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in PSLID client test");
		exception.printStackTrace(System.out);
	}


	}
	
	// wait here till url is retrieved
	public static void handshake(WebClientInterface wci) {
		while (!wci.isDone()) {
//			System.out.print("*");
			try {
				Thread.sleep(1000);			
			} catch (InterruptedException e) {
				// ignore
			}
		}
		System.out.println("");
	}
}

// examples of compartment _image_urls
// for now we hardcode one of them
//	String compartmentImageURL = "http://pslid.cbi.cmu.edu/develop/makecompartmentimage.jsp?file=/images/3T3/3D/Mito012-Jarvik/mito012-1/mito012-1--16----1.tif";
//	String compartmentImageURL = "http://pslid.cbi.cmu.edu/develop/makecompartmentimage.jsp?file=/images/Hela/3D/LAM/cell49/prot/LAM20020118ss12s06q1i049.z9.tif";
// 	String compartmentImageURL = "http://pslid.cbi.cmu.edu/develop/makecompartmentimage.jsp?file=/images/Hela/2D_tif/h4b4/prot/r06aug97.h4b4.03--1---2.tif";

/*
// get xml file for this protein - example of usage
wci = new WebClientInterface();
wci.requestProteinData("LAMP2");
handshake(wci);
result = wci.getDoc().toString();
*/

/*		
wci.execute(myURL);



while (!wci.isDone()) {
//	System.out.print("*");
	try {
		Thread.sleep(1000);			
	} catch (InterruptedException e) {
		// ignore
	}
}
System.out.println("");
//System.out.println(wci.getDoc());

// parsing
try {
	Reader r = new StringReader(wci.getDoc().toString());
	HTMLEditorKit.Parser parser;
	parser = new ParserDelegator();
	parser.parse(r, new HTMLParseLister(), true);
	r.close();
}
catch (Exception e) {
    System.err.println("Error: " + e);
    e.printStackTrace(System.err);
}  */

/*		// TEST an URL
wci = new WebClientInterface();
wci.test("http://www.yahoo.com");
handshake(wci);
result = wci.getDoc().toString();
*/
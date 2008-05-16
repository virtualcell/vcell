package cbit.vcell.pslid;

import javax.swing.*;

import swingthreads.SwingWorker;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cbit.util.AsynchProgressPopup;
import cbit.vcell.field.PSLIDPanel;
import cbit.vcell.server.PropertyLoader;

public class WebClientInterface {

	final int modeReturnText = 0;
	final int modeReturnBytes = 1;
	final int modeSaveImage = 2;
	
	final int URL_RequestTimeout = 3600;
	
	private int mode;
	private StringBuffer doc;
	private String myURL;
	private boolean done = false;
	private boolean expired = false;
	
	// automated testing possible with pp = null
	public AsynchProgressPopup pp;
	private PSLIDPanel panel;

	public WebClientInterface () {
		pp = null;
		panel = null;
	}
	public WebClientInterface (AsynchProgressPopup app) {
		panel = null;
		if(app != null) {
			pp = app;
		} else {
			pp = null;
		}
	}
	public WebClientInterface (PSLIDPanel pslidPanel, AsynchProgressPopup app) {
		if(pslidPanel != null) {
			panel = pslidPanel;
		} else {
			panel = null;
		}
		if(app != null) {
			pp = app;
		} else {
			pp = null;
		}
	}

	// high lvl API - specific to PSLID
	public void requestAllProteinList() {					// returns list of all proteins
		System.out.println("request list of all proteins");
		final String pslidURL = new String(PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.pslidAllProteinListURL));
		//  "http://pslid.cbi.cmu.edu/develop/return_xml_list.jsp?listtype=target";
 		execute(pslidURL, modeReturnText);
	}
	public void requestCellProteinListExperimental() {		// returns list of all proteins separated by cell type (exp results)
        System.out.println("request list of all proteins separated by cell type (exp)");
        final String pslidURL = new String(PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.pslidCellProteinListExpURL));
//		final String pslidURL = "http://pslid.cbi.cmu.edu/develop/return_xml_list.jsp?listtype=target_cell_name";
		execute(pslidURL, modeReturnText);
	}
	public void requestCellProteinListGenerated() {			// returns list of all proteins separated by cell type (generatd model)
        System.out.println("request list of all proteins separated by cell type (gen)");
        final String pslidURL = new String(PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.pslidCellProteinListGenURL));
//		final String pslidURL = "http://pslid.cbi.cmu.edu/develop/return_xml_list.jsp?listtype=gen_model";
		execute(pslidURL, modeReturnText);
	}
	public void requestProteinCellDetails(String protein, String cell) {	// returns info about a protein/cell pair
		//   ex:  http://pslid.cbi.cmu.edu/develop/searchreturnxml.jsp?target=LAMP2&cell_name=HeLa
		final String baseURL1 = new String(PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.pslidCellProteinImageInfoExpURL));
		final String baseURL2 = "&cell_name=";
		String builtURL = baseURL1 + protein + baseURL2 + cell;
//		String builtURL = baseURL1 + protein;		// simplified call for now - we only specify protein (target); full call above
        System.out.println("request list images for protein/cell pair at: " + builtURL);
		execute(builtURL, modeReturnText);
	}
	public void requestURL(String url) {			// saves to a file the requested image
        System.out.println("requestURL(): " + url);
		myURL = url;
		execute(myURL, modeReturnText);
	}
	public void requestImage(String url) {			// saves to a file the requested image
        System.out.println("requestImage() "+url);
		myURL = url;
		execute(myURL, modeSaveImage);
	}
	public void requestGenerativeModelImage(String protset, String activity) {	// returns 
		final String baseURL1 = "http://pslid.cbi.cmu.edu/tcnp/genmodel_TCNP.jsp?protset1=";
		final String baseURL2 = "&selectset2=";
		final String baseURL3 = "&settype=regionset&settitle=2d+region+set&task=genmodel&table=tblregion_Sets&setnum=2&multisel=0&next=Continue";
		String builtURL = baseURL1 + protset + baseURL2 + activity + baseURL3;
	    System.out.println("request generative model for " + protset);
		execute(builtURL, modeSaveImage);
//		execute("http://pslid.cbi.cmu.edu/develop/genmodel_TCNP.jsp?protset1=central_slice_lys_2&selectset2=using&settype=regionset&settitle=2d+region+set&task=genmodel&table=tblregion_Sets&setnum=2&multisel=0&next=Continue", modeSaveImage);
	}
	public void test(String testURL) {					// returns list of all proteins
        System.out.println("testing URL: " + testURL);
		execute(testURL, modeReturnText);
	}

	public StringBuffer getDoc() {
		return this.doc;
	}
	public boolean isDone() { return this.done; }
	public boolean isExpired() { return this.expired; }
	public void handshake() {
		int count = 0;
		while (!isDone()) {
			if(pp != null) {
				switch (count) {	// decreasing progress bar speed
				case 0:
				case 1:
				case 2:
					pp.setProgress(pp.getProgress()+ 2);
					break;
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					pp.setProgress(pp.getProgress()+ 1);
					break;
				default:
					if (count > 10 && (count%7 == 0))
					pp.setProgress(pp.getProgress()+ 1);
					break;
				}
			}
//			System.out.print("*");
			count++;
			if (count >= URL_RequestTimeout) {
				System.out.println("Thread is expired!");
				expired = true;
				return;
			}
			if(panel != null && (panel.getThreadState() != Thread.currentThread()) ) {
				System.out.println("Thread is interrupted!");
				expired = true;
				return;
			}
			try {
				Thread.sleep(1000);			
			} catch (InterruptedException e) {
				// ignore
			}
		}
		System.out.println("*done");
	}
    public static byte[] getBytesFromFile(File file) throws IOException {
        
        InputStream is = new FileInputStream(file);
        // Get the size of the file
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    
    //==============================================================
	// generic API lvel
	private void execute(String url, int mode) {
		this.done = false;
		this.mode = mode;
		this.myURL = url;
		worker.start();
	}
	
	private SwingWorker worker = new SwingWorker() {
		
		public StringBuffer construct() {
			return WebClient.doWork(myURL, mode, pp);
		}
		public void finished() {
	        try {
        		doc = (StringBuffer)getValue();
	            done = true;
	            System.out.println("  finished(): Done");
	            //fire....
//	        } catch (InterruptedException ignore) {}
//	        catch (java.util.concurrent.ExecutionException e) {
	        }  catch (Exception e) {
	            String why = null;
	            Throwable cause = e.getCause();
	            if (cause != null) {
	                why = cause.getMessage();
	            } else {
	                why = e.getMessage();
	            }
	            System.err.println("Error fetching data: " + why);
	        }
		}
	};
}

/*
worker = new SwingWorker() {
   public Object construct() {
      return doWork();
   }
   public void finished() {
      startButton.setEnabled(true);
      interruptButton.setEnabled(false);
      statusField.setText(get().toString());
   }
};
*/

/*
private SwingWorker worker = new SwingWorker<StringBuffer, Void>() {
	public StringBuffer doInBackground() {
		return WebClient.execute(myURL, mode);
	}
	
	public void done() {
        try {
            doc = get();
            done = true;
//            System.out.println(doc);
            System.out.println("--------- get() Done");
            //fire....
        } catch (InterruptedException ignore) {}
        catch (java.util.concurrent.ExecutionException e) {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            System.err.println("Error fetching data: " + why);
        }
	}
};
*/

/*		doc = new StringBuffer();						// simulation mode
doc.append("atp5a1\n");
doc.append("LAMP2");   */

/* 
	// !!!  obsolete - do not call
	public void requestProteinData(String protein) {	// returns xml document with list of images
		final String baseURL = "http://pslid.cbi.cmu.edu/develop/searchreturnxml.jsp?protein=";
        System.out.println("requestProteinData()");
		myURL = baseURL+protein;
		execute(myURL, modeReturn);
	}
	// !!!  obsolete - do not call
	public void requestImageList(StringBuffer xmlFile) {	// parses xml document and extracts images
		done = false;
        System.out.println("requestImageList()");
		done = true;
	}
	
	*/


/*
	import java.net.URL;
	import java.net.URLConnection;
	import java.net.HttpURLConnection;
	import java.io.DataOutputStream;
	import java.io.DataInputStream;
	import java.io.FileOutputStream;

 	public void tryLog() {
        try {
        	URL url = new URL("http://pslid.cbi.cmu.edu/develop/login.html");
        	HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod("POST");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setAllowUserInteraction(true);
            HttpURLConnection.setFollowRedirects(true);
            urlConn.setInstanceFollowRedirects(true);
            urlConn.setRequestProperty("content-type-Type", "text/html; charset=iso-8859-1");
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
          //  String content = "email=" + URLEncoder.encode(EMAIL_ID, "ISO-8859-1") + "&pass=" + URLEncoder.encode(PASS_WORD, "ISO-8859-1") + "&action=login";
             String content = "email=" + EMAIL_ID + "&pass=" + PASS_WORD + "&action=login.php";
            System.out.println(content + "\n" + "sending form to HTTP server ...");
            out.writeBytes(content);
           System.out.println(urlConn.getPermission());
            setCookie(urlConn.getHeaderField("Set-Cookie"));
            //System.out.println("sessionid: " + session_id);

            out.flush();
            out.close();
            // get input connection
//            StringBuffer in = new StringBuffer(new InputStreamReader(urlConn.getInputStream()));	// BufferedReader
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/	





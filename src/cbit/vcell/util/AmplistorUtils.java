package cbit.vcell.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.util.Hex;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.KeyValue;

import cbit.util.xml.XmlUtil;
import cbit.vcell.server.AuthenticationException;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.Simulation;

public class AmplistorUtils {

	private static Random rand = null;

	private static final String AMPLISTOR_CUSTOM_META_PREFIX = "X-Ampli-Custom-Meta-";
	public static final String CUSTOM_FILE_MODIFICATION_DATE = AMPLISTOR_CUSTOM_META_PREFIX+"modification-date";

	private static enum AMPLI_OP_KIND {FILE,DIR};
	private static enum AMPLI_OP_METHOD {GET,PUT,DELETE};
	
	public static final String DEFAULT_AMPLI_SERVICE_VCELL_URL = 	"http://obj1.cam.uchc.edu:8080/namespace/service_vcell/";
	public static final String DEFAULT_AMPLI_VCELL_LOGS_URL = 		"http://obj1.cam.uchc.edu:8080/namespace/vcell_logs/";
	public static final String DEFAULT_PROXY_AMPLI_VCELL_LOGS_URL = "http://archive.cam.uchc.edu/namespace/vcell_logs/";
	public static final String DEFAULT_AMPLI_VCELL_VCDBBACKUP_URL = "http://obj1.cam.uchc.edu:8080/namespace/vcell_vcdbbackup/";
	
	public static class AmplistorCredential {
		public String userName;
		public String password;
		public AmplistorCredential(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}
	}
	
	public static SimulationData.SimDataAmplistorInfo getSimDataAmplistorInfoFromPropertyLoader(){
		SimulationData.SimDataAmplistorInfo simDataAmplistorInfo = null;
		String amplistor_VCell_Users_RootPath = PropertyLoader.getProperty(PropertyLoader.amplistorVCellServiceURL, null);
		if(amplistor_VCell_Users_RootPath != null){
			String ampliUserName = PropertyLoader.getProperty(PropertyLoader.amplistorVCellServiceUser, null);
			String ampliPassword = PropertyLoader.getProperty(PropertyLoader.amplistorVCellServicePassword, null);
			simDataAmplistorInfo = new SimulationData.SimDataAmplistorInfo(amplistor_VCell_Users_RootPath,
				(ampliUserName!=null && ampliPassword != null?new AmplistorUtils.AmplistorCredential(ampliUserName, ampliPassword):null));
		}
		return simDataAmplistorInfo;
	}
	
	public static boolean bFileExists(URL ampliURLFilePath,AmplistorCredential amplistorCredential) throws Exception{
		CheckOPHelper checkOPHelper = null;
		try{
			checkOPHelper = ampliCheckOP(ampliURLFilePath, amplistorCredential, AMPLI_OP_METHOD.GET, null, AMPLI_OP_KIND.FILE, null);
			return true;
		}catch(FileNotFoundException fnfe){
			return false;
		}finally{
			if(checkOPHelper != null && checkOPHelper.httpURLConnection != null){
				checkOPHelper.httpURLConnection.disconnect();
			}
		}
	}
	

	public static byte[] getObjectData(String urlStr,AmplistorCredential amplistorCredential) throws Exception{
		CheckOPHelper checkOPHelper = null;
		try{
			URL httpURL = new URL(urlStr);
			checkOPHelper = ampliCheckOP(httpURL, amplistorCredential, AMPLI_OP_METHOD.GET, null, AMPLI_OP_KIND.FILE,null);
			
			String size = checkOPHelper.httpURLConnection.getHeaderField("X-Ampli-Size");
//			System.out.println("----- "+size+" -----");
			byte[] data = new byte[Integer.parseInt(size)];
			
			DataInputStream dis = new DataInputStream(checkOPHelper.httpURLConnection.getInputStream());
			dis.readFully(data);
			dis.close();
			return data;
		}finally{
			if(checkOPHelper != null && checkOPHelper.httpURLConnection != null){checkOPHelper.httpURLConnection.disconnect();}
		}
	}

	public static void getObjectDataPutInFile(String urlStr,AmplistorCredential amplistorCredential,File toFile) throws Exception{
		CheckOPHelper checkOPHelper = null;
		BufferedOutputStream bos = null;
		try{
			URL httpURL = new URL(urlStr);
			checkOPHelper = ampliCheckOP(httpURL, amplistorCredential, AMPLI_OP_METHOD.GET, null, AMPLI_OP_KIND.FILE,null);
			long contentLength = 256*256;
			try{
				contentLength = Long.parseLong(checkOPHelper.httpURLConnection.getHeaderField("X-Ampli-Size"));
			}catch(NumberFormatException nfe){
				nfe.printStackTrace();
			}
			if (contentLength > 0) { //zero-byte files have no data to transfer
				int totalRead = 0;
				BufferedInputStream bis = new BufferedInputStream(checkOPHelper.httpURLConnection.getInputStream());
				byte[] tempBuffer = new byte[(int)Math.min(contentLength, Math.pow(8, 7))];
				bos = new BufferedOutputStream(new FileOutputStream(toFile));
				while(totalRead < contentLength){
					int numread = bis.read(tempBuffer,0,tempBuffer.length);
					if(numread == -1){
						throw new IOException("Premature end of data transferring " + urlStr + " to " + toFile.getAbsolutePath()
								+ ", " + totalRead + " bytes read, " + contentLength + " expected");
					}
					totalRead += numread;
					bos.write(tempBuffer,0,numread);
				}
				bos.flush();
			}
			else { //if empty, simply create the file
				toFile.createNewFile();
			}
	        if(checkOPHelper.httpURLConnection.getHeaderField(CUSTOM_FILE_MODIFICATION_DATE) != null){
				Date customModificationDate = convertDateMetaData(checkOPHelper.httpURLConnection.getHeaderField(CUSTOM_FILE_MODIFICATION_DATE));
				toFile.setLastModified(customModificationDate.getTime());
	        }
		}finally{
			if(bos!=null){try{bos.close();}catch(Exception e){e.printStackTrace();}}
			if(checkOPHelper != null && checkOPHelper.httpURLConnection != null){checkOPHelper.httpURLConnection.disconnect();}
		}
	}

	private static Date convertDateMetaData(String dateLong) throws Exception{
		if(dateLong.indexOf('"') != -1){//get rid of quotes
			if(dateLong.charAt(0)=='"' && dateLong.charAt(dateLong.length()-1)=='"'){
				dateLong = dateLong.substring(1, dateLong.length()-1);
			}else{
				throw new Exception("Unexpected quotes in date string '"+dateLong+"'");
			}
		}
		int dotIndex = dateLong.indexOf('.');
		Date date = null;
		if(dotIndex != -1){
			//parse fractional seconds and convert to milliseconds
			String beforeDot = dateLong.substring(0,dotIndex);
			String afterDot = dateLong.substring(dotIndex,dateLong.length());
			double secFrac = (afterDot.equals(".")?0.0:Double.parseDouble(afterDot));
			afterDot = ""+(int)(secFrac*1000);
			afterDot = (afterDot.length()<2?"0":"")+(afterDot.length()<3?"0":"")+afterDot;
			date = new Date(Long.parseLong(beforeDot+afterDot));
		}else{
			date = new Date(Long.parseLong(dateLong+"000"));//add 000 milisecs
		}
		return date;
	}

	public static Hashtable<File, Exception> uploadFilesOperation(File userDir,URL remoteDestinationDirURL,final AmplistorCredential amplistorCredential) throws Exception{
		return uploadFilesOperation0(userDir, remoteDestinationDirURL,null, amplistorCredential);
	}
	public static Hashtable<File, Exception> uploadSimFilesOperation(File userDir,URL remoteDestinationDirURL,KeyValue simID,final AmplistorCredential amplistorCredential) throws Exception{
		return uploadFilesOperation0(userDir, remoteDestinationDirURL,simID, amplistorCredential);
	}
	private static Hashtable<File, Exception> uploadFilesOperation0(File localDir,final URL remoteDestinationDirURL,KeyValue simID,final AmplistorCredential amplistorCredential) throws Exception{
		class MultiThreadCounter{
			private int maxThread;
			private int currentThreadCount;
			public MultiThreadCounter(int maxThread){
				this.maxThread = maxThread;
				this.currentThreadCount = maxThread;
			}
			public synchronized int getAccess(){
				if(currentThreadCount == 0){
					return currentThreadCount;
				}
				currentThreadCount--;
				return currentThreadCount+1;
			}
			public synchronized void release() throws Exception{
				if(currentThreadCount == maxThread){
					throw new Exception("Unexpected release");
				}
				currentThreadCount++;
			}
			public synchronized boolean isIdle(){
				return currentThreadCount == maxThread;
			}
		};
		final MultiThreadCounter multiThreadCounter = new MultiThreadCounter(10);
//		System.out.println("Getting list of files for "+localDir);
//		try {
		try{
			ampliDirOperation(remoteDestinationDirURL.toString(), amplistorCredential, AMPLI_OP_METHOD.GET);
		}catch(FileNotFoundException e){
			ampliDirOperation(remoteDestinationDirURL.toString(), amplistorCredential, AMPLI_OP_METHOD.PUT);
		}
		File[] listFiles0 = null;
		if(simID != null){
			final String simIDPrefix = Simulation.createSimulationID(simID);
			listFiles0 = localDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().startsWith(simIDPrefix);
				}
			});
		}else{
			listFiles0 = localDir.listFiles();
		}
		final File[] listFiles = listFiles0;
		final Hashtable<File, Exception> failures = new Hashtable<File,Exception>();
		for (int i = 0; i < listFiles.length; i++) {
//				if(i%100 == 0){
//					System.out.println("progress="+((i*100)/listFiles.length)+" percent");
//				}
			if(listFiles[i].isFile()){
				final File currentFile = listFiles[i];
				@SuppressWarnings("unused")
				int threadid = 0;
				while((threadid = multiThreadCounter.getAccess()) == 0){Thread.sleep(100);}//wait for available thread
//					System.out.println(threadid+" "+currentFile.getName());
				new Thread(new Runnable() {
					@Override
					public void run(){
						try{
							uploadFile(remoteDestinationDirURL, currentFile, amplistorCredential);
						}catch(Exception e){
							failures.put(currentFile,e);
						}finally{
							try{multiThreadCounter.release();}catch(Exception e){System.out.println("RELEASE error "+e.getMessage());}
						}
					}
				}).start();
			}
		}
		while(!multiThreadCounter.isIdle()){Thread.sleep(100);}//wait for last threads to finish
		if(failures.size() != 0){
			return failures;
		}
		return null;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}

	public static void uploadString(String remoteDestinationFileURL,AmplistorCredential amplistorCredential,String uploadStr) throws Exception{
		byte[] uploadBytes = uploadStr.getBytes();
		InputStream is = new ByteArrayInputStream(uploadBytes);
		createWithPutXML(remoteDestinationFileURL.toString(), amplistorCredential,is,uploadBytes.length,null);
	}
	public static void uploadStream(URL remoteDestinationFileURL,AmplistorCredential amplistorCredential,InputStream is,long size) throws Exception{
		createWithPutXML(remoteDestinationFileURL.toString(), amplistorCredential,is,size,null);		
	}
	public static void uploadFile(URL remoteDestinationDirURL,File currentFile,AmplistorCredential amplistorCredential) throws Exception{
		BufferedInputStream bis = null;
		try{
			bis = new BufferedInputStream(new FileInputStream(currentFile));
			String urlStr = remoteDestinationDirURL.toString()+"/"+currentFile.getName();
			uploadStream(new URL(urlStr), amplistorCredential, bis, currentFile.length());
			setFileMetaData(urlStr, amplistorCredential, CUSTOM_FILE_MODIFICATION_DATE, currentFile.lastModified()/1000+".0");
		}finally{
			try{if(bis != null){bis.close();}}catch(Exception e){System.out.println("bis close error "+e.getMessage());}
		}

	}
	public static void setFileMetaData(String targetURL,AmplistorCredential amplistorCredential,String metaDataName,String metaDataValue) throws Exception{
		HashMap<String, String> metaData = new HashMap<String, String>();
		metaData.put(metaDataName, metaDataValue);
		ampliCheckOP(new URL(targetURL+"?update=set"), amplistorCredential, AMPLI_OP_METHOD.PUT, null, AMPLI_OP_KIND.FILE, metaData);
	}

	public static void deleteFilesOperation(String[] fileNames,String fullAmplistorDirURL,AmplistorCredential amplistorCredential) throws Exception{
		for(String fileName:fileNames){
			URL httpURL = new URL(fullAmplistorDirURL+(fullAmplistorDirURL.charAt(fullAmplistorDirURL.length()-1)=='/'?"":"/")+fileName);
			ampliCheckOP(httpURL, amplistorCredential, AMPLI_OP_METHOD.DELETE, null, AMPLI_OP_KIND.FILE,null);
		}
	}
	public static void createDir(String dirNameURL,AmplistorCredential amplistorCredential) throws Exception{
		ampliDirOperation(dirNameURL, amplistorCredential,AmplistorUtils.AMPLI_OP_METHOD.PUT);
	}
	public static void deleteDir(String dirNameURL,AmplistorCredential amplistorCredential) throws Exception{
		ampliDirOperation(dirNameURL, amplistorCredential,AmplistorUtils.AMPLI_OP_METHOD.DELETE);
	}
	private static void ampliDirOperation(String dirNameURL,AmplistorCredential amplistorCredential,AMPLI_OP_METHOD ampliOpMethod) throws Exception{
	    URL httpURL = new URL(dirNameURL/*+"?meta=xml"*/);//don't use "?meta=xml" when deleteing
	    ampliCheckOP(httpURL, amplistorCredential, ampliOpMethod, null, AMPLI_OP_KIND.DIR,null);
	}
	private static Document getResponseXML(HttpURLConnection urlCon) throws Exception{
		if(urlCon.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT){
			return null;
		}
		InputStreamReader isr = null;
		BufferedReader br = null;
		try{
			isr = new InputStreamReader(urlCon.getInputStream());
			br = new BufferedReader(isr);
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			
			return XmlUtil.stringToXML(response.toString(), null);
		}finally{
			if(br != null){try{br.close();}catch(Exception e){e.printStackTrace();}}
			if(isr != null){try{isr.close();}catch(Exception e){e.printStackTrace();}}
		}
	}

	private static class CheckOPHelper{
		Document responseDoc;
		HttpURLConnection httpURLConnection;
		public CheckOPHelper(Document responseDoc,HttpURLConnection httpURLConnection) {
			this.responseDoc = responseDoc;
			this.httpURLConnection = httpURLConnection;
		}
	}
	private static CheckOPHelper ampliCheckOP(URL httpURL,AmplistorCredential amplistorCredential,AMPLI_OP_METHOD ampliOpMethod,String authorizationResponse,AMPLI_OP_KIND ampliOpKind,HashMap<String, String> metaData) throws Exception{
	    HttpURLConnection httpURLConnection = null;
	    CheckOPHelper checkOPHelper = null;
	    try{
		    httpURLConnection = ampliOperationConnection(httpURL,ampliOpMethod,authorizationResponse,ampliOpKind,metaData);
		    
		    int responseCode = httpURLConnection.getResponseCode();
//		    System.out.println("Status code: " + responseCode);
		    if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED && amplistorCredential == null){
		    	throw new AuthenticationException("Authentication required but no credentials available");
		    }else if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED && authorizationResponse == null){
//		    	System.out.println("Responding to Amplistor server authentication request...");
		    	String authorizationRequestNew = respondToChallenge(httpURLConnection,amplistorCredential,ampliOpMethod);
		    	httpURLConnection.disconnect();
		    	return ampliCheckOP(httpURL, amplistorCredential, ampliOpMethod, authorizationRequestNew,ampliOpKind,metaData);
		    }else if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED && authorizationResponse != null){
		    	throw new AuthenticationException("User "+amplistorCredential.userName+" authentication failed for URL="+httpURL);
		    }else if(responseCode == HttpURLConnection.HTTP_NOT_FOUND && (ampliOpMethod == AMPLI_OP_METHOD.GET || ampliOpMethod == AMPLI_OP_METHOD.DELETE)){
		    	throw new FileNotFoundException("File not found "+httpURL);
		    }else if(responseCode == HttpURLConnection.HTTP_OK && ampliOpMethod == AMPLI_OP_METHOD.GET && ampliOpKind == AMPLI_OP_KIND.DIR){
		    	checkOPHelper = new CheckOPHelper(getResponseXML(httpURLConnection),httpURLConnection);
		    }else if(responseCode == HttpURLConnection.HTTP_OK && ampliOpMethod == AMPLI_OP_METHOD.GET && ampliOpKind == AMPLI_OP_KIND.FILE){
		    	checkOPHelper = new CheckOPHelper(null,httpURLConnection);
		    }else if((ampliOpMethod==AMPLI_OP_METHOD.PUT && responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) || 
		    		(ampliOpMethod==AMPLI_OP_METHOD.DELETE && responseCode != HttpURLConnection.HTTP_NO_CONTENT) ||
		    		(ampliOpMethod==AMPLI_OP_METHOD.GET && responseCode != HttpURLConnection.HTTP_OK)){
		    	throw new Exception("Unexpected response code="+responseCode+" from amplistor for URL="+httpURL);
		    }
		    return checkOPHelper;
	    }finally{
	    	if(httpURLConnection != null && checkOPHelper == null){httpURLConnection.disconnect();}
	    }

	}
	private static HttpURLConnection ampliOperationConnection(URL httpURL,AMPLI_OP_METHOD ampliOpMethod,String authorizationResponse,AMPLI_OP_KIND ampliOpKind,HashMap<String, String> metaData) throws Exception{
		HttpURLConnection httpURLConnection = null;
		httpURLConnection = (HttpURLConnection) httpURL.openConnection();
	    httpURLConnection.setRequestMethod(ampliOpMethod.toString());
	    httpURLConnection.setRequestProperty("Date", getRFC1123FormattedDate());
	    httpURLConnection.setRequestProperty("Accept","application/xml");
	    if(ampliOpMethod==AMPLI_OP_METHOD.PUT && ampliOpKind == AMPLI_OP_KIND.DIR){
	    	httpURLConnection.setRequestProperty("X-Ampli-Kind","Directory");
	    }
	    if(authorizationResponse != null){
	    	httpURLConnection.setRequestProperty("Authorization", authorizationResponse);
	    }
	    if(metaData != null){
	    	for(String metaKey:metaData.keySet()){
	    		httpURLConnection.setRequestProperty(metaKey,"\""+metaData.get(metaKey)+"\"");
			}
	    }
	    return httpURLConnection;
	}
	private static String respondToChallenge(HttpURLConnection httpURLConnection,AmplistorCredential amplistorCredential,AMPLI_OP_METHOD ampliOpMethod) throws Exception{
    	Hashtable<String, String> challengeHash = getChallengeHash(httpURLConnection);
    	if(challengeHash == null){
    		throw new Exception("responseCode=HTTP_UNAUTHORIZED but no challenge was found for URL="+httpURLConnection.getURL());
    	}
		challengeHash.put(AUTH_DIGEST_URI, httpURLConnection.getURL().getPath()/*+"?"+httpURLConnection.getURL().getQuery()*/);//don't use "?meta=xml" when deleteing
		challengeHash.put(AUTH_USER, amplistorCredential.userName);
		challengeHash.put(AUTH_PASSWORD, amplistorCredential.password);
		
		String authorizationRequest = generateAuthorizationRequest(challengeHash,ampliOpMethod);
		return authorizationRequest;
	}
	private static String calculateMD5(String inputStr){
	    MessageDigest md5 = null;
	    try {
	      md5 = MessageDigest.getInstance("MD5");
	    } catch(NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }

	    // Digest password using the MD5 algorithm
	    md5.update(inputStr.getBytes());
	    String digestedInputStr = digest2HexString(md5.digest());
//	    System.out.println(inputStr + " " + digestedInputStr);
	    return digestedInputStr;
	}
	private static String digest2HexString(byte[] digest)
	{
	   String digestString="";
	   int low, hi ;

	   for(int i=0; i < digest.length; i++)
	   {
	      low =  ( digest[i] & 0x0f ) ;
	      hi  =  ( (digest[i] & 0xf0)>>4 ) ;
	      digestString += Integer.toHexString(hi);
	      digestString += Integer.toHexString(low);
	   }
	   return digestString ;
	}

	private static final String AUTH_DIGEST_URI = "AUTH_URI";
	private static final String AUTH_USER = "AUTH_USER";
	private static final String AUTH_PASSWORD = "AUTH_PASSWORD";
	private static Hashtable<String, String> getChallengeHash(HttpURLConnection httpURLConnection){
		Hashtable<String, String> challengeHash = new Hashtable<String, String>();
		boolean isChallenge = false;
		Map<String, List<String>> map = httpURLConnection.getHeaderFields();
		for(String key:map.keySet()){
//			System.out.println(key);
			for(String val:map.get(key)){
//				System.out.println("  '"+val+"'");
				if(key != null && key.equals("WWW-Authenticate")){
					isChallenge = true;
					StringTokenizer st = new StringTokenizer(val, " ");
					while(st.hasMoreElements()){
						String token = st.nextToken();
//						System.out.println("    '"+token+"'");
						if(!token.equals("Digest")){
							StringTokenizer st2 = new StringTokenizer(token, ",=\"");
							while(st2.hasMoreElements()){
								String challengeKey = st2.nextToken();
								String challengeVal = st2.nextToken();
//								System.out.println("      "+challengeKey+" = "+challengeVal);
								challengeHash.put(challengeKey, challengeVal);
							}
						}else{
							challengeHash.put("challenge", token);
						}
					}
				}
			}
		}
		if(!isChallenge){
			return null;
		}
		return challengeHash;
	}
	private static String generateAuthorizationRequest(Hashtable<String, String> challengeHash,AMPLI_OP_METHOD ampliOpMethod) throws Exception{
		byte[] cNonceBytes = new byte[8];
		if(rand == null){
			rand = SecureRandom.getInstance("SHA1PRNG");//This automatically chooses good random seed
		}
		rand.nextBytes(cNonceBytes); 
		String cNonce=Hex.toString(cNonceBytes);
		
		String uri = challengeHash.get(AUTH_DIGEST_URI);
        String ha1 = calculateMD5(challengeHash.get(AUTH_USER) + ":" + challengeHash.get("realm") + ":" + challengeHash.get(AUTH_PASSWORD));
        String ha2 = calculateMD5(ampliOpMethod.toString() + ":" + uri);
        String response = calculateMD5(ha1 + ":" + 
        		challengeHash.get("nonce") + ":" +
            "00000001" + ":" +
            cNonce + ":" +
            challengeHash.get("qop") + ":" +
            ha2);
        
        
        String authorizationRequest = challengeHash.get("challenge") + " " + 
        "username=\"" + challengeHash.get(AUTH_USER) + "\", " +
        "realm=\"" + challengeHash.get("realm") + "\", " +
        "nonce=\"" + challengeHash.get("nonce") + "\", " +
        "uri=\"" + uri + "\", " +
        "qop=" + challengeHash.get("qop") + ", " +
        "nc=" + "00000001" + ", " +
        "cnonce=\"" + cNonce + "\", " +
        "response=\"" + response + "\", " +
        "opaque=\"" + challengeHash.get("opaque")+"\"";
        
        return authorizationRequest;
	}
	
	public interface AmplistorFileNameMatcher{
		boolean accept(String fileName);
	}
	public static ArrayList<String> listDir(String dirURL,AmplistorFileNameMatcher onlyTheseMatchingFiles,AmplistorCredential amplistorCredential) throws Exception{
		return listOrDelete0(dirURL, /*null,*/ false,onlyTheseMatchingFiles, amplistorCredential);
	}
	public static ArrayList<String> deleteSimFilesNotInHash(String dirURL,HashSet<KeyValue> doNotDeleteTheseSimKeys,boolean bScanOnly,AmplistorCredential amplistorCredential) throws Exception{
		if(doNotDeleteTheseSimKeys == null){
			throw new IllegalArgumentException("Parameter doNotDeleteTheseSimKeys cannot be null (being empty is OK)");
		}
		AmplistorFileNameMatcher filter = new AmplistorFileNameMatcher() {
			@Override
			public boolean accept(String fileName) {
				KeyValue parsedSimIDKey = null;
				StringTokenizer st = new StringTokenizer(fileName, "_");
				if(st.countTokens() > 2 && st.nextToken().equals("SimID")){
					try{
						parsedSimIDKey = new KeyValue(Integer.parseInt(st.nextToken())+"");
					}catch(NumberFormatException e){
						//wasn't an integer, assume filename not SimID_xxx_ format and ignore
					}
				}
				if(parsedSimIDKey != null && !doNotDeleteTheseSimKeys.contains(parsedSimIDKey)){
					return true;
				}
				return false;
			}
		};
		return listOrDelete0(dirURL, /*doNotDeleteTheseSimKeys,*/ !bScanOnly, filter,amplistorCredential);
	}
	public static long deleteAllFiles(String dirURL,AmplistorCredential amplistorCredential) throws Exception{
		return listOrDelete0(dirURL, /*null,*/ true, null,amplistorCredential).size();
	}
	private static ArrayList<String> listOrDelete0(String userURL,/*HashSet<KeyValue> doNotDeleteTheseSimKeys,*/boolean bDelete,AmplistorFileNameMatcher onlyTheseMatchingFiles,AmplistorCredential amplistorCredential) throws Exception{
		ArrayList<String> affectedFileNames = new ArrayList<String>();
//		long resultCount = 0;
		int count = 0;
		String marker = null;
		while(true){
			CheckOPHelper checkOpHelper = ampliCheckOP(new URL(userURL+(marker!=null?"?marker="+marker+"&limit=1000":"")), amplistorCredential, AMPLI_OP_METHOD.GET, null, AMPLI_OP_KIND.DIR,null);
//				Document responseDoc = getXMLDirList(userURL+(marker!=null?"?marker="+marker+"&limit=1000":""));
			if(checkOpHelper != null && checkOpHelper.responseDoc != null){
				int currentCount = 0;
				@SuppressWarnings("unchecked")
				List<Element> dirEntryElements = checkOpHelper.responseDoc.getRootElement().getChildren();
				for(Element dirEntry:dirEntryElements){
					String fileName = dirEntry.getText();
					if(fileName.equals(marker)){
//							System.out.println("-----skipping marker");
						continue;
					}
					
					marker = fileName;
					currentCount++;
					if(onlyTheseMatchingFiles != null){
//						if((currentCount+count)%5000 == 0){
//							System.out.println("checked "+(count+currentCount)+" files so far");
//						}
						if(onlyTheseMatchingFiles.accept(fileName)){
							affectedFileNames.add(fileName);
	//						resultCount++;
							if(bDelete){
								ampliCheckOP(new URL(userURL+"/"+fileName), amplistorCredential, AMPLI_OP_METHOD.DELETE, null, AMPLI_OP_KIND.FILE,null);
							}
						}
					}else{
						affectedFileNames.add(fileName);
//						resultCount++;
						if(bDelete){
							ampliCheckOP(new URL(userURL+"/"+fileName), amplistorCredential, AMPLI_OP_METHOD.DELETE, null, AMPLI_OP_KIND.FILE,null);									
						}
//						else{
//							System.out.println((count+currentCount)+" File='"+userURL+"/"+fileName+"'");
//						}
					}
				}
				if(currentCount == 0){
					break;
				}
				count+= currentCount;
				
//					System.out.println("File total count="+count+(match != null?", match count="+matchCount:""));
			}else{
				break;
			}
		}
//		System.out.println("Total count="+count+(doNotDeleteTheseSimKeys != null?", match count="+resultCount:""));
		return affectedFileNames;

	}
	private final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
	private final static String RFC1123_PATTERN = "EEE, dd MMM yyyyy HH:mm:ss z";
	private final static DateFormat rfc1123Format = new SimpleDateFormat(RFC1123_PATTERN, Locale.US);
	static {rfc1123Format.setTimeZone(GMT_ZONE);
	}
	private static String getRFC1123FormattedDate(){
		return rfc1123Format.format(new Date());
	}

	public static class AmpliCustomHeaderHelper{
		public Date customCreation;
		public Date customModification;
		public AmpliCustomHeaderHelper(Date customCreation,Date customModification) {
			this.customCreation = customCreation;
			this.customModification = customModification;
		}
		
	}
	public static AmpliCustomHeaderHelper printHeaderFields(String urlStr,AmplistorCredential amplistorCredential) throws Exception{
		Map<String, List<String>> headerMap = getHeaderFields(urlStr,amplistorCredential);
		Date customCreationDate = null;
		Date customModifiedDate = null;
		for(String headerFieldName:headerMap.keySet()){
			System.out.println(headerFieldName+":"+headerMap.get(headerFieldName));
			if("Last-Modified".equals(headerFieldName)){
				System.out.println(headerFieldName+":"+((List<String>)headerMap.get(headerFieldName)).get(0));
			}else if("Date".equals(headerFieldName)){
				System.out.println(headerFieldName+":"+((List<String>)headerMap.get(headerFieldName)).get(0));
//			}else if("X-Ampli-Modification-Date".equals(headerFieldName) || "X-Ampli-Creation-Date".equals(headerFieldName) || "X-Ampli-Verification-Date".equals(headerFieldName)){
			}else if("Modification-Date".equals(headerFieldName) || "Creation-Date".equals(headerFieldName) || "Verification-Date".equals(headerFieldName)){
				String dateLong = ((List<String>)headerMap.get(headerFieldName)).get(0)+"000";//add milliseconds
				Date date = new Date(Long.parseLong(dateLong));
				String dateStr = rfc1123Format.format(date);
				System.out.println(headerFieldName+"("+dateLong+")"+":"+dateStr);
//			}else if("X-Ampli-Custom-Meta-creation-date".equals(headerFieldName) || "X-Ampli-Custom-Meta-modification-date".equals(headerFieldName)){
			}else if("Custom-Meta-creation-date".equals(headerFieldName) || "Custom-Meta-modification-date".equals(headerFieldName)){
				String dateLong = ((List<String>)headerMap.get(headerFieldName)).get(0);
				if(dateLong.indexOf('"') != -1){//get rid of quotes
					dateLong = dateLong.substring(1, dateLong.length()-1);
				}
				int dotIndex = dateLong.indexOf('.');
				Date date = null;
				if(dotIndex != -1){
					String beforeDot = dateLong.substring(0,dotIndex);
					String afterDot = dateLong.substring(dotIndex,dateLong.length());
					double secFrac = Double.parseDouble(afterDot);
					afterDot = ""+(int)(secFrac*1000);
					afterDot = (afterDot.length()<2?"0":"")+(afterDot.length()<3?"0":"")+afterDot;
//					if(millisecFrac < .01){
//						System.out.println(millisecFrac+" "+afterDot);
//					}else if(millisecFrac < .1){
//						System.out.println(millisecFrac+" "+afterDot);
//					}else{
//						System.out.println(millisecFrac+" "+afterDot);
//					}
					date = new Date(Long.parseLong(beforeDot+afterDot));
				}else{
					date = new Date(Long.parseLong(dateLong+"000"));//add 000 milisecs
				}
				String dateStr = rfc1123Format.format(date);
				System.out.println(headerFieldName+"("+dateLong+")"+":"+dateStr+"   "+urlStr);
//				if("X-Ampli-Custom-Meta-creation-date".equals(headerFieldName)){
				if("Custom-Meta-creation-date".equals(headerFieldName)){
					customCreationDate = date;
//				}else if("X-Ampli-Custom-Meta-modification-date".equals(headerFieldName)){
				}else if("Custom-Meta-modification-date".equals(headerFieldName)){
					customModifiedDate = date;
				}
			}
		}
		return new AmpliCustomHeaderHelper(customCreationDate, customModifiedDate);
	}

	private static HashMap<String, List<String>> getHeaderFields(String urlStr,AmplistorCredential amplistorCredential) throws Exception{
		HashMap<String, List<String>> results = new HashMap<>();
		CheckOPHelper checkOPHelper = null;
		try{
			checkOPHelper = ampliCheckOP(new URL(urlStr+"?meta=xml"), amplistorCredential, AMPLI_OP_METHOD.GET, null, AMPLI_OP_KIND.FILE,null);
			Document doc = getResponseXML(checkOPHelper.httpURLConnection);
			List<Element> dirEntryElements = doc.getRootElement().getChildren();
			for(Element dirEntry:dirEntryElements){
				ArrayList<String> values = new ArrayList<>();
				values.add(dirEntry.getText());
				results.put(dirEntry.getName(), values);
			}
		}finally{
			if(checkOPHelper != null && checkOPHelper.httpURLConnection != null){
				checkOPHelper.httpURLConnection.disconnect();
			}
		}
		return results;
	}
	private static final String TRY_WITH_NO_AUTHENTICATION = "";
	private static void createWithPutXML(String urlStr,AmplistorCredential amplistorCredential,InputStream in,long contentLength,String authorizationRequest) throws Exception{
		URL url = new URL(urlStr);
		HttpURLConnection urlCon = null;
		try{
			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setChunkedStreamingMode(1048576);
//			if(contentLength > Integer.MAX_VALUE){//chunk xfer to be implemented
//				throw new IllegalArgumentException("Can't xfer files larger than "+Integer.MAX_VALUE);
//			}
			//
			//if authorizationRequest == null, try zero length upload to get authorization challenge from server
			//
//			urlCon.setFixedLengthStreamingMode((authorizationRequest==null?0:contentLength));
//			urlCon.setFixedLengthStreamingMode((int)contentLength);
			urlCon.setDoOutput(true);
			urlCon.setRequestMethod(AMPLI_OP_METHOD.PUT.toString());
			urlCon.setRequestProperty("Date", getRFC1123FormattedDate());
			urlCon.setRequestProperty("Content-Type","binary/octet-stream");
//			urlCon.setRequestProperty("Content-Length",(authorizationRequest==null?"0":contentLength+""));
//			urlCon.setRequestProperty("Content-Length",contentLength+"");
			urlCon.setRequestProperty("Accept","application/xml");
			if(authorizationRequest != null && !authorizationRequest.equals(TRY_WITH_NO_AUTHENTICATION)){
				urlCon.setRequestProperty("Authorization", authorizationRequest);
			}
			
			if(authorizationRequest != null){
				BufferedOutputStream bos = new BufferedOutputStream(urlCon.getOutputStream());
				byte[] tempBuffer = new byte[1048576];
				while(true){
					int numRead = in.read(tempBuffer);
					if(numRead == -1){
						break;
					}
					bos.write(tempBuffer, 0, numRead);
				}
				bos.close();
			}
			
			int responseCode = urlCon.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
				if(authorizationRequest == null){//try again with authorization
					String reqAuth = respondToChallenge(urlCon, amplistorCredential, AMPLI_OP_METHOD.PUT);
					urlCon.disconnect();
					createWithPutXML(urlStr,amplistorCredential, in, contentLength, reqAuth);
				}else{
					throw new Exception("HTTP authorization failed");
				}
			}else if (authorizationRequest == null && (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED)){//try again with no authorization
				createWithPutXML(urlStr,amplistorCredential, in, contentLength, TRY_WITH_NO_AUTHENTICATION);
			}else if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED){//nothing worked
				throw new Exception("Unexpected responseCode="+responseCode);
			}
			
		}finally{
			if(urlCon != null){urlCon.disconnect();}
		}
	}	
}

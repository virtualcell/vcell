package org.vcell.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.MimeUtility;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPInputStream;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPNestedMessage;
import com.sun.mail.imap.IMAPSSLStore;



public class ListservMail {

	private static Pattern pattern = Pattern.compile("\\<\\S+@\\S+\\>");
	private static TreeSet<String> bouncedEMails = new TreeSet<>();
    // mail server connection parameters and bounce search parameters
    private static String mailServer = "itowa.uchc.edu";
	private static String mailUser = "frm";
	private static String mailFolder = "vcell-users-l bounced";
	private static File bounceFile = new File("C:/Users/frm/workspace/VCell_trunk/listserv/bouncedEmails.txt");
	private static String subjectSearchForBounced = "VCELL-USERS-L: error report from";
	
	public static void main(String args[]) throws Exception {
		IMAPSSLStore store = null;
		IMAPFolder[] inboxs = null;
		BufferedWriter bw = null;
		try{
		    System.out.print("Please enter your passwd: ");
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String password = br.readLine();
	
		    // connect to mail server using imaps protocol
		    Properties properties = System.getProperties();
		    Session session = Session.getDefaultInstance(properties);
		    store = (IMAPSSLStore) session.getStore("imaps");// Use this instead of pop3s to access non 'Inbox' folders
		    store.connect(mailServer, mailUser, password);
		    inboxs = new IMAPFolder[]  {(IMAPFolder) store.getFolder(mailFolder)};
		    inboxs[0].open(Folder.READ_ONLY);
	
		    // get the list of messages from folder
		    Message[] messages = inboxs[0].getMessages();
	
		    if (messages.length == 0) System.out.println("No messages found.");
	
		    for (int i = 0; i < messages.length; i++) {
//			      // stop after listing ten messages
//			      if (i > 10) {
//			    	  break;
//			      }
			      System.out.println("Message " + (i + 1));
			      IMAPMessage message = (IMAPMessage)messages[i];
			      System.out.println("From : " + message.getFrom()[0]);
			      System.out.println("Subject : " + message.getSubject());
			      System.out.println("Sent Date : " + message.getSentDate());
			      System.out.println("Type : " + message.getContentType());
			      if(message.getSubject().contains(subjectSearchForBounced)){
			    	  readMessage(message,0,0);
			      }
			      System.out.println();		      
		      }
		    
		      System.out.println("Found "+bouncedEMails.size()+" bounced emails");
		      bw = new BufferedWriter(new FileWriter(bounceFile));
		      for(String s:bouncedEMails){
		    	bw.write(s+"\n");  
		      }
		      
		}finally{
			if(bw != null){try{bw.close();}catch(Exception e){e.printStackTrace();}}
			
			if(inboxs != null){
				for (int i = 0; i < inboxs.length; i++) {
					if(inboxs[i] != null && inboxs[i].isOpen()){try{inboxs[i].close(true);}catch(Exception e){e.printStackTrace();}}				
				}
			}
			
			if(store != null){try{store.close();}catch(Exception e){e.printStackTrace();}}
		}
	  }

	private static void readMessage(IMAPMessage message,int partNum,int size) throws Exception{
  	  Object obj = message.getContent();
  	  if(obj instanceof Multipart){
  		  readMultiPart(((Multipart)obj), partNum, size);
  	  }else{
  		System.out.println("-----not multipart\n"+message);
  	  }
	}
	
	private static void readMultiPart(Multipart mp,int partNum,int size) throws Exception{
	  	  for (int j = 0; j < mp.getCount(); j++) {
	  		  Part part = mp.getBodyPart(j);
//	  		  Enumeration<Header> enumHeaders = part.getAllHeaders();
//	  		  while(enumHeaders.hasMoreElements()){
//	  			  Header h = enumHeaders.nextElement();
//	  			  System.out.println(h.getName()+" "+h.getValue());
//	  		  }
	  		  Object obj = null;
	  		  try{
	  			  obj = part.getContent();
	  		  }catch(UnsupportedEncodingException uce){
	  			  obj = getStringFromStream(part.getInputStream(),part.getSize());
	  		  }
	  		  if((obj instanceof IMAPNestedMessage)){
	  			  readMessage(((IMAPMessage)obj),j,part.getSize());
	  		  }else if(obj instanceof IMAPInputStream){
	  			  String s = getStringFromStream(((IMAPInputStream)obj),part.getSize());
	  			  System.out.println("stream content= "+s.length()+" "+size+" "+s.contains("@")+" disp="+part.getDisposition()+" type="+part.getContentType()+" descr="+part.getDescription());
//	  			  DataInputStream dis = new DataInputStream(((IMAPInputStream)obj));
//	  			  byte[] bytes = new byte[size];
//	  			  dis.readFully(bytes);
//	  			  String s  = new String(bytes);
//	  			  System.out.println("stream content= "+s.length()+" "+size+" "+s.contains("@")+" disp="+part.getDisposition()+" type="+part.getContentType()+" descr="+part.getDescription());
//	  			  dis.close();
	  		  }else if((obj instanceof Multipart)){
	  			readMultiPart(((Multipart)obj),j,part.getSize());
	  		  }else if((obj instanceof String)){
	  			  String s = (String)obj;
	  			  Matcher matcher = pattern.matcher(s);
	  			  while(matcher != null && matcher.find()){
	  				  MatchResult matchResult = matcher.toMatchResult();
	  				  String email = matchResult.group();
	  				  System.out.println(email);
	  				  bouncedEMails.add(email);
	  			  }
	  			System.out.println("string content= "+s.length()+" "+size+" "+s.contains("@")+" disp="+part.getDisposition()+" type="+part.getContentType()+" descr="+part.getDescription());
	  		  }else{
	  			  System.out.println("-----TBI part "+partNum+"\n"+obj.getClass().getName());
	  		  }
	  	  }
	}
	  private static String getStringFromStream(InputStream is,int size) throws Exception{
			  DataInputStream dis = null;
			  try{
				  dis = new DataInputStream(is);
				  byte[] bytes = new byte[size];
				  dis.readFully(bytes);
				  String s  = new String(bytes);
				  return s;
			  }finally{
				  if(dis != null){try{dis.close();}catch(Exception e){e.printStackTrace();}}
			  }
			  
  	  }

	  private static final String mailto = "mailto:";
	  private static final String ptoken = "<p>";
	  private static final String brtoken = "<br>";
	  public static void cleanupMailAddresses(File bouncefile,File sentFile) throws Exception{
		  TreeSet<String> bounceSet = new TreeSet<>();
		  BufferedReader br = new BufferedReader(new FileReader(bouncefile));
		  String line = null;
		  while((line = br.readLine()) != null){
			  if(line.toLowerCase().contains("vcell-users-l") ||
				 line.toLowerCase().contains("listserv.uconn.edu")){
				  System.out.println("--skip: "+line);
				  continue;
			  }
			  if(line.toLowerCase().startsWith(ptoken)){
				  line = line.substring(ptoken.length());
			  }
			  if(line.toLowerCase().endsWith(brtoken)){
				  line = line.substring(0, line.length()-brtoken.length());
			  }
			  StringTokenizer st = new StringTokenizer(line, "<>@");
			  if(st.countTokens() != 2){
				  System.out.println("-----bad: "+line);
				  continue;
			  }
			  String user = st.nextToken();
			  String mailserver = st.nextToken();
			  if(user.toLowerCase().startsWith(mailto)){
				  user = user.substring(mailto.length());
			  }
			  user = user.toLowerCase();
			  mailserver = mailserver.toLowerCase();
			  String address = user+"@"+mailserver;
			  bounceSet.add(address);
		  }
		  br.close();
		  
		  TreeSet<String> sentSet = new TreeSet<>();
		  br = new BufferedReader(new FileReader(sentFile));
		  while((line = br.readLine()) != null){
			  sentSet.add(line.toLowerCase());
		  }
		  
		  System.out.println("Bounceset size = "+bounceSet.size());
		  TreeSet copyBounceSet = (TreeSet)bounceSet.clone();
		  copyBounceSet.removeAll(sentSet);
		  Iterator<String> iter = copyBounceSet.iterator();
		  while(iter.hasNext()){
			  System.out.println("leftover: "+iter.next());
		  }
	  }
}

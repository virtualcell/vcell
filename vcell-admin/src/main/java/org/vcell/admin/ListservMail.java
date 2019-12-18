package org.vcell.admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPInputStream;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPNestedMessage;
import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3SSLStore;

import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.SimulationTable;
import cbit.vcell.modeldb.UserTable;



public class ListservMail {

	private static Pattern pattern = Pattern.compile("\\<\\S+@\\S+\\>");
	private static TreeSet<String> bouncedEMails = new TreeSet<>();
    // mail server connection parameters and bounce search parameters
    private static String mailServer = "itowa.uchc.edu";
	private static String mailUser = "frm";
	private static String mailFolder = "vcell-users-l bounced";
	private static String vcellDbSchema = "vcell";
	private static File rawBounceFile = new File("C:/Users/frm/workspace/VCell_trunk_latest/listserv/rawBouncedEmails.txt");
	private static File cleanBounceFile = new File("C:/Users/frm/workspace/VCell_trunk_latest/listserv/cleanBouncedEmails.txt");
	private static File listservEmailFileMinusBounce = new File("C:/Users/frm/workspace/VCell_trunk_latest/listserv/listservEmailFileMinusBounce.txt");
	private static String subjectSearchForBounced = "VCELL-USERS-L: error report from";
	
	private static enum listServeCmds {parsebounce,createemaillist}
	
	public static void main(String args[]) throws Exception {
		
		if(args.length != 1){
			usage();
		}
		if(args[0].toLowerCase().equals(listServeCmds.parsebounce.name())){
	        getBouncedEmails(getPassword("email"));
	        cleanupBouncedMailAddresses();
		}else if(args[0].toLowerCase().equals(listServeCmds.createemaillist.name())){
			ArrayList<String> allEmails = queryEmails(getPassword("vcellDB"));
			TreeSet<String> allDbEmailsSet = new TreeSet<>(allEmails);
			createListservEmails(allDbEmailsSet);
		}else{
			usage();
		}
	  }

	private static void createListservEmails(TreeSet<String> allDbEmailsSet) throws Exception{
		  TreeSet<String> bounceSet = new TreeSet<>();
		  try (BufferedReader br = new BufferedReader(new FileReader(cleanBounceFile));
				  BufferedWriter bw = new BufferedWriter(new FileWriter(listservEmailFileMinusBounce))){
			  String line = null;
			  while((line = br.readLine()) != null){
				  bounceSet.add(line);
			  }
			  TreeSet<String> filteredEmails = (TreeSet<String>)allDbEmailsSet.clone();
			  filteredEmails.removeAll(bounceSet);
			  for(String s:filteredEmails){
				  bw.write(s+"\n");
			  }
		  }

	}
	private static String getPassword(String descr) throws IOException{
	    System.out.print("Enter "+descr+" password: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String password = br.readLine();
        return password;
	}
	private static void usage(){
		System.out.println("Usage: ListServMail {"+listServeCmds.parsebounce.name()+","+listServeCmds.createemaillist.name()+"}");
		System.exit(1);
	}
	private static void getBouncedEmails(String emailPassword) throws Exception{
//		IMAPSSLStore store = null;
		Store store = null;
		Folder[] inboxs = null;
		BufferedWriter bw = null;
		try{
	
		    // connect to mail server using imaps protocol
		    Properties properties = System.getProperties();
		    Session session = Session.getDefaultInstance(properties);
		    store = session.getStore("imap");// Use this instead of pop3s to access non 'Inbox' folders
//		    store = session.getStore("imaps");// Use this instead of pop3s to access non 'Inbox' folders (alternative?)
//		    store = session.getStore("pop3s");// 
		    store.connect(mailServer, mailUser, emailPassword);
		    inboxs = new Folder[]  {(Folder) store.getFolder(mailFolder)};
//		    inboxs = new Folder[]  {(Folder) store.getDefaultFolder().getFolder("INBOX").getFolder(mailFolder)};
		    ((IMAPFolder)inboxs[0]).open(Folder.READ_ONLY);
	
		    // get the list of messages from folder
		    Message[] messages = inboxs[0].getMessages();
	
		    if (messages.length == 0) System.out.println("No messages found.");
	
		    for (int i = 0; i < messages.length; i++) {
//			      // stop after listing ten messages
//			      if (i > 10) {
//			    	  break;
//			      }
			      System.out.println("Message " + (i + 1));
			      Message message = messages[i];
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
		      bw = new BufferedWriter(new FileWriter(rawBounceFile));
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
	
	public static ArrayList<String> queryEmails(String dbPassword) throws Exception{
		String driverName = "oracle.jdbc.driver.OracleDriver";
		String host = "vcell-db.cam.uchc.edu";
		String db = "vcelldborcl";
		String connectURL = "jdbc:oracle:thin:@" + host + ":1521:" + db;
		
		ArrayList<String> results = new ArrayList<>();
		Connection con = null;
		try{
			String sql =
				"SELECT " +
					"DISTINCT("+UserTable.table.email.getQualifiedColName()+")" +
				" FROM "+
					UserTable.table.getTableName()+","+SimulationJobTable.table.getTableName()+","+SimulationTable.table.getTableName()+" " +
				" WHERE "+
					SimulationJobTable.table.startDate.getQualifiedColName()+" >= (CURRENT_TIMESTAMP - (5*365)) "+
				" AND "+
					SimulationJobTable.table.simRef.getQualifiedColName() + " = " + SimulationTable.table.id.getQualifiedColName() +
				" AND " +
					UserTable.table.id.getQualifiedColName() + " = " + SimulationTable.table.ownerRef.getQualifiedColName() +
//				" AND " +
//					"LOWER("+UserTable.table.notify.getQualifiedColName()+")='on'" +
				" AND " +
					UserTable.table.email.getQualifiedColName()+ " NOT LIKE '%@qq.com' " +//these looked like multiple robot generated dates so exclude
				" AND "+
					UserTable.table.email.getQualifiedColName()+ " LIKE '%@%' " +// at least make sure the email has an @ sign, reject all others
				" ORDER BY " + UserTable.table.email.getUnqualifiedColName();
							
			Class.forName(driverName);
			con = java.sql.DriverManager.getConnection(connectURL, vcellDbSchema, dbPassword);
			con.setAutoCommit(false);
			con.setReadOnly(true);
			Statement stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while(rset.next()){
				results.add(rset.getString(1));
				System.out.println(results.get(results.size()-1));
			}
			rset.close();
			stmt.close();
			return results;
		}finally{
			if(con != null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
//		select distinct(vc_userinfo.email)
//		from
//		vc_userinfo,
//		vc_simulationjob,vc_simulation
//		where
//		vc_simulationjob.startdate >= to_date('01-NOV-2011','DD-MON-YY') and
//		vc_simulationjob.simref=vc_simulation.id and
//		vc_userinfo.id= vc_simulation.ownerref and
//		--lower(vc_userinfo.notify)='on' and
//		vc_userinfo.email not like '%@qq.com' and
//		vc_userinfo.email like'%@%'
//		order by email;
	}
	
	private static void readMessage(Message message,int partNum,int size) throws Exception{
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
	  public static void cleanupBouncedMailAddresses(/*File rawBouncefile,File currentListservEmailList*/) throws Exception{
		  BufferedReader br = null;
		  BufferedWriter bw = null;
		  try{
			  TreeSet<String> bounceSet = new TreeSet<>();
			  br = new BufferedReader(new FileReader(rawBounceFile));
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
		      bw = new BufferedWriter(new FileWriter(cleanBounceFile));
		      for(String s:bounceSet){
		    	bw.write(s+"\n");  
		      }
			  
//			  TreeSet<String> sentSet = new TreeSet<>();
//			  br = new BufferedReader(new FileReader(currentListservEmailList));
//			  while((line = br.readLine()) != null){
//				  sentSet.add(line.toLowerCase());
//			  }
//			  br.close();
//			  
//			  System.out.println("Bounceset size = "+bounceSet.size());
//			  TreeSet copyBounceSet = (TreeSet)bounceSet.clone();
//			  copyBounceSet.removeAll(sentSet);
//			  Iterator<String> iter = copyBounceSet.iterator();
//			  while(iter.hasNext()){
//				  System.out.println("leftover: "+iter.next());
//			  }
//			  
//			  sentSet.removeAll(bounceSet);
//		      bw = new BufferedWriter(new FileWriter(cleanBounceFile));
//		      for(String s:sentSet){
//		    	bw.write(s+"\n");  
//		      }
		  }finally{
			  if(br != null){try{br.close();}catch(Exception e){e.printStackTrace();}}
			  if(bw != null){try{bw.close();}catch(Exception e){e.printStackTrace();}}
		  }
	  }
}

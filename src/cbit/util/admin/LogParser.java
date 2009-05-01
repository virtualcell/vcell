package cbit.util.admin;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
/**
 * Insert the type's description here.
 * Creation date: (8/27/01 12:06:50 PM)
 * @author: Jim Schaff
 */
public class LogParser {
	private File fieldFiles[] = new File[0];
	private String swVersion = null;
	private cbit.sql.ConnectionFactory connFactory = null;
	private EventSummary totalEventSummary = new EventSummary();
	private int numErrors = 0;
	//
	// one hashtable per month/year for Jan-1999 to Dec-2002
	// for each hashtable:
	//     key = userid
	//     entry = EventSummary
	//
	private int startYear = 1999;
	private int stopYear = 2006;
	private java.util.Hashtable yearHashTables[] = new java.util.Hashtable[stopYear-startYear+1];
	private java.util.Hashtable monthHashTables[] = new java.util.Hashtable[12*(stopYear-startYear+1)];
	private java.util.Hashtable totalUserHashtable = new java.util.Hashtable();

/**
 * LogParser constructor comment.
 */
public LogParser(File files[], String argSWVersion, cbit.sql.ConnectionFactory argConnFactory) throws IOException, java.sql.SQLException {
	this.fieldFiles = files;
	this.swVersion = argSWVersion;
	this.connFactory = argConnFactory;
	for (int i = 0; i < monthHashTables.length; i++){
		monthHashTables[i] = new java.util.Hashtable();
	}
	for (int i = 0; i < yearHashTables.length; i++){
		yearHashTables[i] = new java.util.Hashtable();
	}
	parseAll();
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:12:31 PM)
 */
private void parseAll() throws IOException, java.sql.SQLException {
	LogEvent logEvent = new LogEvent(); // reuse this many times.
	Object lock = new Object();
	java.sql.Connection con = connFactory.getConnection(lock);
	try {
		for (int i = 0; i < fieldFiles.length; i++){
			File file = fieldFiles[i];
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				try {
					if (line.length()>65000){
						this.numErrors++;
						continue;
					}
					logEvent.parseLine(line);
					totalEventSummary.add(logEvent);
					if (logEvent.isLogin() || logEvent.isSimulationRun() || logEvent.isSave()){
						//saveUserEvent(logEvent);
						saveUserEventToDatabase(logEvent,file.getAbsolutePath(),swVersion,con);
					}
				}catch (java.text.ParseException e){
					e.printStackTrace(System.out);
				}catch (Exception e){
					this.numErrors++;
				}
			}
		}
	}finally{
		connFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/29/01 1:24:40 PM)
 * @param logEvent cbit.util.admin.LogEvent
 */
private void saveUserEvent(LogEvent logEvent) throws java.text.ParseException {
	//
	// record in the monthly summary for this user
	//
	java.util.Calendar calendar = java.util.Calendar.getInstance();
	calendar.setTime(logEvent.getDate());
	int month = calendar.get(java.util.Calendar.MONTH);
	int year = calendar.get(java.util.Calendar.YEAR);
	if (year>stopYear){
		throw new RuntimeException("event "+logEvent+" extends past stopYear");
	}
	int monthIndex = (year-startYear)*12 + month;
	int yearIndex = year-startYear;
	
	EventSummary userEventMonthSummary = (EventSummary)monthHashTables[monthIndex].get(logEvent.getUserid());
	if (userEventMonthSummary == null){
		userEventMonthSummary = new EventSummary();
		monthHashTables[monthIndex].put(logEvent.getUserid(),userEventMonthSummary);
	}
	userEventMonthSummary.add(logEvent);
	
	EventSummary userEventYearSummary = (EventSummary)yearHashTables[yearIndex].get(logEvent.getUserid());
	if (userEventYearSummary == null){
		userEventYearSummary = new EventSummary();
		yearHashTables[yearIndex].put(logEvent.getUserid(),userEventYearSummary);
	}
	userEventYearSummary.add(logEvent);
	
	//
	// record in the monthly total (for all users in that month)
	//
	EventSummary monthEventSummary = (EventSummary)monthHashTables[monthIndex].get("<<<TOTALS>>>");
	if (monthEventSummary == null){
		monthEventSummary = new EventSummary();
		monthHashTables[monthIndex].put("<<<TOTALS>>>",monthEventSummary);
	}
	monthEventSummary.add(logEvent);
	
	//
	// record in the yearly total (for all users in that year)
	//
	EventSummary yearEventSummary = (EventSummary)yearHashTables[yearIndex].get("<<<TOTALS>>>");
	if (yearEventSummary == null){
		yearEventSummary = new EventSummary();
		yearHashTables[yearIndex].put("<<<TOTALS>>>",yearEventSummary);
	}
	yearEventSummary.add(logEvent);
	
	//
	// record in the accumulated summary for this user
	//
	EventSummary totalUserEventSummary = (EventSummary)totalUserHashtable.get(logEvent.getUserid());
	if (totalUserEventSummary == null){
		totalUserEventSummary = new EventSummary();
		totalUserHashtable.put(logEvent.getUserid(),totalUserEventSummary);
	}
	totalUserEventSummary.add(logEvent);
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/29/01 1:24:40 PM)
 * @param logEvent cbit.util.admin.LogEvent
 */
private void saveUserEventToDatabase(LogEvent logEvent, String filePath, String swVersion, java.sql.Connection con) throws java.sql.SQLException, java.text.ParseException {

	java.sql.Statement s = con.createStatement();

	String sql = "INSERT INTO " + cbit.vcell.modeldb.UserLogTable.table.getTableName() + " " + 
				cbit.vcell.modeldb.UserLogTable.table.getSQLColumnList() +
			" VALUES " + cbit.vcell.modeldb.UserLogTable.table.getSQLValueList(logEvent.getUserid(),logEvent.getDate(),logEvent.getMessage(),filePath,logEvent.getEventType(),swVersion, logEvent.getClientIP());
	
	try {
System.out.println("SQL = "+sql);
		int retcode = s.executeUpdate(sql);
		if (retcode!=1){
			throw new RuntimeException("failed to save logEvent '"+logEvent.getMessage()+"'");
		}
		con.commit();
	}catch (java.sql.SQLException e){
		e.printStackTrace(System.out);
		//con.rollback();
	} finally {
		s.close();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:10:21 PM)
 * @return java.lang.String
 */
public String showSummary() {
	// statistics	
	final String MONTH[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	StringBuffer buffer = new StringBuffer();
	for (int i=0;i<monthHashTables.length;i++){
		int monthTotalUsersThatLoggedOn = 0;
		int monthTotalUsersThatSaved = 0;
		int monthTotalUsersThatSimulated = 0;
		buffer.append("Month of "+MONTH[i%12]+" "+(startYear+(i/12))+"\n");
		java.util.Hashtable userHash = monthHashTables[i];
		java.util.Enumeration enumKeys = userHash.keys();
		String users[] = (String[])org.vcell.util.BeanUtils.getArray(enumKeys,String.class);
		java.util.Arrays.sort(users);
		for (int j=0;j<users.length;j++){
			String userid = users[j];
			EventSummary userEventSummary = (EventSummary)userHash.get(userid);
			if (userid.equals("<<<TOTALS>>>")){
				buffer.append("     TOTALS: "+userEventSummary.toString()+"\n");
			}else{
				if (userEventSummary.getNumLogins()>0){
					monthTotalUsersThatLoggedOn++;
				}
				if (userEventSummary.getNumSaves()>0){
					monthTotalUsersThatSaved++;
				}
				if (userEventSummary.getNumSimulations()>0){
					monthTotalUsersThatSimulated++;
				}
				//userid = userid + ",";
				//while (userid.length()<15){
					//userid = userid + " ";
				//}
				//buffer.append("     user: "+userid+" "+userEventSummary.toString()+"\n");

			}
		}
		buffer.append("<<<<< User Summary >>>>>  "+monthTotalUsersThatLoggedOn+" users logged in, "+monthTotalUsersThatSaved+" users saved, "+monthTotalUsersThatSimulated+" users simulated\n\n");
	}
	

	for (int i=0;i<yearHashTables.length;i++){
		int yearTotalUsersThatLoggedOn = 0;
		int yearTotalUsersThatSaved = 0;
		int yearTotalUsersThatSimulated = 0;
		buffer.append("Summary for Year "+(startYear+i)+"\n");
		java.util.Hashtable userHash = yearHashTables[i];
		java.util.Enumeration enumKeys = userHash.keys();
		String users[] = (String[])org.vcell.util.BeanUtils.getArray(enumKeys,String.class);
		java.util.Arrays.sort(users);
		for (int j=0;j<users.length;j++){
			String userid = users[j];
			EventSummary userEventSummary = (EventSummary)userHash.get(userid);
			userid = userid + ",";
			while (userid.length()<15){
				userid = userid + " ";
			}
			if (userid.equals("<<<TOTALS>>>")){
				buffer.append("     TOTALS: "+userEventSummary.toString()+"\n");
			}else{
				if (userEventSummary.getNumLogins()>0){
					yearTotalUsersThatLoggedOn++;
				}
				if (userEventSummary.getNumSaves()>0){
					yearTotalUsersThatSaved++;
				}
				if (userEventSummary.getNumSimulations()>0){
					yearTotalUsersThatSimulated++;
				}
				//userid = userid + ",";
				//while (userid.length()<15){
					//userid = userid + " ";
				//}
				//buffer.append("     user: "+userid+" "+userEventSummary.toString()+"\n");
			}

		}
		buffer.append("<<<<< User Summary >>>>>  "+yearTotalUsersThatLoggedOn+" users logged in, "+yearTotalUsersThatSaved+" users saved, "+yearTotalUsersThatSimulated+" users simulated\n\n");
	}
	buffer.append("\n\nTOTALS BY USER\n\n");
	java.util.Enumeration enumKeys = totalUserHashtable.keys();
	String users[] = (String[])org.vcell.util.BeanUtils.getArray(enumKeys,String.class);
	java.util.Arrays.sort(users);
	int totalUsersThatLoggedOn = 0;
	int totalUsersThatSaved = 0;
	int totalUsersThatSimulated = 0;
	for (int j=0;j<users.length;j++){
		String userid = users[j];
		EventSummary userEventSummary = (EventSummary)totalUserHashtable.get(userid);
		if (userid.equals("<<<TOTALS>>>")){
			buffer.append("     TOTALS: "+userEventSummary.toString()+"\n");
		}else{
			if (userEventSummary.getNumLogins()>0){
				totalUsersThatLoggedOn++;
			}
			if (userEventSummary.getNumSaves()>0){
				totalUsersThatSaved++;
			}
			if (userEventSummary.getNumSimulations()>0){
				totalUsersThatSimulated++;
			}
			//userid = userid + ",";
			//while (userid.length()<15){
				//userid = userid + " ";
			//}
			//buffer.append("     user: "+userid+" "+userEventSummary.toString()+"\n");
		}

	}
	buffer.append("<<<<< User Summary >>>>>  "+totalUsersThatLoggedOn+" users logged in, "+totalUsersThatSaved+" users saved, "+totalUsersThatSimulated+" users simulated\n\n");
	buffer.append("\n\nTOTAL Events,   "+totalEventSummary.toString());
	return buffer.toString();
}
}
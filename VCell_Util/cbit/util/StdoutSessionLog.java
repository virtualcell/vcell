package cbit.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.rmi.server.*;
import java.rmi.*;
import java.util.*;
import java.io.*;

/**
 * This type was created in VisualAge.
 */
public class StdoutSessionLog implements SessionLog {
	private String userid = null;
	private PrintStream out = null;
/**
 * StdoutSessionLog constructor comment.
 */
public StdoutSessionLog(String userid) {
	this.userid = userid;
	this.out = System.out;
}
/**
 * StdoutSessionLog constructor comment.
 */
public StdoutSessionLog(String userid, PrintStream outStream) {
	this.userid = userid;
	this.out = outStream;
}
/**
 * print method comment.
 */
public synchronized void alert(String message) {
	String host = null;
	try {
		host = "(remote:"+RemoteServer.getClientHost()+")";
	}catch (Exception e){
		host = "(localhost)";
	}
	String time = Calendar.getInstance().getTime().toString();
	out.println("<<<ALERT>>> "+userid+" "+host+" "+time+" "+message);
	return;
}
/**
 * print method comment.
 */
public synchronized void exception(Throwable exception) {
	String host = null;
	try {
		host = "(remote:"+RemoteServer.getClientHost()+")";
	}catch (Exception e){
		host = "(localhost)";
	}
	String time = Calendar.getInstance().getTime().toString();

	//
	// capture stack in String
	//
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	PrintWriter pw = new PrintWriter(bos);
	exception.printStackTrace(pw);
	pw.close();
	String stack = bos.toString();

	//
	// parse stack string, and prefix each line with identifying info
	//
	StringBuffer buffer = new StringBuffer();
	StringTokenizer tokens = new StringTokenizer(stack,"\n");
	while (tokens.hasMoreTokens()){
		String line = tokens.nextToken();
		buffer.append("<<<EXCEPTION>>> "+userid+" "+host+" "+time+" "+line+"\n");
	}

	//
	// print to log (stdout)
	//
	out.println(buffer.toString());
	return;
}
/**
 * print method comment.
 */
public synchronized void print(String message) {
	String host = null;
	try {
		host = "(remote:"+RemoteServer.getClientHost()+")";
	}catch (Exception e){
		host = "(localhost)";
	}
	String time = Calendar.getInstance().getTime().toString();
	out.println(userid+" "+host+" "+time+" "+message);
	return;
}
}

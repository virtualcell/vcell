package cbit.util.xml;
/**
 * First draft for a multipurpose logger. Implementors may also support user interaction. 
 * Creation date: (9/24/2004 12:03:08 PM)
 * @author: Rashad Badrawi
 */
public interface VCLogger {

	//importance of messages from the translator, decides whether to bail out, interrupt/request feedback, or just report.
	public static final int LOW_PRIORITY = 0;
	public static final int MEDIUM_PRIORITY = 1;
	public static final int HIGH_PRIORITY = 2;

	public boolean hasMessages();


	public void sendAllMessages();


	public void sendMessage(int messageLevel, int messageType) throws Exception;


	public void sendMessage(int messageLevel, int messageType, String message) throws Exception;
}
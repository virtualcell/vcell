package cbit.vcell.messaging.admin;
import cbit.vcell.messaging.*;

/**
 * Insert the type's description here.
 * Creation date: (8/8/2003 10:23:15 AM)
 * @author: Fei Gao
 */
public interface ManageConstants {
	public static final long INTERVAL_PING_SERVICE = 10 * MessageConstants.MINUTE; // in minutes
	public static final long INTERVAL_PING_RESPONSE = 10 * MessageConstants.SECOND; // in milliseconds

	public static final String MESSAGE_TYPE_PROPERTY = MessageConstants.MESSAGE_TYPE_PROPERTY;
	public static final String MESSAGE_TYPE_ISSERVICEALIVE_VALUE	= "IsServiceAlive";
	public static final String MESSAGE_TYPE_IAMALIVE_VALUE	= "IAmAlive";		
	public static final String MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE	= "AskPerformance";
	public static final String MESSAGE_TYPE_REFRESHSERVERMANAGER_VALUE	= "RefreshServerManager";
	public static final String MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE	= "ReplyPerformance";
	public static final String MESSAGE_TYPE_STARTSERVICE_VALUE	= "StartService";
	public static final String MESSAGE_TYPE_STOPSERVICE_VALUE	= "StopService";

	public static final String MESSAGE_TYPE_BROADCASTMESSAGE_VALUE	= "BroadcastMessage";
	public static final String BROADCASTMESSAGE_CONTENT_PROPERTY = "BroadcastMessageContent";

	public static final String FILE_NAME_PROPERTY = "FileName";
	public static final String FILE_LENGTH_PROPERTY = "FileLength";
	
	public static final int SERVICE_STARTUPTYPE_AUTOMATIC = 0;	// restart it if the service is dead 
	public static final int SERVICE_STARTUPTYPE_MANUAL = 1;
	
	public static final String[] SERVICE_STARTUP_TYPES = {"automatic", "manual"};

	public static final int SERVICE_STATUS_RUNNING = 0;	// restart it if the service is dead 
	public static final int SERVICE_STATUS_NOTRUNNING = 1;	// restart it if the service is dead
	public static final int SERVICE_STATUS_FAILED = 2; 	

	public static final String[] SERVICE_STATUSES = {"running", "not running", "failed"};
	
	public static final String SERVICE_ID_PROPERTY	= "ServiceID";
	
	public static final String SERVERID_RELEASE = "REL";
	public static final String SERVERID_ALPHA = "ALPHA";
	public static final String SERVERID_BETA = "BETA";
	
	public static final String AllSites[] = {SERVERID_ALPHA, SERVERID_BETA, SERVERID_RELEASE};	
}
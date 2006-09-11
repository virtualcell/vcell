package cbit.vcell.messaging;

/**
 * Insert the type's description here.
 * Creation date: (6/6/2003 11:39:44 AM)
 * @author: Fei Gao
 */
public interface JmsErrorCode {
	public static final int ERR_UNKNOWN = -1;
	
	public static final int ERR_CAN_NOT_INSTANTIATE_CLASS = 0;            
	public static final int ERR_CHANNEL_ACTIVE  = 1;            
	public static final int ERR_CHANNEL_ALREADY_ESTABLISHED = 2;            
	public static final int ERR_CHANNEL_DUPLICATE = 3;            
	public static final int ERR_CHANNEL_DUPLICATE_DETECT_UNSUPPORTED = 4;            
	public static final int ERR_CHANNEL_FATAL_DUP_DETECT_EXCEPTION = 5;            
	public static final int ERR_CHANNEL_ID_ALREADY_EXISTS = 6;            
	public static final int ERR_CHANNEL_IMPLICITLY_CANCELLED = 7;            
	public static final int ERR_CHANNEL_INTERNAL_ERROR = 8;            
	public static final int ERR_CHANNEL_INVALID_DECRYPTION_KEY = 9;	            
	public static final int ERR_CHANNEL_INVALID_KEY_TYPE = 10;	            
	public static final int ERR_CHANNEL_IO_ERROR = 11;	            
	public static final int ERR_CHANNEL_JCE_UNAVAILABLE = 12;	            
	public static final int ERR_CHANNEL_RECOVER_FILE_UNREADABLE = 13;	            
	public static final int ERR_CHANNEL_RETRY_TIMEOUT = 14;	            
	public static final int ERR_CHANNEL_TIMEOUT = 15;	            
	public static final int ERR_CHANNEL_TRANSFER_CLOSED = 16;	            
	public static final int ERR_CHANNEL_UUID_IN_USE = 17;	            
	public static final int ERR_CLASS_ACCESS_ERROR = 18;	            
	public static final int ERR_CLASS_CAST_ERROR = 19;	            
	public static final int ERR_CLASS_NOT_FOUND = 20;	            
	public static final int ERR_CONNECTION_DROPPED = 21;	            
	public static final int ERR_CONNECTION_LIMIT_EXCEEDED = 22;	            
	public static final int ERR_EXCEPTION_IN_INIT = 23;	            
	public static final int ERR_EXTERNAL_AUTNENTICATION_FAILED = 24;	            
	public static final int ERR_FLOW_CONTROL_EXCEPTION = 25;	            
	public static final int ERR_GENERAL_SECURITY_ERR = 26;	            
	public static final int ERR_GUARANTEE_NOT_AUTHORIZED = 27;	            
	public static final int ERR_INTEGRITY_FAILED = 28;	            
	public static final int ERR_JMS_OBJECT_CLOSED = 29;	            
	public static final int ERR_LINKAGE_ERROR = 30;	            
	public static final int ERR_MESSAGELISTENER_RUNTIME_EXCEPTION = 31;
	public static final int ERR_NONREPUDIATION_FAILED = 32;	            
	public static final int ERR_PRIVACY_FAILED = 33; 	            
	public static final int ERR_PUBLISH_NOT_AUTHORIZED = 34;	            
	public static final int ERR_REQUEST_NOSUB_FOR_SUBJECT = 35;	            
	public static final int ERR_SECURITY_ERROR = 36; 	            
	public static final int ERR_STORE = 37; 	            
	public static final int ERR_STORE_INIT = 38;	            
	public static final int ERR_STORE_SIZE_EXCEEDED = 39;	            
	public static final int ERR_SUBSCRIBE_NOT_AUTHORIZED = 40; 	            
	public static final int ERR_THREAD_INTERRUPTED = 41;	            
	public static final int ERR_TOO_LARGE_FOR_QUEUE = 42;	            
	public static final int ERR_TXN_ACCESS_VIOLATION = 43;	            
	public static final int ERR_TXN_IDLE_TIMEOUT = 44;	            
	public static final int ERR_TXN_NOT_FOUND = 45;	            
	public static final int ERR_TXN_SEQUENCE_ERR = 46;	            
	public static final int ERR_WRONG_SUBJECT_ADDR = 47;	            
	public static final int ERROR = 48;	            
	public static final int TXN_DATABASE_EXCEPTION = 49;	            
	public static final int TXN_INDEX_ALREADY_EXISTS = 50;	            
	public static final int TXN_INDICES_NOT_SUPPORTED = 51;	            
	public static final int TXN_INVALID_DATA  = 52;	
}

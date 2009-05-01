package org.vcell.util;
/**
 * Insert the type's description here.
 * Creation date: (10/23/2001 12:16:47 PM)
 * @author: Jim Schaff
 */
public interface MessageConstants {
	public static final int MAX_SERVICE_LISTENERS = 5;	
	
	public static final String METHOD_NAME_PROPERTY	= "MethodName";	
	public static final String USERNAME_PROPERTY	= "UserName";
	public static final String SIZE_MB_PROPERTY		= "SizeMB";
	public static final String SIMKEY_PROPERTY		= "SimKey";
	public static final String JOBINDEX_PROPERTY		= "JobIndex";
	public static final String TASKID_PROPERTY		= "TaskID";
	public static final String FIELDDATAID_PROPERTY		= "FieldDataID";

	// bitmapped counter so that allows 3 retries for each request (but preserves ordinal nature)
	// bits 0-3: retry count
	// bits 4-31: submit
	// max retries must be less than 15.
	public static final int TASKID_USERCOUNTER_MASK		= 0xFFFFFFF0;
	public static final int TASKID_RETRYCOUNTER_MASK	= 0x0000000F;
	public static final int TASKID_USERINCREMENT	    = 0x00000010;
	public static final int TASKID_MAX_RETRIES = 3;

	public static final String JMSCORRELATIONID_PROPERTY	= "JMSCorrelationID";
	
	public static final String MESSAGE_TYPE_SIMULATION_JOB_VALUE = "SimulationJob";
	public static final String MESSAGE_TYPE_SIMSTATUS_VALUE	= "SimStatus";
	public static final String MESSAGE_TYPE_RPC_SERVICE_VALUE = "RPCService";
	public static final String MESSAGE_TYPE_EXPORT_EVENT_VALUE = "ExportEvent";
	public static final String MESSAGE_TYPE_DATA_EVENT_VALUE = "DataEvent";
	
	public static final String MESSAGE_TYPE_PROPERTY	= "MessageType";
	public static final String MESSAGE_TYPE_STOPSIMULATION_VALUE	= "StopSimulation";	

	public static final String COMPUTE_RESOURCE_PROPERTY	= "ComputeResource";	
	
	public static final String HOSTNAME_PROPERTY		= "HostName";
	public static final String SERVICE_TYPE_PROPERTY	= "ServiceType";
	
	public enum ServiceType { 
		DB ("Db"),	
		DATA ("Data"),
		DATAEXPORT ("Exprt"),
		DISPATCH ("Dsptch"),
		PBSCOMPUTE ("PbsC"),	// submit everything to PBS
		LOCALCOMPUTE ("LclC"),   // local pde and ode
		SERVERMANAGER ("ServerManager");
		
		private final String typeName;
		ServiceType(String tn) {
			typeName = tn;
		}
		
		public String getName() {
			return typeName;
		}

		@Override
		public String toString() {
			return typeName;
		}
		
		public static ServiceType fromName(String name) {
			for (ServiceType st : ServiceType.values()) {
				if (st.getName().equals(name)) {
					return st;
				}
			}			
			throw new RuntimeException(name + " is not a legitiamte service type");
		}
	}

	public static final int PRIORITY_LOW = 0;
	public static final int PRIORITY_DEFAULT = 5;
	public static final int PRIORITY_HIGH = 9;

	public static final int QUEUE_ID_WAITING = 0;
	public static final int QUEUE_ID_SIMULATIONJOB = 1;
	public static final int QUEUE_ID_NULL = 2;

	public static final int SECOND = 1000; // in milliseconds
	public static final int MINUTE = 60000; // in milliseconds

	public static final long INTERVAL_PING_SERVER = 5 * MINUTE; // in milliseconds
	public static final long INTERVAL_SERVER_FAIL = 10 * MINUTE; // in milliseconds
	public static final long INTERVAL_DATABASE_SERVER_FAIL = 10 * 60; // in seconds
	public static final long INTERVAL_PROGRESS_MESSAGE = 5 * SECOND;	
}
package cbit.vcell.message;

import cbit.vcell.resource.PropertyLoader;

public class VCellQueue implements VCDestination {
	public final static VCellQueue DataRequestQueue = new VCellQueue(PropertyLoader.jmsDataRequestQueue, "dataReq");
	public final static VCellQueue DbRequestQueue = new VCellQueue(PropertyLoader.jmsDbRequestQueue, "dbReq");
	public final static VCellQueue SimJobQueue = new VCellQueue(PropertyLoader.jmsSimJobQueue, "simJob");
	public final static VCellQueue SimReqQueue = new VCellQueue(PropertyLoader.jmsSimReqQueue, "simReq");
	public final static VCellQueue WorkerEventQueue = new VCellQueue(PropertyLoader.jmsWorkerEventQueue, "workerEvent");

	private final String vcellPropertyName;
	private final String queueName;
	
	private VCellQueue(String propertyName, String defaultQueueName){
		this.vcellPropertyName = propertyName;
		String queueNameProp = PropertyLoader.getProperty(vcellPropertyName,null);
		if (queueNameProp!=null){
			queueName = queueNameProp;
		}else{
			queueName = defaultQueueName;
		}
	}
	
	public VCellQueue(String queueName){
		vcellPropertyName = null;
		this.queueName = queueName;
	}
	
	public String getName() {
		return queueName;
	}
	
	public boolean equals(Object obj){
		if (obj instanceof VCellQueue){
			VCellQueue other = (VCellQueue)obj;
			return other.getName().equals(getName());
		}
		return false;
	}
	
	public int hashCode(){
		return getName().hashCode();
	}

}
package cbit.vcell.message;

import cbit.vcell.resource.PropertyLoader;

public class VCellQueue implements VCDestination {
	public final static VCellQueue DataRequestQueue = new VCellQueue(PropertyLoader.jmsDataRequestQueue,null);
	public final static VCellQueue DbRequestQueue = new VCellQueue(PropertyLoader.jmsDbRequestQueue,null);
	public final static VCellQueue SimJobQueue = new VCellQueue(PropertyLoader.jmsSimJobQueue,null);
	public final static VCellQueue SimReqQueue = new VCellQueue(PropertyLoader.jmsSimReqQueue,null);
	public final static VCellQueue WorkerEventQueue = new VCellQueue(PropertyLoader.jmsWorkerEventQueue,null);

	private String vcellPropertyName;
	private String queueName;
	
	private VCellQueue(String propertyName, String queueName){
		this.vcellPropertyName = propertyName;
		this.queueName = queueName;
	}
	
	public VCellQueue(String queueName){
		this(null,queueName);
	}
	
	public String getName() {
		if (queueName!=null){
			return queueName;
		}
		return PropertyLoader.getRequiredProperty(vcellPropertyName);
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
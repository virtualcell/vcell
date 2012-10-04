package cbit.vcell.message;

import org.vcell.util.MessageConstants.ServiceType;


public class VCRpcConsumer extends VCMessagingConsumer {
	private Object serviceImplementation = null;
	private ServiceType serviceType = null;

	public VCRpcConsumer(Object serviceImplementation, VCellQueue queue, ServiceType serviceType, VCMessageSelector selector, String threadName) {
		super(queue, selector, threadName);
		this.serviceImplementation = serviceImplementation;
		this.serviceType = serviceType;
	}
	public Object getServiceImplementation() {
		return serviceImplementation;
	}
	public VCellQueue getQueue() {
		return (VCellQueue)getVCDestination();
	}
	public ServiceType getServiceType() {
		return serviceType;
	}
}
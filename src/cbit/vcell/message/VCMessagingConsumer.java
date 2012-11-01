package cbit.vcell.message;

public abstract class VCMessagingConsumer {

	private VCDestination vcDestination = null;
	private String threadName = null;
	private VCMessageSelector selector = null;
	
	public VCMessagingConsumer(VCDestination vcDestination, VCMessageSelector selector, String threadName) {
		this.vcDestination = vcDestination;
		this.selector = selector;
		this.threadName = threadName;
	}
	
	public VCDestination getVCDestination(){
		return this.vcDestination;
	}
	
	public String getThreadName(){
		return this.threadName;
	}
	
	public VCMessageSelector getSelector(){
		return this.selector;
	}
}
package cbit.vcell.message;

public abstract class VCMessagingConsumer {

	private VCDestination vcDestination = null;
	private String threadName = null;
	private VCMessageSelector selector = null;
	private int prefetchLimit = 1;
	
	public VCMessagingConsumer(VCDestination vcDestination, VCMessageSelector selector, String threadName, int prefetchLimit) {
		this.vcDestination = vcDestination;
		this.selector = selector;
		this.threadName = threadName;
		this.prefetchLimit = prefetchLimit;
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

	public int getPrefetchLimit() {
		return this.prefetchLimit;
	}
}
package cbit.vcell.message;

public final class VCTopicConsumer extends VCMessagingConsumer {
	
	public interface TopicListener {
		void onTopicMessage(VCMessage vcMessage, VCMessageSession session);
	}

	private TopicListener listener = null;
	
	public VCTopicConsumer(VCellTopic topic, TopicListener listener, VCMessageSelector selector, String threadName, int prefetchLimit){
		super(topic,selector,threadName,prefetchLimit);
		this.listener = listener;
	}
	public VCellTopic getTopic() {
		return (VCellTopic)getVCDestination();
	}
	public TopicListener getTopicListener() {
		return listener;
	}
}
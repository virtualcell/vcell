package cbit.vcell.message.local;

import java.util.ArrayList;

import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService.VCMessagingDelegate;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;

public class LocalVCMessageAdapter extends VCMessageSessionLocal {
		
	public interface LocalVCMessageListener {
		void onLocalVCMessage(VCDestination vcDestination, VCMessage vcMessage);
	}
	
	private ArrayList<MessageRecord> messageRecords = new ArrayList<MessageRecord>();
	private static class MessageRecord {
		final VCDestination vcDestination;
		final VCMessage vcMessage;
		MessageRecord(VCDestination vcDestination, VCMessage vcMessage){
			this.vcDestination = vcDestination;
			this.vcMessage = vcMessage;
		}
	}
	private final LocalVCMessageListener localVCMessageListener;
	
	public LocalVCMessageAdapter(LocalVCMessageListener localVCMessageListener){
		this.localVCMessageListener = localVCMessageListener;
	}
	
	@Override
	public synchronized void sendQueueMessage(VCellQueue vcQueue, VCMessage vcMessage, Boolean persistent, Long timeToLive) throws VCMessagingException {
		System.out.println("\n ##====>>  ##====>>  ##====>>  ##====>>  ##====>>  ##====>> LocalVCellMessageAdapter queuing message for queue "+vcQueue.getName()+", msg="+vcMessage.show()+"\n");
		messageRecords.add(new MessageRecord(vcQueue, vcMessage));
	}

	@Override
	public synchronized void sendTopicMessage(VCellTopic vcTopic, VCMessage vcMessage) throws VCMessagingException {
		System.out.println("\n ##====>>  ##====>>  ##====>>  ##====>>  ##====>>  ##====>> LocalVCellMessageAdapter queuing message for topic "+vcTopic.getName()+", msg="+vcMessage.show()+"\n");
		messageRecords.add(new MessageRecord(vcTopic, vcMessage));
	}

	public synchronized void deliverAll(){
		for (MessageRecord messageRecord : messageRecords){
			System.out.println("\n <<====##  <<====##  <<====##  <<====##  <<====##  <<====## LocalVCellMessageAdapter delivering message for "+messageRecord.vcDestination.getName()+", msg="+messageRecord.vcMessage.show()+"\n");
			localVCMessageListener.onLocalVCMessage(messageRecord.vcDestination, messageRecord.vcMessage);
		}
		messageRecords.clear();
	}

	@Override
	public VCMessagingDelegate getDelegate() {
		return new SimpleMessagingDelegate();
	}
}

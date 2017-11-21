package cbit.vcell.message.jms.test;

import java.util.ArrayList;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.jms.VCMessageJms;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.resource.PropertyLoader;

/**
 * Hello world!
 */
public class TestBlobMessages {

	private static final int NUM_PRODUCERS = 2;
    private static final int NUM_COMSUMERS = 3;
	private static final int NUM_MESSAGES = 100;
	
    public static class Calculator {
    	private int sum = 0;
    	public synchronized void add(int number){
    		sum += number;
    		System.out.println("sum is :"+sum);
    	}
    	public int getSum(){
    		return sum;
    	}
    }

	public static void main(String[] args) throws Exception {
		try {
	    	PropertyLoader.loadProperties();
	    	//System.getProperties().setProperty(PropertyLoader.jmsURL,"tcp://nrcamdev5.cam.uchc.edu:61616");
	    	
	    	VCMessagingService messagingService = new VCMessagingServiceActiveMQ();
	    	messagingService.setDelegate(new SimpleMessagingDelegate());
	    		        
	        // creating one messageProducer session
	        ArrayList<VCMessageSession> sessions = new ArrayList<VCMessageSession>();
	        for (int i=0;i<NUM_PRODUCERS;i++){
	        	sessions.add(messagingService.createProducerSession());
	        }
	        for (int i=0;i<NUM_MESSAGES;i++){
	        	for (int s=0;s<NUM_PRODUCERS;s++){
	        		VCMessageSession session = sessions.get(s);
		        	VCMessage message = session.createObjectMessage(new byte[40000*(i+1)]);
//		        	VCMessage message = session.createObjectMessage(new byte[100000000]);
		        	session.sendQueueMessage(VCellTestQueue.JimQueue, message, false, 100000L);
		        	session.commit();
		        }
	        }
	        
	        // reading message and computing sum
	        // create N comsumers
	        for (int i=0;i<NUM_COMSUMERS;i++){
	        	QueueListener listener = new QueueListener() {
	        		public void onQueueMessage(VCMessage vcMessage,	VCMessageSession session) throws RollbackException {
	        			byte[] byteArray = (byte[])vcMessage.getObjectContent();
	        			String blobInfo = "<no blob>";
	        			if (vcMessage.propertyExists(VCMessageJms.BLOB_MESSAGE_FILE_NAME)){
	        				String objectType = vcMessage.getStringProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_TYPE);
							int objectSize = vcMessage.getIntProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_SIZE);
							String fileName = vcMessage.getStringProperty(VCMessageJms.BLOB_MESSAGE_FILE_NAME);
							String dirName = vcMessage.getStringProperty(VCMessageJms.BLOB_MESSAGE_PRODUCER_TEMPDIR);
							blobInfo = "BLOB: object="+objectType+", size="+objectSize+", dir="+dirName+", file="+fileName;
	        			}
	        			System.out.println("timestampMS="+vcMessage.getTimestampMS()+", "+toString()+",  elapsedTimeS="+((System.currentTimeMillis()-vcMessage.getTimestampMS())/1000.0)+", Received: "+byteArray.length+": "+blobInfo);
	        		}
	        	};
	        	VCQueueConsumer queueConsumer = new VCQueueConsumer(VCellTestQueue.JimQueue, listener, null, "Queue["+VCellTestQueue.JimQueue.getName()+"] ==== Consumer Thread "+i,1);
	        	messagingService.addMessageConsumer(queueConsumer);
	        }
	        
	        Thread.sleep(1000*300);
	        	    	
	    	System.out.println("main program calling closeAll()");
	    	messagingService.close();
	    	System.out.println("main program exiting");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
    }
	
}
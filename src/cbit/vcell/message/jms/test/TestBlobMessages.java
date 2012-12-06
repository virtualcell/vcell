package cbit.vcell.message.jms.test;

import java.util.ArrayList;

import org.vcell.util.PropertyLoader;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCMessagingService.VCMessagingDelegate;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCellQueue;

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
	    	
	    	VCMessagingService messagingService = VCMessagingService.createInstance();
	    	VCMessagingDelegate messagingDelegate = new VCMessagingDelegate() {
				public void onMessagingException(Exception e) {
					e.printStackTrace(System.out);
				}
			};
			messagingService.setDelegate(messagingDelegate);
	    		        
	        // creating one messageProducer session
	        ArrayList<VCMessageSession> sessions = new ArrayList<VCMessageSession>();
	        for (int i=0;i<NUM_PRODUCERS;i++){
	        	sessions.add(messagingService.createProducerSession());
	        }
	        for (int i=0;i<NUM_MESSAGES;i++){
	        	for (int s=0;s<NUM_PRODUCERS;s++){
	        		VCMessageSession session = sessions.get(s);
		        	VCMessage message = session.createObjectMessage(new byte[1000]);
//		        	VCMessage message = session.createObjectMessage(new byte[100000000]);
		        	session.sendQueueMessage(VCellQueue.JimQueue, message);
		        	session.commit();
		        }
	        }
	        
	        // reading message and computing sum
	        // create N comsumers
	        for (int i=0;i<NUM_COMSUMERS;i++){
	        	QueueListener listener = new QueueListener() {
	        		public void onQueueMessage(VCMessage vcMessage,	VCMessageSession session) throws RollbackException {
	        			byte[] byteArray = (byte[])vcMessage.getObjectContent();
	        			System.out.println("timestampMS="+vcMessage.getTimestampMS()+", "+toString()+",  elapsedTimeS="+((System.currentTimeMillis()-vcMessage.getTimestampMS())/1000.0)+", Received: "+byteArray.length+" bytes");
	        		}
	        	};
	        	VCQueueConsumer queueConsumer = new VCQueueConsumer(VCellQueue.JimQueue, listener, null, "Queue["+VCellQueue.JimQueue.getName()+"] ==== Consumer Thread "+i,1);
	        	messagingService.addMessageConsumer(queueConsumer);
	        }
	        
	        Thread.sleep(1000*300);
	        	    	
	    	System.out.println("main program calling closeAll()");
	    	messagingService.closeAll();
	    	System.out.println("main program exiting");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
    }
	
}
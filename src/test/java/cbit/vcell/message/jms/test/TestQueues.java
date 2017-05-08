package cbit.vcell.message.jms.test;

import java.util.ArrayList;

import org.vcell.service.VCellServiceHelper;
import org.vcell.util.PropertyLoader;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceEmbedded;
import cbit.vcell.message.server.ServerMessagingDelegate;

/**
 * Hello world!
 */
public class TestQueues {

	private static final int NUM_PRODUCERS = 3;
    private static final int NUM_COMSUMERS = 1;
	private static final int NUM_MESSAGES = 10;
	
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
	    	
	    	VCMessagingService messagingService = new VCMessagingServiceEmbedded();
	    	messagingService.setDelegate(new SimpleMessagingDelegate());
	    	
	        final Calculator calculator = new Calculator();
	        
	        // creating one messageProducer session
	        ArrayList<VCMessageSession> sessions = new ArrayList<VCMessageSession>();
	        for (int i=0;i<NUM_PRODUCERS;i++){
	        	sessions.add(messagingService.createProducerSession());
	        }
	        int sum = 0;
	        for (int i=0;i<NUM_MESSAGES;i++){
	        	for (int s=0;s<NUM_PRODUCERS;s++){
	        		sum ++;
	        		VCMessageSession session = sessions.get(s);
		        	VCMessage message = session.createTextMessage("message "+i+" from session "+s);
		        	session.sendQueueMessage(VCellTestQueue.JimQueue, message, false, 100000L);
		        	session.commit();
		        }
	        	Thread.sleep(2000);
	        }
	        System.out.println("Correct sum is "+sum);


	        Thread.sleep(30);
	        
	        // reading message and computing sum
	        // create N comsumers
	        for (int i=0;i<NUM_COMSUMERS;i++){
	           	QueueListener listener = new QueueListener() {
					public void onQueueMessage(VCMessage vcMessage,	VCMessageSession session) throws RollbackException {
						//new Thread()
						System.out.println("timestampMS="+vcMessage.getTimestampMS()+", "+toString()+",  elapsedTimeS="+((System.currentTimeMillis()-vcMessage.getTimestampMS())/1000.0)+", Received: "+vcMessage.getTextContent());
//						int number = Integer.parseInt(vcMessage.getTextContent());
						calculator.add(1);
					}
				};
				VCQueueConsumer queueConsumer = new VCQueueConsumer(VCellTestQueue.JimQueue, listener, null, "Queue["+VCellTestQueue.JimQueue.getName()+"] ==== Consumer Thread "+i,1);
	        	messagingService.addMessageConsumer(queueConsumer);
	        }
	        
	        while (calculator.getSum()<sum){
	        	System.out.println("calculator sum = "+calculator.getSum());
	        	Thread.sleep(1000);
	        }
	    	System.out.println("calculator sum = "+calculator.getSum());
	    	
	    	System.out.println("main program calling closeAll()");
	    	messagingService.closeAll();
	    	System.out.println("main program exiting");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
    }
	
}
package cbit.vcell.message.jms.test;

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
public class TestQueues {

    private static final int NUM_COMSUMERS = 1;
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
	    	
	    	VCMessagingService messagingService = VCMessagingService.createInstance();
	    	VCMessagingDelegate messagingDelegate = new VCMessagingDelegate() {
				public void onMessagingException(Exception e) {
					e.printStackTrace(System.out);
				}
			};
			messagingService.setDelegate(messagingDelegate);
	    	
	        final Calculator calculator = new Calculator();
	        
	        // reading message and computing sum
	        // create N comsumers
	        for (int i=0;i<NUM_COMSUMERS;i++){
	           	QueueListener listener = new QueueListener() {
					public void onQueueMessage(VCMessage vcMessage,	VCMessageSession session) throws RollbackException {
						//new Thread()
						System.out.println(toString()+",  t="+System.currentTimeMillis()+", Received: "+vcMessage.getTextContent());
						int number = Integer.parseInt(vcMessage.getTextContent());
						calculator.add(number);
					}
				};
				VCQueueConsumer queueConsumer = new VCQueueConsumer(VCellQueue.JimQueue, listener, null, "Queue["+VCellQueue.JimQueue.getName()+"] ==== Consumer Thread "+i);
	        	messagingService.addMessageConsumer(queueConsumer);
	        }
	        
	        // creating one messageProducer session
	        VCMessageSession messageSession = messagingService.createProducerSession();
	        int sum = 0;
	        for (int i=0;i<NUM_MESSAGES;i++){
	        	sum += i;
	        	VCMessage message = messageSession.createTextMessage(i+"");
	        	messageSession.sendQueueMessage(VCellQueue.JimQueue, message);
	        	messageSession.commit();
	        }        
	        System.out.println("Correct sum is "+sum);

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
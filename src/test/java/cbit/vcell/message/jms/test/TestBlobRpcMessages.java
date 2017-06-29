package cbit.vcell.message.jms.test;

import java.util.ArrayList;

import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingInvocationTargetException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCRpcMessageHandler;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.resource.PropertyLoader;

/**
 * Hello world!
 */
public class TestBlobRpcMessages {

	private static final int NUM_PRODUCERS = 2;
    private static final int NUM_COMSUMERS = 3;
	private static final int NUM_MESSAGES = 5;
	
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

	public static class MyRpcServer {
		public byte[] concat(byte[] a, byte[] b){
			byte[] newArray = new byte[a.length+b.length];
			System.arraycopy(a, 0, newArray, 0, a.length);
			System.arraycopy(b, 0, newArray, a.length, b.length);
			return newArray;
		}
	}

	public static void main(String[] args) throws Exception {
		try {
	    	PropertyLoader.loadProperties();
	    	//System.getProperties().setProperty(PropertyLoader.jmsURL,"tcp://nrcamdev5.cam.uchc.edu:61616");
	    	
	    	VCMessagingService messagingService = new VCMessagingServiceActiveMQ();
	    	messagingService.setDelegate(new SimpleMessagingDelegate());
	    	StdoutSessionLog log = new StdoutSessionLog("log");

	        // reading message and computing sum
	        // create N comsumers
	        MyRpcServer myRpcServer = new MyRpcServer();
	        for (int i=0;i<NUM_COMSUMERS;i++){
	        	VCRpcMessageHandler rpcMessageHandler = new VCRpcMessageHandler(myRpcServer, VCellTestQueue.JimQueue, log);
				VCQueueConsumer rpcConsumer = new VCQueueConsumer(VCellTestQueue.JimQueue, rpcMessageHandler, null, "Queue["+VCellTestQueue.JimQueue.getName()+"] ==== RPC Consumer Thread "+i, 1);
	        	messagingService.addMessageConsumer(rpcConsumer);
	        }
	    		        
	        // creating one messageProducer session
	        ArrayList<VCMessageSession> sessions = new ArrayList<VCMessageSession>();
	        for (int i=0;i<NUM_PRODUCERS;i++){
	        	sessions.add(messagingService.createProducerSession());
	        }
	        for (int i=0;i<NUM_MESSAGES;i++){
	        	for (int s=0;s<NUM_PRODUCERS;s++){
	        		VCMessageSession session = sessions.get(s);
		        	try {
		        		//
		        		// create simple RPC request for service "Testing_Service"
		        		//
			        	User user = new User("schaff",new KeyValue("17"));
			        	byte[] array1 = new byte[20000000];
			        	byte[] array2 = new byte[20000000];
			        	VCRpcRequest rpcRequest = new VCRpcRequest(user, ServiceType.TESTING_SERVICE, "concat", new Object[] { array1, array2 });
			        	
			        	//
			        	// send request and block for response (or timeout).
			        	// RPC invocations don't need commits.
			        	//
			        	Object returnValue = session.sendRpcMessage(VCellTestQueue.JimQueue, rpcRequest, true, 20000, null, null, null);
			        	
			        	//
			        	// print result.
			        	//
			        	if (returnValue instanceof byte[]){
			        		System.out.println("concat(byte["+array1.length+"], byte["+array2.length+"]) ===> byte["+(((byte[])returnValue).length)+"]");
			        	}else{
			        		System.out.println("unexpected return value of "+returnValue);
			        	}
		        	}catch (VCMessagingInvocationTargetException e){
		        		e.printStackTrace(System.out);
		        		System.out.println("the rpc service threw an exception");
		        		e.getTargetException().printStackTrace(System.out);
		        	}
	        	}
	        }        
	        	    	
	    	System.out.println("main program calling closeAll()");
	    	messagingService.closeAll();
	    	System.out.println("main program exiting");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
    }
	
}
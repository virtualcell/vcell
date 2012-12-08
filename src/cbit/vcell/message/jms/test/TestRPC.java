package cbit.vcell.message.jms.test;

import org.vcell.util.PropertyLoader;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingInvocationTargetException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCRpcConsumer;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.server.ServiceSpec.ServiceType;

/**
 * Hello world!
 */
public class TestRPC {

    private static final int NUM_COMSUMERS = 1;
	private static final int NUM_MESSAGES = 100;
	
	public static class MyRpcServer {
		public int add(int a, int b){
			return a+b;
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			
			PropertyLoader.loadProperties();
			
    		VCMessagingService messagingService = VCMessagingService.createInstance();
	    	
	        // reading message and computing sum
	        // create N comsumers
	        MyRpcServer myRpcServer = new MyRpcServer();
	        for (int i=0;i<NUM_COMSUMERS;i++){
				VCRpcConsumer rpcConsumer = new VCRpcConsumer(myRpcServer, VCellQueue.JimQueue, ServiceType.TESTING_SERVICE, null, "Queue["+VCellQueue.JimQueue.getName()+"] ==== RPC Consumer Thread "+i, 1);
	        	messagingService.addMessageConsumer(rpcConsumer);
	        }
	        
	        // creating one messageProducer session
	        VCMessageSession messageSession = messagingService.createProducerSession();

	        for (int i=0;i<NUM_MESSAGES;i++){
	        	try {
	        		//
	        		// create simple RPC request for service "Testing_Service"
	        		//
		        	User user = new User("schaff",new KeyValue("17"));
		        	Integer n1 = new Integer(i);
		        	Integer n2 = new Integer(i+1);
		        	VCRpcRequest rpcRequest = new VCRpcRequest(user, ServiceType.TESTING_SERVICE, "add", new Object[] { n1, n2 });
		        	
		        	//
		        	// send request and block for response (or timeout).
		        	// RPC invocations don't need commits.
		        	//
		        	Object returnValue = messageSession.sendRpcMessage(VCellQueue.JimQueue, rpcRequest, true, 20000, null, null);
		        	
		        	//
		        	// print result.
		        	//
		        	if (returnValue instanceof Integer){
		        		System.out.println("add("+n1+","+n2+") ===> "+returnValue);
		        	}else{
		        		System.out.println("unexpected return value of "+returnValue);
		        	}
	        	}catch (VCMessagingInvocationTargetException e){
	        		e.printStackTrace(System.out);
	        		System.out.println("the rpc service threw an exception");
	        		e.getTargetException().printStackTrace(System.out);
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
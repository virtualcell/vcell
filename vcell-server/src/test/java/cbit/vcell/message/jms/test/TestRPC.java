package cbit.vcell.message.jms.test;

import org.vcell.service.VCellServiceHelper;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingInvocationTargetException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCPooledQueueConsumer;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCRpcMessageHandler;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import cbit.vcell.resource.PropertyLoader;

/**
 * Hello world!
 */
public class TestRPC {

    private static final int NUM_THREADS = 5;
	private static final int NUM_MESSAGES = 20;
	
	public static class MyRpcServer {
		public int add(int a, int b){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return a+b;
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			
			PropertyLoader.loadProperties();
    		VCMessagingService messagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class);
    		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
    		int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
    		messagingService.setConfiguration(new SimpleMessagingDelegate(), jmshost, jmsport);
	    	
	        // reading message and computing sum
	        // create N comsumers
	        MyRpcServer myRpcServer = new MyRpcServer();
			VCRpcMessageHandler rpcMessageHandler = new VCRpcMessageHandler(myRpcServer, VCellTestQueue.JimQueue);
			VCPooledQueueConsumer pooledQueueConsumer = new VCPooledQueueConsumer(rpcMessageHandler, NUM_THREADS, messagingService.createProducerSession());
			pooledQueueConsumer.initThreadPool();
			VCQueueConsumer rpcConsumer = new VCQueueConsumer(VCellTestQueue.JimQueue, pooledQueueConsumer, null, "Queue["+VCellTestQueue.JimQueue.getName()+"] ==== RPC Consumer Master Thread ", 1000);

//			VCRpcMessageHandler rpcMessageHandler = new VCRpcMessageHandler(myRpcServer, VCellQueue.JimQueue, log);
//			VCQueueConsumer rpcConsumer = new VCQueueConsumer(VCellQueue.JimQueue, rpcMessageHandler, null, "Queue["+VCellQueue.JimQueue.getName()+"] ==== RPC Consumer Master Thread ", 1000);

			messagingService.addMessageConsumer(rpcConsumer);
	        

	        for (int i=0;i<NUM_MESSAGES;i++){
		        // creating one messageProducer session
		        final VCMessageSession messageSession = messagingService.createProducerSession();
	        	class MyTask implements Runnable {
	        		int msgNum;
	        		MyTask(int msgNum){
	        			this.msgNum = msgNum;
	        		}
	        		public void run(){
	    	        	try {
	    	        		//
	    	        		// create simple RPC request for service "Testing_Service"
	    	        		//
	    		        	User user = new User("schaff",new KeyValue("17"));
	    		        	Integer n1 = new Integer(msgNum);
	    		        	Integer n2 = new Integer(msgNum+1);
	    		        	VCRpcRequest rpcRequest = new VCRpcRequest(user, RpcServiceType.TESTING_SERVICE, "add", new Object[] { n1, n2 });
	    		        	
	    		        	//
	    		        	// send request and block for response (or timeout).
	    		        	// RPC invocations don't need commits.
	    		        	//
	    		        	Object returnValue = messageSession.sendRpcMessage(VCellTestQueue.JimQueue, rpcRequest, true, 20000, null, null, null);
	    		        	
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
	    	        	} catch (VCMessagingException e) {
							e.printStackTrace();
						}
	        		}
	        	};
	        	new Thread(new MyTask(i)).start();
	        }        

	        Thread.sleep(2000);
	    	System.out.println("main program calling closeAll()");
	    	pooledQueueConsumer.shutdownAndAwaitTermination();
	    	messagingService.close();
	    	System.out.println("main program exiting");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
    }	

}
package cbit.vcell.message.jms.test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.bouncycastle.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.jms.VCMessageJms;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.mongodb.VCMongoDbDriver;
import cbit.vcell.resource.PropertyLoader;

@Ignore
public class TestBlobMessages {

	private static final int NUM_PRODUCERS = 2;
    private static final int NUM_COMSUMERS = 3;
	private static final int NUM_MESSAGES = 5;
	
	VCMongoDbDriver mongoDbDriver = null;
	VCMessagingServiceActiveMQ messagingService = null;
	
	@Before
	public void setUp() throws Exception {
		System.getProperties().put("vcell.mongodb.host","localhost");
		System.getProperties().put("vcell.mongodb.port","27017");
		System.getProperties().put("vcell.mongodb.database","test");	
		mongoDbDriver = VCMongoDbDriver.getInstance();
		System.getProperties().put("vcell.jms.url","failover:(tcp://localhost:61616)");
		System.getProperties().put("vcell.jms.user","clientUser");
		System.getProperties().put("vcell.jms.pswdfile","~/vcellkeys/jmspswd.txt");
		System.getProperties().put("vcell.jms.blobMessageUseMongo","true");
		System.getProperties().put("vcell.jms.blobMessageMinSize","100000");
		messagingService = new VCMessagingServiceActiveMQ();
		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
		int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
		messagingService.setConfiguration(new SimpleMessagingDelegate(), jmshost, jmsport);
	}
	
	@After
	public void tearDown() throws Exception {
		mongoDbDriver.shutdown();
		messagingService.close();
	}
	
    @Test
    public void testBlobMessages() throws VCMessagingException {
	    		        
        // creating one messageProducer session
        ArrayList<VCMessageSession> sessions = new ArrayList<VCMessageSession>();
        for (int i=0;i<NUM_PRODUCERS;i++){
        		sessions.add(messagingService.createProducerSession());
        }
        
        AtomicInteger messagesToRead = new AtomicInteger(0);
        long startTime_MS = System.currentTimeMillis();
        final long TIMEOUT_MS = 1000*2;
        
        for (int i=0;i<NUM_MESSAGES;i++){
	        	for (int s=0;s<NUM_PRODUCERS;s++){
	        		VCMessageSession session = sessions.get(s);
		        	byte[] objectContent = new byte[40000*(i+1)];
		        	Arrays.fill(objectContent, (byte)22);
				VCMessage message = session.createObjectMessage(objectContent);
	//		    VCMessage message = session.createObjectMessage(new byte[100000000]);
		        	session.sendQueueMessage(VCellTestQueue.JimQueue, message, false, 100000L);
		        	session.commit();
		        	int numMessages = messagesToRead.incrementAndGet();
		        	System.out.println("sent message:  messages unread = "+numMessages);
	        }
        }
        
        // reading message and computing sum
        // create N comsumers
        for (int i=0;i<NUM_COMSUMERS;i++){
	        	QueueListener listener = new QueueListener() {
	        		public void onQueueMessage(VCMessage vcMessage,	VCMessageSession session) throws RollbackException {
	        			Object objectContent = vcMessage.getObjectContent();
	        			System.out.println("object content is '"+objectContent+"' of type "+objectContent.getClass().getName());
	        			String blobInfo = "<no blob>";
	        			if (vcMessage.propertyExists(VCMessageJms.BLOB_MESSAGE_MONGODB_OBJECTID)){
	        				String objectType = vcMessage.getStringProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_TYPE);
	        				int objectSize = vcMessage.getIntProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_SIZE);
	        				String objectid = vcMessage.getStringProperty(VCMessageJms.BLOB_MESSAGE_MONGODB_OBJECTID);
	    					String persistenceType = vcMessage.getStringProperty(VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE);
						blobInfo = "BLOB: persistenceType="+persistenceType+", object="+objectType+", size="+objectSize+", objectId="+objectid;
	        			}
	        			System.out.println("<<<< onQueueMessage() >>>> timestampMS="+vcMessage.getTimestampMS()+", "+toString()+",  elapsedTimeS="+((System.currentTimeMillis()-vcMessage.getTimestampMS())/1000.0)+", Received: "+blobInfo);
	        			session.commit();
	        			int numMessages = messagesToRead.decrementAndGet();
			        	System.out.println("read message:  messages unread = "+numMessages);
	        		}
	        	};
	        	VCQueueConsumer queueConsumer = new VCQueueConsumer(VCellTestQueue.JimQueue, listener, null, "Queue["+VCellTestQueue.JimQueue.getName()+"] ==== Consumer Thread "+i,1);
	        	messagingService.addMessageConsumer(queueConsumer);
        }
        
        System.out.println("waiting for messages to be read");
        int numUnreadMessages = messagesToRead.get();
        while (numUnreadMessages>0) {
        		
	        	long elapsedTime = System.currentTimeMillis()-startTime_MS;
	        	System.out.println("elapsed time is "+elapsedTime+" ms, "+numUnreadMessages+" unread messages");
			if (elapsedTime > TIMEOUT_MS) {
	        		throw new RuntimeException("timeout before processing all messages, "+numUnreadMessages+" unread");
	        	}
	        try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
	        numUnreadMessages = messagesToRead.get();
        }
        System.out.println("done");
    }
	
}
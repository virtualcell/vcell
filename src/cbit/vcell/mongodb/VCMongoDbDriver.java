package cbit.vcell.mongodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vcell.util.NullSessionLog;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class VCMongoDbDriver implements Runnable {
	
	public static boolean bQuiet = true;
	
	private static VCMongoDbDriver mongoDriverSingleton = null;

	private Mongo m = null;
	private SessionLog log = new StdoutSessionLog("mongoDbDriver");
	private NullSessionLog nullSessionLog = new NullSessionLog();
	private ConcurrentLinkedQueue<VCMongoMessage> messageOutbox = new ConcurrentLinkedQueue<VCMongoMessage>();
	private boolean processing = false;
	private Thread messageProcessingThread = null;

	public static VCMongoDbDriver getInstance(){
		if (mongoDriverSingleton == null){
			mongoDriverSingleton = new VCMongoDbDriver();
		}
		return mongoDriverSingleton;
	}
	
	private VCMongoDbDriver() {
		super();
	}

	public void setSessionLog(SessionLog log){
		this.log = log;
	}
	
	public SessionLog getSessionLog(){
		if(bQuiet){
			return nullSessionLog;
		}
		return log;
	}
	
	private synchronized void sendMessages() {
    	VCMongoMessage[] queuedMessages = messageOutbox.toArray(new VCMongoMessage[0]);
   		
   		if (queuedMessages!=null && queuedMessages.length>0){
   			try {
	   			// remove the messages whether the save is successful or not.
	   			messageOutbox.removeAll(Arrays.asList(queuedMessages));
	 
	   			// create DBObjects for each message (to send to MongoDB)
	   			ArrayList<DBObject> dbObjectsToSend = new ArrayList<DBObject>();
	    		for (VCMongoMessage message : queuedMessages){
	    			dbObjectsToSend.add(message.getDbObject());
	    		}
	
	    		// send to MongoDB
	        	if (m==null){
	        		String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHost);
	        		int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPort)); // default 27017
	        		m = new Mongo(mongoDbHost,mongoDbPort);
	        	}
	        	
	        	String mongoDbDatabaseName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
	        	DB db = m.getDB(mongoDbDatabaseName);
	        	String mongoDbLoggingCollectionName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbLoggingCollection);
	        	DBCollection dbCollection = db.getCollection(mongoDbLoggingCollectionName);
	        	WriteConcern writeConcern = WriteConcern.SAFE;
	        	WriteResult writeResult = dbCollection.insert(dbObjectsToSend, writeConcern);
	        	
	        	//
	        	// error handling?? ... if couldn't save, then log it locally
	        	//
	        	String errorString = writeResult.getError();////???????
	        	if (errorString !=null && errorString.length()>0){
	        		for (VCMongoMessage message : queuedMessages){
	        			getSessionLog().alert("VCMongoMessage failedToSend : "+message);
	        		}
	        	}else{
		    		for (VCMongoMessage message : queuedMessages){
		    			getSessionLog().alert("VCMongoMessage sent : "+message);
		    		}
	        	}
//   			} catch (MongoException e){
//   				e.printStackTrace(System.out);
//   			} catch (UnknownHostException e) {
//   				e.printStackTrace(System.out);
   			} catch (Exception e) {
   				e.printStackTrace(System.out);
   				try {
   					if (m!=null){
   						m.close();
   					}
   				}catch (Exception e2){
   					e2.printStackTrace(System.out);
   				} finally {
   					m = null;
   				}
   				final int minutesToWaitUponFailure = 20;
   				getSessionLog().alert("MongoDB failure ... waiting "+minutesToWaitUponFailure+" minutes before trying to connect again");
   				for (VCMongoMessage msg : queuedMessages){
   					try {
   						getSessionLog().alert("MongoDB failure: discarding message: "+msg);
   					}catch (Exception e4){
   						e4.printStackTrace(System.out);
   					}
   				}
   				try {
   					Thread.sleep(1000*60*minutesToWaitUponFailure); // wait 20 minutes to try again.
   				}catch (Exception e3){
   				}
			}
    	}
     }
    
    public void run()
    {
    	if (Thread.currentThread() == messageProcessingThread) {
			getSessionLog().print("Starting MongoDB Thread");
	        while(processing && VCMongoMessage.enabled) {
	            try {
	                sendMessages();
	                try {
	                	int sleepTimeMS = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.mongodbThreadSleepMS,"2000"));
						Thread.sleep(sleepTimeMS);
					} catch (InterruptedException e) {
					}
	            } catch (Exception e) {
					e.printStackTrace(System.out);
				}
	        }
	        getSessionLog().print("Ended MongoDB Thread");
    	}
    	else {
    		throw new RuntimeException("invalid thread");
    	}
    }


    /**
     * begin mongo processing thread
     */
    public void startProcessing()
    {
        if(!processing )
        {
            processing = true;
            messageProcessingThread = new Thread(this,"MongoDB Process Thread");
            messageProcessingThread.setDaemon(true);
            messageProcessingThread.start();
        }
    }
    
    /**
     * stop processing thread
     */
    public void stopProcessing()
    {
        processing = false;
        if (messageProcessingThread != null) {
        	messageProcessingThread.interrupt();
        }
        
    }

    public boolean IsProcessing()
    {
        return processing;
    }
    
    public void addMessage(VCMongoMessage message)
    {
//    	getSessionLog().print("VCMongoMessage queued : "+message);
    	messageOutbox.add(message);
    	if (!IsProcessing()){
    		startProcessing();
    	}
    }
    
	public void flush() {
		if (!processing) {
			return;
		}
		if (this.messageProcessingThread != null) {
			this.messageProcessingThread.interrupt();
		}
		Thread flushThread = new Thread(new Runnable() {
			@Override
			public void run() {
				sendMessages();
			}
		});
		flushThread.setDaemon(true);
		flushThread.start();
		//wait small amount of time because some callers of this method exit immediately
		//and will kill the sendMessages thread.
		try{
			Thread.sleep(10*1000);
		}catch(InterruptedException e){
			e.printStackTrace();
			//ignore
		}
	}
	
	/**
	 * stops daemon processing thread and flushes queue on calling thread
	 */
	public void shutdown() {
		if (!processing) {
			return;
		}
		stopProcessing();
		sendMessages( );
	}
     

    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PropertyLoader.loadProperties();
			VCMongoDbDriver mongoDbDriver = VCMongoDbDriver.getInstance();
			BasicDBObject doc = new BasicDBObject();
			doc.put(VCMongoMessage.MongoMessage_msgtype,  VCMongoMessage.MongoMessage_type_testing);
			doc.put(VCMongoMessage.MongoMessage_msgTime, System.currentTimeMillis());
			mongoDbDriver.addMessage(new VCMongoMessage(doc));

			mongoDbDriver.startProcessing();
			Thread.sleep(1000*2);
			mongoDbDriver.stopProcessing();
			
			//
			// "independently" query the contents of the log collection
			//
    		String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHost);
    		int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPort)); // default 27017
    		Mongo m = new Mongo(mongoDbHost,mongoDbPort);
        	String mongoDbDatabaseName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
        	DB db = m.getDB(mongoDbDatabaseName);
        	String mongoDbLoggingCollectionName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbLoggingCollection);
        	DBCollection dbCollection = db.getCollection(mongoDbLoggingCollectionName);

            BasicDBObject query = new BasicDBObject();

            query.put(VCMongoMessage.MongoMessage_msgtype, VCMongoMessage.MongoMessage_type_testing);

            DBCursor cur = dbCollection.find(query);
            while(cur.hasNext()) {
                System.out.println(cur.next());
            }
            
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}


}

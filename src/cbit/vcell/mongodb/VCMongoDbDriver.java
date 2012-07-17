package cbit.vcell.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class VCMongoDbDriver implements Runnable {
	
	private static VCMongoDbDriver mongoDriverSingleton = null;

	private Mongo m = null;
	private SessionLog log = new StdoutSessionLog("mongoDbDriver");
	private ConcurrentLinkedQueue<VCMongoMessage> messageOutbox = new ConcurrentLinkedQueue<VCMongoMessage>();
	private boolean processing = false;

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
		return log;
	}
	
	private void sendMessages() {
    	VCMongoMessage[] queuedMessages = messageOutbox.toArray(new VCMongoMessage[0]);
   		
   		if (queuedMessages!=null && queuedMessages.length>0){
   			try {
	   			// remove the messages whether the save is sucessfull or not.
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
	        			log.alert("VCMongoMessage failedToSend : "+message);
	        		}
	        	}else{
		    		for (VCMongoMessage message : queuedMessages){
		    			log.alert("VCMongoMessage sent : "+message);
		    		}
	        	}
//   			} catch (MongoException e){
//   				e.printStackTrace(System.out);
//   			} catch (UnknownHostException e) {
//   				e.printStackTrace(System.out);
   			} catch (Exception e) {
   				e.printStackTrace(System.out);
   				VCMongoMessage.enabled = false;
			}
    	}
     }
    
    public void run()
    {
		log.print("Starting MongoDB Thread");
        while(processing) {
            try {
                sendMessages();
                try {
                	int sleepTimeMS = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbThreadSleepMS));
					Thread.sleep(sleepTimeMS);
				} catch (InterruptedException e) {
				}
            } catch (Exception e) {
				e.printStackTrace(System.out);
			}
        }
        log.print("Ended MongoDB Thread");
    }


    public void startProcessing()
    {
        if(!processing )
        {
            processing = true;
            Thread messageProcessingThread = new Thread(this,"MongoDB Process Thread");
            messageProcessingThread.setDaemon(true);
            messageProcessingThread.start();
        }
    }
    
    public void stopProcessing()
    {
        processing = false;
    }

    public boolean IsProcessing()
    {
        return processing;
    }
    
    public synchronized void addMessage(VCMongoMessage message)
    {
    	messageOutbox.add(message);
    	log.print("VCMongoMessage queued : "+message);
    	if (!IsProcessing()){
    		startProcessing();
    	}
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

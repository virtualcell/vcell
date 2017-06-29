package cbit.vcell.mongodb;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.SessionLog;
import org.vcell.util.logging.Log4jSessionLog;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.VCellExecutorService;

public class VCMongoDbDriver {
	private static VCMongoDbDriver mongoDriverSingleton = null;
	private static final Logger lg  = Logger.getLogger(VCMongoDbDriver.class);
	private final String mongoDbDatabaseName;
	private final String mongoDbLoggingCollectionName;

	private Mongo m = null;
	private ConcurrentLinkedQueue<VCMongoMessage> messageOutbox = new ConcurrentLinkedQueue<VCMongoMessage>();
	private boolean processing = false;

	private SessionLog log = null;
	private ScheduledFuture<?> command = null;

	public static VCMongoDbDriver getInstance(){
		if (mongoDriverSingleton == null){
			mongoDriverSingleton = new VCMongoDbDriver();
		}
		return mongoDriverSingleton;
	}
	
	private VCMongoDbDriver() {
    	mongoDbDatabaseName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
    	mongoDbLoggingCollectionName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbLoggingCollection);
	}

	public void setSessionLog(SessionLog log){
		this.log = log;
	}
	
	public SessionLog getSessionLog(){
		if(log == null) {
			log = new Log4jSessionLog(lg);
		}
		return log;
	}
	
	private synchronized void sendMessages() {
   		if (messageOutbox.size() > 0) {
   			try {
	   			// create DBObjects for each message (to send to MongoDB)
	   			final int limit = messageOutbox.size( ) + 16; //padded in case more messages arrive while processing queue
	   			ArrayList<DBObject> dbObjectsToSend = new ArrayList<DBObject>(limit);
	   			for ( VCMongoMessage message = messageOutbox.poll(); message != null && dbObjectsToSend.size() < limit; message = messageOutbox.poll()) {
	    			dbObjectsToSend.add(message.getDbObject());
	    		}
	
	    		// send to MongoDB
	        	if (m==null){
	        		String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHost);
	        		int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPort)); // default 27017
	        		m = new Mongo(mongoDbHost,mongoDbPort);
	        	}
	        	
	        	DB db = m.getDB(mongoDbDatabaseName);
	        	DBCollection dbCollection = db.getCollection(mongoDbLoggingCollectionName);
	        	WriteConcern writeConcern = WriteConcern.SAFE;
	        	WriteResult writeResult = dbCollection.insert(dbObjectsToSend, writeConcern);
	        	
	        	//
	        	// error handling?? ... if couldn't save, then log it locally
	        	//
	        	String errorString = writeResult.getError();////???????
	        	if (StringUtils.isNotEmpty(errorString) && lg.isEnabledFor(Level.WARN)) {
        			lg.warn("VCMongoMessage failedToSend : "+ errorString);
	        	}else if (lg.isDebugEnabled()){
	        		lg.debug("VCMongoMessage sent : "+ dbObjectsToSend.size() + " messages");
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
			}
    	}
     }
    
	public void run()
	{
		if(processing && VCMongoMessage.enabled) {
			try {
				sendMessages();
			} catch (Exception e) {
				lg.warn(e);
			}
		}
	}


    /**
     * begin mongo processing thread
     */
    public void startProcessing()
    {
        if(!processing )
        {
        	ScheduledExecutorService es = VCellExecutorService.get( );
            processing = true;
            command = es.scheduleAtFixedRate(( ) -> run( ),2,2,TimeUnit.SECONDS);
        }
    }
    
    /**
     * stop processing thread
     */
    public void stopProcessing()
    {
        processing = false;
        if (command != null) {
        	command.cancel(true);
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
		if (command != null) {
			command.cancel(true);
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

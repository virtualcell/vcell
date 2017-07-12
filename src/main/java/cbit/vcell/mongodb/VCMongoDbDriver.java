package cbit.vcell.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.vcell.util.SessionLog;
import org.vcell.util.logging.Log4jSessionLog;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.VCellExecutorService;

public class VCMongoDbDriver {
	private static VCMongoDbDriver mongoDriverSingleton = null;
	private static final Logger lg  = Logger.getLogger(VCMongoDbDriver.class);
	private final String mongoDbDatabaseName;
	private final String mongoDbLoggingCollectionName;

	private MongoClient m = null;
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
	   			List<Document> dbObjectsToSend = new ArrayList<Document>(limit);
	   			for ( VCMongoMessage message = messageOutbox.poll(); message != null && dbObjectsToSend.size() < limit; message = messageOutbox.poll()) {
	    			dbObjectsToSend.add(message.getDbObject());
	    		}
	
	    		// send to MongoDB
	        	if (m==null){
	        		String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHost);
	        		int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPort)); // default 27017
	        		m = new MongoClient(mongoDbHost,mongoDbPort);
	        	}
	        	
	        	MongoDatabase db = m.getDatabase(mongoDbDatabaseName);
	        	MongoCollection<Document> dbCollection = db.getCollection(mongoDbLoggingCollectionName);
	        	//WriteConcern writeConcern = WriteConcern.ACKNOWLEDGED;
	        	InsertManyOptions options = new InsertManyOptions().ordered(true);
	        	try {
		        	dbCollection.insertMany(dbObjectsToSend, options);
		        	if (lg.isDebugEnabled()) {
		        		lg.debug("VCMongoMessage sent : "+ dbObjectsToSend.size() + " messages");
		        	}
	        	} catch (MongoException e){
		        	lg.debug("failed to write to mongodb", e);
		        	if (lg.isEnabledFor(Level.WARN)) {
	        			lg.warn("VCMongoMessage failedToSend : "+ e.getMessage());
		        	}
		        }
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
		MongoClient m = null;
		try {
			PropertyLoader.loadProperties();
			VCMongoDbDriver mongoDbDriver = VCMongoDbDriver.getInstance();
			Document doc = new Document();
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
    		m = new MongoClient(mongoDbHost,mongoDbPort);
        	String mongoDbDatabaseName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
        	MongoDatabase db = m.getDatabase(mongoDbDatabaseName);
        	String mongoDbLoggingCollectionName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbLoggingCollection);
        	MongoCollection<Document> dbCollection = db.getCollection(mongoDbLoggingCollectionName);

            Bson filter = Filters.eq(VCMongoMessage.MongoMessage_msgtype, VCMongoMessage.MongoMessage_type_testing);

            FindIterable<Document> cur = dbCollection.find(filter);
            for (Document document : cur) {
            	System.out.println(document);
			}
            
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (m!=null){
				m.close();
			}
		}
	}


}

package cbit.vcell.mongodb;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.VCellExecutorService;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.connection.TransportSettings;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class VCMongoDbDriver {
	private static VCMongoDbDriver mongoDriverSingleton = null;
	private static final Logger lg  = LogManager.getLogger(VCMongoDbDriver.class);
	private final String mongoDbDatabaseName;
	private final String mongoDbLoggingCollectionName;

	private MongoClient m = null;
	private ConcurrentLinkedQueue<VCMongoMessage> messageOutbox = new ConcurrentLinkedQueue<VCMongoMessage>();
	private boolean processing = false;

	private ScheduledFuture<?> command = null;

	public static VCMongoDbDriver getInstance(){
		if (mongoDriverSingleton == null){
			mongoDriverSingleton = new VCMongoDbDriver();
		}
		return mongoDriverSingleton;
	}
	
	private VCMongoDbDriver() {
    	mongoDbDatabaseName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
    	mongoDbLoggingCollectionName = PropertyLoader.getProperty(PropertyLoader.mongodbLoggingCollection, "logging");
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
	        		String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostInternal);
	        		int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortInternal)); // default 27017
					String mongoUri = "mongodb://"+mongoDbHost+":"+mongoDbPort;
					m = MongoClients.create(mongoUri);
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
		        	lg.debug("failed to write to mongodb: "+e.getMessage(), e);
		        	if (lg.isWarnEnabled()) {
	        			lg.warn("VCMongoMessage failedToSend : "+ e.getMessage());
		        	}
		        }
   			} catch (Exception e) {
   				lg.error(e.getMessage(), e);
   				try {
   					if (m!=null){
   						m.close();
   					}
   				}catch (Exception e2){
   					lg.error(e.getMessage(), e);
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

	@Deprecated
    public void addMessage(Level level, VCMongoMessage mongoMessage, String message)
    {
    	if (lg.isEnabled(level)){
			// Use Log4J ThreadContext (and JsonTemplateLayout in config) for pure JSON logs using
			// ECS (Elastic Common Schema), so no parsing/transformation needed by elastic stack or other
			// observability frameworks.
			//
			// The structured logging introduced by VCMongoMessage will be refactored (below is a trivial example)
			// The next step may be to return a ClosableThreadContext
			// The final goal is to integrate it with OpenTelemetry Trace/Spans and associated context variables.
			//
			//
			// 1. logging metadata: timestamp, log level, thread name, logger, line number
			// 2. application context (e.g. simulation ID, user):
			//    a. <to-be-replaced> JSON formatted structured logging from VCMongoMessage (from here)
			//    b. <future> inject same context using Log4J ThreadContext to decorate all logs in same transaction.
			// 3. transaction/tracing context:
			//    a. <future> injected trace info (e.g. OpenTelemetry trace/span ids) to correlate logs with transactions.
			//
			LinkedHashMap<String, String> contextMap = new LinkedHashMap<>();
			Document dbObject = mongoMessage.getDbObject();
			for (String field : dbObject.keySet()){
				Object value = dbObject.get(field);
				if (value != null) {
					contextMap.put(field, value.toString());
				}
			}
			try (final CloseableThreadContext.Instance ctc = CloseableThreadContext.putAll(contextMap)) {
				lg.log(level, message);
			}
		}

		// no longer write logs to MongoDB
//    	getSessionLog().print("VCMongoMessage queued : "+message);
//    	messageOutbox.add(message);
//    	if (!IsProcessing()){
//    		startProcessing();
//    	}
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
			lg.error(e);
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
     
	public ObjectId storeBLOB(String blobName, String blobType, byte[] blob) {
		try {
			// send to MongoDB
			if (m == null) {
				String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostInternal);
				int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortInternal)); // default 27017
				String mongoUri = "mongodb://"+mongoDbHost+":"+mongoDbPort;
				m = MongoClients.create(mongoUri);
			}

			MongoDatabase db = m.getDatabase(mongoDbDatabaseName);
			GridFSBucket gridFSBucket = GridFSBuckets.create(db);

			Document metadata = new Document();
			metadata.put("type", blobType);
			long msgTime = System.currentTimeMillis();
			metadata.put("inserttime",msgTime);
			metadata.put("inserttime_nice", new Date(msgTime).toString());
		
			GridFSUploadOptions options = new GridFSUploadOptions()
			                                    .chunkSizeBytes(1024)
			                                    .metadata(metadata);
			ByteArrayInputStream in = new ByteArrayInputStream(blob);
			ObjectId fileId = gridFSBucket.uploadFromStream(blobName, in, options);
			return fileId;
		} catch (Exception e) {
			lg.error(e.getMessage(), e);
			try {
				if (m != null) {
					m.close();
				}
			} catch (Exception e2) {
				lg.error(e.getMessage(), e);
			} finally {
				m = null;
			}
			throw new RuntimeException("failed to store BLOB named "+blobName+": "+e.getMessage(),e);
		}
	}
	
	public byte[] getBLOB(ObjectId objectId) {
		try {
			if (m == null) {
				String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostInternal);
				int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortInternal)); // default 27017
				String mongoUri = "mongodb://"+mongoDbHost+":"+mongoDbPort;
				m = MongoClients.create(mongoUri);
			}
			ByteArrayOutputStream streamToDownloadTo = new ByteArrayOutputStream();
			MongoDatabase db = m.getDatabase(mongoDbDatabaseName);
			GridFSBucket gridFSBucket = GridFSBuckets.create(db);
			gridFSBucket.downloadToStream(objectId, streamToDownloadTo);
			byte[] blob = streamToDownloadTo.toByteArray();
			return blob;
		} catch (Exception e) {
			lg.error(e.getMessage(), e);
			try {
				if (m != null) {
					m.close();
				}
			} catch (Exception e2) {
				lg.error(e.getMessage(), e);
			} finally {
				m = null;
			}
			throw new RuntimeException("failed to retrieve BLOB with ObjectId "+objectId.toHexString()+": "+e.getMessage(),e);
		}
	}
    
	public void removeBLOB(ObjectId objectId) {
		try {
			if (m == null) {
				String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostInternal);
				int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortInternal)); // default 27017
				String mongoUri = "mongodb://"+mongoDbHost+":"+mongoDbPort;
				m = MongoClients.create(mongoUri);
			}
			MongoDatabase db = m.getDatabase(mongoDbDatabaseName);
			GridFSBucket gridFSBucket = GridFSBuckets.create(db);
			gridFSBucket.delete(objectId);
		} catch (Exception e) {
			lg.error(e.getMessage(), e);
			try {
				if (m != null) {
					m.close();
				}
			} catch (Exception e2) {
				lg.error(e.getMessage(), e);
			} finally {
				m = null;
			}
			throw new RuntimeException("failed to retrieve BLOB with ObjectId "+objectId.toHexString()+": "+e.getMessage(),e);
		}
	}
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MongoClient m = null;
		try {
			VCMongoDbDriver mongoDbDriver = VCMongoDbDriver.getInstance();
			Document doc = new Document();
			doc.put(VCMongoMessage.MongoMessage_msgtype,  VCMongoMessage.MongoMessage_type_testing);
			doc.put(VCMongoMessage.MongoMessage_msgTime, System.currentTimeMillis());
			mongoDbDriver.addMessage(Level.INFO, new VCMongoMessage(doc), "test message");

			mongoDbDriver.startProcessing();
			Thread.sleep(1000*2);
			mongoDbDriver.stopProcessing();
			
			//
			// "independently" query the contents of the log collection
			//
    		String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostInternal);
    		int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortInternal)); // default 27017
			String mongoUri = "mongodb://"+mongoDbHost+":"+mongoDbPort;
			m = MongoClients.create(mongoUri);
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
			lg.error(e.getMessage(), e);
		} finally {
			if (m!=null){
				m.close();
			}
		}
	}


}

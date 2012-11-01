import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.vcell.util.PropertyLoader;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.messaging.db.SimulationExecutionStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationIdentifier;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


public class MongoAnalysis {
	final static long MINUTE = 1000*60;
	final static long HOUR = MINUTE*60;
	final static long DAY = HOUR * 24;
	
	public static class ObjectCompareResults {
		private String key;
		private Object value1;
		private Object value2;

		public ObjectCompareResults(String key, Object value1, Object value2){
			this.key = key;
			this.value1 = value1;
			this.value2 = value2;
		}
		public String getKey(){
			return key;
		}
		public Object getValue1(){
			return value1;
		}
		public Object getvalue2(){
			return value2;
		}
		public String toString(){
			if (isSame()){
				return "\""+value1+"\"";
			}else{
				return "\""+value1+"\" ====> \""+value2+"\"";
			}
		}
		public boolean isSame(){
			return (value1==value2 || (value1!=null && value2!=null && value1.equals(value2)));
		}
	}
	
	
	public static void querySimJobStatusUpdates(String serverId){
		Mongo m = null;
		try {
			PropertyLoader.loadProperties();
			String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHost);
			int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPort)); // default 27017
			m = new Mongo(mongoDbHost,mongoDbPort);

			String mongoDbDatabaseName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
			DB db = m.getDB(mongoDbDatabaseName);
			String mongoDbLoggingCollectionName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbLoggingCollection);
			DBCollection dbCollection = db.getCollection(mongoDbLoggingCollectionName);
			
			
			//-------------------------------
			// query
			//-------------------------------
			BasicDBObject querySpec = new BasicDBObject();
			
			// serverId
			if (serverId!=null){
				querySpec.put(VCMongoMessage.MongoMessage_serverId, serverId);
			}
			
			// message types
			ArrayList<String> msgTypes = new ArrayList<String>();
			msgTypes.add(VCMongoMessage.MongoMessage_msgtype_simJobStatusInsert);
			msgTypes.add(VCMongoMessage.MongoMessage_msgtype_simJobStatusInsert_AlreadyInserted);
			msgTypes.add(VCMongoMessage.MongoMessage_msgtype_simJobStatusUpdate);
			msgTypes.add(VCMongoMessage.MongoMessage_msgtype_simJobStatusUpdate_DBCacheMiss);
			querySpec.put(VCMongoMessage.MongoMessage_msgtype, new BasicDBObject("$in",msgTypes));

			// message time interval, last 2 hours
			long currTime = System.currentTimeMillis();
			long timeInterval = currTime - 2*HOUR;
			querySpec.put(VCMongoMessage.MongoMessage_msgTime, new BasicDBObject("$gte",timeInterval));
			
			
			//-------------------------------
			// sorting
			//-------------------------------
			// msgTime descending
			BasicDBObject sortSpec = new BasicDBObject(VCMongoMessage.MongoMessage_msgTime, new Integer(-1));
			
			//-------------------------------
			// limit
			//-------------------------------
			int limit = 5;

			// gather all objects into memory and close cursor
			DBCursor cursor = dbCollection.find(querySpec).limit(limit).sort(sortSpec);
			ArrayList<DBObject> objects = new ArrayList<DBObject>();
			while (cursor.hasNext()){
				objects.add(cursor.next());
			}
			cursor.close();
			
			for (DBObject dbObject : objects){
				String msgType = (String)dbObject.get(VCMongoMessage.MongoMessage_msgtype);
				System.out.println("\n\n"+msgType+": ");
				if (msgType.equals(VCMongoMessage.MongoMessage_msgtype_simJobStatusInsert)){
					HashMap<String,ObjectCompareResults> results = compare((DBObject)dbObject.get(VCMongoMessage.MongoMessage_newSimJobStatus), (DBObject)dbObject.get(VCMongoMessage.MongoMessage_updatedSimJobStatus),true);
					printCompare("new,updated",results);
//				}else if (msgType.equals(VCMongoMessage.MongoMessage_msgtype_simJobStatusInsert_AlreadyInserted)){
//					HashMap<String,ObjectCompareResults> results = compare((DBObject)dbObject.get(VCMongoMessage.MongoMessage_newSimJobStatus), (DBObject)dbObject.get(VCMongoMessage.MongoMessage_updatedSimJobStatus),true);
//					System.out.println("new,updated: "+results);
				}else if (msgType.equals(VCMongoMessage.MongoMessage_msgtype_simJobStatusUpdate)){
					HashMap<String,ObjectCompareResults> results1 = compare((DBObject)dbObject.get(VCMongoMessage.MongoMessage_oldSimJobStatus), (DBObject)dbObject.get(VCMongoMessage.MongoMessage_newSimJobStatus),true);
					printCompare("old,new",results1);
					HashMap<String,ObjectCompareResults> results2 = compare((DBObject)dbObject.get(VCMongoMessage.MongoMessage_newSimJobStatus), (DBObject)dbObject.get(VCMongoMessage.MongoMessage_updatedSimJobStatus),true);
					printCompare("new,updated:",results2);
				}else if (msgType.equals(VCMongoMessage.MongoMessage_msgtype_simJobStatusUpdate_DBCacheMiss)){
					HashMap<String,ObjectCompareResults> results1 = compare((DBObject)dbObject.get(VCMongoMessage.MongoMessage_oldSimJobStatus), (DBObject)dbObject.get(VCMongoMessage.MongoMessage_newSimJobStatus),true);
					printCompare("old,new",results1);
					HashMap<String,ObjectCompareResults> results2 = compare((DBObject)dbObject.get(VCMongoMessage.MongoMessage_cachedSimJobStatus), (DBObject)dbObject.get(VCMongoMessage.MongoMessage_newSimJobStatus),true);
					printCompare("cached,new",results2);
					HashMap<String,ObjectCompareResults> results3 = compare((DBObject)dbObject.get(VCMongoMessage.MongoMessage_cachedSimJobStatus), (DBObject)dbObject.get(VCMongoMessage.MongoMessage_oldSimJobStatus),true);
					printCompare("old,cached",results3);
				}else{
					System.out.println("unexpected message type '"+msgType+"',  .... skipping ... ");
				}
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (m != null){
				m.close();
			}
		}	
	}
	
	private static void printCompare(String title,	HashMap<String, ObjectCompareResults> results) {
		System.out.println("\n"+title);
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(results.keySet());
		Collections.sort(keys);
		for (String key : keys){
			System.out.println(key+": "+results.get(key));
		}
	}

	public static HashMap<String,ObjectCompareResults> compare(DBObject obj1, DBObject obj2, boolean bIgnoreSame){
		HashMap<String, ObjectCompareResults> results = new HashMap<String, MongoAnalysis.ObjectCompareResults>();
		Set<String> keySet1 = obj1.keySet();
		Set<String> keySet2 = obj2.keySet();
		HashSet<String> allKeys = new HashSet<String>();
		allKeys.addAll(keySet1);
		allKeys.addAll(keySet2);
		for (String key : allKeys){
			Object value1 = obj1.get(key);
			Object value2 = obj2.get(key);
			ObjectCompareResults objResult = new ObjectCompareResults(key, value1, value2);
			if (!bIgnoreSame || !objResult.isSame()){
				results.put(key, objResult);
			}
		}
		return results;
	}

	public static void queryPbsSubmissionElapsedTimes(String serverId, Integer serviceOrdinal){
		Mongo m = null;
		try {
			PropertyLoader.loadProperties();
			String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHost);
			int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPort)); // default 27017
			m = new Mongo(mongoDbHost,mongoDbPort);

			String mongoDbDatabaseName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
			DB db = m.getDB(mongoDbDatabaseName);
			String mongoDbLoggingCollectionName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbLoggingCollection);
			DBCollection dbCollection = db.getCollection(mongoDbLoggingCollectionName);
			
			//-------------------------------
			// query
			//-------------------------------
			BasicDBObject querySpec = new BasicDBObject();
			
			// serverId
			if (serverId!=null){
				querySpec.put(VCMongoMessage.MongoMessage_serverId, serverId);
			}
			
			// message time interval, last 2 hours
			long currTime = System.currentTimeMillis();
			long timeInterval = currTime - 2*HOUR;
			querySpec.put(VCMongoMessage.MongoMessage_msgTime, new BasicDBObject("$gte",timeInterval));
			
			querySpec.put(VCMongoMessage.MongoMessage_serviceName, VCMongoMessage.ServiceName.pbsWorker.name());
			if (serviceOrdinal!=null){
				querySpec.put(VCMongoMessage.MongoMessage_serviceOrdinal, serviceOrdinal);
			}
			
			//-------------------------------
			// sorting
			//-------------------------------
			// msgTime descending
			BasicDBObject sortSpec = new BasicDBObject(VCMongoMessage.MongoMessage_msgTime, new Integer(-1));
			
			//-------------------------------
			// limit
			//-------------------------------
			int limit = 500;

			// gather all objects in memory and close cursor
			DBCursor cursor = dbCollection.find(querySpec).limit(limit).sort(sortSpec);
			ArrayList<DBObject> objects = new ArrayList<DBObject>();
			while (cursor.hasNext()){
				objects.add(cursor.next());
			}
			cursor.close();

			// sort objects in memory
			Collections.sort(objects,new Comparator<DBObject>() {
				public int compare(DBObject o1, DBObject o2) {
					Long msgTime1 = (Long)o1.get(VCMongoMessage.MongoMessage_msgTime);
					Long msgTime2 = (Long)o2.get(VCMongoMessage.MongoMessage_msgTime);
					return msgTime1.compareTo(msgTime2);
				}
			});
			
			// print out analysis
			long starting = 0;
			long ending = 0;
			for (DBObject dbObject : objects){
				if (dbObject.containsField(VCMongoMessage.MongoMessage_htcWorkerMsg)){
					if (dbObject.get(VCMongoMessage.MongoMessage_htcWorkerMsg).equals("PBSSolver.submit2PBS() returned")){
						ending = (Long)dbObject.get(VCMongoMessage.MongoMessage_msgTime);
						String endTimeNice = (String)dbObject.get(VCMongoMessage.MongoMessage_msgTimeNice);
						if (endTimeNice == null){
							endTimeNice = new Date(ending).toString();
						}
//						System.out.println("Simulation "+dbObject.get(VCMongoMessage.MongoMessage_simId)+" endTime (sec) = "+ending);
						System.out.println(endTimeNice+", Simulation "+dbObject.get(VCMongoMessage.MongoMessage_simId)+" submitTime (sec) = "+((ending-starting)/1000.0));
					}
					if (dbObject.get(VCMongoMessage.MongoMessage_htcWorkerMsg).equals("calling PBSSolver.submit2PBS()")){
						starting = (Long)dbObject.get(VCMongoMessage.MongoMessage_msgTime);
//						System.out.println("Simulation "+dbObject.get(VCMongoMessage.MongoMessage_simId)+" startTime (sec) = "+starting);
					}
				}
//				System.out.println(dbObject.toString());
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (m != null){
				m.close();
			}
		}	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			querySimJobStatusUpdates("BETA");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

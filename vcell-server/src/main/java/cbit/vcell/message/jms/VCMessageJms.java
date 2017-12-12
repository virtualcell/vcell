package cbit.vcell.message.jms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.bson.types.ObjectId;
import org.fusesource.hawtbuf.ByteArrayInputStream;

import cbit.vcell.message.MessagePropertyNotFoundException;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingDelegate;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.mongodb.VCMongoDbDriver;
import cbit.vcell.resource.PropertyLoader;

public class VCMessageJms implements VCMessage {
	
	public static final String BLOB_MESSAGE_FILE_NAME = "blobMessageFileName";
	public static final String BLOB_MESSAGE_PRODUCER_TEMPDIR = "blobProducerTempDir";
	public static final String BLOB_MESSAGE_OBJECT_TYPE = "blobObjectType";
	public static final String BLOB_MESSAGE_OBJECT_SIZE = "blobObjectSize";
	public static final String BLOB_MESSAGE_PERSISTENCE_TYPE = "blobPersistenceType";
	public static final String BLOB_MESSAGE_PERSISTENCE_TYPE_FILE = "file";
	public static final String BLOB_MESSAGE_PERSISTENCE_TYPE_MONGODB = "mongodb";
	public static final String BLOB_MESSAGE_MONGODB_OBJECTID = "mongoObjectId";
	
	private transient Serializable blobObject = null;
	private transient File blobFile = null;
	private transient ObjectId blobObjectId = null;
	private VCMessagingDelegate delegate = null;
	
	private Message jmsMessage = null;
		
	public VCMessageJms(Message jmsMessage, VCMessagingDelegate delegate){
		this.jmsMessage = jmsMessage;
		this.delegate = delegate;
	}
	
	public VCMessageJms(Message jmsMessage, Serializable blobObject, VCMessagingDelegate delegate){
		this.jmsMessage = jmsMessage;
		this.blobObject = blobObject;
		this.delegate = delegate;
	}
	
	public Message getJmsMessage(){
		return jmsMessage;
	}
	
	/**
	 * see Property jmsBlobMessageMinSize "vcell.jms.blobMessageMinSize"
	 * see Property jmsBlobMessageTempDir "vcell.jms.blobMessageTempDir"
	 * see class MessageProducerSessionJms
	 * 
	 * 
	 * 1) Message Producer serializes into byte[] and compares size with PropertyLoader.jmsBlobMessageMinSize.
	 * 2) For Large Object Messages (> threshold bytes), MessageProducerSessionJms writes bytes to a local file (e.g. MyBlobMessageTempDir/BlobMessage2295645974283237270.data).
	 * 3) Pass file name as message properties so that receiver can delete file when done.
	 * 4) consumer-side VCMessage infrastructure receives BlobMessage and invokes VCMessageJms.loadBlobMessage()
	 * 5) loadBlobMessage() reads object from stream and attempts to delete both original and broker files.
	 * 6) consumer's message listener calls getObjectContent() not knowing if it was sent as a Blob or not.
	 * 7) message consumer calls VCMessageJms.removeBlobFile() to clean up disk.
	 * 
	 */
	public void loadBlobFile(){
		if (blobObject!=null){
			return;
		}
		if (jmsMessage instanceof ObjectMessage
				&& propertyExists(BLOB_MESSAGE_PERSISTENCE_TYPE) 
				&& getStringProperty(BLOB_MESSAGE_PERSISTENCE_TYPE).equals(BLOB_MESSAGE_PERSISTENCE_TYPE_FILE)){

			try {				
				long t1 = System.currentTimeMillis();
				//
				// read serialized object from inputStream (from Broker's data file)
				//
				String blobFileName = jmsMessage.getStringProperty(BLOB_MESSAGE_FILE_NAME);
				
				//
				// get directory to retrieve Message BLOBs
				//
				File localBlobDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.jmsBlobMessageTempDir));				
				File producerBlobDir = new File(getStringProperty(BLOB_MESSAGE_PRODUCER_TEMPDIR));
				blobFile = new File(localBlobDir,blobFileName);
				if (!blobFile.exists()){
					blobFile = new File(producerBlobDir,blobFileName);
					if (!blobFile.exists()){
						throw new RuntimeException("Message BLOB file \""+blobFileName+"\" not found local=\""+localBlobDir+"\" or producer=\""+producerBlobDir+"\"");
					}
				}
				FileInputStream fis = new FileInputStream(blobFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(bis);
				blobObject = (Serializable) ois.readObject();
				ois.close();
				bis.close();
				fis.close();
				delegate.onTraceEvent("VCMessageJms.loadBlobFile(): size="+jmsMessage.getIntProperty(BLOB_MESSAGE_OBJECT_SIZE)+", type="+jmsMessage.getStringProperty(BLOB_MESSAGE_OBJECT_TYPE)+", elapsedTime = "+(System.currentTimeMillis()-t1)+" ms");
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		if (jmsMessage instanceof ObjectMessage 
				&& propertyExists(BLOB_MESSAGE_PERSISTENCE_TYPE) 
				&& getStringProperty(BLOB_MESSAGE_PERSISTENCE_TYPE).equals(BLOB_MESSAGE_PERSISTENCE_TYPE_MONGODB)){
			try {				
				long t1 = System.currentTimeMillis();
				//
				// read serialized object from inputStream (from Broker's data file)
				//
				String mongo_objectid_hex = jmsMessage.getStringProperty(BLOB_MESSAGE_MONGODB_OBJECTID);
				blobObjectId = new ObjectId(mongo_objectid_hex);
				
				byte[] blob = VCMongoDbDriver.getInstance().getBLOB(blobObjectId);

				ByteArrayInputStream bis = new ByteArrayInputStream(blob);
				ObjectInputStream ois = new ObjectInputStream(bis);
				blobObject = (Serializable) ois.readObject();
				ois.close();
				bis.close();
				delegate.onTraceEvent("VCMessageJms.loadBlobFile(): size="+jmsMessage.getIntProperty(BLOB_MESSAGE_OBJECT_SIZE)+", type="+jmsMessage.getStringProperty(BLOB_MESSAGE_OBJECT_TYPE)+", elapsedTime = "+(System.currentTimeMillis()-t1)+" ms");
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}
	
	public void removeBlobFile(){
		if (blobFile!=null){
			//
			// remove blob file if it still exists (after a successful commit)
			//
			try {
				blobFile.delete();
			}catch (Exception e){
				delegate.onException(e);
				e.printStackTrace(System.out);
			}
		}
		if (blobObjectId != null) {
			try {
				VCMongoDbDriver.getInstance().removeBLOB(blobObjectId);
			}catch (Exception e) {
				delegate.onException(e);
				e.printStackTrace(System.out);
			}
		}
	}
	


	public Object getObjectContent(){
		if (jmsMessage instanceof ObjectMessage){
			if (propertyExists(BLOB_MESSAGE_FILE_NAME)){
				return blobObject;
			}else{
				try {
					return ((ObjectMessage)jmsMessage).getObject();
				} catch (JMSException e) {
					handleJMSException(e);
					throw new RuntimeException(e.getMessage(),e);
				}
			}
		}else{
			return null;
		}
	}
	
	public String getTextContent(){
		if (jmsMessage instanceof TextMessage){
			try {
				return ((TextMessage)jmsMessage).getText();
			} catch (JMSException e) {
				handleJMSException(e);
				throw new RuntimeException(e.getMessage(),e);
			}
		}else{
			return null;
		}
	}
	
	public void setBooleanProperty(String propertyName, boolean value){
		try {
			jmsMessage.setBooleanProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public void setDoubleProperty(String propertyName, double value){
		try {
			jmsMessage.setDoubleProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public void setIntProperty(String propertyName, int value){
		try {
			jmsMessage.setIntProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public void setLongProperty(String propertyName, long value){
		try {
			jmsMessage.setLongProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public void setObjectProperty(String propertyName, Object value){
		try {
			jmsMessage.setObjectProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public void setStringProperty(String propertyName, String value){
		try {
			jmsMessage.setStringProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public Enumeration<String> getPropertyNames(){
		try {
			return jmsMessage.getPropertyNames();
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public boolean getBooleanProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		try {
			return jmsMessage.getBooleanProperty(propertyName);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public double getDoubleProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		try {
			return jmsMessage.getDoubleProperty(propertyName);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public int getIntProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		try {
			return jmsMessage.getIntProperty(propertyName);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public long getLongProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		try {
			return jmsMessage.getLongProperty(propertyName);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public Object getObjectProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		try {
			return jmsMessage.getObjectProperty(propertyName);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public String getStringProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		try {
			return jmsMessage.getStringProperty(propertyName);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public boolean propertyExists(String propertyName){
		try {
			return jmsMessage.propertyExists(propertyName);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	private void handleJMSException(JMSException e){
		delegate.onException(e);
		e.printStackTrace(System.out);
	}

	public VCDestination getReplyTo() {
		try {
			Destination destination = jmsMessage.getJMSReplyTo();
			if (destination == null){
				return null;
			}
			if (destination instanceof Queue){
				return new VCellQueue(((Queue) destination).getQueueName());
			}else if (destination instanceof Topic){
				return new VCellTopic(((Topic) destination).getTopicName());
			}else{
				throw new RuntimeException("unexpected destination: "+destination);
			}
		}catch (JMSException e){
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public String getCorrelationID() {
		try {
			return jmsMessage.getJMSCorrelationID();
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public String getMessageID() {
		try {
			return jmsMessage.getJMSMessageID();
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public void setCorrelationID(String correlationID) {
		try {
			jmsMessage.setJMSCorrelationID(correlationID);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public String show(){
		StringBuffer buffer = new StringBuffer();
		try {
			java.util.Enumeration enum1 = jmsMessage.getPropertyNames();
			while (enum1.hasMoreElements()){
				String propName = (String)enum1.nextElement();
				try {
					String value = jmsMessage.getStringProperty(propName);
					buffer.append(" " + propName + "='" + value + "'");
				} catch (MessagePropertyNotFoundException ex) {
					// definitely should not happen
					delegate.onException(ex);
				}
			}
			int maxContentLength = 120;
			if (jmsMessage instanceof TextMessage){
				buffer.append("  textContent='");
				String textContent = ((TextMessage)jmsMessage).getText();
				if (textContent!=null && textContent.length()>maxContentLength){
					buffer.append(textContent.substring(0, maxContentLength-3)+"...");
				}else{
					buffer.append(textContent);
				}
				buffer.append("'");
			}else if (jmsMessage instanceof ObjectMessage){
				buffer.append("  objectContent='");
				String text = ""+((ObjectMessage)jmsMessage).getObject();
				if (text.length()>maxContentLength){
					buffer.append(text.substring(0, maxContentLength-3)+"...");
				}else{
					buffer.append(text);
				}
				buffer.append("'");
			}
		} catch (JMSException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage(),e);
		}
		return buffer.toString();
	}

	@Override
	public long getTimestampMS() {
		try {
			return jmsMessage.getJMSTimestamp();
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
}

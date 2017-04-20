package cbit.vcell.message.local;

import java.util.Enumeration;
import java.util.Vector;

import org.bson.BasicBSONObject;

import cbit.vcell.message.MessagePropertyNotFoundException;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;

public class VCMessageBSON implements VCMessage {
	private final String textContent;
	private final Object objectContent;
	private BasicBSONObject bsonObject = new BasicBSONObject();
	private VCDestination replyTo = null;
	private String correlationID = null;
	private final String messageID;
	private final long timestampMS;

	VCMessageBSON(){
		this.textContent = null;
		this.objectContent = null;
		this.messageID = "VCMessageBSON("+System.identityHashCode(this)+")";
		this.timestampMS = System.currentTimeMillis();
	}
	
	VCMessageBSON(String textContent){
		this.textContent = textContent;
		this.objectContent = null;
		this.messageID = "VCMessageBSON("+System.identityHashCode(this)+")";
		this.timestampMS = System.currentTimeMillis();
	}
	
	VCMessageBSON(Object objectContent){
		this.textContent = null;
		this.objectContent = objectContent;
		this.messageID = "VCMessageBSON("+System.identityHashCode(this)+")";
		this.timestampMS = System.currentTimeMillis();
	}
	
	@Override
	public Object getObjectContent() {
		return objectContent;
	}

	@Override
	public String getTextContent() {
		return textContent;
	}

	@Override
	public void setBooleanProperty(String propertyName, boolean value) {
		bsonObject.put(propertyName, new Boolean(value));
	}

	@Override
	public void setDoubleProperty(String propertyName, double value) {
		bsonObject.put(propertyName, new Double(value));
	}

	@Override
	public void setIntProperty(String propertyName, int value) {
		bsonObject.put(propertyName, new Integer(value));
	}

	@Override
	public void setLongProperty(String propertyName, long value) {
		bsonObject.put(propertyName, new Long(value));
	}

	@Override
	public void setObjectProperty(String propertyName, Object value) {
		bsonObject.put(propertyName, value);
	}

	@Override
	public void setStringProperty(String propertyName, String value) {
		bsonObject.put(propertyName, value);
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		Vector<String> propertyNameVector = new Vector<String>(bsonObject.keySet());
		return propertyNameVector.elements();
	}

	@Override
	public boolean getBooleanProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		return bsonObject.getBoolean(propertyName);
	}

	@Override
	public double getDoubleProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		return bsonObject.getDouble(propertyName);
	}

	@Override
	public int getIntProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		return bsonObject.getInt(propertyName);
	}

	@Override
	public long getLongProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		return bsonObject.getLong(propertyName);
	}

	@Override
	public Object getObjectProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		return bsonObject.get(propertyName);
	}

	@Override
	public String getStringProperty(String propertyName) throws MessagePropertyNotFoundException {
		if (!propertyExists(propertyName)){
			throw new MessagePropertyNotFoundException(propertyName+" not found in message");
		}
		return bsonObject.getString(propertyName);
	}

	@Override
	public boolean propertyExists(String propertyName) {
		return bsonObject.containsField(propertyName);
	}

	@Override
	public VCDestination getReplyTo() {
		return replyTo;
	}

	@Override
	public String getCorrelationID() {
		return correlationID;
	}

	@Override
	public String getMessageID() {
		return this.messageID;
	}

	@Override
	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}

	@Override
	public String show() {
		StringBuffer buffer = new StringBuffer();
		java.util.Enumeration enum1 = getPropertyNames();
		while (enum1.hasMoreElements()){
			String propName = (String)enum1.nextElement();
			try {
				String value = getStringProperty(propName);
				buffer.append(" " + propName + "='" + value + "'");
			} catch (MessagePropertyNotFoundException ex) {
				// definitely should not happen
			}
		}
		int maxContentLength = 120;
		if (textContent!=null){
			buffer.append("  textContent='");
			if (textContent.length()>maxContentLength){
				buffer.append(textContent.substring(0, maxContentLength-3)+"...");
			}else{
				buffer.append(textContent);
			}
			buffer.append("'");
		}else if (objectContent!=null){
			buffer.append("  objectContent='");
			String text = objectContent.toString();
			if (text.length()>maxContentLength){
				buffer.append(text.substring(0, maxContentLength-3)+"...");
			}else{
				buffer.append(text);
			}
			buffer.append("'");
		}
		return buffer.toString();
	}

	@Override
	public long getTimestampMS() {
		return timestampMS;
	}

}

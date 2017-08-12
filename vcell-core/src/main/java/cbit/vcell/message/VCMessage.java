package cbit.vcell.message;

import java.util.Enumeration;

public abstract interface VCMessage {

	public Object getObjectContent();
	
	public String getTextContent();
	
	public void setBooleanProperty(String propertyName, boolean value);
	
	public void setDoubleProperty(String propertyName, double value);
	
	public void setIntProperty(String propertyName, int value);
	
	public void setLongProperty(String propertyName, long value);
	
	public void setObjectProperty(String propertyName, Object value);
	
	public void setStringProperty(String propertyName, String value);
	
	public Enumeration<String> getPropertyNames();
	
	public boolean getBooleanProperty(String propertyName) throws MessagePropertyNotFoundException;

	public double getDoubleProperty(String propertyName) throws MessagePropertyNotFoundException;

	public int getIntProperty(String propertyName) throws MessagePropertyNotFoundException;

	public long getLongProperty(String propertyName) throws MessagePropertyNotFoundException;

	public Object getObjectProperty(String propertyName) throws MessagePropertyNotFoundException;

	public String getStringProperty(String propertyName) throws MessagePropertyNotFoundException;

	public boolean propertyExists(String propertyName);

	public VCDestination getReplyTo();
	
	public String getCorrelationID();
	
	public String getMessageID();
	
//	public void setReplyTo(VCDestination destination);
	
	public void setCorrelationID(String correlationID);

	public String show();

	public long getTimestampMS();
	
//	public void setMessageID(String messageID);
}

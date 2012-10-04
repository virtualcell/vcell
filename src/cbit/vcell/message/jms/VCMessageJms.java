package cbit.vcell.message.jms;

import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import cbit.vcell.message.MessagePropertyNotFoundException;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;

public class VCMessageJms implements VCMessage {
	
	private Message jmsMessage = null;
		
	public VCMessageJms(Message jmsMessage){
		this.jmsMessage = jmsMessage;
	}
	
	public Message getJmsMessage(){
		return jmsMessage;
	}
	
	public Object getObjectContent(){
		if (jmsMessage instanceof ObjectMessage){
			try {
				return ((ObjectMessage)jmsMessage).getObject();
			} catch (JMSException e) {
				handleJMSException(e);
				throw new RuntimeException(e.getMessage());
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
				throw new RuntimeException(e.getMessage());
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
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void setDoubleProperty(String propertyName, double value){
		try {
			jmsMessage.setDoubleProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void setIntProperty(String propertyName, int value){
		try {
			jmsMessage.setIntProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void setLongProperty(String propertyName, long value){
		try {
			jmsMessage.setLongProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void setObjectProperty(String propertyName, Object value){
		try {
			jmsMessage.setObjectProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void setStringProperty(String propertyName, String value){
		try {
			jmsMessage.setStringProperty(propertyName,value);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public Enumeration<String> getPropertyNames(){
		try {
			return jmsMessage.getPropertyNames();
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage());
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
			throw new RuntimeException(e.getMessage());
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
			throw new RuntimeException(e.getMessage());
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
			throw new RuntimeException(e.getMessage());
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
			throw new RuntimeException(e.getMessage());
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
			throw new RuntimeException(e.getMessage());
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
			throw new RuntimeException(e.getMessage());
		}
	}

	public boolean propertyExists(String propertyName){
		try {
			return jmsMessage.propertyExists(propertyName);
		} catch (JMSException e) {
			handleJMSException(e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private void handleJMSException(JMSException e){
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
			throw new RuntimeException(e.getMessage());
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
				}
			}
			int maxContentLength = 120;
			if (jmsMessage instanceof TextMessage){
				buffer.append("  textContent='");
				String textContent = ((TextMessage)jmsMessage).getText();
				if (textContent.length()>maxContentLength){
					buffer.append(textContent.substring(0, maxContentLength-3)+"...");
				}else{
					buffer.append(textContent);
				}
				buffer.append("'");
			}else if (jmsMessage instanceof ObjectMessage){
				buffer.append("  objectContent='");
				String text = ((ObjectMessage)jmsMessage).getObject().toString();
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
	
}

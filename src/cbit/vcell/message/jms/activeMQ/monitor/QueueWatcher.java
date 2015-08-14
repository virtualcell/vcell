package cbit.vcell.message.jms.activeMQ.monitor;

import java.io.PrintWriter;
import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;

public class QueueWatcher {
	private Session session;
	private final Destination queue;
	private final PrintWriter writer;

	public QueueWatcher(Session session, Destination queue, PrintWriter log) {
		this.session = session;
		this.queue = queue;
		this.writer = log; 
	}

	public void start( ) throws JMSException {
		/*
		Destination consumer = session.createTopic(AdvisorySupport.getConsumerAdvisoryTopic(queue).getPhysicalName()); 
		listenTo(consumer, msg -> messageConsumer(msg) );

		Destination producer = session.createTopic(AdvisorySupport.getProducerAdvisoryTopic(queue).getPhysicalName()); 
		listenTo(producer, msg -> messageProducer(msg) );
		*/
		
		ActiveMQTopic[] tpcs = AdvisorySupport.getAllDestinationAdvisoryTopics(queue);
		for (ActiveMQTopic tp : tpcs) {
			writer.println("watching " + tp.getTopicName());
			Destination ds = session.createTopic(tp.getPhysicalName()); 
			MessageConsumer c = session.createConsumer(ds);
			c.setMessageListener(new AdvListener(tp.getTopicName()) );
		}
	}

	private void listenTo(Destination subj, MessageListener listener) throws JMSException {
		MessageConsumer c = session.createConsumer(subj);
		c.setMessageListener( listener );
	}
	
	private void showProperties(Message msg) throws JMSException {
		Enumeration<?> e = msg.getPropertyNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement().toString();
			String value = msg.getStringProperty(key);
			writer.println(' ' + key + " = " + value);
		}
		ActiveMQMessage aMsg = (ActiveMQMessage) msg;
		writer.println(' ' + aMsg.getDataStructure().toString());
	}

	private void messageProducer(Message msg) {
		try {
			writer.println("PRODUCER");
			showProperties(msg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void messageConsumer(Message msg) {
		try {
			writer.println("CONSUMER");
			showProperties(msg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private class AdvListener implements MessageListener {
		private final String topicName;

		public AdvListener(String topicName) {
			super();
			this.topicName = topicName;
		}

		@Override
		public void onMessage(Message msg) {
		try {
			writer.println(topicName);
			showProperties(msg);
		} catch (JMSException e) {
			e.printStackTrace();
			
		}
		
	}
	}

}

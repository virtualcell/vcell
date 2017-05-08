package cbit.vcell.message.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.scijava.service.AbstractService;

import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConsumer;
import cbit.vcell.message.VCMessagingDelegate;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;

public abstract class VCMessagingServiceJms extends AbstractService implements VCMessagingService {
	
	private ArrayList<ConsumerContextJms> consumerContexts = new ArrayList<ConsumerContextJms>();
    private ArrayList<MessageProducerSessionJms> messagingProducerSessions = new ArrayList<MessageProducerSessionJms>();
	protected HashMap<String,Destination> destinationMap = new HashMap<String,Destination>();
	private VCMessagingDelegate delegate = new SimpleMessagingDelegate();
	
	
	public VCMessagingServiceJms() {
		super();
	}
	
	private void onException(JMSException e){
		e.printStackTrace(System.out);
	}
	
	@Override
	public VCMessagingDelegate getDelegate(){
		return this.delegate;
	}

	@Override
	public void setDelegate(VCMessagingDelegate delegate){
		this.delegate = delegate;
	}
	
	@Override
	public VCMessageSession createProducerSession(){
		MessageProducerSessionJms messageProducerSession;
		try {
			messageProducerSession = new MessageProducerSessionJms(this){
				@Override
				public void close() {
					messagingProducerSessions.remove(this);
					super.close();
				}
			};
			messagingProducerSessions.add(messageProducerSession);
			return messageProducerSession;
		} catch (JMSException e) {
			onException(e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public abstract ConnectionFactory createConnectionFactory() throws JMSException;
		
	@Override
	public void closeAll() throws VCMessagingException {
		System.out.println(toString()+" closeAll() started");
		for (ConsumerContextJms consumerContext : consumerContexts){
			consumerContext.stop();
		}
		try {
			Thread.sleep(ConsumerContextJms.CONSUMER_POLLING_INTERVAL_MS*2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	//	System.out.println(toString()+" consumer close() invocations");
		
		{
			Iterator<ConsumerContextJms> iter = consumerContexts.iterator();
			while (iter.hasNext()) {
				ConsumerContextJms cc = iter.next();
				iter.remove();
				cc.close();
			}
		}
		
//		System.out.println(toString()+" message producer close requests");
		Iterator<MessageProducerSessionJms> iter = messagingProducerSessions.iterator();
		while (iter.hasNext()) {
			MessageProducerSessionJms mp = iter.next( );
			iter.remove();
			mp.close();
		}
//		System.out.println(toString()+" closeAll() complete");
	}

	@Override
	public void addMessageConsumer(VCMessagingConsumer vcMessagingConsumer) {
		for (ConsumerContextJms context : consumerContexts){
			if (context.getVCConsumer()==vcMessagingConsumer){
				return;
			}
		}
		
		ConsumerContextJms consumerContext = new ConsumerContextJms(this,vcMessagingConsumer);
		consumerContexts.add(consumerContext);
		
		try {
			consumerContext.init();
		} catch (JMSException e1) {
			e1.printStackTrace();
			onException(e1);
		}
		consumerContext.start();
	}

	@Override
	public void removeMessageConsumer(VCMessagingConsumer vcMessagingConsumer) {
		for (ConsumerContextJms context : consumerContexts){
			if (context.getVCConsumer() == vcMessagingConsumer){
				try {
					context.stop();
				} finally {
					consumerContexts.remove(context);
				}
				return;
			}
		}
	}

	@Override
	public List<VCMessagingConsumer> getMessageConsumers() {
		ArrayList<VCMessagingConsumer> consumers = new ArrayList<VCMessagingConsumer>();
		for (ConsumerContextJms context : consumerContexts){
			consumers.add(context.getVCConsumer());
		}
		return consumers;
	}
	
	@Override
	public VCMessageSelector createSelector(String selectorString){
		return new VCMessageSelectorJms(selectorString);
	}

	public abstract MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination, VCMessageSelector vcSelector, int prefetchLimit) throws JMSException;
	
}

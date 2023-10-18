package cbit.vcell.message.jms;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConsumer;
import cbit.vcell.message.VCMessagingDelegate;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.resource.PropertyLoader;

public abstract class VCMessagingServiceJms implements VCMessagingService {
	private final static Logger lg = LogManager.getLogger(VCMessagingServiceJms.class);
	
	private ArrayList<ConsumerContextJms> consumerContexts = new ArrayList<ConsumerContextJms>();
    private ArrayList<MessageProducerSessionJms> messagingProducerSessions = new ArrayList<MessageProducerSessionJms>();
	protected HashMap<String,Destination> destinationMap = new HashMap<String,Destination>();
	private VCMessagingDelegate delegate = new SimpleMessagingDelegate();
	protected String jmshost = null;
	protected Integer jmsport = null;

	private Timer garbageCollectorTimer = new Timer(true);
	private final static long BlobGarbageCollector_initialDelayMS 	= 1000*3600;		// one hour
	private final static long BlobGarbageCollector_periodMS 		= 1000*3600; 		// one hour
	private final static long BlobGarbageCollector_AgeLimitMS 		= 1000*3600*24*7;	// one week
	
	private TimerTask blobGarbageCollectorTask = new TimerTask() {
		@Override
		public void run() {
			String blobDirPath = PropertyLoader.getProperty(PropertyLoader.jmsBlobMessageTempDir,null);
			if (blobDirPath!=null && new File(blobDirPath).isDirectory()) {
				try {
					File blobDir = new File(blobDirPath);
					FileFilter gcFileVisitor = new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							long ageMS = System.currentTimeMillis() - pathname.lastModified();
							if (pathname.getName().startsWith("BlobMessage") && pathname.getName().endsWith(".data") && ageMS > BlobGarbageCollector_AgeLimitMS){
								pathname.delete();
							}
							return false; // always return false (empty list)
						}
					};
					blobDir.listFiles(gcFileVisitor);
				}catch (Exception e){
					lg.error(e.getMessage(), e);
					if (getDelegate()!=null){
						getDelegate().onException(e);
					}
				}
			}
		}
	};
	
	
	public VCMessagingServiceJms() {
		super();
		garbageCollectorTimer.schedule(
				blobGarbageCollectorTask, 
				BlobGarbageCollector_initialDelayMS, 
				BlobGarbageCollector_periodMS);
	}
	
	private void onException(Exception e){
		delegate.onException(e);
		lg.error(e.getMessage(), e);
	}
	
	@Override
	public VCMessagingDelegate getDelegate(){
		return this.delegate;
	}

	@Override
	public void setConfiguration(VCMessagingDelegate delegate, String jmshost, int jmsport){
		this.delegate = delegate;
		this.jmshost = jmshost;
		this.jmsport = jmsport;
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
		} catch (JMSException | VCMessagingException e) {
			onException(e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public abstract ConnectionFactory createConnectionFactory() throws JMSException, VCMessagingException;
		
	@Override
	public void close() throws VCMessagingException {
		lg.info(toString()+" closeAll() started");
		for (ConsumerContextJms consumerContext : consumerContexts){
			consumerContext.stop();
		}
		try {
			Thread.sleep(ConsumerContextJms.CONSUMER_POLLING_INTERVAL_MS*2);
		} catch (InterruptedException e) {
			lg.error(e);
		}
	//	lg.info(toString()+" consumer close() invocations");
		
		{
			Iterator<ConsumerContextJms> iter = consumerContexts.iterator();
			while (iter.hasNext()) {
				ConsumerContextJms cc = iter.next();
				iter.remove();
				cc.close();
			}
		}
		
//		lg.info(toString()+" message producer close requests");
		Iterator<MessageProducerSessionJms> iter = messagingProducerSessions.iterator();
		while (iter.hasNext()) {
			MessageProducerSessionJms mp = iter.next( );
			iter.remove();
			mp.close();
		}
//		lg.info(toString()+" closeAll() complete");
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
			lg.error(e1);
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

	public abstract MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination, VCMessageSelector vcSelector, int prefetchLimit) throws JMSException, VCMessagingException;
	
}

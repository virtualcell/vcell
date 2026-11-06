package cbit.vcell.message.jms;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessagingConsumer;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCTopicConsumer;

public class ConsumerContextJms implements Runnable {
	public static final long CONSUMER_POLLING_INTERVAL_MS = 2000;
	private VCMessagingServiceJms vcMessagingServiceJms = null;
	private VCMessagingConsumer vcConsumer = null;
	private Session jmsSession = null;
	private Connection jmsConnection = null;
	private MessageConsumer jmsMessageConsumer = null;
	// volatile: stop() and stopAndClose() run on a different thread from the polling loop,
	// which must see the change promptly or it will keep calling receive() after close()
	private volatile boolean bProcessing = false;
	private volatile Thread thread = null;
	private static Logger lg = LogManager.getLogger(ConsumerContextJms.class);
	
	public ConsumerContextJms(VCMessagingServiceJms vcMessagingServiceJms, VCMessagingConsumer consumer){
		this.vcMessagingServiceJms = vcMessagingServiceJms;
		this.vcConsumer = consumer;
	}
	
	public void run(){
		bProcessing = true;
		lg.info(toString()+" consumer thread starting.");
		while (bProcessing){
			MessageProducerSessionJms temporaryMessageProducerSession = null;
			try {
				Message jmsMessage = jmsMessageConsumer.receive(CONSUMER_POLLING_INTERVAL_MS);
				if (jmsMessage!=null){
//						lg.info(toString()+"===============message received within "+CONSUMER_POLLING_INTERVAL_MS+" ms");
					if (vcConsumer instanceof VCQueueConsumer){
						VCQueueConsumer queueConsumer = (VCQueueConsumer)vcConsumer;
						VCMessageJms vcMessage = new VCMessageJms(jmsMessage, vcMessagingServiceJms.getDelegate());
						vcMessage.loadBlobFile();
						vcMessagingServiceJms.getDelegate().onMessageReceived(vcMessage,vcConsumer.getVCDestination());
						temporaryMessageProducerSession = new MessageProducerSessionJms(vcMessagingServiceJms);
						queueConsumer.getQueueListener().onQueueMessage(vcMessage, temporaryMessageProducerSession);
						temporaryMessageProducerSession.commit();
						jmsSession.commit();
						vcMessage.removeBlobFile();
					} else if (vcConsumer instanceof VCTopicConsumer){
						VCTopicConsumer topicConsumer = (VCTopicConsumer)vcConsumer;
						VCMessageJms vcMessage = new VCMessageJms(jmsMessage, vcMessagingServiceJms.getDelegate());
						vcMessage.loadBlobFile();
						vcMessagingServiceJms.getDelegate().onMessageReceived(vcMessage,vcConsumer.getVCDestination());
						temporaryMessageProducerSession = new MessageProducerSessionJms(vcMessagingServiceJms);
						topicConsumer.getTopicListener().onTopicMessage(vcMessage, temporaryMessageProducerSession);
						temporaryMessageProducerSession.commit();
						jmsSession.commit();
						//
						// if we knew this was the only subscriber for this topic, then remove file immediately.
						// since we don't know for sure, don't remove the file here.
						// instead, we need to periodically remove old Blob files (maybe daily) 
						//
						//  vcMessage.removeBlobFile();
					}else{
						throw new RuntimeException("unexpected VCConsumer type "+vcConsumer);
					}
				}else{
//						lg.info(toString()+"no message received within "+CONSUMER_POLLING_INTERVAL_MS+" ms");
				}
			} catch (JMSException e) {
				if (!bProcessing){
					// stop() has already been requested, and close() unblocks a thread parked
					// in receive(); that is shutdown, not a failure. Logging it as one and
					// looping would spin on the closed consumer. Every deliberate shutdown
					// path (closeAll(), stopAndClose()) clears bProcessing before close(),
					// so this test alone identifies them.
					lg.debug(toString()+" consumer closed while polling", e);
					break;
				}
				if (e instanceof javax.jms.IllegalStateException){
					// The session died underneath a consumer we are still meant to be polling:
					// a broker restart, or the failover transport exhausting its reconnect
					// budget. receive() throws immediately from here on, so looping would spin
					// -- but leaving quietly is worse. It leaves a process that consumes
					// nothing, logs nothing and still reports healthy, which is how dev's
					// submit service sat dead for 6h50m before anyone noticed (issue #2031).
					// Escalate instead, and let the wiring decide what a lost broker means
					// for this process.
					vcMessagingServiceJms.getFailoverWatchdog()
							.onTerminalFailure("consumer session for "+vcConsumer.getVCDestination(), e);
					break;
				}
				onException(e);
			} catch (RollbackException e) {
				lg.error(e.getMessage(),e);
				try {
					jmsSession.rollback();
				} catch (JMSException e1) {
					onException(e1);
				}
			} catch (Exception e) {
				lg.error(e.getMessage(),e);
			}finally{
				if(temporaryMessageProducerSession != null){
					temporaryMessageProducerSession.close();
				}
			}
		}
		lg.info(toString()+" consumer thread exiting.");
	}
	
	
	
	public void start() {
		if (bProcessing){
			throw new RuntimeException("consumer already started");
		}
		setThread(new Thread(this,vcConsumer.getThreadName()));
		getThread().setDaemon(true);
		getThread().start();
	}
	/**
	 * Stop the polling thread and release the JMS resources behind it.
	 *
	 * stop() on its own only asks the thread to finish its current loop -- the MessageConsumer
	 * stays registered with the broker, which keeps dispatching to it. With a prefetch limit
	 * those messages then sit in a consumer nobody is reading and are not redelivered until
	 * the connection dies.
	 */
	void stopAndClose(){
		stop();
		Thread consumerThread = getThread();
		if (consumerThread != null && consumerThread != Thread.currentThread()){
			try {
				// the loop re-checks bProcessing once per receive() timeout
				consumerThread.join(CONSUMER_POLLING_INTERVAL_MS*2);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		close();
	}
	
	public void stop(){
		if (bProcessing){
			bProcessing=false;
			lg.info(toString()+" consumer thread stop requested");
		}
	}
	public void init() throws JMSException {
		boolean bTransacted = true;
		int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
		try {
			this.jmsConnection = vcMessagingServiceJms.createConnectionFactory().createConnection();
			vcMessagingServiceJms.getFailoverWatchdog().attach(this.jmsConnection);
			this.jmsConnection.setExceptionListener(new ExceptionListener() {
				public void onException(JMSException arg0) {
					ConsumerContextJms.this.onException(arg0);
				}
			});
			this.jmsConnection.start();
			this.jmsSession = this.jmsConnection.createSession(bTransacted, acknowledgeMode);
			this.jmsMessageConsumer = this.vcMessagingServiceJms.createConsumer(this.jmsSession, vcConsumer.getVCDestination(), vcConsumer.getSelector(), vcConsumer.getPrefetchLimit());
		}catch (JMSException | VCMessagingException e){
			lg.error(e);
			onException(e);
		}
	}
	
	private void onException(Exception e){
		vcMessagingServiceJms.getDelegate().onException(e);
		lg.error(e.getMessage(),e);
	}
	
	public VCMessagingConsumer getVCConsumer() {
		return vcConsumer;
	}
	
	void close() {
		try {
			if (jmsMessageConsumer!=null){
				jmsMessageConsumer.close();
			}
			if (jmsSession!=null){
				jmsSession.close();
			}
			if (jmsConnection!=null){
				jmsConnection.stop();
				jmsConnection.close();
			}
		}catch (JMSException e){
			onException(e);
		}
	}
	public Thread getThread() {
		return thread;
	}
	public void setThread(Thread thread) {
		this.thread = thread;
	}
}